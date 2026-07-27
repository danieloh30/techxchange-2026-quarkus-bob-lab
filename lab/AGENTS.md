# AGENTS.md — Miles of Smiles Fleet Management

> **Exercise 1 task:** You will build this file with IBM Bob before writing any Java code.
> Bob reads this file on every request — it replaces codebase scanning and saves 2,000–5,000
> tokens per complex task. Keep it accurate as you add agents through the exercises.
>
> See the root [`AGENTS.md`](../AGENTS.md) for the complete reference. This file is your
> project-local working copy; update it after each exercise as you add new agents.

---

## Project identity

| Field | Value |
|-------|-------|
| Name | `techxchange-2026-quarkus-bob-lab` |
| Story | Miles of Smiles fleet management (IBM TechXchange 2026) |
| Runtime | IBM Enterprise Build of Quarkus 3.37.4 / Java 25 |
| AI extension | `quarkus-langchain4j` 1.12.0 |
| LLM | OpenAI `gpt-4o` via `${OPENAI_API_KEY}`; temperature = 0 |
| Build | Maven (`./mvnw`) |
| Main package | `com.carmanagement` |
| Port | 8080 |

---

## Mandatory programming model

All AI agents are **Java interfaces** with these annotations:

```java
public interface MyAgent {

    @SystemMessage("Role and hard rules for the LLM.")
    @UserMessage("Per-call prompt with {placeholder} substitution.")
    @Agent(description = "One sentence.", outputKey = "myKey")  // outputKey required for workflows
    @ToolBox(MyTool.class)   // omit if no tools
    String process(String param);
}
```

Tools are `@ApplicationScoped` CDI beans:

```java
@ApplicationScoped
public class MyTool {
    @Tool("Description the LLM reads.")
    @Transactional   // required if calling entity.persist()
    public String doAction(String input) { ... }
}
```

---

## Project rules (Bob must follow all 10)

1. **Never create a class that implements an agent interface** — Quarkus generates it.
2. **Never add CDI scope annotations** (`@ApplicationScoped`, `@Singleton`) to agent interfaces.
3. **`@ToolBox` only** — no constructor injection of tools into agent interfaces.
4. **`outputKey` is mandatory** on every agent used inside `@SequenceAgent` or `@SupervisorAgent`.
5. **`@Transactional` is mandatory** on any tool method that calls `entity.persist()`.
6. **Never log full customer feedback text** — PII risk. Log only car number and status.
7. **No secrets in `@SystemMessage` / `@UserMessage`** — use `application.properties` vars.
8. **Approval gate first** — propose diff and wait for approval before writing files.
9. **Tests are required** — every agent/tool change needs a `@QuarkusTest`.
10. **Never invent IBM APIs** not present in `pom.xml` or this file.

---

## Domain model

| Type | Fields / Values |
|------|----------------|
| `CarInfo` (Panache entity) | `id` (Long), `make`, `model`, `year`, `condition`, `status` |
| `CarStatus` (enum) | `RENTED`, `AVAILABLE`, `AT_CLEANING`, `IN_MAINTENANCE`, `PENDING_DISPOSITION` |
| `CarAssignment` (enum) | `DISPOSITION`, `MAINTENANCE`, `CLEANING`, `NONE` |
| `FeedbackTask` (record) | `feedbackType` (`FeedbackType`), `systemInstructions` — factory methods: `FeedbackTask.cleaning()`, `.maintenance()`, `.disposition()` |
| `FeedbackType` (enum) | `CLEANING`, `MAINTENANCE`, `DISPOSITION` |
| `FeedbackAnalysisResults` (record) | `cleaningAnalysis`, `maintenanceAnalysis`, `dispositionAnalysis` |
| `CarConditions` (record) | `generalCondition` (String), `carAssignment` (CarAssignment) |

---

## API endpoints

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/car-management/return/{carNumber}` | Trigger car return workflow |
| `GET` | `/cars` | List all cars |
| `GET` | `/cars/{id}` | Single car by ID |

---

## Seeded fleet (import.sql)

| ID | Make | Model | Year | Initial status |
|----|------|-------|------|---------------|
| 1 | Mercedes-Benz | C-Class | 2024 | RENTED |
| 2 | BMW | X5 | 2025 | IN_MAINTENANCE |
| 3 | Audi | Q4 | 2025 | RENTED |
| 4 | Nissan | Altima | 2018 | AT_CLEANING |
| 5 | Ford | Focus | 2014 | RENTED |
| 6 | Toyota | Corolla | 2023 | RENTED |
| 7 | Honda | Civic | 2022 | RENTED |
| 8 | Ford | F-150 | 2024 | IN_MAINTENANCE |

---

## Agents (update this table as you complete each exercise)

| Interface | File | Role | outputKey | Exercise |
|-----------|------|------|-----------|---------|
| `CleaningAgent` | `agents/CleaningAgent.java` | Requests cleaning via `CleaningTool` | `analysisResult` | Ex 2 |
| `MaintenanceAgent` | `agents/MaintenanceAgent.java` | Returns maintenance plan (no tool) | `analysisResult` | Ex 3 |
| `FeedbackAnalysisAgent` | `agents/FeedbackAnalysisAgent.java` | Parameterized; classifies by task type | `feedbackAnalysis` | Ex 4 |
| `PricingAgent` | `agents/PricingAgent.java` | Estimates vehicle market value | `carValue` | Ex 5 |
| `DispositionAgent` | `agents/DispositionAgent.java` | Decides SCRAP/SELL/DONATE/KEEP | `dispositionAction` | Ex 5 |
| `CarConditionFeedbackAgent` | `agents/CarConditionFeedbackAgent.java` | Final CarConditions (typed return) | `carConditions` | Ex 5 |
| `FleetSupervisorAgent` | `agents/FleetSupervisorAgent.java` | `@SupervisorAgent` orchestrator | `supervisorDecision` | Ex 5 |

---

## Workflows

| Interface | Annotation | Sub-agents | Exercise |
|-----------|------------|------------|---------|
| `FeedbackAnalysisWorkflow` | `@ParallelMapperAgent` | `FeedbackAnalysisAgent` × 3 tasks | Ex 4 |
| `CarProcessingWorkflow` | `@SequenceAgent` | `FeedbackAnalysisWorkflow` → `FleetSupervisorAgent` → `CarConditionFeedbackAgent` | Ex 5 |
