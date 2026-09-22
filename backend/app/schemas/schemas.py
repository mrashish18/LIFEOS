from pydantic import BaseModel, Field, field_validator
from typing import Optional, List
import re

SAFE_ID_REGEX = re.compile(r"^[a-zA-Z0-9_.-]{1,64}$")

class TaskCreate(BaseModel):
    id: str = Field(..., max_length=64)
    title: str = Field(..., min_length=1, max_length=255)
    description: str = Field(default="", max_length=2048)
    priority: str = Field(default="MEDIUM", pattern="^(LOW|MEDIUM|HIGH|URGENT)$")
    status: str = Field(default="PENDING", pattern="^(PENDING|IN_PROGRESS|COMPLETED|POSTPONED|ABANDONED)$")
    category: str = Field(default="GENERAL", pattern="^(WORK|PERSONAL|HEALTH|LEARNING|GENERAL)$")
    estimatedMinutes: Optional[int] = Field(default=None, ge=1, le=1440)
    dueAtEpochMillis: Optional[int] = None
    createdAtEpochMillis: Optional[int] = None
    updatedAtEpochMillis: Optional[int] = None

class TaskResponse(BaseModel):
    id: str
    title: str
    description: str
    priority: str
    status: str
    category: str
    estimatedMinutes: Optional[int] = None
    dueAtEpochMillis: Optional[int] = None
    createdAtEpochMillis: int
    updatedAtEpochMillis: int

class PaginatedTasksResponse(BaseModel):
    items: List[TaskResponse]
    total: int
    limit: int
    offset: int
    has_more: bool

class RealityCheckRequest(BaseModel):
    text: str = Field(..., min_length=1, max_length=500)
    sourceUrl: Optional[str] = Field(default=None, max_length=2048)

    @field_validator("text")
    @classmethod
    def sanitize_claim(cls, v: str) -> str:
        clean = re.sub(r"[\x00-\x08\x0B\x0C\x0E-\x1F\x7F]", "", v).strip()
        if not clean:
            raise ValueError("Claim cannot be empty")
        return clean

class RealityCheckResponse(BaseModel):
    id: str
    claim: str
    verdict: str
    confidenceScore: float
    confidencePercentage: int
    reasoning: str
    evidenceCount: int
    topSources: List[str]
    cached: bool = False

class EmergencyMessageCreate(BaseModel):
    messageId: str = Field(..., max_length=64)
    senderId: str = Field(..., max_length=64)
    recipientId: Optional[str] = Field(default=None, max_length=64)
    type: str = Field(default="EMERGENCY", pattern="^(EMERGENCY|CHECK_IN|HAZARD_ALERT|RESOURCE_REQUEST)$")
    priority: str = Field(default="NORMAL", pattern="^(CRITICAL|HIGH|NORMAL)$")
    status: str = Field(default="QUEUED", pattern="^(DRAFT|QUEUED|RELAYING|SENT|DELIVERED|FAILED|EXPIRED)$")
    payload: str = Field(..., min_length=1, max_length=1000)
    createdAtEpochMillis: int
    expiresAtEpochMillis: int
    hopCount: int = Field(default=0, ge=0, le=20)
    maxHops: int = Field(default=5, ge=1, le=20)
    transportType: str = Field(default="NETWORK")
    fingerprintSha256: str = Field(..., min_length=64, max_length=64)

class EmergencyMessageResponse(BaseModel):
    messageId: str
    senderId: str
    recipientId: Optional[str]
    type: str
    priority: str
    status: str
    payload: str
    createdAtEpochMillis: int
    expiresAtEpochMillis: int
    hopCount: int
    maxHops: int
    transportType: str
    fingerprintSha256: str

class MeshSyncResponse(BaseModel):
    acceptedCount: int
    duplicateCount: int
    expiredCount: int
    rejectedCount: int
    explanation: str
