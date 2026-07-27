# Exercise 4 — Parallel Workflow: FeedbackAnalysisWorkflow

**Timebox:** 10 minutes  
**Persona:** Chris — Ops lead  
**You work in:** `lab/` (keep Quarkus running)  
**Files to edit:**
- `lab/src/main/java/com/carmanagement/agentic/agents/FeedbackAnalysisAgent.java`
- `lab/src/main/java/com/carmanagement/agentic/workflow/FeedbackAnalysisWorkflow.java`

> 💡 **Solution fallback:** [`exercises/03-supervisor/solution`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/tree/main/exercises/03-supervisor/solution) — open if stuck.

---

## The goal

Instead of calling one agent at a time, run cleaning, maintenance, and disposition analysis **concurrently**. Wall-clock time ≈ slowest single call (not sum). Then assemble results into a typed `FeedbackAnalysisResults` record.

Pattern: `@ParallelMapperAgent` + `@Output`

---

## How `AgenticScope` connects agents

```
FeedbackAnalysisAgent × 3 (concurrent)
  outputKey = "feedbackAnalysis"  ← each writes its result here
         │
         ▼ (FeedbackAnalysisWorkflow @Output collects all three)
FeedbackAnalysisResults { cleaningAnalysis, maintenanceAnalysis, dispositionAnalysis }
  outputKey = "feedbackAnalysisResults"
         │
         ▼
FleetSupervisorAgent reads "feedbackAnalysisResults" from AgenticScope  (Exercise 5)
```

> **Rule:** Every agent in a workflow **must** declare `outputKey`. Without it, the next agent finds nothing in scope and the workflow fails at runtime.

---

## Step 1 — Ask Bob to implement FeedbackAnalysisAgent

```text
Read AGENTS.md.

Implement FeedbackAnalysisAgent in lab/src/main/java/com/carmanagement/agentic/agents/FeedbackAnalysisAgent.java.
Follow the TODO comments:
- @SystemMessage("{task.systemInstructions}")
  Note: the system prompt is DYNAMIC — it comes from FeedbackTask.systemInstructions at runtime.
  This is how the same interface handles cleaning, maintenance, and disposition analysis.
- @UserMessage: carInfo.make, carInfo.model, carInfo.year, carInfo.condition, feedback
- @Agent(description="Feedback analyzer...", outputKey="feedbackAnalysis")
  ⚠ outputKey MUST be "feedbackAnalysis" — FeedbackAnalysisWorkflow reads this exact key
- Method: String analyzeFeedback(FeedbackTask task, CarInfo carInfo,
                                  Integer carNumber, String feedback)

Wait for approval before applying.
```

---

## Step 2 — Ask Bob to implement FeedbackAnalysisWorkflow

```text
Implement FeedbackAnalysisWorkflow in lab/src/main/java/com/carmanagement/agentic/workflow/FeedbackAnalysisWorkflow.java.
Follow the TODO comments:

Step 2a — @ParallelMapperAgent method:
  @ParallelMapperAgent(
      description = "Analyzes car feedback in parallel",
      outputKey = "feedbackAnalysisResults",
      subAgent = FeedbackAnalysisAgent.class,
      itemsProvider = "tasks")
  FeedbackAnalysisResults analyzeFeedback(List<FeedbackTask> tasks, CarInfo carInfo,
                                           Integer carNumber, String feedback);

Step 2b — @Output static method:
  @Output
  static FeedbackAnalysisResults output(AgenticScope scope,
                                        List<String> feedbackAnalysisResults) {
      return new FeedbackAnalysisResults(
              feedbackAnalysisResults.get(0),
              feedbackAnalysisResults.get(1),
              feedbackAnalysisResults.get(2));
  }

Wait for approval before applying.
```

---

## Step 3 — Observe parallel execution

After hot-reload, check the Dev UI CDI beans panel — `FeedbackAnalysisAgent` and `FeedbackAnalysisWorkflow` should now appear as managed beans.

Return Car **#5** (Ford Focus) with:
```text
Engine warning light is on and the cabin smells like smoke
```

**Expected in logs:** Three `analyzeFeedback` invocations with **overlapping timestamps** (parallel). All three analysis types run concurrently.

Ask Bob:
```text
Explain why @ParallelMapperAgent uses itemsProvider="tasks" instead of just
iterating the list in Java. What does AgenticScope provide that a simple for-loop cannot?
```

---

## Done when

- [ ] `FeedbackAnalysisAgent` and `FeedbackAnalysisWorkflow` compile and appear in Dev UI CDI beans
- [ ] Parallel log timestamps overlap on a test return
- [ ] You can explain `outputKey` rule from memory — what breaks without it
- [ ] You can explain how `@SystemMessage("{task.systemInstructions}")` enables one interface for 3 tasks
