# Exercise 2 — Your First Agent: CleaningAgent

**Timebox:** 10 minutes  
**Persona:** Maya — Rental desk manager  
**You work in:** `lab/` (keep Quarkus running — hot reload)  
**Files to edit:** `lab/src/main/java/com/carmanagement/agentic/agents/CleaningAgent.java`  
                   `lab/src/main/java/com/carmanagement/agentic/tools/CleaningTool.java`

> 💡 **Solution fallback:** [`exercises/01-first-agents/solution`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/tree/main/exercises/01-first-agents/solution) — open if stuck.

---

## The goal

By the end of this exercise, returning a dirty car flips its status to `AT_CLEANING` and triggers a visible tool call in the logs. Returning a clean car produces `CLEANING_NOT_REQUIRED` with no tool call.

---

## Step 1 — Ask Bob to implement CleaningAgent

```text
Read AGENTS.md.

Implement CleaningAgent in lab/src/main/java/com/carmanagement/agentic/agents/CleaningAgent.java.
Follow the TODO comments and AGENTS.md rules exactly:
- @SystemMessage: cleaning intake role, requestCleaning tool, CLEANING_NOT_REQUIRED skip rule
- @UserMessage: carInfo.make, carInfo.model, carInfo.year, carNumber, feedback placeholders
- @Agent(description="...", outputKey="analysisResult")
- @ToolBox(CleaningTool.class)
- Method: String processCleaning(CarInfo carInfo, Integer carNumber, String feedback)

Wait for my approval before applying the diff.
```

**Review Bob's diff:** check that:
- It's an interface (no `implements`, no CDI scope)
- `outputKey = "analysisResult"` is set
- `@ToolBox(CleaningTool.class)` is present

Approve → Bob applies the change → Quarkus hot-reloads.

---

## Step 2 — Ask Bob to implement CleaningTool

```text
Now implement CleaningTool in lab/src/main/java/com/carmanagement/agentic/tools/CleaningTool.java.
Follow the TODO comments and AGENTS.md rules:
- @ApplicationScoped (already there — don't change)
- @Tool("Requests a cleaning with the specified options")
- @Transactional on requestCleaning (JPA mutation — rule 5 in AGENTS.md)
- Body: find CarInfo by carNumber, set status = CarStatus.AT_CLEANING, persist, return summary
- Log.info only car number (not feedback text — rule 6 in AGENTS.md)

Wait for approval before applying.
```

---

## Step 3 — Test it

Open **http://localhost:8080** and return Car **#5** (Ford Focus, RENTED) with:

```text
Car has dog hair all over the back seat
```

**Expected logs:**
```
[dev.langchain4j.agentic] ← LLM: tool_call requestCleaning(carNumber=5, interiorCleaning=true, ...)
  └─ CleaningTool activated for car #5
```

**Expected UI:** Car #5 status → `AT_CLEANING`

Now return Car **#6** (Toyota Corolla) with:
```text
Car looks good, no issues
```
**Expected:** `CLEANING_NOT_REQUIRED` in response; status stays `AVAILABLE`; no tool call logged.

---

## Step 4 — The agent loop (understand it, don't just run it)

```
@UserMessage → LLM → tool_call requestCleaning()
                          │
                          ▼
               CleaningTool.requestCleaning()
               → carInfo.status = AT_CLEANING
               → returns summary string
                          │
                          ▼
              LLM reads tool result → final response
```

Ask Bob: `"Explain why @Transactional is required on CleaningTool.requestCleaning() but NOT on CleaningAgent.processCleaning()."`

---

## Done when

- [ ] Tool call visible in logs for dirty-car return
- [ ] No tool call for clean-car return; status = AVAILABLE
- [ ] Bob's diff respected AGENTS.md rules (interface, outputKey, @Transactional)
- [ ] You can draw the agent loop from memory
