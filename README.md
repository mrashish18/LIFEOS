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

## Design System

- **Surfaces & Canvas**: Clean, soft white / very light slate background (`#F8FAFC`) with pure white elevated cards (`#FFFFFF`) featuring 14–18dp rounded corners and `#F1F5F9` boundary strokes.
- **Atmospheric Scenic Art**: Stylized vector mountain ridges on the Dashboard Command Center, winding trail art on the Goals banner, and verdict-adaptive status headers on Truth Intelligence.
- **Accent Palette**: Royal Indigo (`#4338CA`) and Electric Violet (`#6366F1`) primary accents with semantic accents for Success Mint (`#10B981`), Warning Amber (`#F59E0B`), and Emergency Crimson (`#EF4444`).
- **Typography & Components**: High-contrast typography hierarchy, compact segmented control pills, tactile action buttons, and a custom translucent bottom navigation bar with active rounded pill indicators.

---

## Build & Test

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
