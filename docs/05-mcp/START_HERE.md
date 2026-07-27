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

Complete the full multi-agent pipeline. After this exercise, a single `POST /car-management/return/{id}` triggers:

```
CarProcessingWorkflow (@SequenceAgent)
  │
  ├─► FeedbackAnalysisWorkflow (@ParallelMapperAgent × 3)   ← done in Ex4
  │        └─ FeedbackAnalysisResults { cleaning, maintenance, disposition }
  │
  ├─► FleetSupervisorAgent (@SupervisorAgent)
  │        LLM decides: PricingAgent → DispositionAgent → Maintenance/Cleaning
  │        └─ supervisorDecision (String)
  │
  └─► CarConditionFeedbackAgent (@Agent)
           └─ CarConditions { generalCondition, carAssignment }
                → CarManagementService sets final CarStatus
```

---

## Step 1 — `PricingAgent` (2 min)

Open [`PricingAgent.java`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/blob/main/lab/src/main/java/com/carmanagement/agentic/agents/PricingAgent.java).

Replace the `// TODO` block:

```java
@SystemMessage("""
    You are a vehicle pricing specialist with expertise in market valuations.

    Today is {current_date}. Use this to calculate the current year and vehicle age.

    Use these pricing guidelines:

    Brand Base Values (new current-year models):
    - Luxury brands (Mercedes-Benz, BMW, Audi): $50,000-$70,000
    - Premium trucks (Ford F-150): $45,000-$60,000
    - Mainstream brands (Toyota, Honda, Chevrolet): $28,000-$42,000
    - Economy brands (Nissan): $22,000-$35,000

    Depreciation (calculate age as: current year - vehicle year):
    - Age 1 year: -12% from base value
    - Age 2 years: -15% additional (27% total)
    - Age 3 years: -12% additional (39% total)
    - Age 4 years: -10% additional (49% total)
    - Age 5+ years: -8% per additional year

    Condition Adjustments (apply after depreciation):
    - Excellent/Like new: +5%
    - Good/Recently serviced: No adjustment
    - Fair/Minor issues: -10%
    - Poor/Needs work: -20%

    Format your response as:
    Estimated Value: $XX,XXX
    Justification: [reasoning including vehicle age]
    """)
@UserMessage("""
    Estimate the current market value of this vehicle:
    - Make: {carMake}
    - Model: {carModel}
    - Year: {carYear}
    - Condition: {carCondition}
    """)
@Agent(outputKey = "carValue",
       description = "Pricing specialist that estimates vehicle market value based on make, model, year, and condition")
String estimateValue(String carMake, String carModel, Integer carYear, String carCondition);
```

Add imports:

```java
import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
```

> **`{current_date}` is a built-in Quarkus LangChain4j template variable** — it resolves to today's date automatically. This lets the agent compute vehicle age without hardcoding a year.

---

## Step 2 — `DispositionAgent` (2 min)

Open [`DispositionAgent.java`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/blob/main/lab/src/main/java/com/carmanagement/agentic/agents/DispositionAgent.java).

Replace the `// TODO` block:

```java
@SystemMessage("""
    You are a car disposition specialist for a car rental company.
    Your job is to determine the best disposition action based on the car's value,
    condition, age, and damage.

    Disposition Options:
    - SCRAP: Car is beyond economical repair or has severe safety concerns
    - SELL: Car has value but is aging out of the fleet or has moderate damage
    - DONATE: Car has minimal value but could serve a charitable purpose
    - KEEP: Car is worth keeping in the fleet

    Decision Criteria:
    - If estimated repair cost > 50% of car value: Consider SCRAP or SELL
    - If car is over 5 years old with significant damage: SCRAP
    - If car is 3-5 years old in fair condition: SELL
    - If car has low value (<$5,000) but functional: DONATE
    - If car is valuable and damage is minor: KEEP

    Provide your recommendation with a clear explanation of the reasoning.
    """)
@UserMessage("""
    Determine the disposition for this vehicle:
    - Make: {carMake}
    - Model: {carModel}
    - Year: {carYear}
    - Car Number: {carNumber}
    - Current Condition: {carCondition}
    - Estimated Value: {carValue}
    - Damage/Feedback: {feedback}

    Provide your disposition recommendation (SCRAP/SELL/DONATE/KEEP) and explanation.
    """)
@Agent(outputKey = "dispositionAction",
       description = "Car disposition specialist. Determines how to dispose of a car based on value and condition.")
String processDisposition(String carMake, String carModel, Integer carYear,
                          Integer carNumber, String carCondition,
                          String carValue, String feedback);
```

Add the same three imports as Step 1.

> **Note the `{carValue}` placeholder:** the supervisor passes the `PricingAgent` output (e.g., `"$42,000"`) directly into this `@UserMessage`. `AgenticScope` wires it — no Java code needed to thread values between agents.

