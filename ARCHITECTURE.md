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

### 2. Trust Intelligence (RealityCheck — Future Milestone)
- **Focus**: Claim analysis, factual grounding, and evidence aggregation.
- **Workflow**: Text/URL ingestion → claim extraction → evidence retrieval → source credibility weighting → confidence-calibrated summary.

### 3. Resilience Intelligence (RescueMesh — Future Milestone)
- **Focus**: Offline continuity and emergency peer communications.
- **Workflow**: Local-first message queues, peer-to-peer ad-hoc relay, Time-to-Live (TTL) tracking, duplicate suppression, and opportunistic synchronization when connectivity resumes.

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

### 2. Deterministic Claim Classification
Claims are classified into discrete semantic categories by `ClaimClassifier`:
- `OPINION`: Subjective aesthetic, value, or preference judgments ("best", "worst", "ugly", "overrated").
- `CAUSAL`: Assertions of cause-and-effect ("causes", "leads to", "triggers", "results in").
- `TEMPORAL`: Statements with explicit calendar years, centuries, or historical chronologies.
- `NUMERICAL`: Assertions featuring quantities, percentages, or measurement metrics.
- `FACTUAL`: Declarative propositions of objective existence or properties.
- `UNSUPPORTED`: Input that is too brief (< 4 characters) or lacks alphabetical proposition content.

### 3. Source Quality Classification & Weights
Source categories represent institutional editorial review standards and historical reliability:
- `PRIMARY` (Weight: $1.0$): Peer-reviewed academic journals, scientific societies, creator repositories (e.g., *The Lancet*, *Nature*, *Society for Neuroscience*, *JetBrains*).
- `OFFICIAL` (Weight: $0.9$): Government public health agencies, international bodies (e.g., *CDC*, *WHO*, *NASA*, *NIST*).
- `REPUTABLE_NEWS` (Weight: $0.75$): Journalistic organizations with public correction protocols (e.g., *Scientific American*, *Reuters*, *BBC*).
- `REFERENCE` (Weight: $0.70$): General encyclopedias and clinical reference portals (e.g., *Encyclopaedia Britannica*, *Mayo Clinic*).
- `UNKNOWN` (Weight: $0.40$): Unverified, personal, or arbitrary web endpoints.

### 4. Deterministic Comparison & Verdict Rules
Each evidence item is analyzed against the claim to determine its stance:
- `SUPPORTS`: Corroborates the proposition or affirms a negated claim.
- `CONTRADICTS`: Contains explicit refuting language ("do not work", "ineffective", "myth", "debunked", "no link").
- `MENTIONS`: Contextual discussion without decisive validation or refutation.

Weight contributions are calculated as:
$$\text{WeightContribution} = \text{SourceQuality.weight} \times \text{RelevanceScore}$$

Verdicts describe the objective evidentiary balance:
- `INSUFFICIENT_EVIDENCE`: Total evidentiary signal $< 0.35$ or empty evidence list.
- `SUPPORTED`: Corroborating signals dominate ($\text{supportWeight} \ge 1.3 \times \text{contradictWeight}$).
- `CONTRADICTED`: Refuting signals dominate ($\text{contradictWeight} \ge 1.3 \times \text{supportWeight}$).
- `MIXED`: Substantial evidence exists on both sides ($\text{conflictRatio} > 0.25$).

### 5. Calibrated Confidence Calculation
Empirical confidence is calculated transparently and never claims 100% certainty:
- `INSUFFICIENT_EVIDENCE`: Clamped between $0.20$ and $0.40$.
- `MIXED`: Derived from average source credibility and conflict degree; clamped between $0.45$ and $0.72$.
- `SUPPORTED` / `CONTRADICTED`: Base score $0.60$, plus source credibility bonus, plus multi-source corroboration bonus, minus conflict penalty; clamped strictly between $0.65$ and $0.92$.

### 6. Architectural Boundary: Deterministic Core vs. Future AI
In Milestone 3, all classification, evidence scoring, and verdict synthesis are 100% deterministic and unit-tested. Future LLM/AI integration will be introduced as an optional assistive layer behind `ClaimClassifier` and `EvidenceComparator` for natural language summarization, but will never override deterministic evidentiary safety constraints.

---

## 10. Current Milestone vs. Future Milestones

| Capability | Milestone 1 (Foundation) | Milestone 2 (Personal Intelligence) | Milestone 3 (Trust Intelligence — Current) | Future Milestones |
| :--- | :--- | :--- | :--- | :--- |
| **Task Storage** | In-Memory | Persistent Room SQLite (`tasks` table) | Persistent Room SQLite (`tasks` table) | Cloud sync / conflict resolution |
| **Behavior Tracking** | Event model | Persistent logging (`behavior_events`) | Persistent logging + `CLAIM_*` events | Sensor events & app signals |
| **User Model** | Placeholder | Deterministic `UserBehaviorModel` | Deterministic `UserBehaviorModel` | Bayesian / ML modeling |
| **Decisions** | Static rules | Adaptive scoring + explainability | Adaptive scoring + explainability | Hybrid rules + LLM |
| **Task UI** | Placeholder | Interactive Compose CRUD | Interactive Compose CRUD | Subtasks, recurrence |
| **Dashboard** | Static mock | Live counts & recommendations | Live counts & recommendations | Unified intelligence feed |
| **RealityCheck** | Domain events | Domain events | **Complete Investigation Engine & UI** | Live API indexing / hybrid LLM synthesis |
| **RescueMesh** | Domain events | Storage foundation | Storage foundation | Peer-to-peer BLE / Wi-Fi Direct mesh |

