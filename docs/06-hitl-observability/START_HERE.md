# Exercise 6 — Human-in-the-Loop + Observability

**Timebox:** 10 minutes  
**Persona:** Alex — Compliance officer  
**You work in:** `exercises/06-hitl-observability/solution` (read + run — HITL is pre-built)  
**Learn by:** reading `DispositionProposalAgent` + `HumanApprovalAgent`, running the UI, reading OTel spans

> 💡 **Reference solution:** [`exercises/06-hitl-observability/solution`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/tree/main/exercises/06-hitl-observability/solution)

---

## Why HITL is non-negotiable in enterprise

The compliance rule: **no autonomous disposition of vehicles worth > $15,000**.  
Without a gate, the supervisor would SCRAP a $42,000 BMW based purely on LLM reasoning.  
With `@HumanInTheLoop`, the system proposes and pauses — a human approves or rejects.

---

## Start

```bash
cd exercises/06-hitl-observability/solution
./mvnw quarkus:dev
```

Wait for the LGTM stack:
```
DevServices for Observability started — Grafana: http://localhost:3000
car-management started in ~4s
```

---

## Read the HITL pattern (3 min)

Open [`DispositionProposalAgent.java`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/blob/main/exercises/06-hitl-observability/solution/src/main/java/com/carmanagement/agentic/agents/DispositionProposalAgent.java) and [`HumanApprovalAgent.java`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/blob/main/exercises/06-hitl-observability/solution/src/main/java/com/carmanagement/agentic/agents/HumanApprovalAgent.java).

```
FleetSupervisorAgent
  │ (disposition required)
  ▼
DispositionProposalAgent  → proposed_action=SCRAP, rationale="airbags..."
  │
  ▼
@HumanInTheLoop gate      ← UI: "Awaiting Approval"
  │
  ├── APPROVED → status = PENDING_DISPOSITION
  └── REJECTED → status = IN_MAINTENANCE (reassessment)
```

Ask Bob (with AGENTS.md loaded):
```text
What would I add to my lab/AGENTS.md agents table to document HumanApprovalAgent?
What is its outputKey and what does it prevent automated disposition of?
```

---

## Test the HITL gate (3 min)

Return Car **#1** (Mercedes-Benz C-Class) with:
```text
Major collision damage, airbags deployed, frame is bent, repair cost exceeds value
```

UI shows **"Awaiting Approval"**:
- **Approve** → status becomes `PENDING_DISPOSITION`
- Repeat with same car (reset DB or use Car #3 Audi) → **Reject** → status becomes `IN_MAINTENANCE`

---

## Read OTel spans in Grafana (3 min)

Open **http://localhost:3000** → Explore → Tempo → service `car-management`.

Enable prompt tracing first (uncomment in `application.properties`):
```properties
quarkus.langchain4j.tracing.include-prompt=true
quarkus.langchain4j.tracing.include-tool-arguments=true
```

Find spans and read:

| Attribute | What to look for |
|-----------|-----------------|
| `gen_ai.usage.input_tokens` | How many tokens each LLM call consumed |
| `gen_ai.usage.output_tokens` | Generated tokens (≈ 2–3× cost of input) |
| `langchain4j.tool.name` | Which tool the LLM invoked |
| `duration` | End-to-end latency including all LLM round-trips |

**FinOps thought experiment:** 500 car returns/day × avg 1,500 input tokens × gpt-4o pricing = ~$15/day.  
An unbounded `@UserMessage` without AGENTS.md discipline can double input tokens → $30/day.  
Tracing is how you catch that before the bill arrives.

---

## Bob stretch: add HITL pattern to lab/ (optional)

```text
Based on DispositionProposalAgent and HumanApprovalAgent in the HITL solution,
propose a plan to add HITL approval to my lab/ project for cars worth > $15,000.
List only the files to create/modify and the AGENTS.md table update needed.
Do NOT write code — plan only.
```

---

## Done when

- [ ] HITL gate blocked disposition on a high-value car
- [ ] HITL gate allowed disposition after approval
- [ ] At least one `gen_ai.usage.input_tokens` span found in Grafana/Tempo
- [ ] You can explain `include-prompt=true` trade-off (compliance value vs PII risk)
