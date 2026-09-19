# LIFEOS Architecture Documentation

## 1. Overview & Core Concept
**LIFEOS** is an adaptive personal intelligence platform designed to:
1. **Observe** user context and situational signals.
2. **Understand** immediate conditions, priorities, and constraints.
3. **Decide** by generating explainable recommendations through deterministic rules and heuristics (augmented by AI assistance in future milestones).
4. **Learn & Adapt** by tracking user responses, measuring objective outcomes, and deriving an empirical user behavior model to refine future recommendations.

---

## 2. Intelligence Domains

LIFEOS organizes intelligence capabilities into three distinct domains:

### 1. Personal Intelligence (Active in Milestone 2)
- **Focus**: Persistent tasks, user behavioral events, adaptive prioritization, and cognitive workload management.
- **Goal**: Adaptive productivity without burnout, prioritizing tasks according to real-time availability, urgency, effort, and historical execution habits.

### 2. Trust Intelligence (RealityCheck — Active in Milestone 3)
- **Focus**: Claim analysis, factual grounding, and evidence aggregation.
- **Workflow**: Text/URL ingestion → claim extraction → evidence retrieval → source credibility weighting → confidence-calibrated summary.

### 3. Resilience Intelligence (RescueMesh — Active in Milestone 4)
- **Focus**: Offline survivability, opportunistic relay, cryptographic integrity, and network synchronization.
- **Workflow**: Local-first message queues, peer-to-peer ad-hoc relay, Time-to-Live (TTL) tracking, SHA-256 duplicate suppression, and opportunistic synchronization when connectivity resumes.

---

## 3. Layered Android Architecture

LIFEOS is structured using clean architecture, MVVM, Jetpack Compose, Kotlin Coroutines, and Room local persistence:

```
com.mrashish18.lifeos/
├── core/                  # Core abstractions and intelligence logic
│   ├── model/             # Domain entities (Task, Goal, ContextSnapshot, Recommendation,
│   │                      # UserBehaviorModel, BehaviorEvent, RecommendationFactor)
│   ├── context/           # Context Engine interfaces & local providers (time, network)
│   ├── decision/          # Decision Engine, deterministic rule engine, Learning Loop
│   └── common/            # Dispatchers, Resource wrappers, shared utilities
│
├── data/                  # Data access and local persistence
│   ├── local/             # Room SQLite persistence (LifeOsDatabase, entities, DAOs)
│   │   ├── entity/        # TaskEntity, BehaviorEventEntity
│   │   └── dao/           # TaskDao, BehaviorEventDao
│   ├── remote/            # Retrofit-ready network contracts (LifeOsRemoteDataSource)
│   └── repository/        # Repository implementations (RoomTaskRepository,
│                          # RoomBehaviorEventRepository, InMemoryTaskRepository)
│
├── domain/                # Enterprise domain contracts and business logic
│   ├── repository/        # Clean repository interfaces (TaskRepository, GoalRepository,
│   │                      # BehaviorEventRepository, UserBehaviorRepository)
│   └── usecase/           # Domain use cases (CreateTaskUseCase, UpdateTaskUseCase,
│                          # TransitionTaskStatusUseCase, DeleteTaskUseCase,
│                          # GetDashboardDataUseCase)
│
├── feature/               # Feature presentation & ViewModels
│   ├── dashboard/         # Real metrics Dashboard screen & DashboardViewModel
│   ├── tasks/             # Interactive Task management UI & TasksViewModel
│   ├── goals/             # Personal Intelligence: Goals placeholder
│   ├── intelligence/      # Cognitive engine analytics placeholder
│   ├── realitycheck/      # Trust Intelligence placeholder
│   └── resilience/        # Resilience Intelligence (RescueMesh) placeholder
│
└── ui/                    # Presentation foundation
    ├── components/        # Reusable Compose widgets
    ├── navigation/        # Top-level scaffold, navigation bar, and destination routing
    └── theme/             # Material3 typography, colors, and styling
```

---

## 4. Local Persistence Architecture (Room)

To ensure tasks and behavioral records survive process restarts, app terminations, and device reboots, LIFEOS utilizes **Android Room** via KSP Kotlin codegen (`room.generateKotlin = "true"`):

