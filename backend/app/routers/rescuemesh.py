import time
from typing import List, Optional
from fastapi import APIRouter, Depends, Request, HTTPException, status, Query
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select, func, case
from backend.app.core.database import get_db
from backend.app.core.config import settings
from backend.app.core.rate_limiter import check_rate_limit
from backend.app.models.models import EmergencyMessageModel
from backend.app.schemas.schemas import (
    EmergencyMessageCreate,
    EmergencyMessageResponse,
    MeshSyncResponse
)

router = APIRouter(prefix=f"{settings.API_V1_STR}/rescuemesh", tags=["Resilience Intelligence"])

@router.post("/ingress", response_model=EmergencyMessageResponse)
async def ingress_emergency_message(
    request: Request,
    message_in: EmergencyMessageCreate,
    db: AsyncSession = Depends(get_db)
):
    """
    Ingests an emergency message packet into the RescueMesh network gateway.
    Guarantees:
    - Cryptographic SHA-256 fingerprint deduplication (409 Conflict if duplicate)
    - Strict hop bound enforcement (max 5 hops)
    - TTL expiration rejection
    - Priority ordering (CRITICAL > HIGH > NORMAL)
    """
    await check_rate_limit(request, settings.RATE_LIMIT_EMERGENCY, "mesh:ingress")

    now_millis = int(time.time() * 1000)

    # 1. Hop limit validation
    if message_in.hopCount >= message_in.maxHops or message_in.hopCount >= settings.RESCUEMESH_MAX_HOPS:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Message exceeded maximum hop threshold ({message_in.maxHops}). Dropped to prevent loops."
        )

    # 2. TTL Expiration validation
    if message_in.expiresAtEpochMillis <= now_millis:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Message has expired according to its Time-To-Live (TTL)."
        )

    # 3. Deduplication check via SHA-256 fingerprint
    existing_fingerprint = await db.execute(
        select(EmergencyMessageModel).where(
            EmergencyMessageModel.fingerprint_sha256 == message_in.fingerprintSha256
        )
    )
    if existing_fingerprint.scalar_one_or_none():
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="Duplicate message: SHA-256 fingerprint already exists in gateway store."
        )

    # 4. Check message ID collision
    existing_id = await db.get(EmergencyMessageModel, message_in.messageId)
    if existing_id:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="Duplicate message ID already registered."
        )

    # 5. Persist message with incremented hop count
    new_msg = EmergencyMessageModel(
        message_id=message_in.messageId,
        sender_id=message_in.senderId,
        recipient_id=message_in.recipientId,
        type=message_in.type,
        priority=message_in.priority,
        status="QUEUED",
        payload=message_in.payload,
        created_at_epoch_millis=message_in.createdAtEpochMillis,
        expires_at_epoch_millis=message_in.expiresAtEpochMillis,
        hop_count=message_in.hopCount + 1,
        max_hops=message_in.maxHops,
        transport_type="NETWORK",
        fingerprint_sha256=message_in.fingerprintSha256,
        relay_history_json=f'[{{"relayedBy":"GATEWAY_EGRESS","relayedAt":{now_millis}}}]'
    )
    db.add(new_msg)
    await db.commit()

    return EmergencyMessageResponse(
        messageId=new_msg.message_id,
        senderId=new_msg.sender_id,
        recipientId=new_msg.recipient_id,
        type=new_msg.type,
        priority=new_msg.priority,
        status=new_msg.status,
        payload=new_msg.payload,
        createdAtEpochMillis=new_msg.created_at_epoch_millis,
        expiresAtEpochMillis=new_msg.expires_at_epoch_millis,
        hopCount=new_msg.hop_count,
        maxHops=new_msg.max_hops,
        transportType=new_msg.transport_type,
        fingerprintSha256=new_msg.fingerprint_sha256
    )

