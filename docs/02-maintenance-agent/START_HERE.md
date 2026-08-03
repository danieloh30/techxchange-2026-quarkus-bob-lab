# Exercise 2 — Policy as Prompt

<span class="badge badge--code-along">Code-Along</span>

**Timebox:** 10 minutes  
**Persona:** Chris — Ops lead  
**You work in:** `lab/` (keep Quarkus running)  
**Files to edit:** `lab/src/main/java/com/incidentmanagement/agentic/agents/DiagnosticAgent.java`

!!! tip "Solution fallback"
    [`exercises/02-maintenance-agent/solution`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/tree/main/exercises/02-maintenance-agent/solution){:target="_blank"} — includes `DiagnosticAgent` wired into a composed workflow.

---

## The goal

Discover that `@SystemMessage` **is** the policy — not code logic, not conditional branches. You'll add `DiagnosticAgent` (a text-only agent with no tool), then run a live tuning experiment: edit a single string in `@SystemMessage`, hot-reload, and watch the same incident produce completely different agent behavior.

This is the key insight: **changing a prompt changes the policy**. No redeploy, no `if/else`, no feature flag.

---

## Step 1 — Implement `DiagnosticAgent` (4 min)

Open [`DiagnosticAgent.java`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/blob/main/lab/src/main/java/com/incidentmanagement/agentic/agents/DiagnosticAgent.java){:target="_blank"}.

Replace the `// TODO` block with the following code **exactly**:

```java
@SystemMessage("""
    You handle intake for the diagnostic department of an IT incident management system.
    Based on the incident report, determine what specific diagnostic actions are needed
    and provide a detailed root cause analysis plan.
    Be specific about what actions are needed based on the incident report.

    Available diagnostic actions include:
    - Log analysis
    - Service restart
    - Config rollback
    - Dependency check
    - Performance profiling
    - Network trace (packet capture, DNS, connectivity)

    For infrastructure issues like connectivity or DNS problems, include network trace in your plan.

    Provide your response as a structured diagnostic plan listing the specific actions needed.
    If no diagnostic action is needed based on the request, respond with "DIAGNOSTIC_NOT_REQUIRED".
    """)
@UserMessage("""
    Incident Information:
    System: {incidentInfo.system}
    Service: {incidentInfo.service}
    Priority: P{incidentInfo.priority}
    Incident Number: {incidentNumber}

    Diagnostic Request:
    {diagnosticRequest}
    """)
@Agent(description = "Incident diagnostic specialist. Using incident information and request, determines what diagnostic actions are needed.",
       outputKey = "analysisResult")
String processDiagnostic(IncidentInfo incidentInfo,
                          Integer incidentNumber,
                          String diagnosticRequest);
```

Add these imports at the top:

```java
import com.incidentmanagement.model.IncidentInfo;
import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
```

??? info "Why no `@ToolBox` here?"
    `DiagnosticAgent` returns a *plan* as text — it does not write to the database. The supervisor in Exercise 4 reads this text plan and decides whether to escalate. Text-only agents are faster and cheaper: no tool-call round-trips to the LLM.

    **Compare with `TriageAgent`:** `TriageAgent` must call `TriageTool.requestTriage()` to actually mutate `IncidentStatus`. `DiagnosticAgent` only produces a recommendation. The supervisor decides what happens next.

Save the file. Quarkus hot-reloads. Check the terminal for any compile errors.

---

## Step 2 — Verify the compile (1 min)

`DiagnosticAgent` wires into the supervisor orchestration in Exercise 4, so it can't be tested end-to-end yet. But you can confirm it compiled correctly:

1. Check the Quarkus terminal — no red stack traces after hot reload
2. Open the [Agentic Dev UI](http://localhost:8080/q/dev-ui/quarkus-langchain4j-agentic/agents){:target="_blank"} — confirm `DiagnosticAgent` appears in the agent list with its `outputKey` and description

If you see `Unsatisfied dependency` or `AmbiguousResolutionException`, double-check: `DiagnosticAgent` must be an **interface** (not a class), with **no** `@ApplicationScoped` annotation.

---

## Step 3 — @SystemMessage tuning experiment (5 min)

This is one of the most important insights in this lab: **`@SystemMessage` is a policy declaration, not code logic**.

The full workflow isn't wired in `lab/` yet (that happens in Exercise 4), so you'll run this experiment from the **solution project**.

Stop `lab/` first (`Ctrl+C`), then start the solution:

```bash
cd exercises/02-maintenance-agent/solution
./mvnw quarkus:dev
```

Open `TriageAgent.java` in the solution project and find this line in the `@SystemMessage`:

```
If no triage action is needed based on the report, respond with "TRIAGE_NOT_REQUIRED".
```

**Replace** that line with the strict version:

```
Only request triage for CRITICAL issues: complete service outages,
data loss or corruption, security breaches, or cascading failures affecting multiple systems.
For intermittent errors, slow responses, or single-user complaints, respond with "TRIAGE_NOT_REQUIRED".
```

Quarkus hot-reloads in ~1 second. Open **http://localhost:8080**, click **View** on Incident **#7** (monitoring/alerting-api), and process it with:

```
Alert threshold slightly too sensitive, causing a few extra notifications
```

!!! tip "Incident status changed?"
    If Incident #7 is no longer `OPEN`, press `s` in the Quarkus terminal to force a restart — the H2 dev database resets to seed data. Or use any other `OPEN` incident.

**How to confirm:** Check the Quarkus terminal logs and the incident status in the UI.

**Expected terminal logs (minor report):**
```
ReportAnalysisWorkflow executing...
  ├─ TriageFeedbackAgent analyzing...
  └─ DiagnosticFeedbackAgent analyzing...
IncidentAssignmentWorkflow evaluating conditions...
ResolutionAgent updating...
  └─ Action: MONITOR → Adjust alert sensitivity settings to reduce false positives...
```

The key line is `Action: MONITOR` — the agents decided neither triage nor diagnostic is needed for a minor alert sensitivity issue. Incident status stays `OPEN` in the UI.

Now press `s` to restart (reset the database), and process the same Incident **#7** with a critical report:

```
Complete monitoring blackout — zero alerts firing, all dashboards showing stale data, on-call has no visibility
```

**Expected terminal logs (critical report):**
```
ReportAnalysisWorkflow executing...
  ├─ TriageFeedbackAgent analyzing...
  └─ DiagnosticFeedbackAgent analyzing...
IncidentAssignmentWorkflow evaluating conditions...
ResolutionAgent updating...
  └─ Action: INVESTIGATE → Initiate triage and diagnostics for alerting-api blackout...
```

The key difference: `Action: INVESTIGATE` instead of `MONITOR`. The `DiagnosticFeedbackAgent` flagged this as needing root cause analysis. Incident status changes to `IN_PROGRESS` in the UI.

!!! warning "Key insight"
    You changed agent *behavior* by editing a string — no conditional logic, no redeploy cycle beyond hot reload. The `@SystemMessage` is the policy. This is what "declarative AI engineering" means.

**Revert** the `@SystemMessage` back to the original (simpler) version before moving on.

---

<div class="done-when" markdown>

## :material-check-circle: Done when

- [ ] `DiagnosticAgent.java` compiles — no errors (interface, `outputKey="analysisResult"`, no CDI scope, no `@ToolBox`)
- [ ] `@SystemMessage` threshold experiment completed — strict vs lenient behavior observed
- [ ] You can articulate: when does an agent need `@ToolBox`? When is text-only output correct?

</div>
