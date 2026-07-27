# Exercise 3 — Supervisor Pattern

**Timebox:** 10 minutes  
**Persona:** Priya — Fleet manager  
**Story:** Severe damage needs adaptive disposition. A hardcoded `if/else` tree can't decide whether to price, scrap, sell, or donate a crushed BMW — a supervisor agent can.  
**Solution project:** [`exercises/03-supervisor/solution`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/tree/main/exercises/03-supervisor/solution)  
**Upstream:** [section-2/step-04](https://quarkus.io/quarkus-workshop-langchain4j/section-2/step-04/)

---

## Start

```bash
cd exercises/03-supervisor/solution
./mvnw quarkus:dev
```

---

## Supervisor vs conditional routing

| | `@ConditionalAgent` | `@SupervisorAgent` |
|--|--------------------|--------------------|
| Decision logic | Hardcoded predicates | LLM reasoning at runtime |
| Flexibility | Requires code change + redeploy | Change `@SupervisorRequest` prompt |
| Predictability | Deterministic — easy to unit test | Must trace LLM decisions (OTel) |
| Best for | Known, stable business rules | Multi-factor, evolving policy |
| Risk | Brittleness as rules multiply | Prompt drift — needs careful `@SupervisorRequest` |

---

## Architecture of this exercise

```
POST /car-management/return/{carNumber}
  │
  ├─► FeedbackAnalysisWorkflow
  │       @ParallelMapperAgent × [CLEANING, MAINTENANCE, DISPOSITION]
  │       @Output → FeedbackAnalysisResults
  │
  ├─► FleetSupervisorAgent (@SupervisorAgent)
  │       @SupervisorRequest builds prompt from FeedbackAnalysisResults:
  │           "Disposition required? YES.
  │            Step 1: PricingAgent → get value.
  │            Step 2: DispositionAgent → decide SCRAP/SELL/DONATE/KEEP.
  │            Step 3: if KEEP → invoke Cleaning/Maintenance as needed."
  │       Sub-agents resolved by LLM:
  │           PricingAgent    (always first when disposition=YES)
  │           DispositionAgent
  │           CleaningAgent / MaintenanceAgent (conditional)
  │
  └─► CarConditionFeedbackAgent
          Sets final CarStatus (PENDING_DISPOSITION, IN_MAINTENANCE, AT_CLEANING, AVAILABLE)
```

---

## `@SupervisorRequest` deep-dive

Open [`FleetSupervisorAgent.java`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/blob/main/exercises/03-supervisor/solution/src/main/java/com/carmanagement/agentic/agents/FleetSupervisorAgent.java).

```java
@SupervisorRequest
static String request(CarInfo carInfo, Integer carNumber,
                      FeedbackAnalysisResults feedbackAnalysisResults) {
    boolean dispositionRequired = feedbackAnalysisResults.dispositionAnalysis()
            .toUpperCase().contains("DISPOSITION_REQUIRED");
    // Returns either noDispositionMessage or dispositionMessage string
}
```

Key points to discuss:
1. **Static factory** — `@SupervisorRequest` runs *before* the LLM is invoked. It shapes the system prompt.
2. **Policy as prose** — the `dispositionMessage` string is a numbered instruction list. Changing policy = editing this string, not redeploying Java logic.
3. **Negative instructions matter** — `"DO NOT invoke PricingAgent"` is as important as `"invoke PricingAgent"`. Without explicit exclusions, the LLM may call unnecessary sub-agents.

---

## Do

### Test 1 — Severe damage → disposition path

Return Car **#1** (Mercedes-Benz C-Class, RENTED) with:
```text
Front end crushed after collision; airbags deployed; not driveable
```

Watch logs for this sequence:
1. `FeedbackAnalysisWorkflow` — three parallel tasks complete
2. `[FleetSupervisorAgent]` — supervisor invokes `PricingAgent`
3. `[FleetSupervisorAgent]` — supervisor invokes `DispositionAgent` with pricing result
4. Status → `PENDING_DISPOSITION`

### Test 2 — Minor damage → clean-only path

Return Car **#6** (Toyota Corolla, RENTED) with:
```text
Small scratch on rear bumper, otherwise clean
```

Confirm:
- **No** `PricingAgent` invocation
- **No** `DispositionAgent` invocation
- Only `CleaningAgent` (or `CLEANING_NOT_REQUIRED`)
- Status → `AT_CLEANING` or `AVAILABLE`

---

## Done when

- [ ] Supervisor invoked `PricingAgent` + `DispositionAgent` for severe damage
- [ ] Supervisor invoked only `CleaningAgent` for a minor return
- [ ] You can explain `@SupervisorRequest` in one sentence
- [ ] You can state one reason to prefer supervisors over pure conditionals
