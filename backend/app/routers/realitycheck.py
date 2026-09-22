import time
import hashlib
from typing import Dict
from fastapi import APIRouter, Depends, Request, HTTPException, status
from sqlalchemy.ext.asyncio import AsyncSession
from backend.app.core.database import get_db
from backend.app.core.config import settings
from backend.app.core.rate_limiter import check_rate_limit
from backend.app.core.bounded_queue import ai_worker_pool
from backend.app.core.circuit_breaker import CircuitBreaker, CircuitBreakerOpenException
from backend.app.schemas.schemas import RealityCheckRequest, RealityCheckResponse
from backend.app.models.models import InvestigationModel

router = APIRouter(prefix=f"{settings.API_V1_STR}/realitycheck", tags=["Trust Intelligence"])

external_evidence_circuit_breaker = CircuitBreaker(
    name="external_fact_sources",
    failure_threshold=settings.AI_CIRCUIT_BREAKER_FAILURES,
    recovery_timeout_seconds=settings.AI_CIRCUIT_BREAKER_COOLDOWN_SECONDS
)

# In-memory TTL cache for frequent claim inquiries: claim_hash -> (response_dict, expires_at)
_investigation_cache: Dict[str, tuple] = {}

CURATED_CORPUS = [
    {
        "keywords": ["orbit", "sun", "earth", "365", "days", "year"],
        "verdict": "SUPPORTED",
        "confidence": 0.98,
        "reasoning": "Astrophysical mechanics confirm Earth completes one sidereal orbit around the Sun in approximately 365.2422 mean solar days.",
        "sources": ["NASA Goddard Space Flight Center", "International Astronomical Union"]
    },
    {
        "keywords": ["antibiotic", "virus", "viral", "cold", "flu", "cure", "infection"],
        "verdict": "CONTRADICTED",
        "confidence": 0.99,
        "reasoning": "Clinical pharmacology establishes antibiotics target bacterial cellular structures (cell walls, ribosomes) and have zero efficacy against viral pathogens.",
        "sources": ["World Health Organization", "Centers for Disease Control and Prevention"]
    },
    {
        "keywords": ["moon", "apollo", "1969", "landed", "armstrong", "humans"],
        "verdict": "SUPPORTED",
        "confidence": 0.99,
        "reasoning": "Documented historical telemetry, lunar laser ranging retroreflectors, and physical basalt samples verify Apollo 11 landed on the Moon on July 20, 1969.",
        "sources": ["NASA Historical Archives", "Smithsonian National Air and Space Museum"]
    },
    {
        "keywords": ["brain", "10", "percent", "humans", "use"],
        "verdict": "CONTRADICTED",
        "confidence": 0.95,
        "reasoning": "Functional neuroimaging (fMRI, PET) demonstrates virtually 100% of brain regions exhibit metabolic activation throughout 24-hour cycles.",
        "sources": ["Society for Neuroscience", "Brain Facts / Kavli Foundation"]
    },
    {
        "keywords": ["coffee", "cardiovascular", "heart", "health", "moderate"],
        "verdict": "MIXED",
        "confidence": 0.78,
        "reasoning": "Prospective epidemiological studies associate 2-3 cups daily with reduced all-cause mortality, though individual CYP1A2 metabolic rate varies.",
        "sources": ["American Heart Association", "European Society of Cardiology"]
    }
]

async def _perform_corroboration(claim_text: str) -> dict:
    """
    Simulated bounded evidence corroboration executed inside bounded worker pool
    and guarded by a circuit breaker.
    """
    async def _query_sources():
        tokens = [t.lower() for t in claim_text.split() if len(t) > 2]
        best_match = None
        highest_overlap = 0

        for entry in CURATED_CORPUS:
            overlap = sum(1 for kw in entry["keywords"] if any(kw in token for token in tokens))
            if overlap > highest_overlap:
                highest_overlap = overlap
                best_match = entry

        if best_match and highest_overlap >= 2:
            return {
                "verdict": best_match["verdict"],
                "confidenceScore": best_match["confidence"],
                "confidencePercentage": int(best_match["confidence"] * 100),
                "reasoning": best_match["reasoning"],
                "evidenceCount": len(best_match["sources"]),
                "topSources": best_match["sources"]
            }
        else:
            return {
                "verdict": "INSUFFICIENT_EVIDENCE",
                "confidenceScore": 0.40,
                "confidencePercentage": 40,
                "reasoning": "No authoritative evidentiary consensus found matching the inquiry parameters.",
                "evidenceCount": 0,
                "topSources": ["General Reference Index"]
            }

    return await external_evidence_circuit_breaker.call(_query_sources)

@router.post("/investigate", response_model=RealityCheckResponse)
async def investigate_claim(
    request: Request,
    inquiry: RealityCheckRequest,
    db: AsyncSession = Depends(get_db)
):
    """
    Investigates an empirical claim with:
    - Strict rate limiting per client
    - Bounded concurrency worker pool preventing server exhaustion
    - TTL in-memory caching for hot queries
    - Circuit breaker protecting against upstream network degradation
    """
    await check_rate_limit(request, settings.RATE_LIMIT_AI, "realitycheck:investigate")

    claim_hash = hashlib.sha256(inquiry.text.lower().strip().encode()).hexdigest()
    now = time.time()

    # 1. Check TTL cache
    if claim_hash in _investigation_cache:
        cached_data, expires_at = _investigation_cache[claim_hash]
        if now < expires_at:
            cached_copy = dict(cached_data)
            cached_copy["cached"] = True
            return RealityCheckResponse(**cached_copy)

    # 2. Execute within bounded worker pool
    try:
        result_data = await ai_worker_pool.execute(
            _perform_corroboration,
            inquiry.text,
            timeout=settings.AI_PROCESSING_TIMEOUT_SECONDS
        )
    except CircuitBreakerOpenException:
        raise HTTPException(
            status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
            detail="External evidence verification is temporarily degraded. Upstream circuit breaker OPEN.",
            headers={"Retry-After": "15"}
        )

    response_id = f"rc_{claim_hash[:12]}"
    full_response = {
        "id": response_id,
        "claim": inquiry.text,
        **result_data,
        "cached": False
    }

    # 3. Store in TTL cache (300s)
    _investigation_cache[claim_hash] = (full_response, now + 300)

    # 4. Asynchronously persist investigation record to database
    try:
        investigation_record = InvestigationModel(
            id=response_id,
            claim_hash=claim_hash,
            original_claim=inquiry.text,
            verdict=result_data["verdict"],
            confidence_score=result_data["confidenceScore"],
            confidence_percentage=result_data["confidencePercentage"],
            reasoning=result_data["reasoning"],
            evidence_count=result_data["evidenceCount"],
            top_sources_json=str(result_data["topSources"]),
            created_at_epoch_millis=int(now * 1000)
        )
        db.add(investigation_record)
        await db.commit()
    except Exception:
        await db.rollback()

    return RealityCheckResponse(**full_response)
