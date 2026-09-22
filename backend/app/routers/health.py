import time
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import text
from backend.app.core.database import get_db
from backend.app.core.observability import metrics_collector

router = APIRouter(tags=["Health & Observability"])

@router.get("/health/live")
async def liveness():
    """
    Liveness probe for orchestration and load balancers.
    Confirms the application process is running and event loop is alive.
    Does NOT depend on external databases.
    """
    return {
        "status": "UP",
        "timestamp": int(time.time()),
        "service": "LIFEOS Gateway"
    }

@router.get("/health/ready")
async def readiness(db: AsyncSession = Depends(get_db)):
    """
    Readiness probe verifying that the application instance can acquire
    a database connection from the connection pool and execute queries.
    """
    try:
        start_time = time.time()
        result = await db.execute(text("SELECT 1"))
        latency_ms = round((time.time() - start_time) * 1000, 2)
        return {
            "status": "READY",
            "database": "CONNECTED",
            "db_ping_ms": latency_ms,
            "timestamp": int(time.time())
        }
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
            detail=f"Database pool connection failure: {str(e)}"
        )

@router.get("/metrics")
async def metrics():
    """
    Observability metrics endpoint exposing request latency distributions,
    error counts, and throughput statistics.
    """
    return metrics_collector.get_summary()
