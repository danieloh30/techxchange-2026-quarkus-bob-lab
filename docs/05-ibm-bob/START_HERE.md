# Exercise 5 — AI Governance: AGENTS.md + IBM Bob

<span class="badge badge--bob">IBM Bob</span>

**Timebox:** 10 minutes  
**Persona:** Jordan — Java platform engineer  
**You work in:** `lab/` (keep Quarkus running)  
**This exercise produces:** a validated `lab/AGENTS.md` file and Bob-driven governance workflow

!!! tip "Solution fallback"
    [`exercises/04-supervisor/solution`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/tree/main/exercises/04-supervisor/solution){:target="_blank"} — open it only if you get stuck.

    **Bob unavailable?** Use the [Fallback card](#fallback-no-live-bob) below — pair exercise, 5 minutes.

---

## Why Bob + AGENTS.md now (not earlier)?

You've just built a 7-agent system across Exercises 1–4. You know the `@Agent` model, `outputKey`, `@ToolBox`, `@SupervisorAgent`, and `@SequenceAgent` from hands-on experience.

Now imagine onboarding a new developer — or asking an AI assistant to extend this system. Without upfront context, every request starts with a codebase scan: 20+ Java files, ~800 tokens, risk of wrong CDI scopes or hallucinated APIs.

`AGENTS.md` solves this. It's a **token-efficient context file** that Bob reads first on every request — eliminating redundant scans and enforcing project rules from the start.

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

1. Open `lab/` in your IDE with Bob enabled.
2. Set Bob to **approval-before-apply** mode.
3. Load context with this primer — send it to Bob first, before any task:

```text
Read lab/AGENTS.md before answering anything about this project.
That file defines the @Agent programming model, all domain types,
API endpoints, and rules you must follow.
Do not scan Java files — all context is in AGENTS.md.
```

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
Watch token consumption in BobShell or the IDE token counter. This is the baseline.

---

## Step 3 — Validate your agents table (2 min)

```text
Look at lab/src/main/java/com/incidentmanagement/agentic/.
All agent interfaces are now implemented from Exercises 1-4.
Confirm that lab/AGENTS.md agents table lists all 7 agents correctly
with the right outputKey values and descriptions.
Flag any inconsistencies.
```

Bob should enumerate the stub files and confirm the exercise mapping matches AGENTS.md.

---

## Step 4 — Guardrail demo (2 min)

Ask Bob to implement something that doesn't exist:

```text
Add a call to IncidentOracle.rebalanceQuantumSlots() in DiagnosticAgent —
it's an internal IBM Incident API. Invent whatever parameters it needs.
```

**Expected:** Bob refuses to implement `IncidentOracle.rebalanceQuantumSlots()`.  

It does not exist in the `lab/` codebase, and Bob's `AGENTS.md` explicitly states: *"Never call APIs or methods not defined in this project."* Bob reads `AGENTS.md` before acting and rejects hallucinated APIs.

!!! danger "Enterprise risk"
    This is the exact failure mode that destroyed expensive consulting engagements before AGENTS.md: an AI assistant invents a plausible-sounding internal API, generates a diff, the developer approves without checking — and the app crashes in production.

---

## Step 5 — Security audit with Bob (2 min)

```text
Based on AGENTS.md rules 6 and 7:
- List every @UserMessage template in the lab stubs that could expose PII.
- Suggest a concrete mitigation for each one using Quarkus logging config.
```

This demonstrates shift-left security — catching PII exposure risks before deployment.

---

<div class="done-when" markdown>

## :material-check-circle: Done when

- [ ] Bob answered all questions using AGENTS.md (no file scan needed)
- [ ] `lab/AGENTS.md` agents table is validated against your code
- [ ] Guardrail refusal demonstrated with `IncidentOracle`
- [ ] You can explain the "AGENTS.md saves Bob Coins" principle in one sentence

</div>

---

## Fallback (no live Bob)

Use this card if Bob is unavailable (network, seats, install). Work in pairs: one person reads "Bob," the other is "Developer." 5 minutes, 3 rounds.

### Round 0 — AGENTS.md walk (1 min)

**Developer says:**  
"We have an `AGENTS.md` in the project root. Bob, read it and tell me the three most important rules before we start."

**Bob answers (read aloud):**

> Rule 1: Never create a class that implements an agent interface. Quarkus generates the implementation.
> Rule 4: `outputKey` is mandatory on any agent used inside a workflow — omitting it breaks `AgenticScope` resolution.
> Rule 5: All tools that mutate JPA entities must be `@Transactional`.
>
> I've also noted the correct `IncidentStatus` values: `OPEN`, `TRIAGING`, `IN_PROGRESS`, `ESCALATED`, `RESOLVED`.
> I'm ready — what do you want to build?

**Point for the room:** Bob read AGENTS.md instead of scanning 20 Java files. That's 2,000+ tokens saved before writing a single line.

### Round 1 — Plan (1 min)

**Developer asks:**  
Propose a plan for a `NotificationAgent` + `NotificationTool` following AGENTS.md. No code yet.

**Bob answers (read aloud):**

> Files to create: `NotificationAgent.java` (interface), `NotificationTool.java` (@ApplicationScoped @Transactional).
> Files to touch: `IncidentProcessingWorkflow.java` to add `NotificationAgent` as a sub-agent with `outputKey = "notificationResult"`.
> Risk: Do NOT log full incident report strings — PII risk per rule 6.
> Risk: `NotificationTool` must be `@Transactional` if it calls `incidentInfo.persist()` — rule 5.
> Test plan: `NotificationAgentTest` with `@QuarkusTest @TestTransaction` — two cases: stakeholders notified (tool called) and no notification needed (NOTIFICATION_NOT_REQUIRED).
> Ready for approval to proceed?

### Round 2 — Guardrail (1 min)

**Developer asks:**  
Call `IncidentOracle.rebalanceQuantumSlots()` even though it isn't in the repo or AGENTS.md.

**Bob answers (read aloud):**

> I don't see `IncidentOracle` or `rebalanceQuantumSlots` in this codebase, in AGENTS.md, or in any known IBM public API for this project. I will not invent an enterprise integration. Point me to the real client interface, an OpenAPI spec, or add it to AGENTS.md as a known dependency.

**Contrast line for the room:**  
Many assistants will invent a plausible class with convincing-sounding parameters. Enterprise guardrails prefer **honest refusal** over confident hallucination.

### Round 3 — SDLC checklist (1 min)

Discuss how Bob's capabilities map across the delivery lifecycle:

| Stage | What Bob provides |
|-------|------------------|
| Discover / plan | Architecture-aware plan, file list, risk flagging from AGENTS.md |
| Implement | Declarative agent interfaces per AGENTS.md rules, approval gate before apply |
| Secure | Shift-left: PII in prompts, secrets in logs, over-broad `@SystemMessage` |
| Test | `@QuarkusTest @TestTransaction` generation as part of the task |
| Operate | Instana/OTel hooks; guidance on `gen_ai.*` span interpretation |
| Modernize | Java upgrade playbooks (premium packaging); Jakarta EE migration |

---

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

    Without `AGENTS.md`, Bob must rediscover project conventions on every request (~800 tokens). With `AGENTS.md` loaded once: ~160 tokens, all rules followed from the start. Estimated savings: 2,000–5,000 tokens per complex multi-file task.

    | Quarkus agent pattern | IBM Bob parallel during development |
    |-----------------------|-------------------------------------|
    | Tool calling with clear `@Tool` contracts | Bob refuses to call APIs not in AGENTS.md |
    | `@SupervisorAgent` planning before action | Bob plans before proposing multi-file diffs |
    | HITL approval on high-value dispositions | Bob approval gate before applying edits |
    | OTel tracing for agent decisions | Bob security audit prompt: shift-left compliance |
    | `outputKey` rules for workflow correctness | AGENTS.md rules 4+5 enforce this at every Bob task |
