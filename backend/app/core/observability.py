import time
import json
import logging
import uuid
from typing import Dict
from collections import defaultdict
from fastapi import Request, Response
from starlette.middleware.base import BaseHTTPMiddleware

logger = logging.getLogger("lifeos.gateway")
logging.basicConfig(level=logging.INFO, format="%(message)s")

class MetricsCollector:
    def __init__(self):
        self.request_count: Dict[str, int] = defaultdict(int)
        self.error_count: Dict[str, int] = defaultdict(int)
        self.rate_limit_count: int = 0
        self.latencies = []
        self.max_latency_samples = 1000

    def record_request(self, method: str, path: str, status_code: int, duration_seconds: float):
        endpoint = f"{method} {path}"
        self.request_count[endpoint] += 1

        if status_code >= 400:
            self.error_count[endpoint] += 1
        if status_code == 429:
            self.rate_limit_count += 1

        if len(self.latencies) < self.max_latency_samples:
            self.latencies.append(duration_seconds)
        else:
            # Reservoir sampling replacement
            self.latencies.pop(0)
            self.latencies.append(duration_seconds)

    def get_summary(self) -> dict:
        sorted_lats = sorted(self.latencies) if self.latencies else [0.0]
        n = len(sorted_lats)

        p50 = sorted_lats[int(n * 0.50)]
        p90 = sorted_lats[int(n * 0.90)] if n > 1 else p50
        p95 = sorted_lats[int(n * 0.95)] if n > 1 else p50
        p99 = sorted_lats[int(n * 0.99)] if n > 1 else p95

        return {
            "total_requests": sum(self.request_count.values()),
            "total_errors": sum(self.error_count.values()),
            "rate_limited_requests": self.rate_limit_count,
            "latency_p50_ms": round(p50 * 1000, 2),
            "latency_p90_ms": round(p90 * 1000, 2),
            "latency_p95_ms": round(p95 * 1000, 2),
            "latency_p99_ms": round(p99 * 1000, 2),
            "endpoints": dict(self.request_count)
        }

metrics_collector = MetricsCollector()

class ObservabilityMiddleware(BaseHTTPMiddleware):
    async def dispatch(self, request: Request, call_next) -> Response:
        correlation_id = request.headers.get("X-Correlation-ID") or str(uuid.uuid4())
        request.state.correlation_id = correlation_id

        start_time = time.time()
        try:
            response = await call_next(request)
            duration = time.time() - start_time
            response.headers["X-Correlation-ID"] = correlation_id

            metrics_collector.record_request(
                method=request.method,
                path=request.url.path,
                status_code=response.status_code,
                duration_seconds=duration
            )

            log_entry = {
                "timestamp": time.strftime("%Y-%m-%dT%H:%M:%SZ", time.gmtime()),
                "correlation_id": correlation_id,
                "method": request.method,
                "path": request.url.path,
                "status_code": response.status_code,
                "duration_ms": round(duration * 1000, 2),
                "client_ip": request.client.host if request.client else "unknown"
            }
            logger.info(json.dumps(log_entry))
            return response
        except Exception as exc:
            duration = time.time() - start_time
            metrics_collector.record_request(
                method=request.method,
                path=request.url.path,
                status_code=500,
                duration_seconds=duration
            )
            log_entry = {
                "timestamp": time.strftime("%Y-%m-%dT%H:%M:%SZ", time.gmtime()),
                "correlation_id": correlation_id,
                "method": request.method,
                "path": request.url.path,
                "status_code": 500,
                "duration_ms": round(duration * 1000, 2),
                "error": str(exc)
            }
            logger.error(json.dumps(log_entry))
            raise exc
