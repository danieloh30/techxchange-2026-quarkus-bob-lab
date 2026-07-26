# Exercise 6 — Human-in-the-Loop + Observability

**Timebox:** 10 minutes  
**Story:** Alex requires approval for dispositions on cars worth more than $15,000 — and an audit trail.  
**Projects:**
- HITL solution: [`exercises/06-hitl-observability/solution`](../../exercises/06-hitl-observability/solution) ← [upstream section-2/step-05](https://quarkus.io/quarkus-workshop-langchain4j/section-2/step-05/)
- Observability reference (chatbot + LGTM): [`exercises/06-hitl-observability/observability-reference`](../../exercises/06-hitl-observability/observability-reference) ← [upstream section-1/step-10](https://quarkus.io/quarkus-workshop-langchain4j/section-1/step-10/)

![Observability Dev UI](../images/dev-ui-observability.png)

## Start (HITL)

```bash
cd exercises/06-hitl-observability/solution
./mvnw quarkus:dev
```

## Do

1. Trigger a high-value severe-damage return (use seeded expensive cars).
2. Approve once, reject once — observe different outcomes.
3. Optionally run `observability-reference` to explore Grafana/LGTM Dev Services and `gen_ai` / LangChain4j spans.
4. Note prompt/completion tracing settings discussed in the lab guide and upstream step-10 docs.

## Done when

- [ ] HITL gate blocked or allowed a disposition
- [ ] You can explain what OpenTelemetry gives compliance & FinOps for LLM calls
