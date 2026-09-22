import pytest

@pytest.mark.asyncio
async def test_realitycheck_supported_claim(client):
    payload = {
        "text": "The Earth orbits the Sun every 365 days in a solar year"
    }
    res = await client.post("/api/v1/realitycheck/investigate", json=payload)
    assert res.status_code == 200
    data = res.json()
    assert data["verdict"] == "SUPPORTED"
    assert data["confidencePercentage"] >= 90
    assert len(data["topSources"]) > 0
    assert data["cached"] is False

@pytest.mark.asyncio
async def test_realitycheck_contradicted_claim(client):
    payload = {
        "text": "Taking antibiotics will cure a viral cold infection quickly"
    }
    res = await client.post("/api/v1/realitycheck/investigate", json=payload)
    assert res.status_code == 200
    data = res.json()
    assert data["verdict"] == "CONTRADICTED"
    assert data["confidencePercentage"] >= 90

@pytest.mark.asyncio
async def test_realitycheck_caching(client):
    payload = {
        "text": "Humans landed on the moon during the Apollo mission in 1969"
    }
    # First call - cache miss
    res1 = await client.post("/api/v1/realitycheck/investigate", json=payload)
    assert res1.status_code == 200
    assert res1.json()["cached"] is False

    # Second call - cache hit
    res2 = await client.post("/api/v1/realitycheck/investigate", json=payload)
    assert res2.status_code == 200
    assert res2.json()["cached"] is True
    assert res2.json()["verdict"] == res1.json()["verdict"]

@pytest.mark.asyncio
async def test_realitycheck_insufficient_evidence(client):
    payload = {
        "text": "Quantum entanglement creates purple telepathic pineapples on Neptune"
    }
    res = await client.post("/api/v1/realitycheck/investigate", json=payload)
    assert res.status_code == 200
    data = res.json()
    assert data["verdict"] == "INSUFFICIENT_EVIDENCE"
    assert data["evidenceCount"] == 0

@pytest.mark.asyncio
async def test_realitycheck_empty_payload_validation(client):
    payload = {"text": ""}
    res = await client.post("/api/v1/realitycheck/investigate", json=payload)
    assert res.status_code == 422
