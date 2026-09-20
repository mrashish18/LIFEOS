# LIFEOS

> **Understand. Decide. Adapt.**  
> An adaptive personal intelligence platform for Android.

---

## Overview

LIFEOS is an on-device personal intelligence system designed to observe situational context, make explainable decisions, calibrate to individual behavior patterns, and operate with zero cloud reliance. Built on pure Jetpack Compose, Kotlin Coroutines, and Room SQLite, LIFEOS combines proactive personal execution, multi-source claim verification, and decentralized store-and-forward mesh resilience into a cohesive, private-by-design experience.

---

## Visual Showcase

| 01. Command Center | 02. Personal Intelligence |
| :---: | :---: |
| <img src="screenshots/01_dashboard.png" width="360" /> | <img src="screenshots/02_tasks.png" width="360" /> |
| **Command Center Dashboard**<br/>Scenic atmospheric header, live momentum meter, flagship context recommendation with confidence indicator, 2x2 telemetry grid, and behavior calibration feedback. | **Action Queue**<br/>Prioritized tasks with filter pills (`All`, `Pending`, `In Progress`, `Done`), category badges, inline action controls, and floating task authoring capsule. |

| 03. Task Authoring | 04. Strategic Goals |
| :---: | :---: |
| <img src="screenshots/03_new_task.png" width="360" /> | <img src="screenshots/04_goals.png" width="360" /> |
| **Creation Sheet**<br/>Modal bottom sheet for rapid task entry with segmented priority pills, category selection cards, and duration selector. | **Long-Term Direction**<br/>Scenic landscape trail banner, milestone progress indicators, and quarterly objectives linked to daily execution. |

| 05. Cognitive Architecture | 06. Truth Inquiry |
| :---: | :---: |
| <img src="screenshots/05_intelligence.png" width="360" /> | <img src="screenshots/06_truth_input.png" width="360" /> |
| **System Architecture**<br/>Four-stage closed loop (`01 OBSERVE`, `02 UNDERSTAND`, `03 DECIDE`, `04 ADAPT`) with core principles: On-Device Privacy, Determinism, and Human-Centric Design. | **RealityCheck Input**<br/>Claim verification entry with URL support, quick example pills, and historical investigation audit cards. |

| 07. Investigation Report | 08. Resilience Center |
| :---: | :---: |
| <img src="screenshots/07_truth_result.png" width="360" /> | <img src="screenshots/08_rescuemesh.png" width="360" /> |
| **Corroboration Verdict**<br/>Dynamic verdict hero (`SUPPORTED`, `CONTRADICTED`, `MIXED`) with confidence scoring, domain tags, key takeaways, and authoritative source cards. | **RescueMesh Center**<br/>Local network readiness indicator, 3-node mesh topology visualization, live telemetry (`Queued`, `Relaying`, `Delivered`), and emergency dispatch. |

| 09. Emergency Dispatch | 10. Store-and-Forward Queue |
| :---: | :---: |
| <img src="screenshots/09_emergency_message.png" width="360" /> | <img src="screenshots/10_message_queue.png" width="360" /> |
| **Emergency SOS Modal**<br/>Triaged priority selector (`Normal`, `High`, `Critical`), destination routing, real-emergency safeguard alert, and local store-and-forward dispatch. | **Message Queue & Sync**<br/>Offline packet management, lifecycle status filters, SHA-256 payload integrity, TTL expiration tracking, and opportunistic network sync. |

---

## Core Pillars

### 1. Personal Intelligence
- **Persistent Action Queue**: Backed by Room SQLite with full offline durability.
- **Explainable Decision Engine**: Heuristically weights urgency, continuity, circadian rhythms, and availability without generative hallucinations.
- **Adaptive Learning Loop**: Captures explicit user signals (start, complete, abandon, postpone) to continuously calibrate completion velocities and category momentum.

### 2. Trust Intelligence (RealityCheck)
- **Claim Analysis**: Extracts factual assertions from user text inputs and URLs.
- **Corroboration Engine**: Matches claims against trusted domain corpora (Government, Academic, Established Reference).
- **Calibrated Scoring**: Produces explainable verification verdicts (`SUPPORTED`, `CONTRADICTED`, `MIXED`, `INSUFFICIENT_EVIDENCE`) with explicit source weighting.

### 3. Resilience Intelligence (RescueMesh)
- **Local Store-and-Forward Prototype**: Local Android proof-of-concept demonstrating offline store-and-forward relaying, Room persistence, priority queueing, and opportunistic gateway sync (does not claim an active nationwide radio mesh network).
- **Offline Store-and-Forward**: Persistent Room SQLite emergency message queue with hop-bounded relay (`maxHops = 5`) and opportunistic transmission.
- **Cryptographic Integrity & Deduplication**: Canonical SHA-256 payload hashing (`calculateFingerprint(senderId, payload, createdAt, ttl, hops)`) prevents corruption and packet duplication.
- **Strict TTL Enforcement**: Priority-calibrated Time-to-Live limits (48h Critical, 24h High, 12h Normal) with automatic expired packet purging and clock injection.
- **Priority Queueing**: Deterministic triage order (`CRITICAL` > `HIGH` > `NORMAL`) in Room database queries and sync transmission sweeps.
- **Gateway Synchronization**: Automatic transmission when Wi-Fi/Cellular connectivity is detected, with strict delivery state tracking (`SENT` on egress; `DELIVERED` only upon recipient ACK).

