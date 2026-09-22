import time
import json
from typing import Optional
from fastapi import APIRouter, Depends, Query, Header, HTTPException, status, Request
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select, func
from backend.app.core.database import get_db
from backend.app.core.rate_limiter import check_rate_limit
from backend.app.core.config import settings
from backend.app.models.models import TaskModel, IdempotencyRecordModel
from backend.app.schemas.schemas import TaskCreate, TaskResponse, PaginatedTasksResponse

router = APIRouter(prefix=f"{settings.API_V1_STR}/tasks", tags=["Tasks Sync"])

@router.get("", response_model=PaginatedTasksResponse)
async def list_tasks(
    request: Request,
    limit: int = Query(default=20, ge=1, le=100),
    offset: int = Query(default=0, ge=0),
    status: Optional[str] = Query(default=None),
    category: Optional[str] = Query(default=None),
    db: AsyncSession = Depends(get_db)
):
    """
    Paginated task query with limit, offset, and indexed status/category filters.
    Bounded memory footprint prevents returning unbounded collections.
    """
    await check_rate_limit(request, settings.RATE_LIMIT_SYNC, "tasks:list")

    query = select(TaskModel)
    count_query = select(func.count(TaskModel.id))

    if status:
        query = query.where(TaskModel.status == status.upper())
        count_query = count_query.where(TaskModel.status == status.upper())
    if category:
        query = query.where(TaskModel.category == category.upper())
        count_query = count_query.where(TaskModel.category == category.upper())

    total_count = (await db.execute(count_query)).scalar() or 0

    query = query.order_by(TaskModel.created_at_epoch_millis.desc()).limit(limit).offset(offset)
    result = await db.execute(query)
    tasks = result.scalars().all()

    items = [
        TaskResponse(
            id=t.id,
            title=t.title,
            description=t.description,
            priority=t.priority,
            status=t.status,
            category=t.category,
            estimatedMinutes=t.estimated_minutes,
            dueAtEpochMillis=t.due_at_epoch_millis,
            createdAtEpochMillis=t.created_at_epoch_millis,
            updatedAtEpochMillis=t.updated_at_epoch_millis
        )
        for t in tasks
    ]

    return PaginatedTasksResponse(
        items=items,
        total=total_count,
        limit=limit,
        offset=offset,
        has_more=(offset + len(items)) < total_count
    )

@router.post("/sync", response_model=TaskResponse)
async def sync_task(
    request: Request,
    task_in: TaskCreate,
    idempotency_key: Optional[str] = Header(None, alias="Idempotency-Key"),
    db: AsyncSession = Depends(get_db)
):
    """
    Idempotent task synchronization.
    If an identical Idempotency-Key was already processed, returns the previous response.
    Performs deterministic conflict resolution based on updatedAtEpochMillis.
    """
    await check_rate_limit(request, settings.RATE_LIMIT_SYNC, "tasks:sync")

    now_millis = int(time.time() * 1000)
    created_at = task_in.createdAtEpochMillis or now_millis
    updated_at = task_in.updatedAtEpochMillis or now_millis

    # 1. Check idempotency cache
    if idempotency_key:
        cached = await db.get(IdempotencyRecordModel, idempotency_key)
        if cached:
            cached_data = json.loads(cached.response_body)
            return TaskResponse(**cached_data)

    # 2. Check existing task for conflict resolution
    existing = await db.get(TaskModel, task_in.id)
    if existing:
        # If server version is strictly newer than incoming client version, preserve server state
        if existing.updated_at_epoch_millis > updated_at:
            response = TaskResponse(
                id=existing.id,
                title=existing.title,
                description=existing.description,
                priority=existing.priority,
                status=existing.status,
                category=existing.category,
                estimatedMinutes=existing.estimated_minutes,
                dueAtEpochMillis=existing.due_at_epoch_millis,
                createdAtEpochMillis=existing.created_at_epoch_millis,
                updatedAtEpochMillis=existing.updated_at_epoch_millis
            )
            return response

        # Otherwise update existing task
        existing.title = task_in.title
        existing.description = task_in.description
        existing.priority = task_in.priority
        existing.status = task_in.status
        existing.category = task_in.category
        existing.estimated_minutes = task_in.estimatedMinutes
        existing.due_at_epoch_millis = task_in.dueAtEpochMillis
        existing.updated_at_epoch_millis = updated_at
        saved_task = existing
    else:
        new_task = TaskModel(
            id=task_in.id,
            title=task_in.title,
            description=task_in.description,
            priority=task_in.priority,
            status=task_in.status,
            category=task_in.category,
            estimated_minutes=task_in.estimatedMinutes,
            due_at_epoch_millis=task_in.dueAtEpochMillis,
            created_at_epoch_millis=created_at,
            updated_at_epoch_millis=updated_at
        )
        db.add(new_task)
        saved_task = new_task

    await db.flush()

    response = TaskResponse(
        id=saved_task.id,
        title=saved_task.title,
        description=saved_task.description,
        priority=saved_task.priority,
        status=saved_task.status,
        category=saved_task.category,
        estimatedMinutes=saved_task.estimated_minutes,
        dueAtEpochMillis=saved_task.due_at_epoch_millis,
        createdAtEpochMillis=saved_task.created_at_epoch_millis,
        updatedAtEpochMillis=saved_task.updated_at_epoch_millis
    )

    # 3. Store idempotency record
    if idempotency_key:
        idempotency_record = IdempotencyRecordModel(
            idempotency_key=idempotency_key,
            response_code=200,
            response_body=json.dumps(response.model_dump()),
            created_at_epoch_millis=now_millis
        )
        db.add(idempotency_record)

    await db.commit()
    return response
