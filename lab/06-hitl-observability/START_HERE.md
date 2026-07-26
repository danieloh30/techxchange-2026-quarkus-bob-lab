# Exercise 6 — Human-in-the-Loop + Observability

**Timebox:** 10 minutes  
**Story:** Alex requires approval for dispositions on cars worth more than $15,000 — and an audit trail.

## Start

```bash
cd lab/06-hitl-observability/starter
./mvnw quarkus:dev
```

Upstream HITL: https://quarkus.io/quarkus-workshop-langchain4j/section-2/step-05/  
Observability: https://docs.quarkiverse.io/quarkus-langchain4j/dev/observability.html

## Do

1. Trigger a high-value severe-damage return (use seeded expensive cars).
2. Approve once, reject once — observe different outcomes.
3. Open Grafana / LGTM Dev Services (if enabled) and find LangChain4j / `gen_ai` spans.
4. Note prompt/completion inclusion settings in `application.properties`.

## Done when

- [ ] HITL gate blocked or allowed a disposition
- [ ] You located at least one AI-related trace or metric