---

## Step 3 — `CarConditionFeedbackAgent` (2 min)

Open [`CarConditionFeedbackAgent.java`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/blob/main/lab/src/main/java/com/carmanagement/agentic/agents/CarConditionFeedbackAgent.java).

Replace the `// TODO` block:

```java
@SystemMessage("""
    Analyze car processing results and output a JSON summary.

    Output format:
    {
      "generalCondition": "concise description (max 200 chars)",
      "carAssignment": "DISPOSITION|MAINTENANCE|CLEANING|NONE"
    }

    Rules:
    - Check the ACTUAL DispositionAgent decision in supervisorDecision, not just the analysis
    - If supervisorDecision mentions SCRAP/SELL/DONATE (but NOT KEEP) → DISPOSITION
    - Else if maintenanceAnalysis ≠ "MAINTENANCE_NOT_REQUIRED" → MAINTENANCE
    - Else if cleaningAnalysis ≠ "CLEANING_NOT_REQUIRED" → CLEANING
    - Else → NONE
    - IMPORTANT: If DispositionAgent decided KEEP, do NOT assign DISPOSITION — check maintenance/cleaning instead
    - generalCondition: Summarize the action and reason in plain language
    """)
@UserMessage("""
    Car: {carInfo.year} {carInfo.make} {carInfo.model} (#{carNumber})

    Supervisor Decision: {supervisorDecision}

    Feedback Analysis Results:
    - Disposition: {feedbackAnalysisResults.dispositionAnalysis}
    - Maintenance: {feedbackAnalysisResults.maintenanceAnalysis}
    - Cleaning: {feedbackAnalysisResults.cleaningAnalysis}
    """)
@Agent(description = "Final car condition analyzer. Determines the car's condition and assignment based on all feedback.",
       outputKey = "carConditions")
CarConditions analyzeForCondition(CarInfo carInfo, Integer carNumber,
                                  FeedbackAnalysisResults feedbackAnalysisResults,
                                  String supervisorDecision);
```

Add imports:

```java
import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
```

> **Return type is `CarConditions`, not `String`.**  
> Quarkus LangChain4j deserializes the LLM's JSON output directly into the `CarConditions` record. The `@SystemMessage` instructs the LLM to output well-formed JSON; the framework handles parsing. If parsing fails, an exception is thrown at runtime — which is far better than silently accepting a malformed string downstream.

---

## Step 4 — `FleetSupervisorAgent` (4 min)

Open [`FleetSupervisorAgent.java`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/blob/main/lab/src/main/java/com/carmanagement/agentic/agents/FleetSupervisorAgent.java).

This is the most important agent in the system. It has **two annotated members**.

**4a — `@SupervisorAgent` method** (replace the first `// TODO`):

```java
@SupervisorAgent(
        outputKey = "supervisorDecision",
        subAgents = {
                PricingAgent.class,
                DispositionAgent.class,
                MaintenanceAgent.class,
                CleaningAgent.class
        })
String superviseCarProcessing(CarInfo carInfo, Integer carNumber,
                               FeedbackAnalysisResults feedbackAnalysisResults);
```

**4b — `@SupervisorRequest` static method** (replace the second `// TODO`):

```java
@SupervisorRequest
static String request(CarInfo carInfo, Integer carNumber,
                      FeedbackAnalysisResults feedbackAnalysisResults) {

    boolean dispositionRequired = feedbackAnalysisResults.dispositionAnalysis() != null &&
            feedbackAnalysisResults.dispositionAnalysis().toUpperCase().contains("DISPOSITION_REQUIRED");

    String noDispositionMessage = """
            No disposition has been requested.

            INSTRUCTIONS:
            - DO NOT invoke PricingAgent
            - DO NOT invoke DispositionAgent
            - Only invoke MaintenanceAgent if maintenance needed
            - Only invoke CleaningAgent if cleaning needed
            """;

    String dispositionMessage = """
            The car has to be disposed.

            STEP 1: Invoke PricingAgent to get car value
            STEP 2: Invoke DispositionAgent to decide disposition action (SCRAP/SELL/DONATE/KEEP)
            STEP 3: If DispositionAgent decides KEEP:
                    - Invoke MaintenanceAgent if maintenance needed
                    - Invoke CleaningAgent if cleaning needed

            IMPORTANT: When invoking DispositionAgent:
            - Pass carValue as a STRING with dollar sign (e.g., "$10,710" not 10710)
            - Use the EXACT format from PricingAgent's response

            Follow the decision logic in your system message carefully.
            """;

    return String.format("""
            You are a fleet supervisor for a car rental company. You coordinate action agents based on feedback analysis.

            The feedback has already been analyzed and you have these inputs:
            - cleaningAnalysis: What cleaning is needed (or "CLEANING_NOT_REQUIRED")
            - maintenanceAnalysis: What maintenance is needed (or "MAINTENANCE_NOT_REQUIRED")
            - dispositionAnalysis: Whether severe damage requires disposition (or "DISPOSITION_NOT_REQUIRED")

            Your job is to invoke the appropriate ACTION agents for this car.

            Car: %d %s %s (#%d)
            Current Condition: %s

            Cleaning Analysis: %s
            Maintenance Analysis: %s

            In particular, you have to follow these steps:

            %s
            """,
            carInfo.year, carInfo.make, carInfo.model, carNumber, carInfo.condition,
            feedbackAnalysisResults.cleaningAnalysis(),
            feedbackAnalysisResults.maintenanceAnalysis(),
            dispositionRequired ? dispositionMessage : noDispositionMessage);
}
```

