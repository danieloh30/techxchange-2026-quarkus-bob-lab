# Exercise 6 — Human-in-the-Loop + Observability

**Timebox:** 10 minutes  
**Persona:** Alex — Compliance officer  
**Story:** No autonomous disposition of high-value vehicles (Mercedes-Benz, BMW, Audi in the seed data). Every LLM decision must be traceable. This exercise wires the approval gate and turns on OpenTelemetry tracing.  
**Projects:**
- HITL solution: [`exercises/06-hitl-observability/solution`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/tree/main/exercises/06-hitl-observability/solution) ← [upstream section-2/step-05](https://quarkus.io/quarkus-workshop-langchain4j/section-2/step-05/)
- Observability reference (chatbot + LGTM): [`exercises/06-hitl-observability/observability-reference`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/tree/main/exercises/06-hitl-observability/observability-reference) ← [upstream section-1/step-10](https://quarkus.io/quarkus-workshop-langchain4j/section-1/step-10/)

![Observability Dev UI](../images/dev-ui-observability.png)

---

## Start

```bash
cd exercises/06-hitl-observability/solution
./mvnw quarkus:dev
```

Quarkus **Observability Dev Services** (LGTM stack) auto-starts alongside the PostgreSQL container. Wait for both:
```
DevServices for Observability started — Grafana: http://localhost:3000
car-management ... started in ~4s
```

---

## HITL flow

The compliance rule: **Cars with `HIGH_VALUE` flag or estimated market value > $15,000 require human approval before any disposition action.**

The seeded high-value cars are IDs 1–3 (Mercedes-Benz C-Class, BMW X5, Audi Q4).

```
Return a high-value car with severe damage
  │
  ▼
DispositionProposalAgent
  │  proposed_action = SCRAP
  │  rationale = "airbags deployed, chassis bent, repair cost exceeds value"
  │
  ▼
@HumanInTheLoop gate      ← UI shows "Awaiting approval"
  │
  ├── APPROVED → execute: status → PENDING_DISPOSITION
  └── REJECTED → fallback: status → IN_MAINTENANCE (reassessment path)
```

**To trigger:** Return Car **#1** (Mercedes-Benz C-Class) with:
```text
Major collision damage, frame is bent, airbags deployed,
estimated repair cost exceeds the car's market value
```

In the UI you will see an **"Awaiting Approval"** state. Approve once → observe `PENDING_DISPOSITION`. Reject once → observe `IN_MAINTENANCE`.

---

## OpenTelemetry configuration

The four tracing properties available in this project (confirmed from live config):

```properties
# In application.properties (RUNTIME properties — no rebuild needed to toggle)
quarkus.langchain4j.tracing.include-prompt=true          # exports full prompt text as span attribute
quarkus.langchain4j.tracing.include-completion=true      # exports full LLM response
quarkus.langchain4j.tracing.include-tool-arguments=true  # exports tool call arguments
quarkus.langchain4j.tracing.include-tool-result=true     # exports tool return value
```

> ⚠️ **Production warning:** `include-prompt=true` exports full `@UserMessage` content — which includes car condition text derived from customer feedback. This can contain PII. Disable or redact before production deployment.

The Quarkus OTel extension auto-instruments REST endpoints, JDBC, and CDI beans. LangChain4j adds `gen_ai.*` span attributes to every LLM call.

---

## Reading spans in Grafana

Open **http://localhost:3000** → Explore → Tempo → Search for service `car-management`.

Key span attributes to understand:

| OTel attribute | FinOps / compliance use |
|----------------|------------------------|
| `gen_ai.usage.input_tokens` | Tokens consumed per LLM request — main cost input |
| `gen_ai.usage.output_tokens` | Generated tokens — often 2–3× more expensive per unit |
| `gen_ai.request.model` | Which model was used — cost tier varies |
| `langchain4j.tool.name` | Which `@Tool` the LLM invoked |
| `langchain4j.tool.arguments` | Tool arguments (when `include-tool-arguments=true`) |
| `http.route` | Which REST endpoint triggered the agent chain |
| `duration` | End-to-end latency including all LLM round-trips |

**FinOps calculation example:**  
At 500 returns/day × avg 1,500 input tokens × `gpt-4o` pricing: ~$15/day. A bloated `@UserMessage` (no AGENTS.md discipline) can easily double input tokens → $30/day. Tracing makes this visible and actionable.

---

## Running the observability-reference project (optional)

```bash
cd exercises/06-hitl-observability/observability-reference
./mvnw quarkus:dev -Dquarkus.http.port=8082
```

This is a simpler chatbot + LGTM reference that shows `gen_ai` spans without the full supervisor workflow — useful for understanding the baseline tracing shape before the more complex workflows.

---

## Done when

- [ ] HITL gate blocked a disposition on a high-value car
- [ ] HITL gate allowed a disposition on the same car after approval
- [ ] At least one `gen_ai.usage.input_tokens` span visible in Grafana/Tempo
- [ ] You can explain `include-prompt=true` trade-off: compliance value vs PII risk
