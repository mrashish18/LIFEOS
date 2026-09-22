import time
import pytest

@pytest.mark.asyncio
async def test_rescuemesh_ingress_success(client):
    now_millis = int(time.time() * 1000)
    payload = {
        "messageId": "mesh_msg_001",
        "senderId": "node_alpha",
        "recipientId": "BROADCAST",
        "type": "EMERGENCY",
        "priority": "CRITICAL",
        "payload": "Medical distress at coordinates 37.7749,-122.4194",
        "createdAtEpochMillis": now_millis - 1000,
        "expiresAtEpochMillis": now_millis + 3600000,
        "hopCount": 1,
        "maxHops": 5,
        "fingerprintSha256": "abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890"
    }
    res = await client.post("/api/v1/rescuemesh/ingress", json=payload)
    assert res.status_code == 200
    data = res.json()
    assert data["messageId"] == "mesh_msg_001"
    assert data["hopCount"] == 2  # Incremented on ingress
    assert data["status"] == "QUEUED"

@pytest.mark.asyncio
async def test_rescuemesh_ingress_deduplication(client):
    now_millis = int(time.time() * 1000)
    payload = {
        "messageId": "mesh_msg_002",
        "senderId": "node_beta",
        "recipientId": "BROADCAST",
        "type": "HAZARD_ALERT",
        "priority": "HIGH",
        "payload": "Structural collapse reported",
        "createdAtEpochMillis": now_millis - 1000,
        "expiresAtEpochMillis": now_millis + 3600000,
        "hopCount": 1,
        "maxHops": 5,
        "fingerprintSha256": "1111222233334444555566667777888899990000aaaabbbbccccddddeeeeffff"
    }
    # 1. First ingress succeeds
    res1 = await client.post("/api/v1/rescuemesh/ingress", json=payload)
    assert res1.status_code == 200

    # 2. Duplicate fingerprint returns 409 Conflict
    payload_dup = payload.copy()
    payload_dup["messageId"] = "mesh_msg_002_alt"
    res2 = await client.post("/api/v1/rescuemesh/ingress", json=payload_dup)
    assert res2.status_code == 409
    assert "duplicate" in res2.json()["detail"].lower()

@pytest.mark.asyncio
async def test_rescuemesh_hop_limit_enforcement(client):
    now_millis = int(time.time() * 1000)
    payload = {
        "messageId": "mesh_msg_003",
        "senderId": "node_gamma",
        "recipientId": "BROADCAST",
        "type": "EMERGENCY",
        "priority": "NORMAL",
        "payload": "Looping packet test",
        "createdAtEpochMillis": now_millis - 1000,
        "expiresAtEpochMillis": now_millis + 3600000,
        "hopCount": 5,  # At or above limit
        "maxHops": 5,
        "fingerprintSha256": "deadbeef12345678deadbeef12345678deadbeef12345678deadbeef12345678"
    }
    res = await client.post("/api/v1/rescuemesh/ingress", json=payload)
    assert res.status_code == 400
    assert "hop" in res.json()["detail"].lower()

@pytest.mark.asyncio
async def test_rescuemesh_ttl_expiration(client):
    now_millis = int(time.time() * 1000)
    payload = {
        "messageId": "mesh_msg_004",
        "senderId": "node_delta",
        "recipientId": "BROADCAST",
        "type": "EMERGENCY",
        "priority": "HIGH",
        "payload": "Stale packet test",
        "createdAtEpochMillis": now_millis - 10000,
        "expiresAtEpochMillis": now_millis - 1000,  # Expired
        "hopCount": 0,
        "maxHops": 5,
        "fingerprintSha256": "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"
    }
    res = await client.post("/api/v1/rescuemesh/ingress", json=payload)
    assert res.status_code == 400
    assert "expired" in res.json()["detail"].lower()

@pytest.mark.asyncio
async def test_rescuemesh_sync_batch_and_priority_queue(client):
    now_millis = int(time.time() * 1000)

    # Prepare 3 messages: NORMAL first, CRITICAL second, EXPIRED third
    batch = [
        {
            "messageId": "batch_msg_normal",
            "senderId": "node_n",
            "recipientId": "BROADCAST",
            "type": "RESOURCE_REQUEST",
            "priority": "NORMAL",
            "payload": "Surplus water rations at Sector 4",
            "createdAtEpochMillis": now_millis - 500,
            "expiresAtEpochMillis": now_millis + 3600000,
            "hopCount": 1,
            "maxHops": 5,
            "fingerprintSha256": "norm00001111222233334444555566667777888899990000aaaabbbbccccdddd"
        },
        {
            "messageId": "batch_msg_critical",
            "senderId": "node_c",
            "recipientId": "BROADCAST",
            "type": "EMERGENCY",
            "priority": "CRITICAL",
            "payload": "Trapped civilians at Sector 9",
            "createdAtEpochMillis": now_millis - 300,
            "expiresAtEpochMillis": now_millis + 3600000,
            "hopCount": 0,
            "maxHops": 5,
            "fingerprintSha256": "crit00001111222233334444555566667777888899990000aaaabbbbccccdddd"
        },
        {
            "messageId": "batch_msg_expired",
            "senderId": "node_e",
            "recipientId": "BROADCAST",
            "type": "EMERGENCY",
            "priority": "HIGH",
            "payload": "Expired message",
            "createdAtEpochMillis": now_millis - 10000,
            "expiresAtEpochMillis": now_millis - 5000,
            "hopCount": 0,
            "maxHops": 5,
            "fingerprintSha256": "expi00001111222233334444555566667777888899990000aaaabbbbccccdddd"
        }
    ]

    sync_res = await client.post("/api/v1/rescuemesh/sync", json=batch)
    assert sync_res.status_code == 200
    sync_data = sync_res.json()
    assert sync_data["acceptedCount"] == 2
    assert sync_data["expiredCount"] == 1

    # Verify queue ordering: CRITICAL message should be first despite NORMAL being inserted first
    queue_res = await client.get("/api/v1/rescuemesh/queue?limit=10")
    assert queue_res.status_code == 200
    queue_items = queue_res.json()
    assert len(queue_items) == 2
    assert queue_items[0]["priority"] == "CRITICAL"
    assert queue_items[0]["messageId"] == "batch_msg_critical"
    assert queue_items[1]["priority"] == "NORMAL"
    assert queue_items[1]["messageId"] == "batch_msg_normal"