### Database Schema
- **Database**: `LifeOsDatabase` (SQLite database `lifeos_database.db`, version 1).
- **`tasks` Table**:
  - `id` (TEXT, Primary Key)
  - `title` (TEXT)
  - `description` (TEXT)
  - `priority` (TEXT: `LOW`, `MEDIUM`, `HIGH`, `URGENT`)
  - `status` (TEXT: `PENDING`, `IN_PROGRESS`, `COMPLETED`, `POSTPONED`, `ABANDONED`)
  - `dueAtEpochMillis` (INTEGER, nullable)
  - `estimatedMinutes` (INTEGER, nullable)
  - `category` (TEXT: `WORK`, `PERSONAL`, `HEALTH`, `LEARNING`, `GENERAL`)
  - `createdAtEpochMillis` (INTEGER)
  - `updatedAtEpochMillis` (INTEGER)
- **`behavior_events` Table**:
  - `id` (TEXT, Primary Key)
  - `type` (TEXT: event type name)
  - `timestampEpochMillis` (INTEGER)
  - `metadataJson` (TEXT: JSON key-value pairs)

### Domain vs. Entity Separation
Domain models (`Task`, `BehaviorEvent`) are decoupled from database entities (`TaskEntity`, `BehaviorEventEntity`). Repositories translate between persistence entities and domain models, ensuring Room annotations do not leak into domain use cases or the UI.

---

## 5. Task & Behavior Event Data Flow

### Task Lifecycle Flow
```
User Action (UI) ──► TasksViewModel ──► TaskUseCases (Domain)
                                              │
                         ┌────────────────────┴────────────────────┐
                         ▼                                         ▼
                 TaskRepository                             BehaviorEventRepository
                 (Room SQLite: tasks)                       (Room SQLite: behavior_events)
```

1. **Create Task**: `CreateTaskUseCase` validates input, persists `Task` with `PENDING` status, and logs a `TASK_CREATED` event with metadata (`taskId`, `title`, `priority`, `category`, `estimatedMinutes`).
2. **Start Task**: `TransitionTaskStatusUseCase` marks status `IN_PROGRESS` and logs `TASK_STARTED`.
3. **Complete Task**: `TransitionTaskStatusUseCase` marks status `COMPLETED`, calculates duration elapsed, and logs `TASK_COMPLETED` with duration metadata.
4. **Postpone Task**: `TransitionTaskStatusUseCase` marks status `POSTPONED` and logs `TASK_POSTPONED`.
5. **Abandon Task**: `TransitionTaskStatusUseCase` marks status `ABANDONED` and logs `TASK_ABANDONED`.
6. **Delete Task**: `DeleteTaskUseCase` removes task from database.

---

## 6. Deterministic User Behavior Model

The `UserBehaviorModel` represents measurable patterns derived strictly from observed `BehaviorEvent` history:
- `totalTasksCreated`: Total tasks created.
- `totalTasksCompleted`: Total tasks completed.
- `totalTasksPostponed`: Total tasks postponed.
- `totalTasksAbandoned`: Total tasks abandoned.
- `completionRate`: $\frac{\text{Completed}}{\text{Completed} + \text{Abandoned}}$
- `abandonmentRate`: $\frac{\text{Abandoned}}{\text{Completed} + \text{Abandoned}}$
- `postponementRate`: $\frac{\text{Postponed}}{\text{Created}}$
- `averageCompletedDurationMinutes`: Average duration of completed tasks.
- `preferredCategories`: Distribution of completed tasks across categories.
- `completionsByTimeOfDay`: Completions grouped by time bucket (`MORNING`, `AFTERNOON`, `EVENING`, `NIGHT`).

### Honest Representation of Insufficient Data
If the user has fewer than 3 terminal task events (completed + abandoned), `hasSufficientData` is `false`, and rates (`completionRate`, `postponementRate`, `abandonmentRate`) are explicitly `null` rather than fabricated default numbers (like 0.0). The UI communicates that observations are being gathered.

---

## 7. Deterministic Adaptive Recommendation Engine & Scoring Heuristic

### Architectural Boundary: Deterministic Heuristic vs. Future AI
**Authoritative decisions are controlled by transparent, deterministic rules.**
No LLM or stochastic black-box API makes direct decisions about user tasks or emergency operations. In future milestones, AI will only assist with unstructured text interpretation, task decomposition suggestions, and natural language explanations.

### Scoring Heuristic for Pending Tasks
Pending and postponed tasks are evaluated against a transparent scoring formula:

$$\text{Score} = \text{PriorityWeight} + \text{UrgencyWeight} + \text{EffortWeight} + \text{BehavioralBonus}$$

1. **Priority Weight**:
   - `URGENT`: +40 pts
   - `HIGH`: +30 pts
   - `MEDIUM`: +20 pts
   - `LOW`: +10 pts
2. **Deadline Urgency**:
   - Overdue: +40 pts
   - Due within 24 hours: +30 pts
   - Due within 3 days: +15 pts
