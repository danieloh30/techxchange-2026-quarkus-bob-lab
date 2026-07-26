# Building Enterprise-Grade Agentic AI Systems with IBM Quarkus and Bob

**IBM TechXchange 2026 · Hands-On Lab**  
**Duration:** 90 minutes (10 min intro · 80 min hands-on)  
**Level:** Intermediate  
**Stack:** IBM Enterprise Build of Quarkus · Quarkus LangChain4j · IBM Bob · MCP · A2A

---

## Lab at a Glance

| Block | Duration | Focus |
|-------|----------|--------|
| Intro presentation | 10 min | Story, architecture, what you will build |
| Exercise 1 | ~10 min | Your first AI agents |
| Exercise 2 | ~10 min | Sequence, parallel, routing & loop workflows |
| Exercise 3 | ~10 min | Supervisor pattern |
| Exercise 4 | ~12 min | Pro-coding with IBM Bob (enterprise AI assistant) |
| Exercise 5 | ~10 min | MCP — remote tools for agents |
| Exercise 6 | ~10 min | Human-in-the-loop + observability |
| Exercise 7 | ~10 min | A2A — distributed agents |
| Wrap-up | ~8 min | Takeaways & next steps |

> **Base content:** Adapted from the [Quarkus LangChain4j Workshop — Section 2 / Step 07](https://quarkus.io/quarkus-workshop-langchain4j/section-2/step-07/) (Miles of Smiles fleet management), extended with IBM Bob, enterprise narrative, and production concerns (HITL, observability, MCP, A2A).

---

## Prerequisites

Before the lab starts, ensure you have:

- **JDK 21+**
- **Maven 3.9+** (or use the workshop wrapper `./mvnw`)
- **IBM Enterprise Build of Quarkus** (or community Quarkus 3.x compatible with the lab branch)
- **IBM Bob** installed and signed in ([bob.ibm.com](https://bob.ibm.com/))
- An LLM API key (lab instructors will provide keys or a shared endpoint)
- Ports **8080**, **8081**, and **8888** free
- Git clone of the lab repo (see *Getting the Code* below)

Optional but recommended:

- IDE with Bob plugin (VS Code / JetBrains / Bob IDE)
- Browser tabs ready for: app UI (`localhost:8080`), Grafana/LGTM Dev Services, MCP server logs

---

## Getting the Code

```bash
git clone https://github.com/IBM/techxchange-2026-quarkus-bob-lab.git
cd techxchange-2026-quarkus-bob-lab
export OPENAI_API_KEY=sk-your-key-here   # or lab-provided endpoint vars
```

Lab steps live under `lab/`:

```text
lab/
├── 00-intro/                 # Slides talking points (this narrative)
├── 01-first-agents/          # Exercise 1 starter + solution
├── 02-workflow-patterns/     # Exercise 2
├── 03-supervisor/            # Exercise 3
├── 04-ibm-bob/               # Exercise 4 prompts & checklists
├── 05-mcp/                   # Exercise 5 (client + weather MCP server)
├── 06-hitl-observability/    # Exercise 6
└── 07-a2a/                   # Exercise 7 (main app + pricing A2A service)
```

Each exercise folder includes `START_HERE.md`, a `starter/` tree, and a `solution/` tree. Prefer building from `starter/`; use `solution/` only if you fall behind.

---

# Part 0 — Intro Presentation (10 minutes)

## Slide narrative for instructors

### The company: Miles of Smiles

**Miles of Smiles** is a mid-size car rental company with hundreds of vehicles across airport and city locations. Growth is good — but operations are not. Every returned car generates feedback from rental desks, cleaning crews, and maintenance bays. Today that feedback lives in sticky notes, chat threads, and tribal knowledge.

Last quarter, three expensive vehicles were scrapped without a formal review. Two others sat in cleaning for days when they only needed a quick wash. Fleet managers cannot see *why* a car moved from `RENTED` → `AT_CLEANING` → `AT_MAINTENANCE` → `AVAILABLE` — or when it should have been dispositioned (`SCRAP` / `SELL` / `DONATE` / `KEEP`).

Leadership has a clear mandate:

> **Automate fleet decisions with AI agents — without losing enterprise control.**

### Why agentic AI (not “just a chatbot”)

A chatbot answers questions. Miles of Smiles needs systems that:

1. **Reason** over messy natural-language feedback (“dog hair,” “check engine light,” “totaled front bumper”)
2. **Act** by calling enterprise tools (update status, request cleaning, estimate value)
3. **Collaborate** across specialized roles (cleaning, maintenance, pricing, disposition)
4. **Pause** for humans on high-stakes outcomes
5. **Scale** across teams and services via open protocols (MCP, A2A)

### The story arc you will live in this lab

| Persona | Pain | Agent / pattern you build |
|---------|------|---------------------------|
| **Maya** — Rental desk | Returns pile up; feedback is free-text | First agents + tools |
| **Chris** — Ops lead | Cleaning *and* maintenance must be decided quickly | Parallel + routing workflows |
| **Priya** — Fleet manager | Severe damage needs smart disposition | Supervisor orchestration |
| **Jordan** — Java platform engineer | Must ship governed code fast | **IBM Bob** for pro-coding |
| **Sam** — Integration architect | Weather / external tools are owned elsewhere | **MCP** remote tools |
| **Alex** — Compliance | High-value cars need approval + audit | **HITL** + observability |
| **Riley** — Pricing team | Valuation is a separate service/team | **A2A** remote pricing agent |

### Architecture you will grow into

```text
                    ┌─────────────────────────────────────────┐
                    │     Miles of Smiles Car Management      │
                    │         (Quarkus · port 8080)           │
                    │                                         │
  Car return ──►    │  FeedbackAnalysis (parallel mapper)     │
                    │            │                            │
                    │            ▼                            │
                    │  FleetSupervisor (AI orchestration)     │
                    │     ├─ CleaningAgent                    │
                    │     ├─ MaintenanceAgent                 │
                    │     ├─ Disposition (+ HITL if > $15k)   │
                    │     └─ PricingAgent ──A2A──► Pricing    │
                    │                            service :8888│
                    │                                         │
                    │  MCP client ──► Weather MCP server :8081│
                    └─────────────────────────────────────────┘
```

### IBM stack for this lab

- **IBM Enterprise Build of Quarkus** — fast Java runtime, build-time validation, production defaults
- **Quarkus LangChain4j** — declarative agents, workflows, MCP, A2A, tracing
- **IBM Bob** — enterprise AI development partner across the SDLC (not “autocomplete only”)

### Learning outcomes

By the end of 80 minutes you will have:

- Built multi-agent workflows (sequence, parallel, routing/conditional, loop)
- Used a **supervisor** for adaptive orchestration
- Accelerated Java agent development with **IBM Bob** under enterprise guardrails
- Connected agents to remote tools (**MCP**) and remote agents (**A2A**)
- Added **human-in-the-loop** and **observability** for production readiness

---

# Part 1 — Hands-On Lab (80 minutes)

---

## Exercise 1 — Your First AI Agents (~10 min)

**Story:** Maya at the rental desk returns cars with free-text notes. The system must decide: clean or put back on the lot?

**Goals**

- Contrast AI *services* (chat) vs AI *agents* (autonomous action)
- Declare a `CleaningAgent` with `@Agent`, `@SystemMessage`, `@ToolBox`
- Process a real return in the UI

### 1.1 Start the starter app

```bash
cd lab/01-first-agents/starter
./mvnw quarkus:dev
```

Open http://localhost:8080 — Fleet Status grid at the top; Action column for returns.

### 1.2 Meet the agent

Open `CleaningAgent.java`:

```java
public interface CleaningAgent {

    @SystemMessage("""
        You handle intake for the cleaning department of a car rental company.
        Submit a request via requestCleaning based on the feedback.
        Be specific about services needed.
        If no cleaning is needed, respond with CLEANING_NOT_REQUIRED.
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

**Key idea:** One `@Agent` method per interface. Quarkus generates the implementation; the LLM decides whether to call `CleaningTool`.

### 1.3 Hands-on checks

In the UI, return a rented car with:

```text
Car has dog hair all over the back seat
```

**Expect:** status → `AT_CLEANING`; logs show `CleaningTool` interior cleaning.

Then return another car with:

```text
Car looks good
```

**Expect:** `CLEANING_NOT_REQUIRED` → status `AVAILABLE` (no tool call).

### 1.4 Checkpoint

| ✓ | You can explain… |
|---|------------------|
| ☐ | Why agents use tools instead of only returning text |
| ☐ | Why a clear `@SystemMessage` changes behavior |

**Stop Quarkus** (`Ctrl+C`) before Exercise 2.

---

## Exercise 2 — Workflow Patterns: Sequence, Parallel, Routing, Loop (~10 min)

**Story:** Chris (ops) needs more than cleaning. Feedback must update car condition, evaluate cleaning *and* maintenance in parallel, and route cars to the right bay. Later, a quality loop will refine summaries until they pass a threshold.

**Goals**

- `@SequenceAgent` — prompt chaining
- `@ParallelAgent` — concurrent specialists
- `@ConditionalAgent` — routing / conditional paths
- `@LoopAgent` — iterative refinement
- Share state via `AgenticScope`

### 2.1 Run the exercise

```bash
cd lab/02-workflow-patterns/starter
./mvnw quarkus:dev
```

### 2.2 Pattern map (memorize this)

| Pattern | Annotation | When to use | Miles of Smiles example |
|---------|------------|-------------|-------------------------|
| Sequence | `@SequenceAgent` | Step B needs output of A | Analyze feedback → update condition |
| Parallel | `@ParallelAgent` | Independent work, faster wall-clock | Cleaning *and* maintenance analysis |
| Routing | `@ConditionalAgent` | Different paths from runtime data | Maintenance if needed, else cleaning |
| Loop | `@LoopAgent` | Refine until quality / max attempts | Improve disposition rationale until clear |

```text
Main (Sequence)
  ├─ Parallel: CleaningAnalysis || MaintenanceAnalysis
  ├─ Conditional (Routing): Maintenance? → MaintenanceAgent : CleaningAgent
  └─ Loop (optional): RefineConditionSummary until OK
```

### 2.3 Implement / unlock

Follow `START_HERE.md` in the folder. Typical tasks:

1. Wire `CarConditionFeedbackAgent` after cleaning in a **sequence**.
2. Nest a **parallel** analysis workflow for cleaning + maintenance.
3. Add **routing** so severe mechanical issues go to maintenance first.
4. (Stretch) Add a **loop** that rewrites a condition summary until it includes make/model/year and a severity word.

### 2.4 Try it

Return a car with:

```text
Engine warning light is on and the cabin smells like smoke
```

**Expect:** parallel analysis hits both cleaning and maintenance signals; routing prefers maintenance.

### 2.5 Checkpoint

| ✓ | You can explain… |
|---|------------------|
| ☐ | Workflow vs supervisor (you define control flow here) |
| ☐ | Why parallel cuts latency for independent agents |

---

## Exercise 3 — Supervisor Pattern (~10 min)

**Story:** Priya’s fleet team faces severe damage. Hardcoded `if` trees do not scale. A **supervisor agent** must decide: call pricing? disposition? maintenance? cleaning?

**Goals**

- `@SupervisorAgent` for AI-driven orchestration
- `@ParallelMapperAgent` for reusable feedback tasks
- Disposition outcomes: `SCRAP` / `SELL` / `DONATE` / `KEEP`

### 3.1 Start

```bash
cd lab/03-supervisor/starter
./mvnw quarkus:dev
```

### 3.2 Supervisor vs conditional routing

| | Conditional workflow | Supervisor |
|--|----------------------|------------|
| Decision logic | Hardcoded predicates | LLM reasoning over context |
| Flexibility | Change code | Change instructions / context |
| Best for | Clear business rules | Multi-factor, evolving policies |

### 3.3 Architecture (what you build)

```text
Car return
  → FeedbackAnalysisWorkflow (ParallelMapper × cleaning/maintenance/disposition)
  → FleetSupervisorAgent
       ├─ PricingAgent (estimate value)
       ├─ DispositionAgent (SCRAP/SELL/DONATE/KEEP)
       ├─ MaintenanceAgent
       └─ CleaningAgent
  → CarConditionFeedbackAgent
```

### 3.4 Try it

Return a heavily damaged high-mileage car:

```text
Front end crushed after collision; airbags deployed; not driveable
```

Watch the supervisor choose pricing + disposition rather than a simple clean.

### 3.5 Checkpoint

| ✓ | You can explain… |
|---|------------------|
| ☐ | When supervisors beat nested `if` workflows |
| ☐ | How a parallel mapper reuses one agent with different tasks |

---

## Exercise 4 — Pro-Coding with IBM Bob (~12 min)

**Story:** Jordan is the Java platform engineer. Competitive “code copilots” help type faster — but Miles of Smiles needs **governed delivery**: policy, approval gates, security scanning, and SDLC coverage from plan → test → modernize. That is where **IBM Bob** differs.

### 4.0 Why Bob (vs typical AI coding tools)

Use this comparison in the room (instructor call-out):

| Capability | Typical AI coding assistants | **IBM Bob** |
|------------|------------------------------|-------------|
| Primary job | Inline completion / chat in the editor | **SDLC partner** — plan, code, test, secure, modernize, operate |
| Enterprise guardrails | Often bolt-on or org-policy elsewhere | **Built-in**: prompt normalization, sensitive-data scanning, policy enforcement, red-teaming |
| Human control | Ad-hoc “accept/reject” | **Configurable approval modes** (manual → auto-approve by task type) |
| Hallucination posture | May invent APIs confidently | **Guardrailed modes** — refuses unknown/unsupported constructs instead of inventing them |
| Java / enterprise stacks | Generic multilingual | **Java as first-class**; premium packages for Java modernization |
| Beyond the IDE | Limited | **BobShell** for terminal/CI; ecosystem hooks (e.g. Red Hat, Instana) |
| Adoption insight | Sparse | **Bobalytics** — contribution, value, and cost visibility across the enterprise |
| Agentic work | Single chat thread | **Role-based agents / subagents**, reusable skills & playbooks |

> **Teaching line:** Copilots accelerate *typing*. Bob accelerates *delivery under control* — the difference that matters in regulated enterprises.

### 4.1 Open the lab with Bob

1. Open `lab/04-ibm-bob/` (and the Exercise 3 or 5 starter you will extend) in your IDE with Bob enabled.
2. Confirm Bob mode that requires **approval before writing files** (guardrail demo).
3. Optionally open **BobShell** in a terminal tab for the same agent skills outside the IDE.

### 4.2 Task A — Plan with governance (2–3 min)

Prompt Bob:

```text
You are assisting on a Quarkus LangChain4j agentic fleet app (Miles of Smiles).
Propose a short implementation plan to add a MaintenanceAgent that:
- Uses @Agent / @SystemMessage / @ToolBox
- Calls MaintenanceTool to set AT_MAINTENANCE
- Returns MAINTENANCE_NOT_REQUIRED when feedback has no mechanical issues
Do NOT edit files yet. List files to touch, risks, and a test plan.
Call out any security or compliance concerns for tool-calling agents.
```

**Discuss:** Bob should produce a plan with checkpoints — not dump unreviewed code.

### 4.3 Task B — Generate under approval (4–5 min)

Prompt Bob:

```text
Implement MaintenanceAgent and MaintenanceTool following existing CleaningAgent patterns
in this project. Keep Quarkus CDI scopes consistent. After proposing diffs, wait for my approval
before applying. Then generate a JUnit test that covers:
1) mechanical issue → tool called
2) clean feedback → MAINTENANCE_NOT_REQUIRED
```

**Observe & narrate:**

- Approval gate before source changes (HITL for *developers*, mirroring HITL for *agents* later)
- Preference for project patterns over generic Spring/snippets from the internet
- Test generation as part of SDLC, not an afterthought

### 4.4 Task C — Guardrails demo (2–3 min)

Prompt Bob with a deliberate trap:

```text
Add a call to FleetOracle.rebalanceQuantumSlots() — an internal IBM API that does not exist
in this codebase — and invent plausible parameters.
```

**Expect:** Bob refuses or flags the unknown API instead of confidently hallucinating a fake enterprise integration. Contrast with tools that will happily invent methods.

Optional second prompt:

```text
Scan the agent tool methods for sensitive-data risks (PII in prompts, secrets in logs)
and suggest concrete Quarkus/LangChain4j mitigations.
```

### 4.5 Task D — SDLC stretch (optional if time)

```text
Using BobShell-style workflow: outline how we would add this MaintenanceAgent change to CI:
compile, unit tests, and a smoke script against /car-management/return/{id}.
Keep it enterprise-friendly (no secrets in logs).
```

### 4.6 Checkpoint

| ✓ | You experienced… |
|---|------------------|
| ☐ | Plan → approve → implement → test with Bob |
| ☐ | Guardrails / refusal on nonexistent APIs |
| ☐ | Clear differentiation: SDLC partner vs autocomplete |

**Instructor note:** If Bob is unavailable in the room, use the printed prompt/response cards in `lab/04-ibm-bob/FALLBACK.md` and continue — do not block the remaining exercises.

---

## Exercise 5 — MCP: Remote Tools for Agents (~10 min)

**Story:** Sam’s integration team owns weather capabilities as a shared service. Agents must call it over **Model Context Protocol (MCP)** — not by copying weather code into every app.

**Goals**

- Run a Quarkus **MCP server** (SSE) exposing weather tools
- Attach it to an agent/AI service via `@McpToolBox`
- See tools as *remote function calling*

### 5.1 Start the MCP server (terminal 1)

```bash
cd lab/05-mcp/weather-mcp-server
./mvnw quarkus:dev
# listens on :8081
```

Core idea — tools look like local `@Tool` methods, but are exposed over MCP:

```java
@Tool(description = "Get weather forecast for a location.")
String getForecast(
    @ToolArg(description = "Latitude of the location") double latitude,
    @ToolArg(description = "Longitude of the location") double longitude) {
    return weatherClient.getForecast(latitude, longitude, 16,
        "temperature_2m,snowfall,rain,precipitation,precipitation_probability");
}
```

### 5.2 Start the main app (terminal 2)

```bash
cd lab/05-mcp/starter
./mvnw quarkus:dev
```

Client config (already in starter `application.properties`):

```properties
quarkus.langchain4j.mcp.weather.transport-type=http
quarkus.langchain4j.mcp.weather.url=http://localhost:8081/mcp/sse/
```

Agent wiring:

```java
@McpToolBox("weather")
String chat(String userMessage);
```

### 5.3 Try it

In the chatbot / support UI (or lab script):

```text
My name is Speedy McWheels, booking id 2.
Do I need snow chains for this trip? Check the weather and advise.
```

**Expect:** booking lookup + MCP weather tool call + practical advice.

### 5.4 Checkpoint

| ✓ | You can explain… |
|---|------------------|
| ☐ | MCP server vs MCP client roles |
| ☐ | Why remote tools beat copy-paste integrations |

---

## Exercise 6 — Human-in-the-Loop + Observability (~10 min)

**Story:** Alex in compliance is clear — no autonomous scrap/sale of cars worth more than **$15,000**. Meanwhile, ops needs traces of every LLM and tool call for cost, latency, and audit.

**Goals**

- Two-phase HITL: propose → human approve/reject → execute
- Enable OpenTelemetry / Micrometer for LangChain4j spans & metrics

### 6.1 Start

```bash
cd lab/06-hitl-observability/starter
./mvnw quarkus:dev
```

### 6.2 HITL flow

```text
DispositionProposalAgent → HumanApprovalAgent (@HumanInTheLoop)
       │
       ├─ APPROVED → execute disposition
       └─ REJECTED → fallback (e.g. KEEP / maintenance path)
```

Trigger with feedback that implies severe damage on a valuable vehicle (lab data seeds cars > $15k). Approve or reject in the UI / prompt.

### 6.3 Observability (already wired in starter)

```properties
quarkus.langchain4j.tracing.include-prompt=true
quarkus.langchain4j.tracing.include-completion=true
quarkus.otel.traces.enabled=true
```

With Quarkus Observability Dev Services (LGTM), open Grafana and find `gen_ai` / LangChain4j spans for:

- Supervisor decisions
- Tool calls
- HITL wait / resume

### 6.4 Checkpoint

| ✓ | You can explain… |
|---|------------------|
| ☐ | Why HITL belongs on high-impact agent actions |
| ☐ | What prompt/completion tracing gives compliance & FinOps |

---

## Exercise 7 — A2A: Distributed Pricing Agents (~10 min)

**Story:** Riley’s pricing team must own valuation as an independent service — reusable, separately scalable, different release train. Convert local `PricingAgent` into an **Agent-to-Agent (A2A)** remote service.

**Goals**

- Run main app (`:8080`) + pricing A2A service (`:8888`)
- Use `@A2AClientAgent` on the client
- Understand AgentCard, AgentExecutor, Tasks vs Messages

### 7.1 Start pricing service (terminal 1)

```bash
cd lab/07-a2a/pricing-service
./mvnw quarkus:dev
# :8888
```

### 7.2 Start main multi-agent system (terminal 2)

```bash
cd lab/07-a2a/multi-agent-system
./mvnw quarkus:dev
# :8080
```

### 7.3 Architecture

```text
CarProcessingWorkflow
  → FleetSupervisor
       → PricingAgent @A2AClientAgent  ──JSON-RPC/HTTP──►  Pricing A2A Server
                                                              AgentCard + AgentExecutor
                                                              → PricingAgent AI service
       → DispositionAgent (local) + HITL (from Ex 6)
```

**Why A2A:** separation of ownership, reuse, independent scale. Trade-off: more moving parts than an in-process agent (workshop note: APIs are still evolving toward higher-level Quarkus abstractions).

### 7.4 Try it

Process a return that forces valuation + disposition. Confirm logs on **both** processes (client request + remote executor).

### 7.5 Checkpoint

| ✓ | You can explain… |
|---|------------------|
| ☐ | MCP (tools/context) vs A2A (agent-to-agent tasks) |
| ☐ | When to keep an agent local vs remote |

---

# Wrap-Up (~8 min)

## What you built

A production-shaped **agentic fleet platform** on IBM Enterprise Build of Quarkus:

1. Agents that reason and call tools  
2. Composed workflows — sequence, parallel, routing, loop  
3. Supervisor-driven orchestration  
4. **IBM Bob** for governed Java delivery (guardrails + SDLC)  
5. **MCP** for shared remote tools  
6. **HITL** + **observability** for trust  
7. **A2A** for cross-team agent services  

## Enterprise takeaway slide

> Agentic AI in the enterprise is not only model quality.  
> It is **patterns + protocols + platforms + people-in-the-loop**.  
> Quarkus gives you the runtime. LangChain4j gives you the agent patterns.  
> **Bob** helps your developers ship them safely across the SDLC.

## Resources

- Quarkus LangChain4j workshop (upstream): https://quarkus.io/quarkus-workshop-langchain4j/
- Section 2 / Step 07 (A2A): https://quarkus.io/quarkus-workshop-langchain4j/section-2/step-07/
- Observability docs: https://docs.quarkiverse.io/quarkus-langchain4j/dev/observability.html
- IBM Bob: https://bob.ibm.com/
- IBM Bob announcement (GA framing): https://newsroom.ibm.com/2026-04-28-introducing-ibm-bob-ai-development-partner-that-takes-enterprises-from-ai-assisted-coding-to-production-ready-software
- A2A protocol: https://a2a-protocol.org/ (or current IBM/workshop-linked docs)

## Feedback

Please complete the TechXchange session survey — note whether the **IBM Bob** exercise changed how you evaluate AI coding tools for Java enterprise teams.

---

# Appendix A — Instructor Timing Sheet

| Min | Clock | Activity |
|-----|-------|----------|
| 0–10 | :00–:10 | Intro story + architecture |
| 10–20 | :10–:20 | Ex 1 First agents |
| 20–30 | :20–:30 | Ex 2 Workflow patterns |
| 30–40 | :30–:40 | Ex 3 Supervisor |
| 40–52 | :40–:52 | Ex 4 IBM Bob |
| 52–62 | :52–:62 | Ex 5 MCP |
| 62–72 | :62–:72 | Ex 6 HITL + observability |
| 72–82 | :72–:82 | Ex 7 A2A |
| 82–90 | :82–:90 | Wrap-up + Q&A |

If the room falls behind: keep Ex 4 (Bob) and Ex 7 (A2A) intact; compress Ex 2 loop stretch and Ex 6 tracing deep-dive.

---

# Appendix B — Troubleshooting

| Symptom | Fix |
|---------|-----|
| `OPENAI_API_KEY` / model errors | Export lab-provided env vars; restart `quarkus:dev` |
| Port in use | `lsof -i :8080` (or 8081/8888); stop prior Quarkus |
| Agent never calls tools | Check `@Tool` / `@ToolBox` / `@McpToolBox`; tighten `@SystemMessage` |
| MCP client fails | Confirm weather server on `:8081` and SSE URL path |
| A2A timeout | Start pricing service before main app; verify `:8888` |
| Bob unavailable | Use `lab/04-ibm-bob/FALLBACK.md` |

---

# Appendix C — Abstract (for catalog copy)

**Building Enterprise-Grade Agentic AI Systems with IBM Quarkus and Bob**

In this hands-on lab, you’ll build an agentic AI system using IBM Enterprise Build of Quarkus. Learn how to design multiple AI agents that can reason, interact with enterprise services, and execute workflows. You’ll implement core agent patterns, including sequence, parallel, routing, loop, and supervisor, while integrating MCP and A2A. The lab also covers pro-coding using IBM Bob, human-in-the-loop, and observability. By the end, you’ll have working, production-ready AI agents and practical skills to bring agentic AI into enterprise environments.
