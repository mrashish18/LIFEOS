import pytest

@pytest.mark.asyncio
async def test_task_sync_and_idempotency(client):
    task_payload = {
        "id": "task_sync_001",
        "title": "Calibrate Neural Architecture",
        "description": "Tune hyperparameters",
        "priority": "HIGH",
        "status": "PENDING",
        "category": "WORK",
        "estimatedMinutes": 45,
        "createdAtEpochMillis": 1700000000000,
        "updatedAtEpochMillis": 1700000000000
    }

    headers = {"Idempotency-Key": "idemp_key_test_123"}

    # 1. Initial sync
    res1 = await client.post("/api/v1/tasks/sync", json=task_payload, headers=headers)
    assert res1.status_code == 200
    data1 = res1.json()
    assert data1["id"] == "task_sync_001"
    assert data1["title"] == "Calibrate Neural Architecture"

    # 2. Retry with same Idempotency-Key
    res2 = await client.post("/api/v1/tasks/sync", json=task_payload, headers=headers)
    assert res2.status_code == 200
    data2 = res2.json()
    assert data2["id"] == data1["id"]

    # 3. Query list
    list_res = await client.get("/api/v1/tasks?limit=10")
    assert list_res.status_code == 200
    list_data = list_res.json()
    assert list_data["total"] == 1
    assert len(list_data["items"]) == 1
    assert list_data["items"][0]["id"] == "task_sync_001"

@pytest.mark.asyncio
async def test_task_pagination(client):
    for i in range(15):
        payload = {
            "id": f"task_bulk_{i}",
            "title": f"Task {i}",
            "description": "Bulk description",
            "priority": "MEDIUM",
            "status": "PENDING",
            "category": "PERSONAL",
            "createdAtEpochMillis": 1700000000000 + i,
            "updatedAtEpochMillis": 1700000000000 + i
        }
        await client.post("/api/v1/tasks/sync", json=payload)

    # Page 1
    page1 = await client.get("/api/v1/tasks?limit=5&offset=0")
    assert page1.status_code == 200
    d1 = page1.json()
    assert d1["total"] == 15
    assert len(d1["items"]) == 5
    assert d1["has_more"] is True

    # Page 3
    page3 = await client.get("/api/v1/tasks?limit=5&offset=10")
    assert page3.status_code == 200
    d3 = page3.json()
    assert len(d3["items"]) == 5
    assert d3["has_more"] is False