3. **Effort / Quick Win Fit**:
   - Estimated duration $\le 30$ minutes: +15 pts
4. **Behavioral Adaptation (when `hasSufficientData == true`)**:
   - Matches user's top completed category: +15 pts
   - Matches a familiar completed category: +5 pts
   - High historical completion rate ($\ge 70\%$): +5 pts

Confidence is calculated as:
$$\text{Confidence} = \text{clamp}\left(\frac{\text{Score}}{100.0},\, 0.60,\, 0.95\right)$$

### Structured Explainability ("Why am I seeing this?")
Every recommendation contains structured `RecommendationFactor` items:
- Factor Name (e.g. `Priority`, `Due Soon`, `Quick Win`, `Category Habit`)
- Factor Description (e.g. `Urgent priority task`, `Estimated effort <= 30 minutes`, `Demonstrated momentum in WORK`)
- Score Contribution

The UI exposes an interactive **"Why am I seeing this?"** expansion so the user can inspect the exact reasons behind every recommendation.

---

## 8. The Learning Loop in Action

```
Recommendation Shown (Dashboard)
          │
          ├──► User Accepts ──► Logs RECOMMENDATION_ACCEPTED ──► Sets Task IN_PROGRESS
          │                                                            │
          │                                                            ▼
          │                                                     User Completes Task
          │                                                            │
          │                                                            ▼
          │                                                     Logs TASK_COMPLETED
          │                                                            │
          │                                                            ▼
          │                                                     User Behavior Model
          │                                                     Dynamically Updated
          │
          └──► User Dismisses ──► Logs RECOMMENDATION_REJECTED
```

---

## 9. Trust Intelligence Architecture (RealityCheck — Milestone 3)

### Core Principle: Separation of Evidence from Interpretation
RealityCheck operates on an essential epistemic principle:
$$\textbf{Source Evidence} \neq \textbf{System Analysis}$$
The system strictly distinguishes between what independent publishers and researchers stated versus how the system interpreted and weighted those statements. Generated conclusions are never presented as infallible, independently verified metaphysical facts.

### Investigation Pipeline Flow
```
User Input (Claim / URL)
          │
          ▼
   ClaimClassifier          ──► Categorizes claim (FACTUAL, NUMERICAL, TEMPORAL, CAUSAL, OPINION)
          │
          ▼
  EvidenceRepository        ──► Offline-first, verified reference corpus search (token overlap)
          │
          ▼
   SourceClassifier         ──► Deterministic credibility tiering (PRIMARY, OFFICIAL, NEWS, REFERENCE)
          │
          ▼
  EvidenceComparator        ──► Stance detection, weighted consensus, calibrated confidence
          │
          ▼
  RealityCheckResult        ──► Investigation report (Verdict, Confidence, Reasoning, Evidence cards)
          │
          ▼
  LearningLoop Logging      ──► Records CLAIM_SUBMITTED and CLAIM_VERIFIED behavior events
```

### 1. EvidenceRepository Abstraction
To keep the domain layer decoupled from networking or specific fact-checking APIs, the domain repository contract defines:
```kotlin
interface EvidenceRepository {
    suspend fun search(query: String): Result<List<Evidence>>
}
```
The active implementation, `DeterministicEvidenceRepository`, operates against a verified, offline-first corpus spanning medicine, physics, technology, astronomy, and common misconceptions. It performs multi-token keyword intersection and relevance scoring without fabricated web scraping.

### 2. Complete 10-Stage Pipeline
1. **Input**: Raw text or statement entered by user.
2. **Normalization**: Standardizes inquiry formats (e.g., stripping trailing punctuation, converting interrogative prefixes like "Do/Does/Is" into canonical propositions) without fabricating or altering semantic intent.
3. **Classification**: Identifies semantic `ClaimType` (`FACTUAL`, `CAUSAL`, `TEMPORAL`, `NUMERICAL`, `OPINION`, `UNSUPPORTED`) and `DomainCategory` (`MEDICINE`, `ASTRONOMY`, `TECHNOLOGY`, `NEUROSCIENCE`, `PHYSICS`, `GENERAL`).
4. **Evidence Retrieval**: Deterministic retrieval from verified institutional corpus with weighted title and snippet token matching.
5. **Source Quality Analysis**: Institutional credibility assessment (`PRIMARY`: 1.0, `OFFICIAL`: 0.9, `REPUTABLE_NEWS`: 0.75, `REFERENCE`: 0.70, `UNKNOWN`: 0.40) with explainable institutional rationale.
6. **Evidence Comparison**: Multi-factor evaluation measuring individual evidence stance (`SUPPORTS`, `CONTRADICTS`, `MENTIONS`), relevance, and weight contribution.
7. **Confidence Calculation**: Pure multi-factor mathematical derivation ($0.35 \times \text{quality} + 0.30 \times \text{relevance} + 0.20 \times \text{consensus} + 0.15 \times \text{coverage} - \text{conflict penalty}$) without artificial minimum floors.
8. **Verdict**: Objective evidentiary balance (`SUPPORTED`, `CONTRADICTED`, `MIXED`, `INSUFFICIENT_EVIDENCE`).
9. **Explanation & Separation**: Deterministic human-readable explanation explicitly separating **Authoritative Evidence** (external ground truth) from **LIFEOS Interpretation** (system assessment).
10. **Auditable Persistence**: Immediate storage in Room database (`investigation_records` table, non-destructive `MIGRATION_2_3`) with reactive UI updates.

