import pytest
from backend.app.core.rate_limiter import InMemoryRateLimiter

def test_sliding_window_rate_limiter_allows_under_threshold():
    limiter = InMemoryRateLimiter(window_seconds=2)
    key = "test_user_1"

    for _ in range(5):
        allowed, remaining, retry_after = limiter.is_allowed(key, max_requests=5)
        assert allowed is True

    # 6th request should be rejected
    allowed, remaining, retry_after = limiter.is_allowed(key, max_requests=5)
    assert allowed is False
    assert remaining == 0
    assert retry_after > 0

@pytest.mark.asyncio
async def test_rate_limit_http_response(client):
    # Set a small rate limit on an endpoint or test repeated hits
    for _ in range(120):
        res = await client.get("/health/live")
        assert res.status_code == 200
