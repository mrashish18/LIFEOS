import asyncio
from typing import Callable, Any
from fastapi import HTTPException, status
from backend.app.core.config import settings

class BoundedWorkerPool:
    """
    Limits concurrent execution of expensive operations (e.g., AI/LLM/RealityCheck).
    Uses an asyncio.Semaphore to cap active concurrency and rejects when wait queue is full.
    """
    def __init__(self, max_concurrency: int = 25, queue_capacity: int = 200):
        self.semaphore = asyncio.Semaphore(max_concurrency)
        self.max_concurrency = max_concurrency
        self.queue_capacity = queue_capacity
        self._current_waiting = 0

    async def execute(self, func: Callable, *args, timeout: float = 10.0, **kwargs) -> Any:
        if self._current_waiting >= self.queue_capacity:
            raise HTTPException(
                status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
                detail="Intelligence processing pipeline is at peak capacity. Please retry in a few seconds.",
                headers={"Retry-After": "5"}
            )

        self._current_waiting += 1
        try:
            # Acquire semaphore within timeout window
            async with self.semaphore:
                return await asyncio.wait_for(func(*args, **kwargs), timeout=timeout)
        except asyncio.TimeoutError:
            raise HTTPException(
                status_code=status.HTTP_504_GATEWAY_TIMEOUT,
                detail="Intelligence processing timed out under high load."
            )
        finally:
            self._current_waiting -= 1

ai_worker_pool = BoundedWorkerPool(
    max_concurrency=settings.AI_WORKER_CONCURRENCY,
    queue_capacity=settings.AI_QUEUE_CAPACITY
)