### 3. Room Database Migration & Persistence
The investigation history is persisted in Room:
- Table: `investigation_records`
- Fields: `id`, `originalClaim`, `normalizedClaim`, `claimType`, `domainCategory`, `verdict`, `confidenceScore`, `confidencePercentage`, `reasoning`, `evidenceCount`, `topSourceNamesJson`, `timestampEpochMillis`
- Non-destructive migration `MIGRATION_2_3` from database version 2 to 3.

---

## 10. Resilience Intelligence Architecture (RescueMesh)

Milestone 4 implements **RescueMesh**: an offline-first store-and-forward emergency messaging system built to survive network partitions, localized infrastructure failures, and emergency situations.

```
+-------------------------------------------------------------+
|               RescueMesh Command Center (UI)                |
+-------------------------------------------------------------+
                              |
                              v
+-------------------------------------------------------------+
|              ResilienceViewModel & Use Cases                |
|  - CreateEmergencyMessageUseCase   - RelayEmergencyMessage  |
|  - GetEmergencyQueueUseCase        - SyncEmergencyQueue     |
+-------------------------------------------------------------+
          |                                      |
          v                                      v
+------------------------+             +----------------------+
|    RescueMeshEngine    |             |  BehaviorEventRepo   |
|  - SHA-256 Fingerprint |             |  (Privacy Protected: |
|  - State Machine       |             |   No Payload Logged) |
|  - TTL Expiration      |             +----------------------+
|  - Hop Limits (<= 5)   |
|  - Deduplication       |
+------------------------+
          |
          v
+-------------------------------------------------------------+
|               EmergencyMessageRepository                    |
|             (Room SQLite: emergency_messages)               |
+-------------------------------------------------------------+
          |                                      |
          v                                      v
+--------------------------+           +----------------------+
| LocalStoreAndForward     |           | NetworkGateway       |
| Transport (Offline Store)|           | Transport (Egress)   |
+--------------------------+           +----------------------+
```

### 1. Cryptographic Payload Fingerprinting
Every emergency message is assigned a deterministic SHA-256 fingerprint upon creation using standard `java.security.MessageDigest`:
```kotlin
fun calculateFingerprint(
    senderId: String,
    payload: String,
    createdAt: Instant,
    ttl: Duration,
    hops: Int
): String
```
- Deduplication is guaranteed by both `messageId` and the canonical SHA-256 fingerprint.
- Any attempt to create or ingest an identical message twice is detected and rejected without creating duplicate database rows.

### 2. Explicit Finite State Machine
Messages transition strictly through deterministic states:
- `DRAFT`: Initial construction state before queuing.
- `QUEUED`: Stored locally in SQLite offline queue awaiting opportunistic transport.
- `RELAYING`: Actively traversing intermediate mesh nodes.
- `SENT`: Dispatched through a network gateway or external egress node.
- `DELIVERED`: Confirmed delivered by explicit recipient acknowledgment.
- `FAILED`: Transmission error or unrecoverable failure (e.g., hop limit exceeded).
- `EXPIRED`: Message passed its Time-to-Live (TTL) limit.
- `DUPLICATE`: Redundant packet detected and discarded.

> [!IMPORTANT]
> **Strict Delivery Integrity**: In accordance with distributed systems guarantees, `NetworkGatewayTransport` transitions packets to `SENT` upon successful outbound transmission. The system **never** marks a message `DELIVERED` without explicit downstream recipient ACK.

### 3. Priority Ordering & Queue Prioritization
Messages in the emergency queue are prioritized by triage level:
$$\text{Priority Ordering}: \quad \text{CRITICAL} > \text{HIGH} > \text{NORMAL}$$
- SQLite queries explicitly order queued messages by priority (`CASE priority WHEN 'CRITICAL' THEN 1 WHEN 'HIGH' THEN 2 WHEN 'NORMAL' THEN 3 ELSE 4 END ASC, createdAtEpochMillis ASC`).
- During gateway synchronization, critical packets are drained and transmitted before high or normal priority items.

