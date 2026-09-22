from sqlalchemy import (
    Column,
    String,
    Integer,
    BigInteger,
    Float,
    Text,
    Index,
    UniqueConstraint
)
from backend.app.core.database import Base

class TaskModel(Base):
    __tablename__ = "tasks"

    id = Column(String(64), primary_key=True, index=True)
    user_id = Column(String(64), nullable=False, default="default_user", index=True)
    title = Column(String(255), nullable=False)
    description = Column(Text, nullable=False, default="")
    priority = Column(String(32), nullable=False, default="MEDIUM", index=True)
    status = Column(String(32), nullable=False, default="PENDING", index=True)
    category = Column(String(32), nullable=False, default="GENERAL", index=True)
    estimated_minutes = Column(Integer, nullable=True)
    due_at_epoch_millis = Column(BigInteger, nullable=True)
    created_at_epoch_millis = Column(BigInteger, nullable=False, index=True)
    updated_at_epoch_millis = Column(BigInteger, nullable=False)

    __table_args__ = (
        Index("idx_tasks_status_created", "status", "created_at_epoch_millis"),
        Index("idx_tasks_user_status", "user_id", "status"),
    )

class EmergencyMessageModel(Base):
    __tablename__ = "emergency_messages"

    message_id = Column(String(64), primary_key=True, index=True)
    sender_id = Column(String(64), nullable=False, index=True)
    recipient_id = Column(String(64), nullable=True)
    type = Column(String(32), nullable=False, default="EMERGENCY")
    priority = Column(String(32), nullable=False, default="NORMAL", index=True)
    status = Column(String(32), nullable=False, default="QUEUED", index=True)
    payload = Column(Text, nullable=False)
    created_at_epoch_millis = Column(BigInteger, nullable=False, index=True)
    expires_at_epoch_millis = Column(BigInteger, nullable=False, index=True)
    hop_count = Column(Integer, nullable=False, default=0)
    max_hops = Column(Integer, nullable=False, default=5)
    transport_type = Column(String(32), nullable=False, default="NETWORK")
    fingerprint_sha256 = Column(String(64), nullable=False, unique=True, index=True)
    relay_history_json = Column(Text, nullable=False, default="[]")

    __table_args__ = (
        Index("idx_mesh_status_priority_created", "status", "priority", "created_at_epoch_millis"),
    )

class InvestigationModel(Base):
    __tablename__ = "investigations"

    id = Column(String(64), primary_key=True, index=True)
    claim_hash = Column(String(64), nullable=False, index=True)
    original_claim = Column(String(500), nullable=False)
    verdict = Column(String(32), nullable=False, index=True)
    confidence_score = Column(Float, nullable=False)
    confidence_percentage = Column(Integer, nullable=False)
    reasoning = Column(Text, nullable=False)
    evidence_count = Column(Integer, nullable=False, default=0)
    top_sources_json = Column(Text, nullable=False, default="[]")
    created_at_epoch_millis = Column(BigInteger, nullable=False, index=True)

class IdempotencyRecordModel(Base):
    __tablename__ = "idempotency_records"

    idempotency_key = Column(String(128), primary_key=True, index=True)
    response_code = Column(Integer, nullable=False)
    response_body = Column(Text, nullable=False)
    created_at_epoch_millis = Column(BigInteger, nullable=False, index=True)
