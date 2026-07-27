# Building Enterprise-Grade Agentic AI Systems with IBM Quarkus and Bob

**IBM TechXchange 2026 · Hands-On Lab**  
**Duration:** 90 minutes (10 min intro · 80 min hands-on)  
**Level:** Intermediate Java developer  
**Stack:** IBM Enterprise Build of Quarkus · Quarkus LangChain4j · IBM Bob · MCP · A2A

---

## Lab at a Glance

| Block | Time | Cumulative | Focus |
|-------|------|------------|-------|
| Intro presentation | 10 min | :10 | Story, architecture, what you will build |
| Exercise 1 | 12 min | :22 | **IBM Bob setup + author `lab/AGENTS.md`** (Bob used here) |
| Exercise 2 | 10 min | :32 | **Code-along** — `CleaningAgent` + `CleaningTool` (write Java directly) |
| Exercise 3 | 10 min | :42 | **Code-along** — `MaintenanceAgent` + live `@SystemMessage` tuning |
| Exercise 4 | 10 min | :52 | **Code-along** — `@ParallelMapperAgent` + `@Output` + `AgenticScope` |
| Exercise 5 | 15 min | :67 | **Code-along** — full supervisor pipeline: pricing, disposition, sequence |
| Exercise 6 | 10 min | :77 | Human-in-the-loop + OpenTelemetry observability |
| Exercise 7 | 10 min | :87 | MCP + A2A — remote tools and distributed agents |
| Wrap-up | 8 min | :95 | Patterns cheat sheet + Q&A |

> **Working project:** `lab/` — a single Quarkus starter you build incrementally across all exercises.
> **Exercises 2–5 are direct code-along:** open each stub file, read the `// TODO` comments, and type in the code shown in the guide. IBM Bob is used only in Exercise 1 to establish `AGENTS.md` — this minimises Bob Coin consumption for the rest of the lab.
> Reference solutions in `exercises/` are fallbacks — linked at the top of each exercise guide.