### 4. Hop Limits, TTL Expiration & Clock Injection
- **Hop Bound**: Every packet enforces a strict maximum hop limit (`maxHops = 5`). Packets reaching 5 hops without reaching their destination are automatically transitioned to `FAILED`.
- **Time-to-Live (TTL)**:
  - `CRITICAL` priority: 48-hour TTL ($172,800$ seconds).
  - `HIGH` priority: 24-hour TTL ($86,400$ seconds).
  - `NORMAL` priority: 12-hour TTL ($43,200$ seconds).
- Any message evaluated after `expiresAt` is transitioned to `EXPIRED` during queue sweeps and gateway synchronization.
- **Clock Injection**: `RescueMeshEngine` and all use cases accept an injectable `java.time.Clock` (`default = Clock.systemUTC()`), providing 100% deterministic time manipulation during unit testing and simulated network time shifts.

### 5. Local Store-and-Forward Prototype Disclosure
> [!NOTE]
> **Prototype Disclosure**: RescueMesh is currently an Android proof-of-concept / local store-and-forward prototype. It implements offline Room persistence, cryptographic SHA-256 identities, hop degradation tracking, and opportunistic sync upon network return. It does **not** claim to operate an active nationwide radio mesh network.

### 6. Transports & Physical Hardware Architecture
1. `LocalStoreAndForwardTransport`: Local node buffer that guarantees offline durability in Room SQLite (`emergency_messages` table).
2. `NetworkGatewayTransport`: Outbound network egress that automatically activates when Wi-Fi or Cellular connectivity is restored.
3. **Physical Hardware Extension**: BLE (Bluetooth Low Energy) and Wi-Fi Direct protocols are architected behind the clean `MeshTransport` interface, ready to bind to native Android hardware radios without modifying message contracts or database schemas.

### 7. Privacy & Behavioral Analytics Boundary
Emergency messages trigger behavior events to inform cognitive load and adaptive UI systems (e.g. `EMERGENCY_MESSAGE_CREATED`, `EMERGENCY_MESSAGE_SENT`), but **never log sensitive situation payload text** into metadata, preserving absolute user privacy during crises.

---

## 11. Dynamic Theme & Adaptive UI Engine

The `DynamicThemeEngine` bridges real-time contextual signals to high-contrast, tactile UI styles across all screens:
- **Calm State**: Soft indigos, cool slates, high typography contrast.
- **Urgent / Focus State**: Warm ambers, accent rings, distraction-free surfaces.
- **Emergency Crisis State**: High-visibility reds, alert banners, immediate action buttons.
- All colors resolve through `LifeOsSemanticColors` for centralized design system compliance.

---

## 12. Current Milestone vs. Future Milestones

| Capability | Milestone 1 (Foundation) | Milestone 2 (Personal Intelligence) | Milestone 3 (Trust Intelligence) | Milestone 4 (Resilience Intelligence — Current) | Future Milestones |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Task Storage** | In-Memory | Persistent Room SQLite (`tasks` table) | Persistent Room SQLite (`tasks` table) | Persistent Room SQLite (`tasks` table) | Cloud sync / conflict resolution |
| **Emergency Storage** | None | None | None | **Persistent Room SQLite (`emergency_messages`)** | Multi-device encrypted backup |
| **Behavior Tracking** | Event model | Persistent logging (`behavior_events`) | Persistent logging + `CLAIM_*` events | Persistent logging + `EMERGENCY_*` events | Sensor events & ambient signals |
| **User Model** | Placeholder | Deterministic `UserBehaviorModel` | Deterministic `UserBehaviorModel` | Deterministic `UserBehaviorModel` | Multi-agent behavioral modeling |
| **Decisions** | Static rules | Adaptive scoring + explainability | Adaptive scoring + explainability | Adaptive scoring + explainability | Hybrid rules + LLM |
| **RealityCheck** | Domain events | Domain events | **Complete Investigation Engine & UI** | Complete Investigation Engine & UI | Live web indexing & claim retrieval |
| **RescueMesh** | Domain events | Storage foundation | Storage foundation | **Full Store & Forward Engine, SHA-256, UI** | Physical BLE / Wi-Fi Direct hardware binding |
| **Dynamic UI** | Static Material3 | Static Material3 | Refined high-contrast UI | **Dynamic Theme Engine & Semantic Colors** | Fluid ambient micro-interactions |

