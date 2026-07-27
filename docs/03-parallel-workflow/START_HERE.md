# Exercise 3 — Parallel Workflow: @ParallelMapperAgent

**Timebox:** 10 minutes  
**Persona:** Chris — Ops lead  
**You work in:** `lab/` (keep Quarkus running)  
**Files to edit:**
- `lab/src/main/java/com/carmanagement/agentic/agents/FeedbackAnalysisAgent.java`
- `lab/src/main/java/com/carmanagement/agentic/workflow/FeedbackAnalysisWorkflow.java`

> 💡 **Solution fallback:** [`exercises/03-supervisor/solution`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/tree/main/exercises/03-supervisor/solution) — open if stuck.

---

## The goal

Instead of calling cleaning, maintenance, and disposition analysis sequentially, run all three **concurrently** with `@ParallelMapperAgent`. Wall-clock time ≈ slowest single call (not the sum). Then assemble three independent `String` results into a single typed `FeedbackAnalysisResults` record via an `@Output` method.

This introduces two new concepts: **dynamic `@SystemMessage`** (same interface, different prompt at runtime) and **`@Output`** (result aggregation before the next agent reads scope).

---

## How `AgenticScope` connects agents

```
FeedbackAnalysisAgent × 3 (concurrent)
  Each invocation: outputKey = "feedbackAnalysis"
         │
         ▼ @Output collects all three List<String> results
FeedbackAnalysisResults { cleaningAnalysis, maintenanceAnalysis, dispositionAnalysis }
  outputKey = "feedbackAnalysisResults"
         │
         ▼
FleetSupervisorAgent reads "feedbackAnalysisResults" from AgenticScope  (Exercise 4)
```

Each parallel invocation of `FeedbackAnalysisAgent` writes its result under `"feedbackAnalysis"`. The `@ParallelMapperAgent` framework collects these into a `List<String>` and passes it to the `@Output` method, which maps them positionally into `FeedbackAnalysisResults`.

> **Rule:** Every agent in a workflow **must** declare `outputKey`. Without it, the result is silently dropped from scope and the next agent finds nothing.

---

## Step 1 — Implement `FeedbackAnalysisAgent` (4 min)

Open [`FeedbackAnalysisAgent.java`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/blob/main/lab/src/main/java/com/carmanagement/agentic/agents/FeedbackAnalysisAgent.java).

Replace the `// TODO` block with the following code **exactly**:

```java
@SystemMessage("{task.systemInstructions}")
@UserMessage("""
    Car Information:
    Make: {carInfo.make}
    Model: {carInfo.model}
    Year: {carInfo.year}
    Previous Condition: {carInfo.condition}

    Feedback: {feedback}
    """)
@Agent(description = "Feedback analyzer. Using feedback, determines if action is needed based on task type.",
       outputKey = "feedbackAnalysis")
String analyzeFeedback(FeedbackTask task, CarInfo carInfo,
                       Integer carNumber, String feedback);
```

Add these imports:

```java
import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
```

> **`@SystemMessage("{task.systemInstructions}")` — dynamic system prompt**  
> The `{task.systemInstructions}` placeholder is resolved from the `FeedbackTask` parameter at runtime, not from a compile-time constant. The same interface handles three completely different analysis tasks:
>
> | Task index | `systemInstructions` content |
> |------------|------------------------------|
> | 0 | "You are a cleaning analyst. Respond with cleaning assessment or CLEANING_NOT_REQUIRED." |
> | 1 | "You are a maintenance analyst. Respond with maintenance assessment or MAINTENANCE_NOT_REQUIRED." |
> | 2 | "You are a disposition analyst. If severe damage, respond DISPOSITION_REQUIRED + reason. Else DISPOSITION_NOT_REQUIRED." |
>
> One interface declaration powers three concurrent LLM calls with different roles.

---

## Step 2 — Implement `FeedbackAnalysisWorkflow` (3 min)

Open [`FeedbackAnalysisWorkflow.java`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/blob/main/lab/src/main/java/com/carmanagement/agentic/workflow/FeedbackAnalysisWorkflow.java).

Replace both `// TODO` blocks with the following **two members**:

**2a — `@ParallelMapperAgent` method:**

```java
@ParallelMapperAgent(
        description = "Analyzes car feedback in parallel for cleaning, maintenance, and disposition needs",
        outputKey = "feedbackAnalysisResults",
        subAgent = FeedbackAnalysisAgent.class,
        itemsProvider = "tasks")
FeedbackAnalysisResults analyzeFeedback(List<FeedbackTask> tasks,
                                        CarInfo carInfo,
                                        Integer carNumber,
                                        String feedback);
```

**2b — `@Output` static method:**

```java
@Output
static FeedbackAnalysisResults output(AgenticScope scope,
                                      List<String> feedbackAnalysisResults) {
    return new FeedbackAnalysisResults(
            feedbackAnalysisResults.get(0),  // cleaningAnalysis
            feedbackAnalysisResults.get(1),  // maintenanceAnalysis
            feedbackAnalysisResults.get(2)   // dispositionAnalysis
    );
}
```

Add these imports at the top of the file:

```java
import dev.langchain4j.agentic.declarative.Output;
import dev.langchain4j.agentic.declarative.ParallelMapperAgent;
```

> **Why `itemsProvider = "tasks"`?**  
> `@ParallelMapperAgent` needs to know which method parameter holds the list of items to fan out over. `itemsProvider = "tasks"` names the `tasks` parameter — the framework fans out one `FeedbackAnalysisAgent` call per element in `tasks`. The three calls run concurrently in separate virtual threads.
>
> **Why a static `@Output` method instead of a regular method?**  
> `@Output` is a post-processing step, not an LLM call. It receives the collected results from all parallel invocations and transforms them into a typed record. It runs synchronously after all parallel calls complete. Marking it `static` makes it clear it is a pure transformation with no injected dependencies.

Save both files. Quarkus hot-reloads.

---

## Step 3 — Verify parallel execution (2 min)

Return Car **#5** (Ford Focus) with:

```
Engine warning light is on and the cabin smells like smoke
```

**Watch the terminal logs.** You should see three `analyzeFeedback` invocations with **overlapping timestamps** — they start within milliseconds of each other, not sequentially.

You can also open the **Dev UI** (`http://localhost:8080/q/dev`) → **CDI beans** panel and verify `FeedbackAnalysisAgent` and `FeedbackAnalysisWorkflow` appear as managed beans.

> **Conceptual question to ponder:**  
> Why does `@ParallelMapperAgent` use `itemsProvider="tasks"` instead of a Java `for` loop over the list?  
> Answer: `AgenticScope` must be injected per-invocation so each parallel run gets its own scope context, result slot, and `outputKey` entry. A Java loop over LLM calls would run sequentially in the same thread with a shared scope — defeating both the parallelism and the scope isolation.

---

## Done when

- [ ] `FeedbackAnalysisAgent` and `FeedbackAnalysisWorkflow` compile and appear in Dev UI CDI beans
- [ ] Terminal logs show overlapping timestamps for the three `analyzeFeedback` invocations
- [ ] You can explain `outputKey` and `@Output` from memory
- [ ] You can explain how `@SystemMessage("{task.systemInstructions}")` enables one interface for 3 tasks
