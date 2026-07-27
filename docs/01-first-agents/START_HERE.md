# Exercise 1 — Your First AI Agents

**Timebox:** 10 minutes  
**Persona:** Maya — Rental desk manager  
**Story:** Maya's team drowns in returned cars with free-text notes. The system must decide: deep clean or straight to available?  
**Solution project:** [`exercises/01-first-agents/solution`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/tree/main/exercises/01-first-agents/solution)  
**Upstream:** [section-2/step-01](https://quarkus.io/quarkus-workshop-langchain4j/section-2/step-01/)

![Fleet UI](../images/agentic-UI-fleet-status.png)

---

## Start

```bash
cd exercises/01-first-agents/solution
./mvnw quarkus:dev
```

Quarkus Dev Services auto-starts a PostgreSQL container — wait for:
```
car-management 1.0.0 on JVM (powered by Quarkus 3.37.4) started in ~3s
```

Open **http://localhost:8080** — Fleet Status grid with 8 seeded cars; **Return** button in the Action column.

> **Real endpoint:** `POST /car-management/return/{carNumber}` — the UI calls this. You can also `curl` it directly.

---

## Anatomy of `@Agent`

Open [`CleaningAgent.java`](../../exercises/01-first-agents/solution/src/main/java/com/carmanagement/agentic/agents/CleaningAgent.java):

```java
public interface CleaningAgent {

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
    @Agent("Cleaning specialist. Determines what cleaning services are needed.")
    @ToolBox(CleaningTool.class)
    String processCleaning(CarInfo carInfo, Integer carNumber, String feedback);
}
```

**Every annotation has a job:**

| Annotation | Purpose | What breaks without it |
|-----------|---------|------------------------|
| `@SystemMessage` | LLM role + hard rules; sent on every call | LLM has no persona — unreliable decisions |
| `@UserMessage` | Per-call prompt built from method params | No car data injected into the LLM request |
| `@Agent` | Quarkus generates CDI bean at build time | No implementation — `NullPointerException` at runtime |
| `@ToolBox` | Which `@Tool` methods the LLM may call | LLM can reason but can't mutate state |

**Why an interface?** Quarkus generates the implementation at build time (byte-buddy proxy). You declare *intent*; the LLM provides *behavior*. Never write a class that `implements CleaningAgent` — it breaks the framework.

---

## The agent loop (internalize this)

```
1. CarManagementService.processReturn(carNumber, feedback)
        │
        ▼
2. CleaningAgent.processCleaning(carInfo, carNumber, feedback)
        │  Quarkus sends @SystemMessage + @UserMessage to LLM
        ▼
3. LLM response:  tool_call { requestCleaning(carNumber=5, interiorCleaning=true, ...) }
        │
        ▼
4. CleaningTool.requestCleaning(...)  ← your @Transactional CDI bean
        │  sets carInfo.status = CarStatus.AT_CLEANING; carInfo.persist()
        │  returns summary string
        ▼
5. LLM receives tool result, generates final text response
        │
        ▼
6. UI shows status = AT_CLEANING
```

---

## Hands-on

### Test 1 — Dirty car (tool MUST be called)

Return Car **#5** (Ford Focus, RENTED) with:
```text
Car has dog hair all over the back seat
```

Watch the console:
```
[dev.langchain4j.agentic] → processCleaning for car 5
[dev.langchain4j.agentic] ← LLM: tool_call requestCleaning(carNumber=5, interiorCleaning=true, ...)
🚗 CleaningTool result: Cleaning requested for Ford Focus (2014), Car #5: - Interior cleaning
[dev.langchain4j.agentic] ← LLM final: "Interior cleaning scheduled..."
```

**Expected:** status → `AT_CLEANING`; at least one tool call visible in logs.

### Test 2 — Clean car (tool MUST NOT be called)

Return Car **#6** (Toyota Corolla, RENTED) with:
```text
Car looks good, no issues
```

**Expected:** response contains `CLEANING_NOT_REQUIRED`; status stays `AVAILABLE`; no `CleaningTool` log line.

### Stretch — tighten the threshold (optional, ~2 min)

Edit `@SystemMessage` to be more strict:
```text
Only request cleaning for SEVERE contamination (pet hair, food stains, strong odors).
For light dust or minor scuffs, respond with "CLEANING_NOT_REQUIRED".
```

Re-test with `"minor scuff on the door panel"` — confirm it now skips cleaning.

---

## Done when

- [ ] Tool call visible in logs for a dirty-car return
- [ ] No tool call for a clean-car return
- [ ] You can explain the agent loop in one sentence to a colleague
- [ ] You can name all four annotations and their purpose

> **Stop Quarkus** (`Ctrl+C`) before Exercise 2.
