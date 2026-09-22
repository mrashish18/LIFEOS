import time
from contextlib import asynccontextmanager
from fastapi import FastAPI, Request, status
from fastapi.responses import JSONResponse
from fastapi.middleware.cors import CORSMiddleware
from backend.app.core.config import settings
from backend.app.core.database import init_db
from backend.app.core.observability import ObservabilityMiddleware
from backend.app.routers import health, tasks, realitycheck, rescuemesh

@asynccontextmanager
async def lifespan(app: FastAPI):
    # Initialize database tables and connection pool on startup
    await init_db()
    yield

app = FastAPI(
    title=settings.APP_NAME,
    version="1.0.0",
    lifespan=lifespan,
    docs_url="/docs" if settings.DEBUG else None,
    redoc_url=None
)

# 1. Observability and correlation ID middleware
app.add_middleware(ObservabilityMiddleware)

# 2. CORS configuration for web / cross-origin clients
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 3. Register domain routers
app.include_router(health.router)
app.include_router(tasks.router)
app.include_router(realitycheck.router)
app.include_router(rescuemesh.router)

# 4. Global safe error handling
@app.exception_handler(Exception)
async def global_exception_handler(request: Request, exc: Exception):
    correlation_id = getattr(request.state, "correlation_id", "unknown")
    # Never leak internal database stack traces or file system paths
    return JSONResponse(
        status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
        content={
            "error": "InternalServerError",
            "message": "An unexpected error occurred while processing the request.",
            "correlation_id": correlation_id,
            "timestamp": int(time.time())
        }
    )
