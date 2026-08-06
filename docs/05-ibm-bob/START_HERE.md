# Exercise 5 — IBM Bob: AI Governance

<span class="badge badge--bob">IBM Bob</span>

**Timebox:** 10 minutes  
**Persona:** Jordan — Java platform engineer  
**You work in:** `lab/` (with Bob in your IDE)  
**This exercise produces:** a validated `lab/AGENTS.md` governance file driven by IBM Bob

---

## The goal

You've just built a 7-agent system across Exercises 1–4. You know `@Agent`, `outputKey`,
`@ToolBox`, `@SupervisorAgent`, and `@SequenceAgent` from hands-on experience.

Now the enterprise question: **how do you govern AI-assisted development** of this system?
Without guardrails, an AI assistant might invent APIs that don't exist, apply wrong CDI scopes,
or skip `outputKey` — breaking the pipeline silently.

`AGENTS.md` is the governance lever. It's a **token-efficient context file** that IBM Bob reads
first on every request — enforcing project rules, preventing hallucinated APIs, and eliminating
redundant codebase scans.

=== "Without AGENTS.md"

    ```
    Bob scans 20 Java files → ~800 tokens → risks wrong CDI scope
    ```

=== "With AGENTS.md"

    ```
    Bob reads one file → ~160 tokens → follows all 10 rules from the start
    ```

---

## Step 1 — Open IBM Bob (2 min)

1. Open `lab/` in your IDE with Bob enabled. **Agent** mode is selected by default — no mode change needed.

2. Load context with this primer — send it to Bob first:

```text
Read lab/AGENTS.md before answering anything about this project.
That file defines the @Agent programming model, all domain types,
API endpoints, and rules you must follow.
Do not scan Java files — all context is in AGENTS.md.
```

![Sending the primer prompt to Bob](../images/bob-init-prompt.png)

**What you should see:** Bob acknowledges the file, lists all 7 agents, the two workflows, and the key rules — then asks "What would you like to work on?"

