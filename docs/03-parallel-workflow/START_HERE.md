# Exercise 3 — Parallel Agents: @ParallelMapperAgent

<span class="badge badge--code-along">Code-Along</span>

**Timebox:** 10 minutes  
**Persona:** Chris — Ops lead  
**You work in:** `lab/` (keep Quarkus running)  
**Files to edit:**

- `lab/src/main/java/com/incidentmanagement/agentic/agents/IncidentAnalysisAgent.java`
- `lab/src/main/java/com/incidentmanagement/agentic/workflow/IncidentAnalysisWorkflow.java`

!!! tip "Solution fallback"
    [`exercises/03-parallel-workflow/solution`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/tree/main/exercises/03-parallel-workflow/solution){:target="_blank"} — open if stuck.

---

## The goal

Run three **parallel agents** — severity, impact, and resolution analysis — concurrently instead of sequentially. Wall-clock time ≈ slowest single call (not the sum). One interface declaration, three concurrent LLM calls, each with a different `@SystemMessage` injected at runtime.

This introduces two concepts: **dynamic `@SystemMessage`** (same agent interface, different prompt per task) and **`@Output`** (aggregating parallel results into a typed record before downstream agents read scope).

---

## How `AgenticScope` connects agents

```mermaid
%%{init: {'look':'handDrawn','theme':'neutral','themeVariables': {'lineColor':'#4A4035'}}}%%
flowchart TD
    A([IncidentAnalysisAgent x3])
    B([Output → List of String])
    C([IncidentAnalysisResults])
    D([IncidentSupervisorAgent])

    A --> B --> C --> D

    subgraph parallel["Concurrent — outputKey = incidentAnalysis"]
        A1([Severity analyst])
        A2([Impact analyst])
        A3([Resolution analyst])
    end

    A1 --> B
    A2 --> B
    A3 --> B

    style A fill:#D4E6F1,stroke:#2E6B8A
    style B fill:#E8E0F0,stroke:#6B5B8A
    style C fill:#E8E0F0,stroke:#6B5B8A
    style D fill:#E8DCC4,stroke:#6B5B45
    style A1 fill:#D8F0D8,stroke:#3D7A3D
    style A2 fill:#D8F0D8,stroke:#3D7A3D
    style A3 fill:#D8F0D8,stroke:#3D7A3D
    style parallel fill:#F5F5F0,stroke:#8B8070
```

Each parallel invocation of `IncidentAnalysisAgent` writes its result under `"incidentAnalysis"`. The `@ParallelMapperAgent` framework collects these into a `List<String>` and passes it to the `@Output` method, which maps them positionally into `IncidentAnalysisResults`.

!!! warning "Rule"
    Every agent in a workflow **must** declare `outputKey`. Without it, the result is silently dropped from scope and the next agent finds nothing.

---

## Step 1 — Implement `IncidentAnalysisAgent` (4 min)

Open [`IncidentAnalysisAgent.java`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/blob/main/lab/src/main/java/com/incidentmanagement/agentic/agents/IncidentAnalysisAgent.java){:target="_blank"}.

Replace the `// TODO` block with the following code **exactly**:

```java
@SystemMessage("{task.systemInstructions}")
@UserMessage("""
    Incident Information:
    System: {incidentInfo.system}
    Service: {incidentInfo.service}
    Priority: P{incidentInfo.priority}
    Current Description: {incidentInfo.description}

    Report: {report}
    """)
@Agent(description = "Incident analyzer. Using report, determines if action is needed based on task type.",
       outputKey = "incidentAnalysis")
String analyzeIncident(AnalysisTask task, IncidentInfo incidentInfo,
                       Integer incidentNumber, String report);
```

Add these imports:

```java
import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
```

??? info "Dynamic `@SystemMessage` — one interface, three roles"
    The `{task.systemInstructions}` placeholder is resolved from the `AnalysisTask` parameter at runtime, not from a compile-time constant. The same interface handles three completely different analysis tasks:

    | Task index | `systemInstructions` content |
    |------------|------------------------------|
    | 0 | "You are a severity analyzer. Classify as P1-P4 or SEVERITY_LOW." |
    | 1 | "You are a business impact analyzer. Assess revenue/SLA impact or IMPACT_MINIMAL." |
    | 2 | "You are a resolution analyzer. If critical, ESCALATION_REQUIRED. Else ESCALATION_NOT_REQUIRED." |

    One interface declaration powers three concurrent LLM calls with different roles.

---

## Step 2 — Implement `IncidentAnalysisWorkflow` (3 min)

