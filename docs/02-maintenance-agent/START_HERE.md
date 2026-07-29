# Exercise 2 — DiagnosticAgent + @SystemMessage as Policy

<span class="badge badge--code-along">Code-Along</span>

**Timebox:** 10 minutes  
**Persona:** Chris — Ops lead  
**You work in:** `lab/` (keep Quarkus running)  
**Files to edit:** `lab/src/main/java/com/incidentmanagement/agentic/agents/DiagnosticAgent.java`

!!! tip "Solution fallback"
    [`exercises/03-supervisor/solution`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/tree/main/exercises/03-supervisor/solution) — open if stuck.

---

## The goal

Add `DiagnosticAgent` — same `@Agent` pattern as `TriageAgent` but **no tool** (diagnostics returns a structured root cause analysis as text). Then run a live `@SystemMessage` tuning experiment to see how policy-as-prose controls agent behavior without any code logic.

---

## Step 1 — Implement `DiagnosticAgent` (4 min)

Open [`DiagnosticAgent.java`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/blob/main/lab/src/main/java/com/incidentmanagement/agentic/agents/DiagnosticAgent.java).

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
    System: {system}
    Service: {service}
    Priority: P{priority}
    Incident Number: {incidentNumber}

    Diagnostic Request:
    {diagnosticRequest}
    """)
@Agent(description = "Incident diagnostic specialist. Using incident information and request, determines what diagnostic actions are needed.",
       outputKey = "analysisResult")
String processDiagnostic(String system, String service,
                          Integer priority, Integer incidentNumber,
                          String diagnosticRequest);
```

Add these imports at the top:

```java
import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
```

??? info "Why no `@ToolBox` here?"
    `DiagnosticAgent` returns a *plan* as text — it does not write to the database. The supervisor in Exercise 4 reads this text plan and decides whether to escalate. Text-only agents are faster and cheaper: no tool-call round-trips to the LLM.

    **Compare with `TriageAgent`:** `TriageAgent` must call `TriageTool.requestTriage()` to actually mutate `IncidentStatus`. `DiagnosticAgent` only produces a recommendation. The supervisor decides what happens next.

Save the file. Quarkus hot-reloads. `DiagnosticAgent` cannot be tested in isolation yet — it wires into the supervisor in Exercise 4. Check the terminal for any compile errors.

---

## Step 2 — @SystemMessage tuning experiment (4 min)

This is one of the most important insights in this lab: **`@SystemMessage` is a policy declaration, not code logic**.

Open `TriageAgent.java`. Find the threshold line in your `@SystemMessage` and compare:

=== "Original (lenient)"

    ```
    If no triage action is needed based on the report, respond with "TRIAGE_NOT_REQUIRED".
    ```

=== "Strict (replace with this)"

    ```
    Only request triage for CRITICAL issues: complete service outages,
    data loss or corruption, security breaches, or cascading failures affecting multiple systems.
    For intermittent errors, slow responses, or single-user complaints, respond with "TRIAGE_NOT_REQUIRED".
    ```

**Replace** the original line with the strict version.

Quarkus hot-reloads in ~1 second. Now process Incident **#7** (monitoring/alerting-api) with:

```
Alert threshold slightly too sensitive, causing a few extra notifications
```

- **With original threshold:** tool may be called (triage requested)
- **With strict threshold:** `TRIAGE_NOT_REQUIRED` — no tool call, no status change

Now try:

```
Complete monitoring blackout — zero alerts firing, all dashboards showing stale data, on-call has no visibility
```

- **Expected with strict threshold:** `requestTriage` IS called — critical enough to meet the threshold

!!! warning "Key insight"
    You changed agent *behavior* by editing a string — no conditional logic, no redeploy cycle beyond hot reload. The `@SystemMessage` IS the policy. This is what "declarative AI engineering" means.

**Revert** the `@SystemMessage` back to the original (simpler) version before moving on.

---

<div class="done-when" markdown>

## :material-check-circle: Done when

- [ ] `DiagnosticAgent.java` compiles — no errors (interface, `outputKey="analysisResult"`, no CDI scope, no `@ToolBox`)
- [ ] `@SystemMessage` threshold experiment completed — strict vs lenient behavior observed
- [ ] You can articulate: when does an agent need `@ToolBox`? When is text-only output correct?

</div>