@router.post("/sync", response_model=MeshSyncResponse)
async def sync_mesh_batch(
    request: Request,
    batch: List[EmergencyMessageCreate],
    db: AsyncSession = Depends(get_db)
):
    """
    Batch synchronization endpoint for opportunistic store-and-forward flushing.
    Bounded to max 100 messages per request to maintain predictable latency.
    """
    await check_rate_limit(request, settings.RATE_LIMIT_SYNC, "mesh:sync")

    if len(batch) > settings.RESCUEMESH_MAX_BATCH_SIZE:
        raise HTTPException(
            status_code=status.HTTP_413_REQUEST_ENTITY_TOO_LARGE,
            detail=f"Batch size exceeds maximum limit of {settings.RESCUEMESH_MAX_BATCH_SIZE} packets."
        )

    now_millis = int(time.time() * 1000)
    accepted = 0
    duplicates = 0
    expired = 0
    rejected = 0

    for msg in batch:
        if msg.expiresAtEpochMillis <= now_millis:
            expired += 1
            continue
        if msg.hopCount >= msg.maxHops or msg.hopCount >= settings.RESCUEMESH_MAX_HOPS:
            rejected += 1
            continue

        existing = await db.execute(
            select(EmergencyMessageModel.message_id).where(
                (EmergencyMessageModel.message_id == msg.messageId) |
                (EmergencyMessageModel.fingerprint_sha256 == msg.fingerprintSha256)
            )
        )
        if existing.scalar_one_or_none():
            duplicates += 1
            continue

        new_msg = EmergencyMessageModel(
            message_id=msg.messageId,
            sender_id=msg.senderId,
            recipient_id=msg.recipientId,
            type=msg.type,
            priority=msg.priority,
            status="QUEUED",
            payload=msg.payload,
            created_at_epoch_millis=msg.createdAtEpochMillis,
            expires_at_epoch_millis=msg.expiresAtEpochMillis,
            hop_count=msg.hopCount + 1,
            max_hops=msg.maxHops,
            transport_type="NETWORK",
            fingerprint_sha256=msg.fingerprintSha256,
            relay_history_json=f'[{{"relayedBy":"GATEWAY_SYNC","relayedAt":{now_millis}}}]'
        )
        db.add(new_msg)
        accepted += 1

    await db.commit()
    return MeshSyncResponse(
        acceptedCount=accepted,
        duplicateCount=duplicates,
        expiredCount=expired,
        rejectedCount=rejected,
        explanation=f"Batch processed {len(batch)} packets: {accepted} accepted, {duplicates} duplicate, {expired} expired, {rejected} rejected."
    )

@router.get("/queue", response_model=List[EmergencyMessageResponse])
async def list_queued_messages(
    request: Request,
    limit: int = Query(default=20, ge=1, le=50),
    offset: int = Query(default=0, ge=0),
    db: AsyncSession = Depends(get_db)
):
    """
    Paginated inspection of active queued emergency packets, strictly ordered by
    triage priority (CRITICAL > HIGH > NORMAL) and oldest arrival time.
    """
    await check_rate_limit(request, settings.RATE_LIMIT_PUBLIC, "mesh:queue")

    priority_order = case(
        (EmergencyMessageModel.priority == "CRITICAL", 1),
        (EmergencyMessageModel.priority == "HIGH", 2),
        (EmergencyMessageModel.priority == "NORMAL", 3),
        else_=4
    )

    query = (
        select(EmergencyMessageModel)
        .where(EmergencyMessageModel.status == "QUEUED")
        .order_by(priority_order.asc(), EmergencyMessageModel.created_at_epoch_millis.asc())
        .limit(limit)
        .offset(offset)
    )

    result = await db.execute(query)
    messages = result.scalars().all()

    return [
        EmergencyMessageResponse(
            messageId=m.message_id,
            senderId=m.sender_id,
            recipientId=m.recipient_id,
            type=m.type,
            priority=m.priority,
            status=m.status,
            payload=m.payload,
            createdAtEpochMillis=m.created_at_epoch_millis,
            expiresAtEpochMillis=m.expires_at_epoch_millis,
            hopCount=m.hop_count,
            maxHops=m.max_hops,
            transportType=m.transport_type,
            fingerprintSha256=m.fingerprint_sha256
        )
        for m in messages
    ]
