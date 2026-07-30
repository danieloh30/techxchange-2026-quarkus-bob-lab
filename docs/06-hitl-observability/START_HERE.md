# Exercise 6 — Human-in-the-Loop + Observability

<span class="badge badge--run-read">Run + Read</span>

**Timebox:** 10 minutes  
**Persona:** Alex — Compliance officer  
**You work in:** `exercises/06-hitl-observability/solution` (run + read — HITL is pre-built)  
**Learn by:** reading `EscalationProposalAgent` + `HumanApprovalAgent`, running the UI, reading OTel spans

!!! tip "Reference solution"
    [`exercises/06-hitl-observability/solution`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/tree/main/exercises/06-hitl-observability/solution)

---

## Why HITL is non-negotiable in enterprise

The compliance rule: **no autonomous escalation of P1/P2 incidents on revenue-critical systems**.  
Without a gate, the supervisor you built in Exercise 4 would ESCALATE a P1 payment-gateway outage based purely on LLM reasoning.  
With `@HumanInTheLoop`, the system proposes and pauses — a human approves or rejects.

---

## Start

Stop your `lab/` Quarkus process first (`Ctrl+C`), then:

```bash
cd exercises/06-hitl-observability/solution
./mvnw quarkus:dev
```

Wait for the LGTM stack:
```
DevServices for Observability started — Grafana: http://localhost:3000
incident-management started in ~4s
```

---

## Read the HITL pattern (3 min)

!!! tip "Agentic Dev UI"
    Open **http://localhost:8080/q/dev-ui/quarkus-langchain4j-agentic/agents** to see the full agent graph for this exercise. Notice `EscalationProposalAgent` and `HumanApprovalAgent` — the HITL gate shows up as a distinct agent type in the wiring view.

Open [`EscalationProposalAgent.java`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/blob/main/exercises/06-hitl-observability/solution/src/main/java/com/incidentmanagement/agentic/agents/EscalationProposalAgent.java) and [`HumanApprovalAgent.java`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/blob/main/exercises/06-hitl-observability/solution/src/main/java/com/incidentmanagement/agentic/agents/HumanApprovalAgent.java).

```mermaid
%%{init: {'look':'handDrawn','theme':'neutral','themeVariables': {'lineColor':'#4A4035'}}}%%
flowchart TD
    ISA([IncidentSupervisorAgent<br/>escalation required])
    EPA([EscalationProposalAgent<br/>proposed_action = ESCALATE_P1])
    HITL{@HumanInTheLoop gate<br/>UI: Awaiting Approval}
    ESC([ESCALATED])
    INV([IN_PROGRESS<br/>reassessment])

    ISA --> EPA --> HITL
    HITL -->|Escalate to Management| ESC
    HITL -->|Keep at Team Level| INV

    style ISA fill:#FFE4CC,stroke:#B87333
    style EPA fill:#D8F0D8,stroke:#3D7A3D
    style HITL fill:#FFF8DC,stroke:#C4A000
    style ESC fill:#D8F0D8,stroke:#3D7A3D
    style INV fill:#D4E6F1,stroke:#2E6B8A
```

---

## Test the HITL gate (3 min)

Process Incident **#1** (payment-gateway/checkout-api, P2) with:
```text
Complete checkout failure, all transactions failing, revenue loss confirmed at $50k/hr
```

UI shows **"Awaiting Approval"**:
- **Escalate to Management** → status becomes `ESCALATED`
- Repeat with same incident (reset DB or use Incident #3) → **Keep at Team Level** → status becomes `IN_PROGRESS`

<img src="../../images/hitl-approval-modal.png" alt="Human-in-the-loop approval modal" style="width:100%;max-width:960px;display:block;margin:1rem auto;border-radius:8px;box-shadow:0 2px 12px rgba(0,0,0,0.15);">

---

## Read OTel spans in Grafana (3 min)

Open **http://localhost:3000** → Explore → Tempo → service `incident-management`.

<img src="../../images/grafana-dashboard.png" alt="Grafana Tempo trace view" style="width:100%;max-width:960px;display:block;margin:1rem auto;border-radius:8px;box-shadow:0 2px 12px rgba(0,0,0,0.15);">

Find spans and read:

| Attribute | What to look for |
|-----------|-----------------|
| `gen_ai.usage.input_tokens` | How many tokens each LLM call consumed |
| `gen_ai.usage.output_tokens` | Generated tokens |
| `langchain4j.tool.name` | Which tool the LLM invoked |
| `duration` | End-to-end latency including all LLM round-trips |

!!! warning "Production caution"
    `include-prompt=true` exports full prompt text to your tracing backend. This can include PII from `@UserMessage` templates. Disable or redact before production.

**FinOps thought experiment:** 500 incidents/day × avg 1,500 input tokens × gpt-4o pricing = ~$15/day.  
An unbounded `@UserMessage` without AGENTS.md discipline can double input tokens → $30/day.  
Tracing is how you catch that before the bill arrives.

---

<div class="done-when" markdown>

## :material-check-circle: Done when

- [ ] HITL gate blocked escalation on a critical incident
- [ ] HITL gate allowed escalation after approval
- [ ] At least one `gen_ai.usage.input_tokens` span found in Grafana/Tempo
- [ ] You can explain `include-prompt=true` trade-off (compliance value vs PII risk)

</div>
