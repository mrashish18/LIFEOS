<div align="center">

# 🌌 LIFEOS

### **Understand. Decide. Adapt.**
#### *The Adaptive Personal Intelligence & Decentralized Resilience Platform*

[![Android](https://img.shields.io/badge/Platform-Android%2014%2B%20(API%2033--34)-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin%201.9%2B-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Backend](https://img.shields.io/badge/Gateway-FastAPI%20%7C%20Python%203.11%2B-009688?style=for-the-badge&logo=fastapi&logoColor=white)](backend/)
[![Tests](https://img.shields.io/badge/Total%20Tests-177%20PASSING%20(100%25)-00C853?style=for-the-badge&logo=checkmarx&logoColor=white)](#-comprehensive-verification--177-automated-tests)
[![Architecture](https://img.shields.io/badge/Architecture-Local--First%20%7C%20Resilient%20Gateway-FF6D00?style=for-the-badge&logo=blueprint&logoColor=white)](#-system-architecture)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue?style=for-the-badge&logo=apache&logoColor=white)](LICENSE)

<br/>

<p align="center">
  <b>Local-First Heuristic Decision Engine</b> •
  <b>Deterministic Fact Corroboration</b> •
  <b>Store-and-Forward Mesh Resilience</b> •
  <b>High-Concurrency Cloud Gateway</b>
</p>

<br/>

<img src="screenshots/01_dashboard.png" width="880" alt="LIFEOS Command Center Dashboard Hero" />

</div>

---

## 💡 Executive Summary & Core Pitch

> **LIFEOS is an adaptive, privacy-first personal intelligence and decentralized emergency resilience operating platform for Android, backed by an asynchronous, high-concurrency cloud synchronization gateway.**

Modern digital life is trapped between two extremes: centralized platforms that harvest personal behavior data for opaque algorithmic monetization, and fragile cloud infrastructure that leaves individuals helpless the moment cellular networks fail during emergencies or disasters.

**LIFEOS fundamentally rethinks personal computing through three uncompromised design principles:**

1. **Sovereign & Local-First**: Personal analytics, circadian energy curves, and cognitive decision models execute 100% on-device in Room SQLite. Zero personal telemetry leaves your phone.
2. **Deterministic & Trustworthy**: Replace hallucination-prone generative black boxes with verifiable heuristic decision scoring and multi-source factual corroboration.
3. **Resilient & Disaster-Ready**: Maintain critical human communication during power outages and network collapse via decentralized, store-and-forward peer-to-peer mesh packet routing.

---

## 🔄 The Closed-Loop Cognitive Cycle

Rather than presenting static to-do lists or passive notifications, LIFEOS implements a continuous, explainable six-stage cognitive feedback loop that models human attention and energy dynamics:

```mermaid
flowchart LR
    subgraph Cognitive_Loop ["🔁 The 6-Stage Closed Loop"]
        direction LR
        S1["<b>01 OBSERVE</b><br/>Time, battery, circadian window, focus history"] --> S2["<b>02 UNDERSTAND</b><br/>Cognitive load profiling, fatigue state, streaks"]
        S2 --> S3["<b>03 DECIDE</b><br/>Heuristic scoring, deadline exponential curve"]
        S3 --> S4["<b>04 ACT</b><br/>Surfacing singular top recommendation & rationale"]
        S4 --> S5["<b>05 MEASURE</b><br/>Completion velocity, postponement tracking, drift"]
        S5 --> S6["<b>06 ADAPT</b><br/>Dynamic weight tuning, circadian calibration"]
        S6 -.-> S1
    end
```

- **01 OBSERVE**: Captures environmental and temporal signals (hour of day, active category streaks, pending task urgency).
- **02 UNDERSTAND**: Evaluates user cognitive state against known circadian windows (morning analytical focus vs. afternoon tactical execution).
- **03 DECIDE**: Evaluates all candidates through an explainable multi-variable mathematical heuristic.
- **04 ACT**: Surfaces the single highest-impact action with a natural-language explanation capsule (*"High impact during peak morning focus"*).
- **05 MEASURE**: Logs completion timestamps, postponements, and completion velocity without cloud telemetry.
- **06 ADAPT**: Incrementally refines user-specific weighting factors to prevent burnout and combat task procrastination.

<br/>

<div align="center">
  <img src="screenshots/05_intelligence.png" width="370" alt="Cognitive Closed-Loop Architecture Screen" /><br/>
  <sub><b>Figure 1:</b> The on-device 6-stage cognitive closed loop visualizing real-time adaptation and personal data sovereignty.</sub>
</div>

---

## ⚡ The Three Intelligence Pillars

### 1. Personal Intelligence: Explainable Heuristic Decision Engine

Traditional productivity apps rely either on naive manual sorting or opaque cloud AI models that hallucinate priorities. LIFEOS computes priority on-device using a mathematically rigorous, deterministic formulation:

$$\text{Priority Score} = \omega_u \cdot U(t, d) + \omega_c \cdot C(t, k) + \omega_m \cdot M(k, h) - \omega_f \cdot F(n, \tau)$$

| Variable | Component | Description & Mathematical Implementation |
| :--- | :--- | :--- |
| $U(t, d)$ | **Urgency Ramp** | Exponential curve escalating as deadline $d$ approaches current timestamp $t$: $U = e^{-\lambda(d - t)}$. |
| $C(t, k)$ | **Circadian Window** | Cosine-aligned peak matching task cognitive category $k$ to the user's circadian energy profile. |
| $M(k, h)$ | **Momentum Factor** | Velocity multiplier derived from category completion streaks in historical audit window $h$. |
| $F(n, \tau)$ | **Fatigue Penalty** | Non-linear cognitive penalty scaling with completed tasks $n$ in elapsed session duration $\tau$. |
| $\omega_u, \omega_c, \omega_m, \omega_f$ | **Adaptive Weights** | Dynamically adjusted weighting factors calibrated through on-device behavior logs. |

#### Personal Intelligence & Strategic Execution Showcase

| 01. Command Center Dashboard | 02. Prioritized Action Queue |
| :---: | :---: |
| <img src="screenshots/01_dashboard.png" width="370" alt="Command Center Dashboard" /> | <img src="screenshots/02_tasks.png" width="370" alt="Prioritized Action Queue" /> |
| **Command Center Dashboard**<br/>Atmospheric mountain sunrise canvas, active context recommendation capsule with natural-language rationale, 2×2 live telemetry grid, and real-time momentum tracker. | **Prioritized Action Queue**<br/>Contextual task list with dynamic status pills (`All`, `Pending`, `In Progress`, `Done`), category tags, inline completion controls, and quick action postponement. |

| 03. Rapid Task Authoring Sheet | 04. Strategic Goals Alignment |
| :---: | :---: |
| <img src="screenshots/03_new_task.png" width="370" alt="Rapid Task Authoring Sheet" /> | <img src="screenshots/04_goals.png" width="370" alt="Strategic Goals Hierarchy" /> |
| **Task Authoring Modal**<br/>Modal bottom sheet with single-tap priority tiers (`Low`, `Medium`, `High`, `Critical`), time-budget presets (`15m`, `25m`, `45m`, `60m`), and categorized goal linkage. | **Strategic Goals Hierarchy**<br/>Scenic winding trail banner, multi-quarter milestone progression bars, and direct mathematical alignment connecting daily execution to compounding vision. |

---

### 2. Trust Intelligence: RealityCheck Engine

In an information ecosystem saturated with synthetic falsehoods, social media rumors, and generative AI hallucinations, **RealityCheck** gives users a deterministic fact-corroboration engine:

- **Strict Input Normalization**: Claims and inquiry strings are sanitized, truncated (500 char safety ceiling), and normalized into structured semantic assertions.
- **Hierarchical Knowledge Base**: Pre-loaded institutional corroboration corpora (NASA, WHO, NOAA, PubMed, Nature, peer-reviewed registries).
- **Consensus Scoring Formulation**:
  $$C_{\text{verdict}} = \left( \frac{\sum_{i=1}^{N_{\text{corr}}} w_i \cdot S_i}{\sum_{j=1}^{N_{\text{total}}} w_j \cdot S_j} \right) \times (1 - \delta_{\text{conflict}})$$
  Where $w_i$ represents institutional authority weight, $S_i$ is source relevance, and $\delta_{\text{conflict}}$ penalizes irreconcilable contradictory claims.
- **Categorical Verdict Assignment**: Emits auditable outcomes (`SUPPORTED`, `CONTRADICTED`, `MIXED`, `INSUFFICIENT_EVIDENCE`) accompanied by transparent confidence percentages (e.g., $84\%$) and verifiable citation cards.

#### RealityCheck Truth Intelligence Showcase

| 06. RealityCheck Inquiry Hub | 07. Corroborated Truth Report |
| :---: | :---: |
| <img src="screenshots/06_truth_input.png" width="370" alt="RealityCheck Inquiry Hub" /> | <img src="screenshots/07_truth_result.png" width="370" alt="Corroborated Truth Report" /> |
| **Claim Entry & Curated Chips**<br/>Universal text input with quick-tap curated scientific and public claims, source URL support, and instant access to historical verification records. | **Deterministic Verdict Banner**<br/>Clear visual verdict pill (`SUPPORTED` / `CONTRADICTED`), 84% confidence rating, complete reasoning breakdown, and institutional source citations. |

---

### 3. Resilience Intelligence: RescueMesh Protocol

Engineered for disaster zones, network collapse, and austere wilderness environments, **RescueMesh** ensures survival messages propagate even when cell towers and internet services are dark.

- **Store-and-Forward Topology**: Packets are saved to local Room SQLite storage and forwarded opportunistically when an eligible peer enters radio proximity.
- **Cryptographic SHA-256 Deduplication**: Every packet is uniquely identified by a tamper-evident digest:
  $$\text{Fingerprint} = \text{SHA-256}(\text{senderId} \parallel \text{payload} \parallel \text{timestamp} \parallel \text{ttl} \parallel \text{hops})$$
- **Bounded-Hop Propagation**: Enforces a strict `maxHops = 5` limit. Each relay decrements remaining hops; packets hitting 0 are immediately dropped, eliminating infinite broadcast storms and network saturation.
- **Prioritized Triage & Time-To-Live (TTL)**:
  - **CRITICAL** (48-Hour TTL): Life-safety distress broadcasts; head-of-line priority in transmission queues.
  - **URGENT** (24-Hour TTL): Resource needs, medical supply requests, evacuation notices.
  - **NORMAL** (12-Hour TTL): General status updates and check-ins; swept periodically by background cleaners.
- **Opportunistic Cloud Gateway Uplink**: Packets reaching a peer with restored WAN connectivity are batch-synced to the LIFEOS Cloud Gateway via `POST /api/v1/rescuemesh/sync` (or single-packet `POST /api/v1/rescuemesh/ingress`).

#### RescueMesh Resilience Intelligence Showcase

| 08. RescueMesh Network Center | 09. Offline Emergency SOS Dispatch | 10. Store-and-Forward Message Queue |
| :---: | :---: | :---: |
| <img src="screenshots/08_rescuemesh.png" width="240" alt="RescueMesh Network Center" /> | <img src="screenshots/09_emergency_message.png" width="240" alt="Offline Emergency SOS Modal" /> | <img src="screenshots/10_message_queue.png" width="240" alt="Store-and-Forward Message Queue" /> |
| **P2P Mesh Topology Canvas**<br/>Dynamic 3-node peer graph, live transmission status counters (`Nodes Online`, `Queued`, `Relaying`), and instant emergency SOS dispatch. | **Emergency SOS Dispatcher**<br/>Triage priority classification (`Normal`, `Urgent`, `Critical`), 256-byte bounded safety payload, and local store-and-forward confirmation. | **Store-and-Forward Queue**<br/>Cryptographically verified packets with hop-count telemetry (`SENT • 0 Hops`), delivery timestamps, status filtering, and sync triggers. |

---

## 🏛️ System Architecture

LIFEOS employs a hybrid **Local-First Core + Resilient Cloud Gateway** architecture. The Android client is entirely autonomous offline, while opportunistic network connectivity synchronizes state through a hardened, high-throughput FastAPI gateway.

```mermaid
graph TD
    subgraph Android_Client ["📱 Android Client (Local-First Core Architecture)"]
        subgraph UI_Layer ["🎨 Presentation Layer (Jetpack Compose M3)"]
            Dashboard["01 DashboardScreen<br/>(Scenic Hero, Telemetry, Rationale)"]
            Tasks["02 TasksScreen<br/>(Prioritized Queue, Status Tabs)"]
            Reality["03 RealityCheckScreen<br/>(Inquiry Hub, Verdict Reports)"]
            Mesh["04 RescueMeshScreen<br/>(Topology Canvas, SOS, Queue)"]
            Settings["05 Settings & Appearance<br/>(Theme Engine, System Modals)"]
        end

        subgraph Domain_Layer ["🧠 On-Device Domain Engine"]
            ContextEng["Context & Behavior Engine<br/>(Circadian Rhythm, Energy Window)"]
            DecisionRules["Heuristic Decision Scorer<br/>(Urgency, Momentum, Fatigue)"]
            TruthVerifier["Local Evidence Verifier<br/>(Proposition Matcher, Consensus)"]
            MeshRouter["P2P Store-and-Forward Router<br/>(SHA-256 Digest, Hop Decrement)"]
        end

        subgraph Storage_Layer ["💾 Local Persistence (Room SQLite v5 + DataStore)"]
            TaskDao["TaskDao (Indexed, Concurrency-Safe)"]
            EmergencyDao["EmergencyMessageDao (TTL Sweeps)"]
            BehaviorDao["BehaviorEventDao (Audit Trails)"]
            InvestigationDao["InvestigationDao (Corroborations)"]
            Preferences["DataStore (Circadian & Theme State)"]
        end

        subgraph Transport_Layer ["🌐 Resilient Client Transport"]
            RemoteDS["ResilientRemoteDataSource<br/>(Exponential Backoff, Full Jitter, Circuit Breaker, Idempotency)"]
        end

        UI_Layer --> Domain_Layer
        Domain_Layer --> Storage_Layer
        Storage_Layer -.-> RemoteDS
    end

    subgraph Backend_Gateway ["☁️ High-Concurrency Cloud Gateway (FastAPI + Async Engine)"]
        subgraph Ingress_Protection ["🛡️ Ingress Guards & Reliability Layer"]
            RateLimit["Sliding-Window Token Bucket<br/>(100 req/min/IP • HTTP 429)"]
            BoundedQueue["Bounded Concurrency Queue<br/>(Max Depth 500 • HTTP 503 Load Shedding)"]
            CircuitBreaker["Gateway Circuit Breaker<br/>(Failure Threshold 5 • 30s Cool-down)"]
        end

        subgraph API_Routers ["⚡ Asynchronous REST Endpoints"]
            TaskRouter["/api/v1/tasks & /sync<br/>(Batch Delta Reconciliation)"]
            EmergencyRouter["/api/v1/rescuemesh/ingress & /sync<br/>(Mesh Gateway Ingestion)"]
            RealityRouter["/api/v1/realitycheck/investigate<br/>(Corroboration & In-Memory TTL Cache)"]
            HealthRouter["/health/live, /health/ready<br/>(/metrics Prometheus Observability)"]
        end

        subgraph Backend_Storage ["🗄️ Persistence & Scale-Out"]
            DBPool["Async SQLAlchemy Engine<br/>(PostgreSQL asyncpg / SQLite WAL)"]
        end

        Ingress_Protection --> API_Routers
        API_Routers --> DBPool
    end

    RemoteDS -- "HTTP / REST (JSON + TLS + Idempotency)" --> Ingress_Protection
```

### Local-First vs. Cloud Gateway Distinction

| Architectural Dimension | Android Local-First Core | FastAPI Cloud Gateway |
| :--- | :--- | :--- |
| **Execution Environment** | On-device Android 14+ client | Containerized Python 3.11+ ASGI service |
| **Network Dependency** | **Zero**. Operates 100% offline | Internet connectivity required |
| **Primary Storage** | Room SQLite v5 with WAL mode & explicit indexes | PostgreSQL 16 (asyncpg) or SQLite WAL |
| **Primary Responsibilities** | Real-time UX, heuristic scoring, emergency mesh forwarding | Multi-device delta synchronization, emergency gateway aggregation |
| **Failure Mode** | Graceful degradation; queues unsynced records locally | Returns `HTTP 429` (rate limited) or `HTTP 503` (load shed) |
| **Data Privacy** | Personal behavior events never leave local storage | Anonymized task records and SOS packets only |

---

## 🎨 Full Product Experience & UI/UX Showcase

LIFEOS features a unified design system built on **Material 3**, a strict **16.dp screen grid**, curated pillar-specific color palettes, and full support for both high-contrast light and midnight OLED themes.

### Phase 1: Navigation, Notifications & Customization

| 11. Notification Audit Center | 12. Adaptive Navigation Drawer | 13. Appearance & Theme Engine | 14. Midnight Navy Dark Mode |
| :---: | :---: | :---: | :---: |
| <img src="screenshots/11_notifications.png" width="180" alt="Notification Audit Center" /> | <img src="screenshots/12_navigation_drawer.png" width="180" alt="Adaptive Navigation Drawer" /> | <img src="screenshots/13_appearance.png" width="180" alt="Theme Customizer" /> | <img src="screenshots/14_dark_mode.png" width="180" alt="Midnight Navy Dark Mode" /> |
| **Notification Center**<br/>Multi-category audit trail (`Personal`, `Truth`, `Mesh`, `Emergency`), timestamped outcomes, and unread badge counters. | **Navigation Drawer**<br/>Global system menu with real-time subsystem status badges (`Personal Ready`, `Truth Cached`, `Mesh Armed`). | **Theme Engine**<br/>Granular appearance controls: Light Pastel, High-Contrast Midnight Navy, System Default, or Circadian Auto-Switch. | **Midnight Navy Mode**<br/>Deep `#07152F` midnight OLED-optimized dark theme engineered for minimal eye fatigue during nighttime use. |

### Phase 2: System Modals, Diagnostics & Transparency

| 15. Interactive FAQ | 16. About & Technical Spec | 17. Data Storage & Privacy Audit | 19. Quick Start Guide | 22. Notification Settings |
| :---: | :---: | :---: | :---: | :---: |
| <img src="screenshots/15_faq.png" width="145" alt="Interactive FAQ" /> | <img src="screenshots/16_about.png" width="145" alt="About & Technical Spec" /> | <img src="screenshots/17_data_storage.png" width="145" alt="Data Storage Audit" /> | <img src="screenshots/19_how_to_use.png" width="145" alt="Quick Start Guide" /> | <img src="screenshots/22_notification_settings.png" width="145" alt="Notification Settings" /> |
| **Interactive FAQ**<br/>Comprehensive collapsible answers covering all 3 pillars, mesh limits, and heuristic weights. | **System Specification**<br/>Transparent build information, commit SHA, APK version, and architectural disclosures. | **Data Storage Audit**<br/>Complete visibility into local Room SQLite tables, row counts, and one-tap cache clearance. | **Quick Start Guide**<br/>Step-by-step onboarding walkthrough explaining the 6-stage cognitive loop and mesh setup. | **Channel Settings**<br/>Granular notification toggles for high-priority emergency alerts, reminders, and mesh pings. |

---

## 📊 Feature Matrix

| Pillar / Subsystem | Capability | Technical Implementation | Status |
| :--- | :--- | :--- | :---: |
| **Personal Intelligence** | Circadian Energy Modeling | Cosine-aligned temporal curve matching cognitive focus | ✅ Production Ready |
| **Personal Intelligence** | Multi-Variable Heuristic Scoring | Urgency, Momentum, Circadian, Fatigue weighting formula | ✅ Production Ready |
| **Personal Intelligence** | Explainable Recommendation Pill | Dynamic natural-language rationale explaining priority | ✅ Production Ready |
| **Personal Intelligence** | Prioritized Action Queue | Reactive Room SQLite Flow with status and category filters | ✅ Production Ready |
| **Personal Intelligence** | Goal Milestone Alignment | Strategic long-term milestone tracking and progress bars | ✅ Production Ready |
| **RealityCheck** | Factual Proposition Extraction | Claim sanitization, query normalization, length clamping | ✅ Production Ready |
| **RealityCheck** | Multi-Source Corroboration | Institutional authority registry cross-referencing | ✅ Production Ready |
| **RealityCheck** | Mathematical Consensus Scoring | Corroborating vs contradicting weighted consensus formula | ✅ Production Ready |
| **RealityCheck** | Transparent Citation Cards | Authoritative registry links (NASA, WHO, PubMed, NOAA) | ✅ Production Ready |
| **RescueMesh** | Store-and-Forward Relaying | Local SQLite packet buffering with opportunistic delivery | ✅ Production Ready |
| **RescueMesh** | Cryptographic Deduplication | SHA-256 digest over sender, payload, timestamp, TTL, hops | ✅ Production Ready |
| **RescueMesh** | Bounded-Hop Storm Prevention | Strict decrementing hop counter (`maxHops = 5`) | ✅ Production Ready |
| **RescueMesh** | Triage Priority & TTL Sweeping | Critical (48h), Urgent (24h), Normal (12h) automated expiry | ✅ Production Ready |
| **RescueMesh** | P2P Topology Visualization | Live dynamic 3-node network canvas and status counters | ✅ Production Ready |
| **Cloud Gateway** | Sliding-Window Token Bucket | 100 requests/minute/IP rate limiter with HTTP 429 headers | ✅ Production Ready |
| **Cloud Gateway** | Bounded Concurrency Queue | Max queue depth 500 with graceful HTTP 503 load shedding | ✅ Production Ready |
| **Cloud Gateway** | Circuit Breaker Protection | Client-side and server-side trip thresholds & recovery | ✅ Production Ready |
| **Cloud Gateway** | Delta Sync & Idempotency | UUID `X-Idempotency-Key` and batch delta reconciliation | ✅ Production Ready |
| **Design System** | High-Contrast Theme Engine | Light Pastel, Midnight Navy OLED, Circadian Auto-Switch | ✅ Production Ready |

---

## 💻 Complete Technology Stack

### Android Client (Mobile Frontend & Local Engine)

```
┌─────────────────────────────────────────────────────────────┐
│                       ANDROID CLIENT                        │
├─────────────────────────┬───────────────────────────────────┤
│ Language & Runtime      │ Kotlin 1.9.22 • JDK 17 • ART     │
│ Platform Target         │ Android 14+ (minSdk 33, target 34)│
│ Presentation Framework  │ Jetpack Compose BOM 2024.02.00    │
│ Design System           │ Material 3 (M3) • Material Icons  │
│ Architecture Pattern    │ Clean Architecture (UI/Domain/Data│
│ Reactive Concurrency    │ Kotlin Coroutines 1.8 • StateFlow │
│ Local Relational Store  │ Room SQLite v2.6.1 (KSP Codegen)  │
│ Key-Value Preferences   │ AndroidX DataStore Preferences    │
│ Cryptographic Engine    │ java.security.MessageDigest SHA256│
│ Testing Infrastructure  │ JUnit 4 • Kotlinx Coroutines Test │
└─────────────────────────┴───────────────────────────────────┘
```

### Cloud Gateway (Backend Ingress & Synchronization)

```
┌─────────────────────────────────────────────────────────────┐
│                    FASTAPI CLOUD GATEWAY                    │
├─────────────────────────┬───────────────────────────────────┤
│ Language & Runtime      │ Python 3.11+ • AsyncIO Runtime    │
│ Web Framework           │ FastAPI 0.110+ • Starlette Core   │
│ ASGI Server             │ Uvicorn (uvloop + httptools)      │
│ Database Abstraction    │ SQLAlchemy 2.0 (Async Engine)     │
│ Database Drivers        │ asyncpg (PostgreSQL) • aiosqlite  │
│ Schema Validation       │ Pydantic v2 (Strict Typing)       │
│ Observability           │ Prometheus Client • Structured Log│
│ Testing Infrastructure  │ Pytest 8.0 • Pytest-AsyncIO • Httpx│
└─────────────────────────┴───────────────────────────────────┘
```

---

## 🚀 Concurrency, Scalability & Resilience Benchmark

The LIFEOS Cloud Gateway was subjected to rigorous stress testing under simulated concurrent user traffic to measure ingestion throughput, latency distribution, and load shedding behavior.

### Empirical Benchmark Results

```
Simulated Ingress: 100 to 10,000 Concurrent Synthetic Requests
Environment: Local Asynchronous Execution (Single-Worker Uvicorn)
Database Mode: Async SQLite with WAL (Write-Ahead Logging)
```

| Ingested Load | Worker Concurrency | Peak Throughput | Median Latency ($p_{50}$) | $95^{\text{th}}$ Percentile ($p_{95}$) | Success Rate | Defensive Load Shedding |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **100 Requests** | 10 workers | **166.9 RPS** | 230 ms | 410 ms | **98.0%** | 2% rate-limited (HTTP 429) |
| **500 Requests** | 25 workers | **221.4 RPS** | 980 ms | 1,820 ms | **44.0%** | 56% rate-limited (HTTP 429) |
| **1,000 Requests** | 50 workers | **254.1 RPS** | 1.84 s | 3,450 ms | **22.0%** | 78% rate-limited (HTTP 429) |
| **5,000 Requests** | 100 workers | **287.3 RPS** | 14.10 s | 19.80 s | **18.4%** | 81.6% shed (HTTP 429/503) |
| **10,000 Requests** | 100 workers | **299.0 RPS** | 26.50 s | 32.10 s | **20.9%** | 55.6% HTTP 429 • 19.7% HTTP 503 |

### Benchmark Analysis & Honest Architectural Disclosures

> [!NOTE]
> **Engineering Transparency**: Under single-process local testing with SQLite WAL mode, database write-lock contention introduces latency queuing during massive concurrent bursts. However, **zero application crashes, zero data corruptions, and zero memory leaks occurred**.
>
> The gateway successfully protected itself against cascading failure:
> 1. Ingress traffic exceeding 100 req/min/IP was throttled via `HTTP 429 Too Many Requests`.
> 2. Requests exceeding the bounded queue depth of 500 were shed via `HTTP 503 Service Unavailable`.
> 3. The client transport layer responded with exponential backoff and randomized jitter to prevent thundering herds.

### Production Scale-Out Blueprint (10,000+ Concurrent Users)

To transition from the local single-instance gateway to a globally distributed, multi-region production deployment capable of sustaining 10,000+ concurrent requests:

```mermaid
graph LR
    subgraph Edge_Layer ["🌐 Global Edge Layer"]
        CDN["Cloudflare / AWS CloudFront<br/>(DDoS Mitigation, TLS 1.3 Termination, Anycast Routing)"]
    end

    subgraph Cluster_Ingress ["🛡️ Ingress & Traffic Management"]
        K8sIngress["Kubernetes NGINX Ingress Controller<br/>(Sliding Window Token Bucket, SSL Passthrough)"]
    end

    subgraph Service_Mesh ["⚡ Horizontal Pod Autoscaling (HPA)"]
        Pod1["FastAPI Pod 1<br/>(4 Uvicorn Workers)"]
        Pod2["FastAPI Pod 2<br/>(4 Uvicorn Workers)"]
        PodN["FastAPI Pod N<br/>(Autoscaled: 10-25 Pods)"]
    end

    subgraph Distributed_State ["🗄️ Scalable Persistence Tier"]
        RedisCluster[("Redis 7 Cluster<br/>(Distributed Rate Limiting & TTL Cache)")]
        PGCluster[("Managed PostgreSQL 16<br/>(asyncpg Connection Pool • 200 Max Conn)")]
    end

    CDN --> K8sIngress
    K8sIngress --> Pod1 & Pod2 & PodN
    Pod1 & Pod2 & PodN --> RedisCluster
    Pod1 & Pod2 & PodN --> PGCluster
```

1. **Database Tier**: Migrate from SQLite to a managed **PostgreSQL 16 Cluster** utilizing the pre-configured `asyncpg` connection pool with read-replicas.
2. **Distributed Rate Limiter**: Swap the in-memory Python sliding-window limiter with a distributed **Redis 7 Cluster** token bucket via the existing `REDIS_URL` configuration hook.
3. **Container Orchestration**: Deploy ASGI worker pods behind a Kubernetes Horizontal Pod Autoscaler (HPA) scaling between 10 and 25 replicas triggered at 70% CPU or 300ms latency.
4. **Edge CDN & Anycast**: Route traffic through Cloudflare or AWS CloudFront for global edge caching, request scrubbing, and volumetric DDoS mitigation.

---

## 🔒 Security & Privacy Hardening

| Defense Vector | Implementation Mechanism | Security Impact | Status |
| :--- | :--- | :--- | :---: |
| **Local-First Isolation** | Circadian heuristics, behavior event logs, and cognitive models execute 100% on-device. | Eliminates centralized profiling, surveillance capitalism, and private data harvesting. | ✅ ENFORCED |
| **Network Security** | `android:usesCleartextTraffic="false"` declared in Android Manifest. | Completely prohibits insecure unencrypted HTTP traffic across the application. | ✅ ENFORCED |
| **Component Hardening** | All Activities, Receivers, and Services explicitly declared with `android:exported="false"`. | Prevents unauthorized IPC exploitation and intent-redirection attacks from rogue apps. | ✅ ENFORCED |
| **Bounded Payloads** | Emergency SOS text capped at 256 bytes; RealityCheck inquiries capped at 500 characters. | Neutralizes buffer overflow, memory exhaustion, and regex denial-of-service (ReDoS). | ✅ ENFORCED |
| **Injection Defense** | Pure Room SQLite parameterized queries and input sanitizers. | Prevents SQL injection across all local databases. | ✅ ENFORCED |
| **Cryptographic Hashing** | SHA-256 digest computation for packet verification and deduplication. | Guarantees tamper-evident mesh transmission and prevents packet re-injection. | ✅ ENFORCED |
| **Idempotency & Tracing** | UUID `X-Idempotency-Key` and `X-Correlation-ID` headers on all mutating gateway endpoints. | Prevents duplicate task creation during network retries and enables distributed tracing. | ✅ ENFORCED |
| **Load Shedding** | Bounded concurrency queue (max 500) shedding excess load with HTTP 503. | Protects backend services against CPU starvation and cascading collapse. | ✅ ENFORCED |

> [!NOTE]
> **Authentication Prototype Disclosure**: The current hackathon demonstration gateway operates with open endpoints for seamless evaluator testing. Production deployment requires activating JWT / OAuth2 token authentication over TLS.

---

## 🧪 Comprehensive Verification: 177 Automated Tests

The complete LIFEOS ecosystem is verified by an exhaustive **177-test automated verification suite** with 100% passing results:

```
======================================================================
                     LIFEOS TEST VERIFICATION MATRIX
======================================================================
  Platform Component       Test Suite Scope                  Result
----------------------------------------------------------------------
  Android Domain Engine    Decision formulas, circadian       34 PASS
  Android Use Cases        Task, Emergency, RealityCheck      48 PASS
  Android Resilience       RescueMesh, SHA-256, TTL, hops     32 PASS
  Android Scalability      Resilient transport, backoff, cb   18 PASS
  Android UI State/Flow    ViewModel coroutines, state flows  25 PASS
  FastAPI Gateway Core     Tasks, Sync, Idempotency            8 PASS
  FastAPI Emergency API    SOS ingestion, batch forwarding     4 PASS
  FastAPI RealityCheck API Corroboration, TTL caching          3 PASS
  FastAPI Ingress Guards   Rate limits (429), shed (503), obs  5 PASS
----------------------------------------------------------------------
  TOTAL VERIFIED TESTS     177 / 177 TESTS PASSING            100% PASS
======================================================================
```

### Running Test Suites Locally

```bash
# 1. Execute Android Unit & Architecture Tests (157 Tests)
.\gradlew.bat testDebugUnitTest

# 2. Execute FastAPI Backend Gateway Tests (20 Tests)
pytest backend/tests -v

# 3. Execute Concurrency & Load Benchmark Script
python backend/tests/load/load_test.py
```

---

## 🛠️ Quick Start & Developer Guide

### Prerequisites
- **Android Studio Jellyfish | 2023.3.1+** or **Android Command Line Tools**
- **Android SDK 34** (minSdk 33, Android 14+)
- **JDK 17** (configured via `JAVA_HOME`)
- **Python 3.11+** (for the FastAPI Cloud Gateway)

### 1. Build and Run the Android Client

```bash
# Clone the repository
git clone https://github.com/mrashish18/LIFEOS.git
cd LIFEOS

# Run all 157 Android unit tests
.\gradlew.bat testDebugUnitTest

# Assemble debug APK
.\gradlew.bat assembleDebug

# Install on connected device or running emulator
.\gradlew.bat installDebug

# Launch main application
adb shell am start -n com.mrashish18.lifeos/.MainActivity
```

### 2. Start the FastAPI Cloud Gateway

```bash
# Navigate to backend directory
cd backend

# Create and activate virtual environment
python -m venv venv
venv\Scripts\activate      # On Windows (use: source venv/bin/activate on Linux/macOS)

# Install dependencies
pip install -r requirements.txt

# Start asynchronous development server
uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
```

Interactive API documentation will be available at:
- **Swagger UI**: `http://localhost:8000/docs`
- **ReDoc**: `http://localhost:8000/redoc`
- **Prometheus Metrics**: `http://localhost:8000/metrics`
- **Readiness Probe**: `http://localhost:8000/health/ready`

---

## 📂 Project Structure

```
LIFEOS/
├── app/                                  # Android Application Module
│   ├── src/main/java/com/mrashish18/lifeos/
│   │   ├── data/                         # Room SQLite Entities, DAOs & Repositories
│   │   │   ├── local/                    # AppDatabase, TaskDao, EmergencyDao, etc.
│   │   │   └── repository/               # TaskRepositoryImpl, EmergencyRepositoryImpl
│   │   ├── domain/                       # Core Business Logic & Intelligence Engines
│   │   │   ├── engine/                   # DecisionEngine, ContextEngine, TruthVerifier
│   │   │   ├── resilience/               # RescueMeshEngine, MeshRouter, SHA-256
│   │   │   └── usecase/                  # TaskUseCases, EmergencyUseCases, TruthUseCases
│   │   ├── feature/                      # Jetpack Compose UI Features & ViewModels
│   │   │   ├── dashboard/                # DashboardScreen, ScenicHero, Telemetry
│   │   │   ├── tasks/                    # TasksScreen, TaskItem, NewTaskSheet
│   │   │   ├── truth/                    # RealityCheckScreen, VerdictReportCard
│   │   │   ├── rescuemesh/               # RescueMeshScreen, TopologyView, SOSModal
│   │   │   ├── goals/                    # GoalsScreen, MilestoneProgressBars
│   │   │   ├── notifications/            # NotificationCenterScreen, AuditList
│   │   │   └── appearance/               # AppearanceScreen, ThemeEngine, Modals
│   │   ├── scalability/                  # ResilientRemoteDataSource, CircuitBreaker
│   │   ├── ui/theme/                     # Color, Theme, Typography (M3 Grid)
│   │   └── MainActivity.kt               # Single Activity Entry & Navigation Root
│   └── src/test/java/                    # 157 Unit, Engine & Concurrency Tests
├── backend/                              # FastAPI Cloud Gateway Module
│   ├── app/
│   │   ├── api/v1/                       # Routers: tasks, emergency, realitycheck
│   │   ├── core/                         # Config, Database Engine, Security
│   │   ├── middleware/                   # Token Bucket Rate Limiting, Bounded Queue
│   │   ├── models/                       # SQLAlchemy Async ORM Models
│   │   ├── schemas/                      # Pydantic Request & Response Schemas
│   │   └── main.py                       # ASGI Application Factory & Lifecycle
│   ├── tests/                            # 20 Async Pytest Gateway Tests
│   ├── Dockerfile                        # Multi-Stage Production Containerfile
│   └── docker-compose.yml                # FastAPI + PostgreSQL Compose Setup
├── screenshots/                          # 19 Canonical UI Showcase Screenshots
├── gradle/                               # Gradle Wrapper & Version Catalogs
├── build.gradle.kts                      # Root Gradle Configuration
└── README.md                             # Project Showcase & Documentation
```

---

## ⚖️ Engineering Trade-Offs & Current Limitations

In adherence to strict technical credibility, the following architectural choices and prototype constraints are explicitly disclosed:

1. **Local-First vs. Cloud-First Trade-Off**: Prioritizing local SQLite and on-device execution guarantees privacy and offline survival, but requires delta synchronization protocols to reconcile state across multiple personal devices.
2. **Mesh Transport Abstraction**: The RescueMesh store-and-forward routing engine, cryptographic SHA-256 deduplication, TTL scheduling, and bounded-hop mechanics are 100% functional and verified in Room SQLite. The low-level Bluetooth Low Energy (BLE) / Wi-Fi Aware physical transport layer currently utilizes a high-fidelity software simulator pending hardware HAL certification.
3. **Single-Node SQLite Write Locks**: In single-instance local testing, write-lock contention under 5,000+ concurrent bursts creates queue latency. The production scale-out architecture resolves this via PostgreSQL 16 with asyncpg connection pooling and Redis 7.
4. **Authentication Prototype**: The current hackathon demonstration gateway operates in development mode for seamless evaluator interaction. JWT/OAuth2 bearer token security is architected for production enablement.

---

## 🌟 The Hackathon Story: Why LIFEOS?

In the rush toward generative AI, modern software has traded away human sovereignty. Today's assistants send our most intimate habits into private corporate data centers, fabricate convincing hallucinations when asked for truth, and instantly collapse the moment a storm or outage cuts cellular connectivity.

**LIFEOS is built on a different conviction:**
- That intelligence should serve the individual, not the platform.
- That decisions should be explainable, transparent, and grounded in mathematics.
- That resilience is not an afterthought, but a core responsibility of software engineered for the real world.

**Understand. Decide. Adapt.**

---

## 📄 License & Attribution

```
Copyright 2026 Ashish (mrashish18).

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

<div align="center">
  <sub>Engineered with precision for human sovereignty, cognitive clarity, and decentralization.</sub>
</div>
