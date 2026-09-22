import pytest

@pytest.mark.asyncio
async def test_liveness_probe(client):
    response = await client.get("/health/live")
    assert response.status_code == 200
    data = response.json()
    assert data["status"] == "UP"
    assert "timestamp" in data

@pytest.mark.asyncio
async def test_readiness_probe(client):
    response = await client.get("/health/ready")
    assert response.status_code == 200
    data = response.json()
    assert data["status"] == "READY"
    assert data["database"] == "CONNECTED"
    assert "db_ping_ms" in data

@pytest.mark.asyncio
async def test_metrics_endpoint(client):
    # Perform a request to generate metrics
    await client.get("/health/live")
    response = await client.get("/metrics")
    assert response.status_code == 200
    data = response.json()
    assert data["total_requests"] >= 1
    assert "latency_p50_ms" in data
    assert "latency_p95_ms" in data
