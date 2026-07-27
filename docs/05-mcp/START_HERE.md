# Exercise 5 — Full Multi-Agent System: Supervisor + Workflows

**Timebox:** 15 minutes  
**Personas:** Priya (fleet), Riley (pricing), Maya (cleaning)  
**You work in:** `lab/` (keep Quarkus running)  
**Files to edit:**
- `lab/src/main/java/com/carmanagement/agentic/agents/PricingAgent.java`
- `lab/src/main/java/com/carmanagement/agentic/agents/DispositionAgent.java`
- `lab/src/main/java/com/carmanagement/agentic/agents/CarConditionFeedbackAgent.java`
- `lab/src/main/java/com/carmanagement/agentic/agents/FleetSupervisorAgent.java`
- `lab/src/main/java/com/carmanagement/agentic/workflow/CarProcessingWorkflow.java`

> 💡 **Solution fallback:** [`exercises/04-ibm-bob/solution`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/tree/main/exercises/04-ibm-bob/solution) — open if stuck.

---

## The goal

Complete the full multi-agent pipeline. After this exercise, a single `POST /car-management/return/{id}` call triggers:

```
CarProcessingWorkflow (@SequenceAgent)
  │
  ├─► FeedbackAnalysisWorkflow (@ParallelMapperAgent × 3)  ← already done
  │
  ├─► FleetSupervisorAgent (@SupervisorAgent)
  │       dynamically invokes: PricingAgent → DispositionAgent → Maintenance/Cleaning
  │
  └─► CarConditionFeedbackAgent
          → CarConditions { generalCondition, carAssignment }
          → CarManagementService sets final CarStatus
```

---

## Step 1 — PricingAgent and DispositionAgent (3 min)

```text
Read AGENTS.md.

Implement PricingAgent (lab/.../agents/PricingAgent.java) and
DispositionAgent (lab/.../agents/DispositionAgent.java).
Follow each file's TODO comments.

PricingAgent:
- @SystemMessage: vehicle pricing specialist with brand base values + depreciation table
- @UserMessage: {carMake}, {carModel}, {carYear}, {carCondition}
- @Agent(outputKey="carValue", description="Pricing specialist...")
- Method: String estimateValue(String carMake, String carModel,
                               Integer carYear, String carCondition)

DispositionAgent:
- @SystemMessage: SCRAP/SELL/DONATE/KEEP criteria with decision rules
- @UserMessage: {carMake}, {carModel}, {carYear}, {carNumber}, {carCondition}, {carValue}, {feedback}
- @Agent(outputKey="dispositionAction", description="Car disposition specialist...")
- Method: String processDisposition(String carMake, String carModel, Integer carYear,
                                    Integer carNumber, String carCondition,
                                    String carValue, String feedback)

Wait for approval before applying.
```

---

## Step 2 — CarConditionFeedbackAgent (2 min)

```text
Implement CarConditionFeedbackAgent (lab/.../agents/CarConditionFeedbackAgent.java).
Follow the TODO comments.

Important: the return type is CarConditions (a record), not String.
Quarkus LangChain4j deserializes the LLM's JSON output into CarConditions automatically.

@SystemMessage: JSON output format + routing rules:
  { "generalCondition": "...", "carAssignment": "DISPOSITION|MAINTENANCE|CLEANING|NONE" }
  Rules: DISPOSITION if supervisorDecision mentions SCRAP/SELL/DONATE (not KEEP)
         MAINTENANCE if maintenanceAnalysis != MAINTENANCE_NOT_REQUIRED
         CLEANING if cleaningAnalysis != CLEANING_NOT_REQUIRED
         NONE otherwise

@Agent(outputKey="carConditions")
Method: CarConditions analyzeForCondition(CarInfo carInfo, Integer carNumber,
                                          FeedbackAnalysisResults feedbackAnalysisResults,
                                          String supervisorDecision)

Wait for approval.
```

---

## Step 3 — FleetSupervisorAgent (3 min)

```text
Implement FleetSupervisorAgent (lab/.../agents/FleetSupervisorAgent.java).
Follow the TODO — two members to implement:

4a: @SupervisorAgent method
    @SupervisorAgent(outputKey="supervisorDecision",
                     subAgents={PricingAgent.class, DispositionAgent.class,
                                 MaintenanceAgent.class, CleaningAgent.class})
    String superviseCarProcessing(CarInfo carInfo, Integer carNumber,
                                   FeedbackAnalysisResults feedbackAnalysisResults);

4b: @SupervisorRequest static method
    Build the prompt based on whether dispositionAnalysis contains "DISPOSITION_REQUIRED".
    When YES: Step 1 PricingAgent, Step 2 DispositionAgent, Step 3 conditionally Maintenance/Cleaning.
    When NO: DO NOT invoke PricingAgent or DispositionAgent.

Key: @SupervisorRequest is policy-as-prose. Changing policy = editing this string.
     No if/else in Java — the LLM reads the instructions and decides.

Wait for approval.
```

---

## Step 4 — CarProcessingWorkflow (2 min)

```text
Implement CarProcessingWorkflow (lab/.../workflow/CarProcessingWorkflow.java).
Follow the TODO:

5a: @SequenceAgent method
    @SequenceAgent(outputKey="carProcessingAgentResult",
                   subAgents={FeedbackAnalysisWorkflow.class,
                               FleetSupervisorAgent.class,
                               CarConditionFeedbackAgent.class})
    CarConditions processCarReturn(List<FeedbackTask> tasks, CarInfo carInfo,
                                    Integer carNumber, String feedback);

5b: @Output static method
    @Output
    static CarConditions output(CarConditions carConditions) {
        Log.debug("CarConditions: " + carConditions.generalCondition() + " → " + carConditions.carAssignment());
        return carConditions;
    }

Wait for approval.
```

---

## Step 5 — Test the full pipeline

After hot-reload, try all three paths:

**Path 1 — Clean car:**
Return Car **#6** (Toyota Corolla) with `"Car looks great, no issues"`.  
Expected: `AVAILABLE`, no tool calls, supervisor invokes neither pricing nor disposition.

**Path 2 — Dirty car:**
Return Car **#5** (Ford Focus) with `"Dog hair all over, cabin dirty"`.  
Expected: `AT_CLEANING`, `CleaningTool` called.

**Path 3 — Severe damage (supervisor triggers pricing + disposition):**
Return Car **#1** (Mercedes-Benz C-Class) with:
```text
Front end crushed after collision; airbags deployed; not driveable
```
Expected: `PENDING_DISPOSITION`, logs show `PricingAgent` then `DispositionAgent` invoked.  
Supervisor did NOT invoke `CleaningAgent`.

---

## Supervisor vs conditional routing — discuss

| | `@ConditionalAgent` | `@SupervisorAgent` |
|--|--------------------|--------------------|
| Decision logic | Hardcoded predicates | LLM reasoning at runtime |
| Policy change | Code change + redeploy | Edit `@SupervisorRequest` string |
| When to use | Stable, known rules | Multi-factor, evolving policy |

---

## Done when

- [ ] Full pipeline runs: `POST /car-management/return/{id}` produces correct CarStatus
- [ ] Supervisor chose pricing + disposition for severe damage
- [ ] Supervisor chose cleaning only for dirty car
- [ ] You can draw the full agent chain from CarProcessingWorkflow to CarStatus
