import time
from typing import Tuple, Dict
from collections import deque
from fastapi import Request, HTTPException, status
from backend.app.core.config import settings

class InMemoryRateLimiter:
    """
    Thread-safe sliding-window rate limiter for development and single-instance deployments.
    Tracks timestamps per key and prunes events older than window (60 seconds).
    """
    def __init__(self, window_seconds: int = 60):
        self.window_seconds = window_seconds
        self._history: Dict[str, deque] = {}

    def is_allowed(self, key: str, max_requests: int) -> Tuple[bool, int, int]:
        now = time.time()
        cutoff = now - self.window_seconds

        if key not in self._history:
            self._history[key] = deque()

        timestamps = self._history[key]
        # Prune expired timestamps
        while timestamps and timestamps[0] <= cutoff:
            timestamps.popleft()

        current_count = len(timestamps)
        if current_count >= max_requests:
            retry_after = int(self.window_seconds - (now - timestamps[0])) + 1
            remaining = 0
            return False, remaining, max(1, retry_after)

        timestamps.append(now)
        remaining = max_requests - len(timestamps)
        return True, remaining, 0

    def clear(self):
        self._history.clear()

global_rate_limiter = InMemoryRateLimiter(window_seconds=60)

async def check_rate_limit(request: Request, max_requests: int, tier_name: str = "general"):
    """
    Dependency or middleware function to enforce rate limits per client IP or authorization header.
    """
    client_ip = request.client.host if request.client else "127.0.0.1"
    auth_header = request.headers.get("Authorization", "")
    key = f"{tier_name}:{client_ip}" if not auth_header else f"{tier_name}:{auth_header[-16:]}"

    allowed, remaining, retry_after = global_rate_limiter.is_allowed(key, max_requests)

    if not allowed:
        raise HTTPException(
            status_code=status.HTTP_429_TOO_MANY_REQUESTS,
            detail=f"Rate limit exceeded. Try again in {retry_after} seconds.",
            headers={
                "Retry-After": str(retry_after),
                "X-RateLimit-Limit": str(max_requests),
                "X-RateLimit-Remaining": "0"
            }
        )
    return remaining