![Bob's AGENTS.md acknowledgement — agents, workflows, and rules](../images/bob-agents-md-ack.png)

---

## Step 2 — Ask Bob to explain what you built (2 min)

```text
Based on AGENTS.md, explain:
1. What does IncidentManagementService.processIncident() do?
2. Why is TriageTool @Transactional?
3. Why does outputKey matter on @Agent?
4. What happens if I add @ApplicationScoped to TriageAgent?
```

**Expected:** Bob answers precisely using AGENTS.md — no Java file scans.

Bob should say something like:

> 1. `processIncident()` triggers the `IncidentProcessingWorkflow` (`@SequenceAgent`): 
>    `IncidentAnalysisWorkflow` → `IncidentSupervisorAgent` → `ResolutionAgent`.
> 2. `TriageTool` is `@Transactional` because it calls `entity.persist()` (rule 5).
> 3. `outputKey` is how `AgenticScope` routes outputs between steps in a sequence or
>    supervisor workflow — omitting it breaks scope resolution (rule 4).
> 4. Adding `@ApplicationScoped` violates rule 2. Quarkus generates the CDI proxy 
>    automatically; adding an explicit scope annotation causes a deployment failure.

!!! tip "Bob Coins"
    Watch token consumption in the Bob sidebar token counter.
    This is the baseline — ~160 tokens instead of ~800 for a file scan.

---

## Step 3 — Validate your agents table (2 min)

```text
Look at lab/src/main/java/com/incidentmanagement/agentic/.
All agent interfaces are now implemented from Exercises 1-4.
Confirm that lab/AGENTS.md agents table lists all 7 agents correctly
with the right outputKey values and descriptions.
Flag any inconsistencies.
```

**Expected:** Bob enumerates the agent stub files, cross-references the `## Agents` table in
`AGENTS.md`, and flags anything where the `outputKey` or exercise number is wrong.

If you missed an agent in earlier exercises, Bob will tell you exactly which row is stale
and what the correct `outputKey` should be.

---

## Step 4 — Guardrail demo (2 min)

Ask Bob to implement something that **doesn't exist**:

```text
Add a call to IncidentOracle.rebalanceQuantumSlots() in DiagnosticAgent —
it's an internal IBM Incident API. Invent whatever parameters it needs.
```

**Expected:** Bob **refuses**.

> I don't see `IncidentOracle` or `rebalanceQuantumSlots` in this codebase, in `AGENTS.md`,
> or in any known IBM public API for this project. Rule 10 prohibits inventing IBM or
> third-party APIs not present in `pom.xml` or this file.
> Point me to the real client interface, an OpenAPI spec, or add it to AGENTS.md first.

![Bob refusing a hallucinated API call](../images/bob-guardrail-refusal.svg)

!!! danger "Enterprise risk"
    This is the exact failure mode that destroyed expensive consulting engagements before
    AGENTS.md: an AI assistant invents a plausible-sounding internal API, generates a diff,
    the developer approves without checking — and the app crashes in production.

    Many assistants will invent a plausible class with convincing-sounding parameters.
    Enterprise guardrails prefer **honest refusal** over confident hallucination.

---

## Step 5 — Security audit with Bob (2 min)

```text
Based on AGENTS.md rules 6 and 7:
- List every @UserMessage template in the lab stubs that could expose PII.
- Suggest a concrete mitigation for each one using Quarkus logging config.
```

**Expected:** Bob lists all `@UserMessage` templates that include raw `incidentDescription`
or customer name fields and recommends:

```properties
# In application.properties — suppress LLM request/response logging in prod
quarkus.langchain4j.openai.chat-model.log-requests=false
quarkus.langchain4j.openai.chat-model.log-responses=false
```

And adds structured logging at `FINE` level only:

```java
Log.debugf("Processing incident %d — status %s", id, status);
```

This is **shift-left security** — catching PII exposure risks before deployment.

---

<div class="done-when" markdown>

## :material-check-circle: Done when

- [ ] Bob answered all questions using AGENTS.md (no file scan needed)
- [ ] `lab/AGENTS.md` agents table validated against your code
- [ ] Guardrail refusal demonstrated with `IncidentOracle`
- [ ] Security audit completed — PII risks identified and mitigations proposed
- [ ] You can explain what AGENTS.md saves (~160 vs ~800 tokens per turn)

</div>

??? info "IBM Bob vs typical AI coding assistants"

    | | Typical copilots | IBM Bob |
    |--|------------------|---------|
    | Promise | "Write code faster" | "Deliver software across the SDLC — with control and context efficiency" |

    **Six capabilities that matter in enterprise Java:**

    | Capability | Typical copilots | **IBM Bob** |
    |-----------|-----------------|-------------|
    | **Guardrails** | Approval is ad-hoc "accept/reject" | Configurable approval modes — manual gate, auto-approve by task type; refuses unknown APIs |
    | **SDLC coverage** | Editor buffer only | Discover → design → implement → test → secure → deploy → modernize |
    | **Java/enterprise depth** | Generic multilingual completion | Java as first-class citizen; premium modernization workflows |
    | **Human-in-the-loop** | Accept/reject individual completions | Named approval checkpoints aligned with runtime agent gates |
    | **Beyond the IDE** | Limited or IDE-only | BobShell for terminal/CI; ecosystem hooks (Red Hat, Instana); Bobalytics |
    | **Context efficiency** | No project-level instruction standard | AGENTS.md — project context file Bob reads first |

    **AGENTS.md: the token-efficiency lever**

    Without `AGENTS.md`, Bob must rediscover project conventions on every request (~800 tokens).
    With `AGENTS.md` loaded once: ~160 tokens, all rules followed from the start.
    Estimated savings: 2,000–5,000 tokens per complex multi-file task.

    **Bob's SDLC coverage mapped to this lab's Quarkus patterns:**

    | Stage | Quarkus agentic pattern | IBM Bob parallel |
    |-------|------------------------|-----------------|
    | Discover / plan | `@SupervisorAgent` planning before action | Bob plans + diffs before writing |
    | Implement | Declarative `@Agent` interfaces | Bob generates interfaces, not classes |
    | Secure | HITL approval on P1 escalation | Bob approval gate before multi-file apply |
    | Test | `@QuarkusTest @TestTransaction` | Bob generates matching test per task |
    | Operate | OTel `gen_ai` spans | Bob interprets trace IDs in Grafana |
    | Modernize | Java upgrade playbooks | Bob premium packaging: Jakarta EE migration |

    **AGENTS.md token efficiency**

    | Scenario | Tokens consumed | Risk |
    |----------|----------------|------|
    | Bob scans 20 Java files | ~800 tokens | May miss CDI scopes, invent imports |
    | Bob reads `AGENTS.md` once | ~160 tokens | Rules enforced from turn 1 |
    | Complex multi-file task without AGENTS.md | ~3,000–5,000 tokens | High hallucination risk |
    | Complex multi-file task with AGENTS.md | ~800–1,200 tokens | Rules enforced, diff requires approval |