Open [`IncidentAnalysisWorkflow.java`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/blob/main/lab/src/main/java/com/incidentmanagement/agentic/workflow/IncidentAnalysisWorkflow.java){:target="_blank"}.

Replace both `// TODO` blocks with the following **two members**:

**2a — `@ParallelMapperAgent` method:**

```java
@ParallelMapperAgent(
        description = "Analyzes incident reports in parallel for severity, impact, and resolution needs",
        outputKey = "incidentAnalysisResults",
        subAgent = IncidentAnalysisAgent.class,
        itemsProvider = "tasks")
IncidentAnalysisResults analyzeIncident(List<AnalysisTask> tasks,
                                        IncidentInfo incidentInfo,
                                        Integer incidentNumber,
                                        String report);
```

**2b — `@Output` static method:**

```java
@Output
static IncidentAnalysisResults output(AgenticScope scope,
                                      List<String> incidentAnalysisResults) {
    return new IncidentAnalysisResults(
            incidentAnalysisResults.get(0),  // severityAnalysis
            incidentAnalysisResults.get(1),  // impactAnalysis
            incidentAnalysisResults.get(2)   // resolutionAnalysis
    );
}
```

Add these imports at the top of the file:

```java
import dev.langchain4j.agentic.declarative.Output;
import dev.langchain4j.agentic.declarative.ParallelMapperAgent;
```

??? info "Why `itemsProvider = \"tasks\"`?"
    `@ParallelMapperAgent` needs to know which method parameter holds the list of items to fan out over. `itemsProvider = "tasks"` names the `tasks` parameter — the framework fans out one `IncidentAnalysisAgent` call per element in `tasks`. The three calls run concurrently in separate virtual threads.

??? info "Why a static `@Output` method?"
    `@Output` is a post-processing step, not an LLM call. It receives the collected results from all parallel invocations and transforms them into a typed record. It runs synchronously after all parallel calls complete. Marking it `static` makes it clear it is a pure transformation with no injected dependencies.

Save both files. Quarkus hot-reloads.

---

## Step 3 — Verify agent registration (2 min)

!!! note "End-to-end testing starts in Exercise 4"
    `IncidentProcessingWorkflow` is still a TODO stub in `lab/`, so processing an incident from the UI won't trigger the parallel workflow yet. In this step you verify that the agents **compiled and registered** correctly. You'll see them run end-to-end (with overlapping timestamps proving parallelism) after wiring the full pipeline in Exercise 4.

Open the [Agentic Dev UI](http://localhost:8080/q/dev-ui/quarkus-langchain4j-agentic/agents){:target="_blank"} to see the agent graph. Look for these two entries:

| Agent | Type | Key detail |
|-------|------|------------|
| IncidentAnalysisWorkflow | ParallelMapperAgent | subAgent = IncidentAnalysisAgent, itemsProvider = "tasks" |
| IncidentAnalysisAgent | Agent | outputKey = "incidentAnalysis" |

This page visualizes every agent's type, `outputKey`, and sub-agent wiring — use it after every exercise to confirm your changes compiled and registered correctly.

!!! info "Topology view"
    If you check the [topology](http://localhost:8080/q/dev-ui/quarkus-langchain4j-agentic/topology){:target="_blank"}, you'll see "No root agents detected" — that's expected. `IncidentProcessingWorkflow` (the root) already `extends MonitoredAgent`, but its `@SequenceAgent` method is still a TODO stub in `lab/`. The topology tree will appear after you wire it in **Exercise 4, Step 5**. (If you're running the [solution fallback](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/tree/main/exercises/03-parallel-workflow/solution){:target="_blank"}, the topology will already show the full tree.)

??? question "Why not a Java `for` loop instead of `itemsProvider`?"
    `AgenticScope` must be injected per-invocation so each parallel run gets its own scope context, result slot, and `outputKey` entry. A Java loop over LLM calls would run sequentially in the same thread with a shared scope — defeating both the parallelism and the scope isolation.

---

<div class="done-when" markdown>

## :material-check-circle: Done when

- [ ] `IncidentAnalysisAgent` and `IncidentAnalysisWorkflow` appear in the [Agentic Dev UI](http://localhost:8080/q/dev-ui/quarkus-langchain4j-agentic/agents){:target="_blank"}
- [ ] No compile errors in the Quarkus terminal after hot reload
- [ ] You can explain `outputKey` and `@Output` from memory
- [ ] You can explain how dynamic SystemMessage enables one interface for 3 tasks

</div>
