# Exercise 2 — MaintenanceAgent + @SystemMessage as Policy

**Timebox:** 10 minutes  
**Persona:** Chris — Ops lead  
**You work in:** `lab/` (keep Quarkus running)  
**Files to edit:** `lab/src/main/java/com/carmanagement/agentic/agents/MaintenanceAgent.java`

> 💡 **Solution fallback:** [`exercises/03-supervisor/solution`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/tree/main/exercises/03-supervisor/solution) — open if stuck.

---

## The goal

Add `MaintenanceAgent` — same `@Agent` pattern as `CleaningAgent` but **no tool** (maintenance returns a structured plan as text). Then run a live `@SystemMessage` tuning experiment to see how policy-as-prose controls agent behavior without any code logic.

---

## Step 1 — Implement `MaintenanceAgent` (4 min)

Open [`MaintenanceAgent.java`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/blob/main/lab/src/main/java/com/carmanagement/agentic/agents/MaintenanceAgent.java).

Replace the `// TODO` block with the following code **exactly**:

```java
@SystemMessage("""
    You handle intake for the car maintenance department of a car rental company.
    Based on the maintenance request, determine what specific services are needed
    and provide a detailed maintenance plan.
    Be specific about what services are needed based on the maintenance request.

    Available maintenance services include:
    - Oil change
    - Tire rotation
    - Brake service
    - Engine service
    - Transmission service
    - Body work (dent repair, paint, collision repair)

    For body damage like dents, scratches, or collision damage, include body work in your plan.

    Provide your response as a structured maintenance plan listing the specific services needed.
    If no maintenance is needed based on the request, respond with "MAINTENANCE_NOT_REQUIRED".
    """)
@UserMessage("""
    Car Information:
    Make: {carMake}
    Model: {carModel}
    Year: {carYear}
    Car Number: {carNumber}

    Maintenance Request:
    {maintenanceRequest}
    """)
@Agent(description = "Car maintenance specialist. Using car information and request, determines what maintenance services are needed.",
       outputKey = "analysisResult")
String processMaintenance(String carMake, String carModel,
                          Integer carYear, Integer carNumber,
                          String maintenanceRequest);
```

Add these imports at the top:

```java
import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
```

> **Why no `@ToolBox` here?**  
> `MaintenanceAgent` returns a *plan* as text — it does not write to the database. The supervisor in Exercise 4 reads this text plan and decides whether to escalate. Text-only agents are faster and cheaper: no tool-call round-trips to the LLM.
>
> **Compare with `CleaningAgent`:**  
> `CleaningAgent` must call `CleaningTool.requestCleaning()` to actually mutate `CarStatus`. `MaintenanceAgent` only produces a recommendation. The supervisor decides what happens next.

Save the file. Quarkus hot-reloads. `MaintenanceAgent` cannot be tested in isolation yet — it wires into the supervisor in Exercise 4. Check the terminal for any compile errors.

---

## Step 2 — @SystemMessage tuning experiment (4 min)

This is one of the most important insights in this lab: **`@SystemMessage` is a policy declaration, not code logic**.

Open `CleaningAgent.java`. Find this line in your `@SystemMessage`:

```
If no cleaning is needed based on the feedback, respond with "CLEANING_NOT_REQUIRED".
```

**Replace it** with the stricter version:

```
Only request cleaning for SEVERE contamination: pet hair embedded in upholstery,
food stains, strong persistent odors, or biohazardous material.
For light dust, minor scuffs, or normal wear and tear, respond with "CLEANING_NOT_REQUIRED".
```

Quarkus hot-reloads in ~1 second. Now return Car **#7** (Honda Civic) with:

```
There's a small amount of dust on the dashboard and a minor smudge on the window
```

- **With original threshold:** tool may be called (cleaning requested)
- **With strict threshold:** `CLEANING_NOT_REQUIRED` — no tool call, no status change

Now try:

```
Dog hair deeply embedded in both rear seat cushions, strong wet-dog smell throughout cabin
```

- **Expected with strict threshold:** `requestCleaning` IS called — severe enough to meet the threshold

> **Key insight:** You changed agent *behavior* by editing a string — no conditional logic, no redeploy cycle beyond hot reload. The `@SystemMessage` IS the policy. This is what "declarative AI engineering" means.

**Revert** the `@SystemMessage` back to the original (simpler) version before moving on.

---

## Done when

- [ ] `MaintenanceAgent.java` compiles — no errors (interface, `outputKey="analysisResult"`, no CDI scope, no `@ToolBox`)
- [ ] `@SystemMessage` threshold experiment completed — strict vs lenient behavior observed
- [ ] You can articulate: when does an agent need `@ToolBox`? When is text-only output correct?
