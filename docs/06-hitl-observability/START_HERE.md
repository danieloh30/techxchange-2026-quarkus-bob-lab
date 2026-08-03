# Exercise 6 — Human Gate + Tracing

<span class="badge badge--run-read">Run + Read</span>

**Timebox:** 10 minutes  
**Persona:** Alex — Compliance officer  
**You work in:** `exercises/06-hitl-observability/solution` (run + read)

!!! tip "Solution"
    [`exercises/06-hitl-observability/solution`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/tree/main/exercises/06-hitl-observability/solution){:target="_blank"}

---

## The goal

The compliance rule: **no autonomous escalation of P1/P2 incidents on revenue-critical systems**.  
Without a gate, the supervisor you built in Exercise 4 would ESCALATE a P1 payment-gateway outage based purely on LLM reasoning.  
With `@HumanInTheLoop`, the system proposes and pauses — a human approves or rejects. And with OpenTelemetry tracing, every LLM call, tool invocation, and approval decision is auditable.

This exercise introduces two concepts: **`EscalationProposalAgent`** (an LLM agent that creates escalation proposals) and **`@HumanInTheLoop`** (a gate that pauses the workflow until a human decides).

```mermaid
%%{init: {'look':'handDrawn','theme':'neutral','themeVariables': {'lineColor':'#4A4035'}}}%%
flowchart TD
    ISA(["IncidentSupervisorAgent<br/>escalation required"])
    EPA(["EscalationProposalAgent<br/>proposed action ESCALATE_P1"])
    HITL{"HumanInTheLoop gate<br/>Awaiting Approval"}
    ESC([ESCALATED])
    INV(["IN_PROGRESS<br/>reassessment"])

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

## Step 1 — Start and read the HITL agents (3 min)

Stop your `lab/` Quarkus process first (`Ctrl+C`), then start the solution:

```bash
cd exercises/06-hitl-observability/solution
./mvnw quarkus:dev
```

Wait for the LGTM stack:
```
DevServices for Observability started — Grafana: http://localhost:3000
incident-management started in ~4s
```

!!! tip "Agentic Dev UI"
    Open the [Agentic Dev UI](http://localhost:8080/q/dev-ui/quarkus-langchain4j-agentic/agents){:target="_blank"} to see the full agent graph. Notice `EscalationProposalAgent` and `HumanApprovalAgent` — the HITL gate shows up as a distinct agent type in the wiring view.

    Check the [topology](http://localhost:8080/q/dev-ui/quarkus-langchain4j-agentic/topology){:target="_blank"} — compare it to Exercise 4. The tree now includes the HITL agents.

Open [`EscalationProposalAgent.java`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/blob/main/exercises/06-hitl-observability/solution/src/main/java/com/incidentmanagement/agentic/agents/EscalationProposalAgent.java){:target="_blank"} — this is the same `@Agent` pattern you've been coding, with a `@SystemMessage` that defines escalation criteria and a `@UserMessage` template.

Open [`HumanApprovalAgent.java`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/blob/main/exercises/06-hitl-observability/solution/src/main/java/com/incidentmanagement/agentic/agents/HumanApprovalAgent.java){:target="_blank"} — this is the HITL gate. Key differences from a regular `@Agent`:

| Annotation | Purpose |
|-----------|---------|
| `@HumanInTheLoop` | Marks this agent as a human gate — the framework pauses the workflow here |
| `static` method | Not an LLM call — it's a blocking Java method that waits for human input via `CompletableFuture` |

??? info "Why a separate proposal agent?"
    `EscalationProposalAgent` creates a structured proposal — the **what** and **why** of the escalation. `HumanApprovalAgent` is the **gate** — it pauses the workflow, presents the proposal to a human via the UI, and blocks until they decide. Separating proposal from approval means you can change escalation criteria (edit `@SystemMessage`) without touching the approval infrastructure.

---

## Step 2 — Test the HITL gate (3 min)

Open **[http://localhost:8080](http://localhost:8080){:target="_blank"}**, click **View** on Incident **#1** (payment-gateway/checkout-api, P2), and process with:

```text
Complete checkout failure, all transactions failing, revenue loss confirmed at $50k/hr
```

**How to confirm:** The UI shows an **"Awaiting Approval"** modal with two buttons. This is the HITL gate — the system has paused and is waiting for a human decision.

- Click **Escalate to Management** → check that the UI status changes to `ESCALATED`
- Now press `s` in the Quarkus terminal to restart (reset the database). Process Incident **#1** again with the same report → this time click **Keep at Team Level** → check that the UI status changes to `IN_PROGRESS`

In the Quarkus terminal logs, look for `WORKFLOW PAUSED` and `WORKFLOW RESUMED` showing the approval decision.

<img src="../../images/hitl-approval-modal.png" alt="Human-in-the-loop approval modal" style="width:100%;max-width:960px;display:block;margin:1rem auto;border-radius:8px;box-shadow:0 2px 12px rgba(0,0,0,0.15);">

---

## Step 3 — Read OTel spans in Grafana (4 min)

Open **[http://localhost:3000](http://localhost:3000){:target="_blank"}** and navigate to traces:

1. Click the **hamburger menu** (&#9776;) in the top-left → **Explore**
2. In the data source dropdown (top-left of the Explore panel), select **Tempo**
3. Switch the **Query type** tab from "TraceQL" to **Search**
4. In the **Service Name** dropdown, select **incident-management**
5. Click **Run query** (blue button, top-right) — you should see a list of traces

Click any **Trace ID** link to expand the span waterfall. Look for the `POST /incident-management/process/{id}` trace — it contains the full agent workflow.

<img src="../../images/grafana-dashboard.png" alt="Grafana Tempo trace view" style="width:100%;max-width:960px;display:block;margin:1rem auto;border-radius:8px;box-shadow:0 2px 12px rgba(0,0,0,0.15);">

Inside the span detail, find:

| Attribute | What to look for |
|-----------|-----------------|
| gen_ai.usage.input_tokens | How many tokens each LLM call consumed |
| gen_ai.usage.output_tokens | Generated tokens |
| langchain4j.tool.name | Which tool the LLM invoked |
| duration | End-to-end latency including all LLM round-trips |

!!! warning "Production caution"
    `include-prompt=true` exports full prompt text to your tracing backend. This can include PII from `@UserMessage` templates. Disable or redact before production.

**FinOps thought experiment:** 500 incidents/day × avg 1,500 input tokens × gpt-4o pricing = ~$15/day.  
An unbounded `@UserMessage` without AGENTS.md discipline can double input tokens → $30/day.  
Tracing is how you catch that before the bill arrives.

Stop the solution (`Ctrl+C`) and restart `lab/`:

```bash
cd ../../../lab
./mvnw quarkus:dev
```

---

<div class="done-when" markdown>

## :material-check-circle: Done when

- [ ] HITL gate blocked escalation on a critical incident
- [ ] HITL gate allowed escalation after approval
- [ ] You can explain `@HumanInTheLoop` vs regular `@Agent` — what makes the workflow pause
- [ ] At least one `gen_ai.usage.input_tokens` span found in Grafana/Tempo
- [ ] You can explain `include-prompt=true` trade-off (compliance value vs PII risk)

</div>