> **Base content:** Adapted from the [Quarkus LangChain4j Workshop — Section 2 / Step 07](https://quarkus.io/quarkus-workshop-langchain4j/section-2/step-07/) (Miles of Smiles fleet management), extended with IBM Bob, AGENTS.md cost-efficiency techniques, enterprise narrative, and production concerns (HITL, observability, MCP, A2A).

---

## Prerequisites

Before the lab starts, confirm:

| Requirement | Check |
|-------------|-------|
| **JDK 25** (`java -version`) | ✓ |
| **Maven 3.9+** (or use included `./mvnw`) | ✓ |
| **IBM Enterprise Build of Quarkus** / Quarkus **3.37.4** | ✓ |
| **IBM Bob** installed and signed in ([bob.ibm.com](https://bob.ibm.com/)) | ✓ |
| LLM API key — instructors provide `OPENAI_API_KEY` or lab endpoint | ✓ |
| Ports **8080**, **8081**, **8888** free | ✓ |
| Git clone of this repo (see below) | ✓ |

Optional but recommended:
- IDE with Bob plugin (VS Code / JetBrains / Bob IDE) — Tab-complete for agent prompts
- Browser tabs pre-opened: `localhost:8080` (app UI), Grafana Dev Service (Exercise 6)
- Second terminal ready for two-process exercises (5, 7)

---

## Getting the Code

```bash
git clone https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab.git
cd techxchange-2026-quarkus-bob-lab
export OPENAI_API_KEY=sk-your-lab-key-here   # or lab-provided endpoint variables
```

Repository layout:

```text
AGENTS.md                             # Bob project context file (token-efficiency)
docs/                                 # All lab instructions + images
├── LAB_GUIDE.md                      # This file
├── 00-intro/ … 07-a2a/               # Per-exercise START_HERE.md + Bob materials
└── images/

exercises/                            # Completed Quarkus solution projects
├── 01-first-agents/solution
├── 02-workflow-patterns/{solution-sequence, solution-composed}
├── 03-supervisor/solution
├── 04-ibm-bob/solution
├── 05-mcp/{solution, weather-mcp-server}
├── 06-hitl-observability/{solution, observability-reference}
└── 07-a2a/solution/{multi-agent-system, remote-a2a-agent}
```

> **All solutions are pre-built.** You run them, read the code, and make small targeted
> changes. You are not building from scratch — focus is on *understanding patterns* and
> *using Bob to accelerate governed development*.

---

# Part 0 — Intro Presentation (10 minutes)

## Slide narrative for instructors

### The company: Miles of Smiles

**Miles of Smiles** is a mid-size car rental company with hundreds of vehicles across airport and city locations. Fleet operations are chaotic. Every returned car generates free-text feedback from rental desks, cleaning crews, and maintenance bays. Today that feedback lives in sticky notes, chat threads, and tribal knowledge.

Last quarter's incident report:
- Three expensive vehicles were **scrapped without a formal review** — $80k lost
- Two others **sat in AT_CLEANING for 3 days** when a quick wash was all they needed
- Fleet managers cannot see *why* a car moved `RENTED → AT_CLEANING → IN_MAINTENANCE → AVAILABLE` or whether it should have been dispositioned (`SCRAP` / `SELL` / `DONATE` / `KEEP`)

Leadership mandate:

> **Automate fleet decisions with AI agents — without losing enterprise control.**

### Why agentic AI (not "just a chatbot")

| Chatbot | Agent |
|---------|-------|
| Answers questions | Takes actions |
| One turn | Multi-step reasoning |
| No side effects | Calls tools, mutates state |
| No memory between calls | Scope / context across workflow |
| Single model | Composed specialists |

Miles of Smiles needs systems that:

1. **Reason** over messy natural-language feedback ("dog hair," "check engine light," "totaled front bumper")
2. **Act** by calling enterprise tools (update status, request cleaning, estimate value)
3. **Collaborate** across specialized roles (cleaning, maintenance, pricing, disposition)
4. **Pause** for humans on high-stakes outcomes — compliance requires it
5. **Scale** across teams via open protocols (MCP, A2A) without copy-pasting logic

### The story arc

| # | Persona | Pain point | Pattern you learn |
|---|---------|-----------|-------------------|
| 1 | **Maya** — Rental desk | Free-text returns pile up; status is manual | First agents + `@ToolBox` |
| 2 | **Chris** — Ops lead | Cleaning *and* maintenance must be decided fast | Parallel + routing workflows |
| 3 | **Priya** — Fleet manager | Severe damage needs adaptive disposition | `@SupervisorAgent` orchestration |
| 4 | **Jordan** — Java platform engineer | Must ship governed code; copilots hallucinate | **IBM Bob** + AGENTS.md |
| 5 | **Sam** — Integration architect | Weather / external tools are owned elsewhere | **MCP** remote tools |
| 6 | **Alex** — Compliance officer | High-value cars need approval + audit trail | **HITL** + OpenTelemetry |
| 7 | **Riley** — Pricing team | Valuation is a separate service/team | **A2A** remote pricing agent |

### Architecture you will grow into

```
                    ┌──────────────────────────────────────────────┐
                    │      Miles of Smiles Car Management          │
                    │          (Quarkus · port 8080)               │
                    │                                              │
  Car return ──►   │  CarProcessingWorkflow (@SequenceAgent)      │
                    │    │                                         │
                    │    ├─► FeedbackAnalysisWorkflow              │
                    │    │        (@ParallelMapperAgent × 3 tasks) │
                    │    │                                         │
                    │    ├─► FleetSupervisorAgent (@SupervisorAgent)│
                    │    │        ├─ CleaningAgent   (@ToolBox)    │
                    │    │        ├─ MaintenanceAgent              │
                    │    │        ├─ DispositionAgent + HITL gate  │
                    │    │        └─ PricingAgent ──A2A──► :8888  │
                    │    │                                         │
                    │    └─► CarConditionFeedbackAgent             │
                    │                                              │
                    │  MCP client ──► Weather MCP server :8081     │
                    └──────────────────────────────────────────────┘
```

### IBM stack for this lab

| Layer | IBM component | Role |
|-------|--------------|------|
| Runtime | IBM Enterprise Build of Quarkus 3.37.4 | Build-time agent validation, fast startup |
| AI extension | Quarkus LangChain4j 1.12.0 | Declarative agents, workflows, MCP, A2A |
| Dev tooling | IBM Bob | SDLC partner: plan → code → test → secure |
| Context efficiency | `AGENTS.md` (project root) | Targeted Bob context — avoids token-bloat scans |

### Learning outcomes

After 80 minutes you will be able to:

- Declare agents with `@Agent`, `@SystemMessage`, `@ToolBox` and explain what Quarkus generates
- Compose multi-agent workflows: sequence, parallel, routing, loop
- Use `@SupervisorAgent` for adaptive AI orchestration vs hardcoded routing
- Author an `AGENTS.md` to make IBM Bob cost-efficient on agentic projects
- Connect agents to external tools via **MCP** and to remote teams via **A2A**
- Add **human-in-the-loop** gates and read **OpenTelemetry** spans for compliance + FinOps

---

# Part 1 — Hands-On Lab (80 minutes)

---

## Exercise 1 — Your First AI Agents (~10 min)

**Story:** Maya at the rental desk returns cars with free-text notes. The system must decide: clean or put back on the lot?

**Goals**

- Contrast AI *services* (chat completion) vs AI *agents* (autonomous tool-calling action)
- Read `CleaningAgent` — understand every annotation
- Observe tool-call trace in logs: agent decides → calls `CleaningTool` → status flips to `AT_CLEANING`

### 1.1 Start the solution app

```bash
cd exercises/01-first-agents/solution
./mvnw quarkus:dev
```

Open http://localhost:8080 — Fleet Status grid at the top; **Return** button in the Action column.

### 1.2 Anatomy of `CleaningAgent`

Open [`CleaningAgent.java`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/blob/main/exercises/01-first-agents/solution/src/main/java/com/carmanagement/agentic/agents/CleaningAgent.java):

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

**Annotation breakdown:**

| Annotation | What it does |
|-----------|-------------|
| `@SystemMessage` | Sets the agent's role and hard rules. The LLM follows this on *every* call. |
| `@UserMessage` | Builds the per-call prompt from method parameters via `{placeholder}` substitution. |
| `@Agent` | Marks this interface as a Quarkus-managed declarative agent. CDI bean generated at build time. |
| `@ToolBox` | Tells LangChain4j which `@Tool`-annotated methods the LLM may call. The LLM decides *if and when* to call them. |

**Why an interface?** Quarkus generates the implementation at build time using byte-buddy. You declare *intent*; the framework + LLM provide *behavior*. This is the fundamental shift from imperative to declarative AI programming.

Open [`CleaningTool.java`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/blob/main/exercises/01-first-agents/solution/src/main/java/com/carmanagement/agentic/tools/CleaningTool.java):

```java
@ApplicationScoped
public class CleaningTool {

    @Tool("Requests a cleaning with the specified options")
    @Transactional
    public String requestCleaning(
            Integer carNumber, String carMake, String carModel, Integer carYear,
            boolean exteriorWash, boolean interiorCleaning,
            boolean detailing, boolean waxing, String requestText) {

        CarInfo carInfo = CarInfo.findById(carNumber);
        if (carInfo != null) {
            carInfo.status = CarStatus.AT_CLEANING;
            carInfo.persist();
        }
        return generateCleaningSummary(...);
    }
}
```

**Key tool contract:**
1. Return a `String` summary — the LLM reads this as the tool result and continues reasoning.
2. `@Transactional` is required because `CarInfo.persist()` touches JPA.
3. The `@Tool` description is the only thing the LLM sees — make it precise.

### 1.3 Hands-on checks

**Test 1 — Dirty car (tool should be called)**

Return any rented car with:
```text
Car has dog hair all over the back seat
```

Expected result:
- Status → `AT_CLEANING`  
- Logs show `CleaningTool` called with `interiorCleaning=true`  
- Log line: `🚗 CleaningTool result: Cleaning requested for ...`

**Test 2 — Clean car (tool should NOT be called)**

Return another rented car with:
```text
Car looks good, no issues
```

Expected result:
- Response contains `CLEANING_NOT_REQUIRED`  
- Status stays `AVAILABLE`  
- No `CleaningTool` log line

**Trace anatomy** (what you'll see in the console):

```
[LangChain4j] → LLM request: "Car has dog hair..."
[LangChain4j] ← LLM response: tool_call requestCleaning(carNumber=1, interiorCleaning=true, ...)
[CleaningTool] executing requestCleaning for car 1
[LangChain4j] → LLM request (tool result): "Cleaning requested for Toyota Corolla..."
[LangChain4j] ← LLM response: "Interior cleaning scheduled for car #1"
```

This is the **agent loop**: LLM decides → tool executes → LLM resumes with result.

### 1.4 Stretch — Tighten the `@SystemMessage`

Change the cleaning threshold to be more strict:

```text
Only request cleaning for SEVERE dirt (pet hair, food stains, strong odors).
For light dust or minor marks, respond with "CLEANING_NOT_REQUIRED".
```

Re-test `"minor scuff on the door panel"` — does it now skip cleaning?

### 1.5 Checkpoint

| ✓ | You can explain… |
|---|-----------------|
| ☐ | Why agents call tools instead of only returning text |
| ☐ | What `@ToolBox` does at the LLM decision layer vs the runtime layer |
| ☐ | The agent loop: request → optional tool call → final response |
| ☐ | What happens if `@Tool` description is vague or missing |

**Stop Quarkus** (`Ctrl+C`) before Exercise 2.

---

## Exercise 2 — Your First Agent: CleaningAgent + CleaningTool (~10 min)

**Story:** Maya at the rental desk needs automated cleaning decisions. When a car is returned dirty, `CleaningAgent` must call `CleaningTool` to set `AT_CLEANING`; when it's clean, it responds with `CLEANING_NOT_REQUIRED` — no tool call, no status mutation.

**You work in:** `lab/` (keep Quarkus running — hot reload)

**Goals**

- Declare your first `@Agent` interface: `@SystemMessage`, `@UserMessage`, `@ToolBox`
- Implement `CleaningTool.requestCleaning()` with `@Transactional` + JPA mutation
- Understand the agent loop: LLM decides → tool executes → LLM resumes

### 2.1 Open the exercise guide

➡ **[docs/02-workflow-patterns/START_HERE.md](02-workflow-patterns/START_HERE.md)**

The guide shows you the exact code to type into each stub file and explains every annotation.

### 2.2 Checkpoint (after completing the guide)

| ✓ | You can explain… |
|---|-----------------|
| ☐ | Why `@Transactional` is on the tool method but not on the agent method |
| ☐ | What `outputKey` does and what breaks if you omit it |
| ☐ | The agent loop: LLM decides → tool executes → LLM reads result |
| ☐ | Why this is an interface (not a class) |

---

## Exercise 3 — MaintenanceAgent + @SystemMessage Tuning (~10 min)

**Story:** Chris (ops) needs to go beyond cleaning decisions — cars also need maintenance plans. He also wants to understand how `@SystemMessage` threshold changes affect agent behavior without redeploying.

**You work in:** `lab/` (keep Quarkus running)

**Goals**

- Declare `MaintenanceAgent` — same `@Agent` pattern, but text-only output (no `@ToolBox`)
- Live `@SystemMessage` experiment: see how changing a string changes agent policy
- Understand the guardrail demo: Bob refuses hallucinated APIs

### 3.1 Open the exercise guide

➡ **[docs/03-supervisor/START_HERE.md](03-supervisor/START_HERE.md)**

The guide walks you through the `@SystemMessage` content to type, the tuning experiment steps, and the guardrail test.

### 3.2 Checkpoint (after completing the guide)

| ✓ | You can explain… |
|---|-----------------|
| ☐ | When an agent needs `@ToolBox` vs when text-only output is correct |
| ☐ | Why `@SystemMessage` is a policy declaration, not code logic |
| ☐ | What `AGENTS.md` rule 10 protects against (hallucinated APIs) |

---

## Exercise 4 — Parallel Workflow: @ParallelMapperAgent (~10 min)

**Story:** Chris needs cleaning, maintenance, AND disposition analysis — all three running concurrently to cut latency. One interface, three concurrent LLM calls, dynamic `@SystemMessage` per task.

**You work in:** `lab/` (keep Quarkus running)

**Goals**

- Declare `FeedbackAnalysisAgent` with dynamic `@SystemMessage("{task.systemInstructions}")`
- Declare `FeedbackAnalysisWorkflow` with `@ParallelMapperAgent` + `@Output` aggregation
- Understand `AgenticScope` data flow: how results flow from parallel workers to downstream agents

### 4.1 Open the exercise guide

➡ **[docs/04-ibm-bob/START_HERE.md](04-ibm-bob/START_HERE.md)**

The guide shows the exact code for both files and explains why `itemsProvider`, `outputKey`, and `@Output` each exist.

### 4.2 Pattern reference

| Pattern | Annotation | When to use |
|---------|------------|-------------|
| Sequence | `@SequenceAgent` | B needs output of A |
| Parallel fan-out | `@ParallelMapperAgent` | Same agent, multiple inputs, concurrent |
| Aggregation | `@Output` | Collect parallel results into a typed record |
| Routing | `@ConditionalAgent` | Different paths from runtime decision |

### 4.3 Checkpoint (after completing the guide)

| ✓ | You can explain… |
|---|-----------------|
| ☐ | How `@SystemMessage("{task.systemInstructions}")` enables one interface for 3 roles |
| ☐ | Why `@Output` is a static method, not an LLM call |
| ☐ | What `itemsProvider = "tasks"` tells the framework |
| ☐ | How `outputKey` wires parallel results to downstream agents |

---

## Exercise 5 — Full Multi-Agent System: Supervisor + Sequence (~15 min)

**Story:** Priya (fleet), Riley (pricing), and Maya (cleaning) all need to work together on a single car return. A `@SupervisorAgent` decides which specialists to invoke. A `@SequenceAgent` chains the whole pipeline. Policy lives in prose, not `if/else`.

**You work in:** `lab/` (keep Quarkus running)

**Goals**

- Implement `PricingAgent`, `DispositionAgent`, `CarConditionFeedbackAgent`
- Implement `FleetSupervisorAgent` with `@SupervisorAgent` + `@SupervisorRequest`
- Implement `CarProcessingWorkflow` with `@SequenceAgent` + `@Output`
- Verify all three test paths: clean return, dirty return, severe damage + disposition

### 5.1 Open the exercise guide

➡ **[docs/05-mcp/START_HERE.md](05-mcp/START_HERE.md)**

The guide provides exact code for all five files with step-by-step annotations and explanations.

### 5.2 Supervisor vs conditional routing

| | `@ConditionalAgent` | `@SupervisorAgent` |
|---|---|---|
| Decision logic | Hardcoded Java predicates | LLM reasoning on natural-language prompt |
| Policy change | Code change + redeploy | Edit `@SupervisorRequest` string + hot reload |
| Sub-agent selection | Compile-time routing table | Runtime multi-factor reasoning |
| When to use | Stable, binary, well-defined rules | Multi-factor, evolving, nuanced policy |

### 5.3 Checkpoint (after completing the guide)

| ✓ | You can explain… |
|---|-----------------|
| ☐ | Full pipeline: `CarProcessingWorkflow` → `FeedbackAnalysisWorkflow` → `FleetSupervisorAgent` → `CarConditionFeedbackAgent` |
| ☐ | Why policy lives in `@SupervisorRequest` not in `if/else` Java |
| ☐ | Why `CarConditionFeedbackAgent` returns `CarConditions` (record) not `String` |
| ☐ | Why the supervisor test path skips `CleaningAgent` for severe damage |

---

## Exercise 6 — Human-in-the-Loop + Observability (~10 min)

**Story:** Alex in compliance is firm: no autonomous disposition of vehicles worth more than **$15,000**. Every LLM call must be traceable for cost auditing and SOX-style event logs. This exercise wires the approval gate and turns on OpenTelemetry tracing.

**Goals**

- Two-phase HITL: `DispositionProposalAgent` → human approve/reject → execute or fallback
- Enable `gen_ai.*` OpenTelemetry spans for LangChain4j calls
- Read Grafana/LGTM for LLM call latency, token counts, and HITL wait time

### 6.1 Start

```bash
cd exercises/06-hitl-observability/solution
./mvnw quarkus:dev
```

Quarkus **Observability Dev Services** will auto-start a local LGTM (Loki + Grafana + Tempo + Mimir) stack. Wait for the log line:

```
DevServices for Observability started — Grafana: http://localhost:3000
```

### 6.2 HITL flow

```
Car return (value > $15,000 AND severe damage)
  │
  ▼
DispositionProposalAgent
  │  returns: proposed_action=SCRAP, rationale="airbags deployed, chassis bent"
  │
  ▼
@HumanInTheLoop gate
  │
  ├─► APPROVED  → execute disposition (SCRAP/SELL/DONATE)
  └─► REJECTED  → fallback: KEEP + route to IN_MAINTENANCE for assessment
```

**To trigger:** Use a seeded car with value > $15,000 (check `import.sql`) and submit feedback:
```text
Major collision damage, frame is bent, airbags deployed, estimated repair > car value
```

In the UI, you'll see an **Awaiting Approval** state. Approve once, reject once. Observe different outcomes.

### 6.3 OpenTelemetry configuration

In `application.properties` (already in solution):

```properties
quarkus.langchain4j.tracing.include-prompt=true
quarkus.langchain4j.tracing.include-completion=true
quarkus.otel.traces.enabled=true
```

> **Warning — production:** `include-prompt=true` exports full prompt text to your tracing backend.
> This can include PII from `@UserMessage` templates. Disable or redact before production.

### 6.4 Reading LLM spans in Grafana

Open http://localhost:3000 → Explore → Tempo → Search for traces with service `car-management`.

Key span attributes to examine:

| Attribute | What it tells you |
|-----------|------------------|
| `gen_ai.usage.input_tokens` | Tokens consumed per LLM call — FinOps input |
| `gen_ai.usage.output_tokens` | Generated tokens — often more expensive |
| `gen_ai.request.model` | Which model was used |
| `langchain4j.tool.name` | Name of tool the LLM called |
| `langchain4j.hitl.status` | `PENDING` / `APPROVED` / `REJECTED` |
| `duration` | End-to-end latency including LLM round-trips |

**FinOps framing for the room:** At 1,000 dispositions/day with avg 500 tokens/call and GPT-4o pricing, uncontrolled prompt sizes (e.g., not using `AGENTS.md`) could mean $40–$80/day in unnecessary context tokens. Tracing makes this visible.

### 6.5 Checkpoint

| ✓ | You can explain… |
|---|-----------------|
| ☐ | Why HITL is a compliance requirement, not just a feature |
| ☐ | What `include-prompt=true` gives compliance vs what it risks for PII |
| ☐ | How `gen_ai.*` spans enable FinOps cost visibility per workflow |
| ☐ | The HITL approved vs rejected flow and what "fallback" looks like |

---

## Exercise 7 — A2A: Distributed Pricing Agents (~10 min)

**Story:** Riley's pricing team must own vehicle valuation as an independent service. It needs a separate release cycle, independent scalability, and the ability to be reused by other IBM systems beyond Miles of Smiles. Solution: convert `PricingAgent` into an **Agent-to-Agent (A2A)** remote service.

**Goals**

- Run main app (`:8080`) + pricing A2A service (`:8888`) as separate processes
- `@A2AClientAgent` on the caller side — local interface, remote execution
- **AgentCard**, **AgentExecutor**, **Task** vs **Message** semantics
- Trace cross-service call in logs — correlate client request ID with remote executor

### 7.1 Start pricing service (terminal 1)

```bash
cd exercises/07-a2a/solution/remote-a2a-agent
./mvnw quarkus:dev
# port :8888
```

The remote service exposes an `AgentCard` at `GET /.well-known/agent.json`:

```json
{
  "name": "pricing-agent",
  "description": "Estimates car market value for fleet disposition decisions",
  "url": "http://localhost:8888/a2a",
  "capabilities": ["pricing", "valuation"]
}
```

### 7.2 Start main multi-agent system (terminal 2)

```bash
cd exercises/07-a2a/solution/multi-agent-system
./mvnw quarkus:dev
# port :8080
```

### 7.3 A2A architecture deep-dive

```
CarProcessingWorkflow (main app :8080)
  └─► FleetSupervisorAgent
           └─► PricingAgent @A2AClientAgent
                    │
                    │  JSON-RPC / HTTP
                    │  POST /a2a/tasks/send
                    │  { "task": { "input": { "carMake": "Honda", "carModel": "Civic", ... } } }
                    │
                    ▼
             remote-a2a-agent :8888
               AgentExecutor.execute(task)
                 └─► PricingAgent (local AI service)
                          └─► LLM call → "$10,710"
               Task result returned to caller
```

**A2A concepts:**

| Concept | Meaning | Analogy |
|---------|---------|---------|
| `AgentCard` | Capability metadata — what can this agent do? | Service contract / OpenAPI spec |
| `AgentExecutor` | Request handler on the server — processes an incoming `Task` | JAX-RS endpoint for agents |
| `Task` | Long-running, stateful goal with input/output envelope | Async job submission |
| `Message` | Single synchronous exchange within a task | Synchronous REST call |

**MCP vs A2A — the key distinction:**

| | MCP | A2A |
|--|-----|-----|
| What travels | **Tool calls** (functions with typed args) | **Agent tasks** (goals with natural-language input) |
| Who owns | Tool server (same or different team) | Agent service (autonomous, separate team) |
| State | Stateless per call | Optionally stateful (task lifecycle) |
| Best for | Shared capabilities (weather, search) | Delegated reasoning (pricing, legal review) |

### 7.4 Try it

Return a car that requires valuation + disposition:
```text
High-mileage vehicle, transmission slipping, market value uncertain
```

Correlate logs across **both** processes:
- **Client (8080):** `[A2AClient] sending task to http://localhost:8888/a2a`
- **Remote (8888):** `[AgentExecutor] received task, invoking PricingAgent`
- **Remote (8888):** `[PricingAgent] estimated value: $7,200`
- **Client (8080):** `[A2AClient] task completed, result: $7,200`

### 7.5 Trade-offs to discuss

| Factor | Local agent | Remote A2A agent |
|--------|-------------|-----------------|
| Latency | In-process | +HTTP round-trip |
| Ownership | Shared codebase | Independent repo + release |
| Scaling | Scale whole app | Scale pricing service independently |
| Failure mode | Shared crash domain | Network partition risk |
| Reuse | Single app | Any A2A-compatible client |

### 7.6 Checkpoint

| ✓ | You can explain… |
|---|-----------------|
| ☐ | MCP (tools/context) vs A2A (agent-to-agent tasks) — one sentence each |
| ☐ | What `AgentCard` is and why it's the service contract |
| ☐ | When to keep an agent local vs make it remote |
| ☐ | One trade-off of A2A distribution you would raise in a design review |

---

# Wrap-Up (~8 min)

## What you built

A production-shaped **agentic fleet platform** on IBM Enterprise Build of Quarkus:

| # | Pattern | IBM tech | Business value |
|---|---------|----------|----------------|
| 1 | Agents + tools | `@Agent` `@ToolBox` | Automate free-text decisions |
| 2 | Composed workflows | `@SequenceAgent` `@ParallelMapperAgent` | Parallel analysis reduces latency |
| 3 | Supervisor orchestration | `@SupervisorAgent` `@SupervisorRequest` | Policy-as-prompt, not hardcoded if/else |
| 4 | Governed development | IBM Bob + `AGENTS.md` | Ship fast under enterprise SDLC control |
| 5 | Remote tools | MCP + `@McpToolBox` | Reuse shared capabilities across teams |
| 6 | Trust + audit | HITL + OpenTelemetry `gen_ai.*` spans | Compliance, FinOps, human oversight |
| 7 | Distributed agents | A2A + `@A2AClientAgent` | Team ownership, independent scale |

## Enterprise takeaway

> Agentic AI in the enterprise is not only model quality.  
> It is **patterns + protocols + platforms + people-in-the-loop**.  
>
> **Quarkus** gives you the build-time-validated, production-hardened runtime.  
> **LangChain4j** gives you the declarative agent patterns.  
> **IBM Bob** helps your developers ship them safely across the full SDLC.  
> **AGENTS.md** makes Bob cost-efficient — front-load context once, save tokens every turn.

## Patterns cheat sheet (take this with you)

```
Goal: step-by-step → @SequenceAgent
Goal: concurrent work → @ParallelMapperAgent
Goal: data-driven branching → @ConditionalAgent
Goal: refine until good enough → @LoopAgent
Goal: adaptive multi-agent → @SupervisorAgent
Goal: share tools across teams → MCP + @McpToolBox
Goal: delegate to remote team agent → A2A + @A2AClientAgent
Goal: human approval on high-stakes → @HumanInTheLoop
```

## Resources

- Quarkus LangChain4j workshop (upstream): https://quarkus.io/quarkus-workshop-langchain4j/
- Section 2 / Step 07 (A2A): https://quarkus.io/quarkus-workshop-langchain4j/section-2/step-07/
- Observability docs: https://docs.quarkiverse.io/quarkus-langchain4j/dev/observability.html
- IBM Bob: https://bob.ibm.com/
- IBM Bob GA announcement: https://newsroom.ibm.com/2026-04-28-introducing-ibm-bob-ai-development-partner-that-takes-enterprises-from-ai-assisted-coding-to-production-ready-software
- A2A protocol: https://a2a-protocol.org/
- `AGENTS.md` context file (this repo root): see patterns and rules for token-efficient Bob usage

## Feedback

Please complete the TechXchange session survey. The survey has one specific question:
**"Did the IBM Bob exercise + AGENTS.md approach change how you would evaluate AI coding tools for Java enterprise teams?"**

Your answer directly shapes next year's lab content.

---

# Appendix A — Instructor Timing Sheet

| Min | Clock | Activity | Risk if running long |
|-----|-------|----------|----------------------|
| 0–10 | :00–:10 | Intro — story + architecture + stack | Trim architecture diagram walk |
| 10–20 | :10–:20 | Ex 1 — First agents | Skip stretch (tighten `@SystemMessage`) |
| 20–30 | :20–:30 | Ex 2 — Workflow patterns | Skip loop stretch |
| 30–40 | :30–:40 | Ex 3 — Supervisor | Skip "Try minor return" |
| 40–52 | :40–:52 | Ex 4 — IBM Bob + AGENTS.md | Keep Task A + C; skip Task D |
| 52–62 | :52–:62 | Ex 5 — MCP | Skip security note discussion |
| 62–72 | :62–:72 | Ex 6 — HITL + observability | Skip Grafana span deep-dive |
| 72–82 | :72–:82 | Ex 7 — A2A | Skip trade-offs table discussion |
| 82–90 | :82–:90 | Wrap-up + Q&A | Cut to cheat sheet only |

**Priority order if behind:** Keep Ex 4 (Bob + AGENTS.md) and Ex 7 (A2A) intact — these are the highest TechXchange differentiation points. Compress Ex 2 loop stretch, Ex 5 security discussion, and Ex 6 Grafana walk.

---

# Appendix B — Troubleshooting

| Symptom | Likely cause | Fix |
|---------|-------------|-----|
| `OPENAI_API_KEY` / model errors | Key not exported | `export OPENAI_API_KEY=sk-...`; restart `./mvnw quarkus:dev` |
| Port in use | Prior process still running | `lsof -i :8080` (or `:8081`, `:8888`); `kill -9 <pid>` |
| Agent never calls tools | `@Tool` desc too vague, `@ToolBox` missing, or `@SystemMessage` too permissive | Tighten `@SystemMessage`; verify `@ToolBox(WidgetTool.class)` annotation |
| `outputKey` resolution error | Missing `outputKey` on a workflow agent | Every agent used inside `@SequenceAgent` or `@SupervisorAgent` needs `@Agent(outputKey="...")` |
| MCP client fails to connect | Server not running or wrong URL path | Confirm `:8081` up; URL must end in `/mcp/sse/` |
| MCP tool not found | Tool name mismatch | Check `@Tool` description; restart both processes |
| A2A timeout | Pricing service not started first | Start `:8888` before `:8080`; verify `GET http://localhost:8888/.well-known/agent.json` |
| Supervisor invokes wrong agents | `@SupervisorRequest` prompt ambiguous | Add explicit `DO NOT invoke X` instructions for negative cases |
| Bob invents APIs | AGENTS.md not loaded | Explicitly instruct Bob: "Read AGENTS.md before answering" |
| Bob unavailable | Network / seats | Use `docs/04-ibm-bob/FALLBACK.md`; continue with remaining exercises |
| LGTM / Grafana not starting | Docker/Podman not running | Start container runtime; or skip Ex 6 observability section |

---

# Appendix C — Abstract (for TechXchange catalog)

**Session title:** Building Enterprise-Grade Agentic AI Systems with IBM Quarkus and Bob

**Abstract:**

Enterprise AI is moving beyond chatbots. In this 90-minute hands-on lab, you will build a production-shaped agentic fleet-management system using the **IBM Enterprise Build of Quarkus** and **Quarkus LangChain4j**, then use **IBM Bob** to accelerate governed development of that system.

You will implement the five core agentic workflow patterns (sequence, parallel, routing, loop, supervisor), wire agents to remote tools via **MCP**, and decompose a monolithic agent into independently owned services via **A2A**. Human-in-the-loop gates and **OpenTelemetry** tracing give you the compliance and FinOps visibility that regulated enterprises require.

A key lab focus is **token-efficient AI engineering**: you will author an `AGENTS.md` project-context file that gives IBM Bob targeted, upfront knowledge of your agent model — eliminating redundant codebase scans and reducing per-task token consumption by 2,000–5,000 tokens for complex multi-agent projects.

**Attendees will leave able to:**
- Declare AI agents as Java interfaces with `@Agent`, `@SystemMessage`, `@ToolBox`
- Compose multi-agent workflows and explain the trade-offs of each pattern
- Use `AGENTS.md` to make AI-assisted development cost-efficient
- Integrate with shared tool services (MCP) and remote agent teams (A2A)
- Apply HITL and observability for production-readiness

**Level:** Intermediate  
**Prerequisites:** Java, basic familiarity with REST APIs  
**Lab repo:** https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab
