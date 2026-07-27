# Exercise 1 — Your First Agent: CleaningAgent + CleaningTool

**Timebox:** 15 minutes  
**Persona:** Maya — Rental desk manager  
**You work in:** `lab/` (keep Quarkus running — hot reload)  
**Files to edit:**
- `lab/src/main/java/com/carmanagement/agentic/agents/CleaningAgent.java`
- `lab/src/main/java/com/carmanagement/agentic/tools/CleaningTool.java`

> 💡 **Solution fallback:** [`exercises/01-first-agents/solution`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/tree/main/exercises/01-first-agents/solution) — open if stuck.

---

## The goal

By the end of this exercise, returning a dirty car flips its status to `AT_CLEANING` and shows a tool call in the logs. Returning a clean car produces `CLEANING_NOT_REQUIRED` with no tool call.

This exercise introduces the three-part `@Agent` anatomy: **interface declaration**, **@SystemMessage** (LLM policy), and **@ToolBox** (tool wiring).

---

## Step 0 — Start the lab project (2 min)

```bash
cd lab
export OPENAI_API_KEY=sk-your-lab-key-here
./mvnw quarkus:dev
```

Open **http://localhost:8080** — you'll see the Fleet Status UI with 8 seeded cars but no agent behavior yet (returns will fail — that's expected, you haven't wired the agents).

---

## Step 1 — Implement `CleaningAgent` (5 min)

Open [`CleaningAgent.java`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/blob/main/lab/src/main/java/com/carmanagement/agentic/agents/CleaningAgent.java).

Replace the `// TODO` block with the following code **exactly**:

```java
@SystemMessage("""
    You handle intake for the cleaning department of a car rental company.
    It is your job to submit a request to the provided requestCleaning function
    to take action based on the provided feedback.
    Be specific about what services are needed.
    If no cleaning is needed based on the feedback, respond with "CLEANING_NOT_REQUIRED".
    """)
@UserMessage("""
    Car Information:
    Make: {carInfo.make}
    Model: {carInfo.model}
    Year: {carInfo.year}
    Car Number: {carNumber}

    Feedback: {feedback}
    """)
@Agent(description = "Cleaning specialist. Determines what cleaning services are needed.",
       outputKey = "analysisResult")
@ToolBox(CleaningTool.class)
String processCleaning(CarInfo carInfo, Integer carNumber, String feedback);
```

Add these imports at the top (below the existing ones):

```java
import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
```

> **Why `outputKey = "analysisResult"`?**  
> `AgenticScope` is a shared context map passed through a workflow. Every agent writes its result under its `outputKey` so the next agent can read it. Without `outputKey`, the result is silently dropped and downstream agents find nothing. This is the single most common cause of workflow failures.

> **Why is this an interface, not a class?**  
> Quarkus LangChain4j generates the CDI proxy at build time. The framework manages the LLM call, message formatting, and tool invocation. You declare *what* to do via annotations; the framework handles *how*.

---

## Step 2 — Implement `CleaningTool` (4 min)

Open [`CleaningTool.java`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/blob/main/lab/src/main/java/com/carmanagement/agentic/tools/CleaningTool.java).

Add the following method inside the class (replace the `// TODO` block):

```java
@Tool("Requests a cleaning with the specified options")
@Transactional
public String requestCleaning(
        Integer carNumber,
        String carMake,
        String carModel,
        Integer carYear,
        boolean exteriorWash,
        boolean interiorCleaning,
        boolean detailing,
        boolean waxing,
        String requestText) {

    CarInfo carInfo = CarInfo.findById(carNumber);
    if (carInfo != null) {
        carInfo.status = CarStatus.AT_CLEANING;
        carInfo.persist();
    }

    StringBuilder summary = new StringBuilder();
    summary.append("Cleaning requested for ").append(carMake).append(" ")
           .append(carModel).append(" (").append(carYear).append("), Car #")
           .append(carNumber).append(":\n");
    if (exteriorWash)    summary.append("- Exterior wash\n");
    if (interiorCleaning) summary.append("- Interior cleaning\n");
    if (detailing)       summary.append("- Detailing\n");
    if (waxing)          summary.append("- Waxing\n");
    if (requestText != null && !requestText.isEmpty())
        summary.append("Notes: ").append(requestText);

    Log.info("  └─ CleaningTool activated for car #" + carNumber);
    return summary.toString();
}
```

> **Why `@Transactional` here but not on `CleaningAgent`?**  
> `CarInfo.persist()` writes to the PostgreSQL database. JPA requires an active transaction for mutations. `CleaningAgent` is an LLM-backed interface (no JPA operations) — adding `@Transactional` there would have no effect and would mislead future readers.
>
> **Why `@ApplicationScoped` on the tool class?**  
> Tools are CDI beans injected into the LLM call context. They must be scoped. `@ApplicationScoped` is the correct scope — tools hold no per-request state.

Save both files. Quarkus hot-reloads automatically.

---

## Step 3 — Understand the agent loop

Before testing, trace the execution path in your head:

```
processCleaning(carInfo, carNumber, feedback)
        │
        ▼
  @UserMessage → formatted prompt → LLM
        │
        ▼  LLM decides: "I need to call requestCleaning"
  tool_call: requestCleaning(carNumber=5, interiorCleaning=true, ...)
        │
        ▼
  CleaningTool.requestCleaning()
    carInfo.status = AT_CLEANING
    carInfo.persist()
    returns "Cleaning requested for Ford Focus..."
        │
        ▼  LLM reads tool result
  final response written to AgenticScope["analysisResult"]
```

The LLM decides *whether* to call the tool based on the `@SystemMessage` policy. If feedback says "car looks fine", the LLM produces `CLEANING_NOT_REQUIRED` directly — no tool call.

---

## Step 4 — Test it (4 min)

Open **http://localhost:8080** and return Car **#5** (Ford Focus, status: `RENTED`) with:

```
Car has dog hair all over the back seat and muddy footwells
```

**Expected terminal logs:**
```
[dev.langchain4j.agentic] ← LLM: tool_call requestCleaning(carNumber=5, interiorCleaning=true, ...)
  └─ CleaningTool activated for car #5
```

**Expected UI:** Car #5 status → `AT_CLEANING`

Now return Car **#6** (Toyota Corolla, status: `RENTED`) with:
```
Car looks perfect, no issues at all
```

**Expected:** Response contains `CLEANING_NOT_REQUIRED`; status stays `AVAILABLE`; **no** tool call in logs.

---

## Done when

- [ ] Tool call visible in logs for dirty-car return; status = `AT_CLEANING`
- [ ] No tool call for clean-car return; status = `AVAILABLE`
- [ ] You can explain from memory: why `@Transactional` on the tool but not the agent
- [ ] You can explain from memory: what `outputKey` does and what breaks without it

> **Keep Quarkus running** — Exercise 2 adds the next agent with hot reload.