---

## Architecture

```
                       ┌──────────────────────┐
                       │    Context Engine    │
                       │ (Time, Net, Workload)│
                       └──────────┬───────────┘
                                  │
                                  ▼
                       ┌──────────────────────┐
                       │    Decision Engine   │
                       │(Explainable Decision)│
                       └──────────┬───────────┘
                                  │
         ┌────────────────────────┼────────────────────────┐
         ▼                        ▼                        ▼
┌──────────────────┐    ┌──────────────────┐    ┌──────────────────┐
│     Personal     │    │ Trust (Reality)  │    │Resilience (Mesh) │
│   Intelligence   │    │   Intelligence   │    │   Intelligence   │
└────────┬─────────┘    └──────────────────┘    └──────────────────┘
         │
         ▼
┌──────────────────┐
│  Learning Loop   │
│ (Behavior Model) │
└──────────────────┘
```

---

## End-to-End Cognitive Lifecycle

LIFEOS implements a continuous, closed-loop on-device cognitive architecture:

1. **OBSERVE (Context Engine)**: Real-time environmental sensing (time of day, network state, workload demands) with zero cloud dependency.
2. **UNDERSTAND (Behavior Model)**: Longitudinal calibration of individual completion velocity, preferred task sizing, and circadian focus peaks.
3. **DECIDE (Decision Engine)**: Deterministic heuristic scoring of urgency, context fit, and fatigue mitigation with full factor explainability.
4. **ACT (User Execution)**: Tactile execution of focus sessions, task postponements, claim investigations, and emergency transmissions.
5. **MEASURE (Outcome Telemetry)**: Non-invasive instrumentation recording behavioral outcomes without leaking user data.
6. **ADAPT (Learning Loop)**: Dynamic weight adjustment to continuously tune future recommendations and pacing.

---

## Strategic Alignment Hierarchy

Every granular execution is tied to compounding long-term growth:
$$\text{GOAL (Direction)} \longrightarrow \text{MILESTONE (Target)} \longrightarrow \text{TASK (Execution)} \longrightarrow \text{OUTCOME (Growth)}$$

- **Work Tasks** $\leftrightarrow$ **Autonomous Productivity Goal**
- **Health Tasks** $\leftrightarrow$ **Circadian Rhythm & Workload Pacing Goal**
- **Learning Tasks** $\leftrightarrow$ **Deep Cognitive Work Goal**

---

## Dual-Theme Adaptive Design System

LIFEOS employs an intentional dual-theme architecture designed for context-appropriate psychological focus:

1. **Productivity & Ground Truth (Screens 1–7 — Light Lavender & Scenic Pastels)**:
   - **Background**: Soft lavender-tinted slate (`#F8FAFC` to `#EDE9FE`) evoking clarity and cognitive focus.
   - **Surfaces**: Large rounded cards (16–24dp radius), crisp `#F1F5F9` borders, and soft diffusion shadows.
   - **Gradients**: Royal Indigo (`#4F46E5`), Deep Purple (`#7C3AED`), and Electric Blue (`#2563EB`) gradients for flagship actions and banners.
   - **Art & Atmosphere**: Pure Jetpack Compose canvas-rendered mountain sunrise ridges, winding trail illustrations, and glossy 3D verdict cards.

2. **Emergency Resilience (Screens 8–10 — RescueMesh Futuristic Midnight Navy)**:
   - **Background**: Deep midnight navy (`#07152F` / `#0B1F45` / `#101A4A`) designed for high-stress, low-light emergency field operations.
   - **Accents**: High-visibility glowing cyan (`#06B6D4`), electric blue (`#3B82F6`), status emerald (`#10B981`), and emergency crimson (`#EF4444`).
   - **Visualization**: Multi-node mesh topology with dynamic signal beacons, packet relay hop trackers, and sunset ridge hiker hero artwork.
   - **Adaptive Navigation**: Floating bottom bar dynamically shifts from crisp light frosted glass to dark navy midnight whenever transitioning to the Resilience pillar.

---

## Verification & Test Suite

- **102 / 102 Unit Tests PASS** across domain, core engine, persistence, UI state, and use case layers.
- **Strictly Room SQLite backed** with zero synthetic mock data.
- **10/10 Pixel 6 (API 34) Canonical Screenshots** refreshed and verified.

### Run Unit Tests
```bash
.\gradlew.bat testDebugUnitTest
```

### Build Debug APK
```bash
.\gradlew.bat assembleDebug
```

### Install to Connected Device / Emulator
```bash
.\gradlew.bat installDebug
adb shell am start -n com.mrashish18.lifeos/.MainActivity
```

