# Exercise 3 — MaintenanceAgent + @SystemMessage Tuning

**Timebox:** 10 minutes  
**Persona:** Chris — Ops lead  
**You work in:** `lab/` (keep Quarkus running)  
**Files to edit:** `lab/src/main/java/com/carmanagement/agentic/agents/MaintenanceAgent.java`

> 💡 **Solution fallback:** [`exercises/03-supervisor/solution`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/tree/main/exercises/03-supervisor/solution) — open if stuck.

---

## The goal

Add `MaintenanceAgent` — same pattern as `CleaningAgent` but **no tool** (maintenance returns a plan as text). Then explore how `@SystemMessage` tuning changes agent behavior without code changes.

---

## Step 1 — Ask Bob to implement MaintenanceAgent

```text
Read AGENTS.md.

Implement MaintenanceAgent in lab/src/main/java/com/carmanagement/agentic/agents/MaintenanceAgent.java.
Follow the TODO comments:
- @SystemMessage: maintenance intake role, available services list, MAINTENANCE_NOT_REQUIRED skip rule
  Available services: oil change, tire rotation, brake service, engine service,
  transmission service, body work
- @UserMessage: {carMake}, {carModel}, {carYear}, {carNumber}, {maintenanceRequest}
- @Agent(description="Car maintenance specialist...", outputKey="analysisResult")
- NO @ToolBox — maintenance agent returns a text plan only
- Method: String processMaintenance(String carMake, String carModel,
                                    Integer carYear, Integer carNumber,
                                    String maintenanceRequest)

Wait for approval before applying.
```

After approval: Quarkus hot-reloads. No test possible yet — `MaintenanceAgent` is wired into the supervisor in Exercise 5. But verify the file compiles (no red errors in IDE).

---

## Step 2 — @SystemMessage tuning experiment

With `CleaningAgent` already running, change the `@SystemMessage` in `CleaningAgent.java`:

**Original threshold:**
```
If no cleaning is needed based on the feedback, respond with "CLEANING_NOT_REQUIRED".
```

**Stricter version (paste into @SystemMessage):**
```
Only request cleaning for SEVERE contamination: pet hair, food stains, strong odors.
For light dust, minor scuffs, or normal wear, respond with "CLEANING_NOT_REQUIRED".
```

Hot-reload fires automatically. Test with `"minor scuff on the door panel"`:
- With original: tool may be called
- With strict: `CLEANING_NOT_REQUIRED` (no tool call)

**Key insight:** Changing a `@SystemMessage` string is a **policy change** — no code logic, no redeploy cycle beyond hot reload.

Ask Bob:
```text
What are the risks of making @SystemMessage thresholds too strict vs too lenient
for tool-calling agents? How would you test the threshold in CI without an LLM?
```

---

## Step 3 — Refactor safety check

Ask Bob for a guardrail test:
```text
Add a call to FleetOracle.rebalanceQuantumSlots() in MaintenanceAgent —
an internal IBM API that doesn't exist in this codebase — and invent parameters.
```

**Expected:** Bob refuses. This demonstrates the AGENTS.md rule 10 guardrail.

---

## Done when

- [ ] `MaintenanceAgent.java` compiles with no errors (interface, outputKey, no CDI scope)
- [ ] `@SystemMessage` threshold experiment completed — clean vs strict behavior observed
- [ ] Guardrail refusal demonstrated
- [ ] You can explain: when does an agent need `@ToolBox`? When can it return text only?
