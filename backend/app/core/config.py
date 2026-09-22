import os
from typing import Optional
from pydantic_settings import BaseSettings, SettingsConfigDict

class Settings(BaseSettings):
    # Service Information
    APP_NAME: str = "LIFEOS High-Concurrency Gateway & Sync Engine"
    API_V1_STR: str = "/api/v1"
    ENVIRONMENT: str = "production"
    DEBUG: bool = False

    # Database Configuration
    # Supports PostgreSQL in production and high-speed async SQLite (WAL) for local testing/eval
    DATABASE_URL: str = os.getenv(
        "DATABASE_URL",
        "sqlite+aiosqlite:///./lifeos_server.db"
    )
    DB_POOL_SIZE: int = 20
    DB_MAX_OVERFLOW: int = 10
    DB_POOL_TIMEOUT_SECONDS: float = 5.0
    DB_POOL_RECYCLE_SECONDS: int = 1800

    # Concurrency & Request Timeouts
    REQUEST_TIMEOUT_SECONDS: float = 30.0
    MAX_PAYLOAD_BYTES: int = 65_536  # 64 KB strict upper bound

    # Rate Limiting (Requests per minute)
    RATE_LIMIT_PUBLIC: int = 120
    RATE_LIMIT_AUTH: int = 20
    RATE_LIMIT_SYNC: int = 300
    RATE_LIMIT_AI: int = 60
    RATE_LIMIT_EMERGENCY: int = 180

    # Distributed Rate Limiter / Cache Backend
    REDIS_URL: Optional[str] = os.getenv("REDIS_URL", None)

    # Bounded Concurrency for Expensive AI / RealityCheck Processing
    AI_WORKER_CONCURRENCY: int = 25
    AI_QUEUE_CAPACITY: int = 200
    AI_PROCESSING_TIMEOUT_SECONDS: float = 10.0
    AI_CIRCUIT_BREAKER_FAILURES: int = 5
    AI_CIRCUIT_BREAKER_COOLDOWN_SECONDS: float = 20.0

    # RescueMesh Gateway Policy
    RESCUEMESH_MAX_HOPS: int = 5
    RESCUEMESH_MAX_BATCH_SIZE: int = 100

    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        extra="ignore"
    )

settings = Settings()
