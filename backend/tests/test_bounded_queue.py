import pytest
import asyncio
from fastapi import HTTPException
from backend.app.core.bounded_queue import BoundedWorkerPool

@pytest.mark.asyncio
async def test_bounded_worker_pool_concurrency():
    pool = BoundedWorkerPool(max_concurrency=2, queue_capacity=10)
    active = 0
    max_active = 0

    async def worker_job():
        nonlocal active, max_active
        active += 1
        if active > max_active:
            max_active = active
        await asyncio.sleep(0.05)
        active -= 1
        return "done"

    tasks = [pool.execute(worker_job) for _ in range(6)]
    results = await asyncio.gather(*tasks)

    assert len(results) == 6
    assert max_active <= 2

@pytest.mark.asyncio
async def test_bounded_worker_pool_saturation():
    pool = BoundedWorkerPool(max_concurrency=1, queue_capacity=2)

    async def long_job():
        await asyncio.sleep(0.5)

    # Launch 1 running + 2 queued = capacity full
    t1 = asyncio.create_task(pool.execute(long_job))
    t2 = asyncio.create_task(pool.execute(long_job))
    t3 = asyncio.create_task(pool.execute(long_job))
    await asyncio.sleep(0.01)

    # 4th task should be rejected with 503
    with pytest.raises(HTTPException) as exc_info:
        await pool.execute(long_job)
    assert exc_info.value.status_code == 503

    t1.cancel()
    t2.cancel()
    t3.cancel()
