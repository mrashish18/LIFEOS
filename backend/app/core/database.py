from typing import AsyncGenerator
from sqlalchemy.ext.asyncio import (
    create_async_engine,
    async_sessionmaker,
    AsyncSession,
    AsyncEngine
)
from sqlalchemy.orm import DeclarativeBase
from sqlalchemy.pool import AsyncAdaptedQueuePool, NullPool, StaticPool
from backend.app.core.config import settings

class Base(DeclarativeBase):
    pass

def create_database_engine() -> AsyncEngine:
    db_url = settings.DATABASE_URL
    is_sqlite = db_url.startswith("sqlite")

    if is_sqlite:
        # SQLite async engine configuration with WAL mode for concurrency
        if ":memory:" in db_url:
            engine = create_async_engine(
                db_url,
                echo=settings.DEBUG,
                connect_args={"check_same_thread": False},
                poolclass=StaticPool
            )
        else:
            engine = create_async_engine(
                db_url,
                echo=settings.DEBUG,
                connect_args={"check_same_thread": False},
                poolclass=AsyncAdaptedQueuePool,
                pool_size=settings.DB_POOL_SIZE,
                max_overflow=settings.DB_MAX_OVERFLOW,
                pool_timeout=settings.DB_POOL_TIMEOUT_SECONDS,
                pool_pre_ping=True
            )
    else:
        # Production PostgreSQL engine with bounded QueuePool
        engine = create_async_engine(
            db_url,
            echo=settings.DEBUG,
            pool_size=settings.DB_POOL_SIZE,
            max_overflow=settings.DB_MAX_OVERFLOW,
            pool_timeout=settings.DB_POOL_TIMEOUT_SECONDS,
            pool_recycle=settings.DB_POOL_RECYCLE_SECONDS,
            pool_pre_ping=True
        )
    return engine

engine = create_database_engine()
AsyncSessionLocal = async_sessionmaker(
    bind=engine,
    class_=AsyncSession,
    expire_on_commit=False,
    autocommit=False,
    autoflush=False
)

async def init_db():
    async with engine.begin() as conn:
        if settings.DATABASE_URL.startswith("sqlite"):
            await conn.exec_driver_sql("PRAGMA journal_mode=WAL;")
            await conn.exec_driver_sql("PRAGMA synchronous=NORMAL;")
        await conn.run_sync(Base.metadata.create_all)

async def get_db() -> AsyncGenerator[AsyncSession, None]:
    async with AsyncSessionLocal() as session:
        try:
            yield session
            await session.commit()
        except Exception:
            await session.rollback()
            raise