Add imports:

```java
import dev.langchain4j.agentic.declarative.SupervisorAgent;
import dev.langchain4j.agentic.declarative.SupervisorRequest;
```

> **`@SupervisorRequest` is where policy lives.**  
> The boolean `dispositionRequired` is the only Java logic here — it checks whether the upstream `FeedbackAnalysisWorkflow` flagged a disposition case. Everything else is natural-language instructions. To change the supervisor's behavior (add a new sub-agent, change escalation rules), you edit this string. No `if/else` chains, no enum routing tables.
>
> **Why declare sub-agents in `@SupervisorAgent` but not call them explicitly?**  
> The LLM reads the `@SupervisorRequest` prompt and decides which sub-agents to call, in what order, with what parameters. `subAgents` is the capability declaration — it tells the framework what tools to expose. The LLM decides the invocation strategy.

---

## Step 5 — `CarProcessingWorkflow` (2 min)

Open [`CarProcessingWorkflow.java`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/blob/main/lab/src/main/java/com/carmanagement/agentic/workflow/CarProcessingWorkflow.java).

Replace both `// TODO` blocks:

**5a — `@SequenceAgent` method:**

```java
@SequenceAgent(outputKey = "carProcessingAgentResult",
        subAgents = { FeedbackAnalysisWorkflow.class,
                      FleetSupervisorAgent.class,
                      CarConditionFeedbackAgent.class })
CarConditions processCarReturn(List<FeedbackTask> tasks, CarInfo carInfo,
                                Integer carNumber, String feedback);
```

**5b — `@Output` static method:**

```java
@Output
static CarConditions output(CarConditions carConditions) {
    Log.debug("CarConditions: " + carConditions.generalCondition()
              + " → " + carConditions.carAssignment());
    return carConditions;
}
```

Add imports:

```java
import dev.langchain4j.agentic.declarative.Output;
import dev.langchain4j.agentic.declarative.SequenceAgent;
import io.quarkus.logging.Log;
```

Save. Quarkus hot-reloads.

---

## Step 6 — Test all three paths (3 min)

**Path 1 — Clean return:**  
Return Car **#6** (Toyota Corolla) with `"Car looks great, no issues"`.  
Expected: status = `AVAILABLE`, no tool calls, supervisor skips pricing + disposition.

**Path 2 — Dirty car:**  
Return Car **#5** (Ford Focus) with `"Dog hair all over the cabin, smells awful"`.  
Expected: status = `AT_CLEANING`, logs show `CleaningTool` called.

**Path 3 — Severe damage (full supervisor path):**  
Return Car **#1** (Mercedes-Benz C-Class, 2022) with:

```
Front end crushed after collision; airbags deployed; not driveable; major structural damage
```

Expected: status = `PENDING_DISPOSITION`, logs show `PricingAgent` then `DispositionAgent` invoked by the supervisor. `CleaningAgent` is **not** invoked.

---

## Supervisor vs conditional routing

| | `@ConditionalAgent` | `@SupervisorAgent` |
|---|---|---|
| **Decision logic** | Hardcoded Java predicates | LLM reasoning on natural-language prompt |
| **Policy change** | Code change + test + redeploy | Edit `@SupervisorRequest` string + hot reload |
| **Sub-agent selection** | Compile-time routing table | Runtime multi-factor reasoning |
| **When to use** | Stable, binary, well-defined rules | Multi-factor, evolving, nuanced policy |

---

## Done when

- [ ] Full pipeline runs end-to-end: `POST /car-management/return/{id}` produces correct `CarStatus`
- [ ] Supervisor chose pricing + disposition for severe damage (Path 3)
- [ ] Supervisor chose cleaning only for dirty car (Path 2)
- [ ] You can draw the full agent chain from `CarProcessingWorkflow` → `CarStatus` from memory
- [ ] You can explain why policy lives in `@SupervisorRequest` and not in Java `if/else`
