# Lab Overview

## The scenario: Miles of Smiles

**Miles of Smiles** is a mid-size car rental company with hundreds of vehicles across airport and city locations. Every returned car generates free-text feedback from rental desks, cleaning crews, and maintenance bays — but today that feedback lives in sticky notes, chat threads, and tribal knowledge.

Last quarter's incident report told the story:

- Three expensive vehicles were **scrapped without a formal review** — $80k lost
- Two others **sat in AT_CLEANING for 3 days** when a quick wash was all they needed
- Fleet managers cannot see *why* a car moved between statuses or whether it should have been dispositioned

Leadership mandate: **automate fleet decisions with AI agents — without losing enterprise control.**

---

## Why agentic AI (not "just a chatbot")

| Chatbot | Agent |
|---------|-------|
| Answers questions | Takes actions |
| One LLM call | Multi-step reasoning + tool calls |
| No side effects | Mutates state (e.g., `CarStatus → AT_CLEANING`) |
| Stateless | Shares context via `AgenticScope` across workflow |
| Single model | Composed specialists (cleaning, maintenance, pricing) |

Miles of Smiles needs systems that **reason** over messy natural-language feedback, **act** by calling enterprise tools, **collaborate** across specialized roles, **pause** for humans on high-stakes outcomes, and **scale** across teams via open protocols.

---

## What you will build

A production-shaped **agentic fleet platform** on IBM Enterprise Build of Quarkus — from a single agent to a full multi-agent supervisor pipeline with human oversight and distributed services.

```
                    ┌──────────────────────────────────────────────┐
                    │      Miles of Smiles Car Management          │
                    │          (Quarkus · port 8080)               │
                    │                                              │
  Car return ──►   │  CarProcessingWorkflow (@SequenceAgent)      │
                    │    │                                         │
                    │    ├─► FeedbackAnalysisWorkflow              │
                    │    │        (@ParallelMapperAgent × 3 tasks) │
                    │    │                                         │
                    │    ├─► FleetSupervisorAgent (@SupervisorAgent)│
                    │    │        ├─ CleaningAgent   (@ToolBox)    │
                    │    │        ├─ MaintenanceAgent              │
                    │    │        ├─ DispositionAgent + HITL gate  │
                    │    │        └─ PricingAgent ──A2A──► :8888  │
                    │    │                                         │
                    │    └─► CarConditionFeedbackAgent             │
                    └──────────────────────────────────────────────┘
```

---

## Your learning path

Each exercise adds a new capability, guided by a persona facing a real fleet operations problem:

| Exercise | Persona | Problem | Pattern you learn |
|----------|---------|---------|-------------------|
| **1** | **Maya** — Rental desk | Free-text returns pile up; status is manual | `@Agent` + `@ToolBox` — your first agent |
| **2** | **Chris** — Ops lead | Maintenance decisions need policy, not code | `@SystemMessage` as policy declaration |
| **3** | **Chris** — Ops lead | Three analyses must run concurrently | `@ParallelMapperAgent` + `@Output` |
| **4** | **Priya** — Fleet manager | Severe damage needs adaptive disposition | `@SupervisorAgent` orchestration |
| **5** | **Jordan** — Platform engineer | Must ship governed code; copilots hallucinate | **IBM Bob** + `AGENTS.md` |
| **6** | **Alex** — Compliance officer | High-value cars need approval + audit trail | **HITL** + OpenTelemetry |
| **7** | **Riley** — Pricing team | Valuation is a separate service/team | **A2A** remote pricing agent |

**Exercises 1–4** are hands-on code-along — you type agent code into stub files with hot reload.  
**Exercise 5** uses IBM Bob to govern and validate the system you built.  
**Exercises 6–7** run pre-built solutions to explore production patterns.

---

## The IBM stack

| Layer | Component | Role |
|-------|-----------|------|
| Runtime | IBM Enterprise Build of Quarkus 3.37.4 | Build-time agent validation, fast startup |
| AI extension | Quarkus LangChain4j 1.12.0 | Declarative agents, workflows, A2A |
| Dev tooling | IBM Bob | SDLC partner: plan → code → test → secure |
| Context efficiency | `AGENTS.md` | Targeted Bob context — avoids token-bloat scans |

---

## Learning outcomes

After this lab you will be able to:

- Declare agents with `@Agent`, `@SystemMessage`, `@ToolBox` and explain what Quarkus generates at build time
- Compose multi-agent workflows: sequence, parallel, and supervisor orchestration
- Use `@SupervisorAgent` for adaptive AI routing vs hardcoded conditional logic
- Author an `AGENTS.md` to make IBM Bob cost-efficient on agentic projects
- Distribute agents across services with **A2A** (`@A2AClientAgent`)
- Add **human-in-the-loop** gates and read **OpenTelemetry** spans for compliance and FinOps

---

## The seeded fleet

When you start the app in Exercise 1, you'll see 8 cars in the Fleet Status UI:

| Car # | Make | Model | Year | Initial Status |
|-------|------|-------|------|----------------|
| 1 | Mercedes-Benz | C-Class | 2024 | RENTED |
| 2 | BMW | X5 | 2025 | IN_MAINTENANCE |
| 3 | Audi | Q4 | 2025 | RENTED |
| 4 | Nissan | Altima | 2018 | AT_CLEANING |
| 5 | Ford | Focus | 2014 | RENTED |
| 6 | Toyota | Corolla | 2023 | RENTED |
| 7 | Honda | Civic | 2022 | RENTED |
| 8 | Ford | F-150 | 2024 | IN_MAINTENANCE |

You'll use these cars throughout the exercises — returning them with different feedback to trigger different agent behaviors.

---

## Patterns cheat sheet

Keep this handy as you work through the exercises:

```
Goal: step-by-step pipeline     → @SequenceAgent
Goal: concurrent work           → @ParallelMapperAgent
Goal: data-driven branching     → @ConditionalAgent
Goal: refine until good enough  → @LoopAgent
Goal: adaptive multi-agent      → @SupervisorAgent
Goal: delegate to remote agent  → A2A + @A2AClientAgent
Goal: human approval            → @HumanInTheLoop
```
