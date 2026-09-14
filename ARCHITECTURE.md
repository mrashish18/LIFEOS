# LIFEOS Architecture Documentation

## 1. Overview & Core Concept
**LIFEOS** is an adaptive personal intelligence platform designed to:
1. **Observe** user context and situational signals.
2. **Understand** immediate conditions, priorities, and constraints.
3. **Decide** by generating explainable recommendations through deterministic rules (augmented by AI interpretation in later milestones).
4. **Learn & Adapt** by tracking user responses and measuring objective outcomes to tune future behavior.

---

## 2. Intelligence Domains

LIFEOS organizes intelligence capabilities into three distinct domains:

### 1. Personal Intelligence
- **Focus**: Tasks, goals, habit patterns, and cognitive workload management.
- **Goal**: Adaptive productivity without burnout, prioritizing tasks according to real-time availability and capacity.

### 2. Trust Intelligence (RealityCheck)
- **Focus**: Claim analysis, factual grounding, and evidence aggregation.
- **Workflow (Future Milestones)**: Text/URL ingestion → claim extraction → evidence retrieval → source credibility weighting → confidence-calibrated summary.

### 3. Resilience Intelligence (RescueMesh)
- **Focus**: Offline continuity and emergency peer communications.
- **Workflow (Future Milestones)**: Local-first message queues, peer-to-peer ad-hoc relay, Time-to-Live (TTL) tracking, duplicate suppression, and opportunistic synchronization when connectivity resumes.

---

## 3. Layered Android Architecture

LIFEOS is structured using clean architecture and modern Android standards (MVVM, Jetpack Compose, Kotlin Flow/Coroutines):

```
com.mrashish18.lifeos/
├── core/                  # Core abstractions and intelligence logic
│   ├── model/             # Domain entities (Task, Goal, ContextSnapshot, Recommendation, etc.)
│   ├── context/           # Context Engine interfaces & local providers (time, network)
│   ├── decision/          # Decision Engine, deterministic rule engine, Learning Loop
│   └── common/            # Dispatchers, Resource wrappers, shared utilities
│
├── data/                  # Data access and synchronization
│   ├── local/             # Room-ready data access contracts (e.g. TaskLocalDataSource)
│   ├── remote/            # Retrofit-ready network contracts (e.g. LifeOsRemoteDataSource)
│   └── repository/        # Repository implementations (e.g. InMemoryTaskRepository)
│
├── domain/                # Enterprise domain contracts and business logic
│   ├── repository/        # Clean repository interfaces (TaskRepository, GoalRepository)
│   └── usecase/           # Domain use cases (e.g. GetDashboardDataUseCase)
│
├── feature/               # Feature screens & ViewModels
│   ├── dashboard/         # Active Dashboard screen & DashboardViewModel
│   ├── tasks/             # Personal Intelligence: Tasks placeholder
│   ├── goals/             # Personal Intelligence: Goals placeholder
│   ├── intelligence/      # Cognitive engine analytics placeholder
│   ├── realitycheck/      # Trust Intelligence placeholder
│   └── resilience/        # Resilience Intelligence (RescueMesh) placeholder
│
└── ui/                    # Presentation foundation
    ├── components/        # Reusable Compose widgets
    ├── navigation/        # Top-level scaffold and navigation destinations
    └── theme/             # Material3 typography, colors, and styling
```

---

## 4. Context Engine
The **Context Engine** (`ContextEngine`) aggregates disparate situational inputs into an immutable `ContextSnapshot`.

### Key Components:
- `ContextProvider<T>`: Modular interface for individual context providers.
- `AndroidNetworkContextProvider`: Safe, read-only observer utilizing Android's `ConnectivityManager` (requiring only normal permission `ACCESS_NETWORK_STATE`).
- `DefaultContextEngine`: Combines time, day of week, network state, active tasks, workload level, and user availability.
- Reactive: Exposes both a point-in-time snapshot (`captureSnapshot()`) and a reactive stream (`observeSnapshot(): Flow<ContextSnapshot>`).

---

## 5. Decision Engine & Separation of Deterministic Logic vs. AI

### Architectural Principle
**Deterministic rules must govern authoritative application decisions.**

```
Context Snapshot ──► [ Deterministic Rules Engine ] ──► Explainable Decisions
                              ▲
                              │ (Future milestones)
                     [ AI / LLM Assistance ]
                     - Interpretation
                     - Task decomposition
                     - Natural language rationale
                     - Pattern discovery
```

### Why Deterministic Logic and AI are Separated:
1. **Safety & Predictability**: Critical operations (e.g., emergency mesh routing, high workload pause alerts, task scheduling constraints) require guarantees that cannot suffer from LLM hallucinations or stochastic non-determinism.
2. **Offline Resilience**: The system must run on-device when disconnected without depending on remote LLM API latency or connectivity.
3. **Explainability & Transparency**: Every recommendation includes a deterministic reason and confidence score (0.0 to 1.0) inspectable by the user.
4. **Appropriate AI Role**: In future milestones, AI will assist with unstructured language parsing, task breakdown suggestions, and nuance discovery—acting as an advisor rather than an unsupervised controller.

---

## 6. The Learning Loop
The system incorporates an adaptive feedback loop:

$$\text{Observe} \longrightarrow \text{Understand} \longrightarrow \text{Recommend} \longrightarrow \text{User Response} \longrightarrow \text{Measure Outcome} \longrightarrow \text{Update User Model}$$

- **Observe**: Context Engine gathers signals (`ContextSnapshot`).
- **Understand & Recommend**: Decision Engine outputs prioritized `Recommendation`s.
- **User Response**: Captures whether the user `ACCEPTED`, `REJECTED`, or `DISMISSED` the recommendation via `UserFeedback`.
- **Measure Outcome**: Evaluates objective indicators (e.g., task completion, elapsed time) via `OutcomeResult`.
- **Update User Model**: `UserModelUpdater` adjusts contextual thresholds and preference weights.

---

## 7. Current Milestone vs. Future Milestones

| Capability | Milestone 1 (Current) | Future Milestones |
| :--- | :--- | :--- |
| **Core Architecture** | Fully established package structure, domain models, MVVM, Compose | Maintained and expanded across modules |
| **Context Engine** | Local time, day of week, network status, active task state | Calendar sync, sensor signals, app usage metrics |
| **Decision Engine** | Deterministic rule engine (`DeterministicDecisionEngine`) | Hybrid deterministic rules + on-device/cloud LLM assistance |
| **Learning Loop** | Interfaces & models (`LearningLoop`, `UserFeedback`, `OutcomeResult`) | Empirical outcome measurement, preference calibration |
| **Data Layer** | Room/Retrofit ready contracts + `InMemoryTaskRepository` | Room SQLite database, Retrofit HTTP clients, sync engine |
| **RealityCheck** | Domain event definitions & navigation placeholder | Claim extraction, evidence web gathering, credibility scoring |
| **RescueMesh** | Domain event definitions & navigation placeholder | Wi-Fi Direct / BLE peer-to-peer mesh relay, store-and-forward |
| **UI** | Clean Compose Dashboard with live context and interactive recommendations | Comprehensive multi-screen domain UIs |
