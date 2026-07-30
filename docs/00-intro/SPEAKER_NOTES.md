# Lab Overview

## The scenario: Apex Systems

**Apex Systems** is a mid-size enterprise IT services company managing infrastructure across data centers and cloud regions. The NOC (Network Operations Center) receives free-text incident reports from monitoring tools, tickets, and on-call engineers — but today those reports live in chat threads, email chains, and tribal knowledge.

Last quarter's post-mortem told the story:

- Three P1 outages lasted **4+ hours due to P3 misclassification** — $200k revenue loss
- Two incidents were **escalated to the wrong engineering team**, wasting 40 engineer-hours
- Service managers cannot see *why* an incident was routed or whether escalation was appropriate

Leadership mandate: **automate incident triage and routing with AI agents — without losing enterprise control.**

---

## Why agentic AI (not "just a chatbot")

| Chatbot | Agent |
|---------|-------|
| Answers questions | Takes actions |
| One LLM call | Multi-step reasoning + tool calls |
| No side effects | Mutates state (e.g., `IncidentStatus → TRIAGING`) |
| Stateless | Shares context via `AgenticScope` across workflow |
| Single model | Composed specialists (triage, diagnostic, impact) |

Apex Systems needs systems that **reason** over messy natural-language incident reports, **act** by calling enterprise tools, **collaborate** across specialized roles, **pause** for humans on high-stakes outcomes, and **scale** across teams via open protocols.

---

## What you will build

A production-shaped **agentic incident management platform** on IBM Enterprise Build of Quarkus — from a single agent to a full multi-agent supervisor pipeline with human oversight and distributed services.

```mermaid
%%{init: {'look':'handDrawn','theme':'neutral','themeVariables': {'lineColor':'#4A4035'}}}%%
flowchart TD
    IR([Incident Report]) --> IPW

    subgraph main["Apex Systems Incident Management · Quarkus :8080"]
        IPW([IncidentProcessingWorkflow<br/>@SequenceAgent])
        IPW --> IAW([IncidentAnalysisWorkflow<br/>@ParallelMapperAgent × 3])
        IPW --> ISA([IncidentSupervisorAgent<br/>@SupervisorAgent])
        IPW --> RA([ResolutionAgent])

        ISA --> TA([TriageAgent<br/>@ToolBox])
        ISA --> DA([DiagnosticAgent])
        ISA --> EA([EscalationAgent<br/>+ HITL gate])
        ISA --> IA([ImpactAgent])
    end

    IA -->|A2A| RIA([":8888 — Remote<br/>Impact Assessment"])

    style IR fill:#E8DCC4,stroke:#6B5B45
    style IPW fill:#D4E6F1,stroke:#2E6B8A
    style IAW fill:#D4E6F1,stroke:#2E6B8A
    style ISA fill:#FFE4CC,stroke:#B87333
    style RA fill:#D8F0D8,stroke:#3D7A3D
    style TA fill:#FFF8DC,stroke:#C4A000
    style DA fill:#D8F0D8,stroke:#3D7A3D
    style EA fill:#D8F0D8,stroke:#3D7A3D
    style IA fill:#FFF8DC,stroke:#C4A000
    style RIA fill:#F5D0D0,stroke:#A04040
    style main fill:#F5F5F0,stroke:#8B8070
```

---

## Your learning path

Each exercise adds a new capability, guided by a persona facing a real IT operations problem:

| Exercise | Persona | Problem | Pattern you learn |
|----------|---------|---------|-------------------|
| **1** | **Sam** — NOC analyst | Free-text reports pile up; triage is manual | `@Agent` + `@ToolBox` — your first agent |
| **2** | **Chris** — Ops lead | Diagnostic decisions need policy, not code | `@SystemMessage` as policy declaration |
| **3** | **Chris** — Ops lead | Three analyses must run concurrently | `@ParallelMapperAgent` + `@Output` |
| **4** | **Priya** — IT service mgr | Critical incidents need adaptive escalation | `@SupervisorAgent` orchestration |
| **5** | **Jordan** — Platform engineer | Must ship governed code; copilots hallucinate | **IBM Bob** + `AGENTS.md` |
| **6** | **Alex** — Compliance officer | P1 incidents need approval + audit trail | **HITL** + OpenTelemetry |
| **7** | **Riley** — SRE lead | Impact assessment is a separate team | **A2A** remote impact agent |

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

## The seeded incidents

When you start the app in Exercise 1, you'll see 8 incidents in the Incident Dashboard:

| Incident # | System | Service | Priority | Initial Status |
|------------|--------|---------|----------|----------------|
| 1 | payment-gateway | checkout-api | P2 | OPEN |
| 2 | auth-service | user-login | P1 | IN_PROGRESS |
| 3 | inventory-db | stock-sync | P3 | OPEN |
| 4 | cdn-edge | static-assets | P4 | TRIAGING |
| 5 | email-service | notification-api | P2 | OPEN |
| 6 | search-engine | product-search | P3 | OPEN |
| 7 | monitoring | alerting-api | P2 | OPEN |
| 8 | api-gateway | rate-limiter | P1 | IN_PROGRESS |

You'll use these incidents throughout the exercises — processing them with different reports to trigger different agent behaviors.

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
