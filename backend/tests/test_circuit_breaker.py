import pytest
import time
from backend.app.core.circuit_breaker import (
    CircuitBreaker,
    CircuitState,
    CircuitBreakerOpenException
)

@pytest.mark.asyncio
async def test_circuit_breaker_trips_after_failures():
    cb = CircuitBreaker(name="test_cb", failure_threshold=3, recovery_timeout_seconds=0.2)
    assert cb.get_state() == CircuitState.CLOSED

    async def failing_call():
        raise ValueError("Simulated network outage")

    for _ in range(3):
        with pytest.raises(ValueError):
            await cb.call(failing_call)

    # State should now be OPEN
    assert cb.get_state() == CircuitState.OPEN

    # Next call should immediately fail with CircuitBreakerOpenException
    with pytest.raises(CircuitBreakerOpenException):
        await cb.call(failing_call)

    # Wait for cooldown to transition to HALF_OPEN
    time.sleep(0.25)
    assert cb.get_state() == CircuitState.HALF_OPEN

    # A successful call should restore it to CLOSED
    async def successful_call():
        return "OK"

    res = await cb.call(successful_call)
    assert res == "OK"
    assert cb.get_state() == CircuitState.CLOSED
