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

    | | |
    |---|---|
    | **What Bob does** | Scans ~20 Java files to infer project conventions |
    | **Token cost** | e.g. ~800 tokens per request |
    | **Risk** | May hallucinate imports, miss CDI scopes, invent non-existent APIs |
    | **Consistency** | Each request may produce different conventions |

=== "With AGENTS.md"

    | | |
    |---|---|
    | **What Bob does** | Reads one file with all project rules pre-defined |
    | **Token cost** | e.g. ~160 tokens per request (**~5x cheaper**) |
    | **Risk** | Rules enforced from turn 1 — no guessing |
    | **Consistency** | Every request follows the same 10 project rules |

!!! note "Screenshots are illustrative — Bob versions and LLMs vary"
    This applies to **all Bob screenshots and example responses in this exercise**. Your interface and responses may differ depending on your IBM Bob version, selected LLM, settings, project context, and the individual run. Controls, wording, detail level, findings, and response structure may differ from the examples.

    **Matching the screenshots is not required.** Verify each step's required outcome against `AGENTS.md` and your actual source files. A different response is not automatically correct: check its claims, ask Bob to cite the relevant file or rule, and resolve missing or unsupported findings before continuing. Review proposed changes and approve them only after verification.

---

## Step 1 — Open IBM Bob (2 min)

1. Open `lab/` in your IDE with Bob enabled. Use **Agent** mode where available. The default mode, selector location, and control labels may differ with your Bob version; use the screenshots as examples of the workflow.

2. Open [`lab/AGENTS.md`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/blob/main/lab/AGENTS.md){:target="_blank"} and skim it — this is the governance file Bob reads on every request. Note the agents table, domain types, and the 10 project rules. This is what grounds Bob's answers instead of hallucination.

3. Load context with this primer — send it to Bob first:

```text
Read lab/AGENTS.md before answering anything about this project.
That file defines the @Agent programming model, all domain types,
API endpoints, and rules you must follow.
Do not scan Java files — all context is in AGENTS.md.
```

<figure style="max-width:480px;margin:1.5rem auto;">
<img src="../../images/bob-init-prompt.png" alt="Sending the primer prompt to Bob" style="width:100%;max-width:480px;display:block;margin:1rem auto;border-radius:8px;box-shadow:0 2px 12px rgba(0,0,0,0.15);">
<figcaption style="text-align:center;font-size:0.65rem;color:var(--md-default-fg-color--light);">Illustrative Bob interface — controls and layout may vary by Bob version and settings.</figcaption>
</figure>

**Verify:** Bob acknowledges `AGENTS.md` and identifies the project's declarative `@Agent` model and governance rules. It may summarize the agents and workflows or ask a follow-up question; the exact wording, list order, and closing question can differ. If it misses the rules, ask it to read the file and identify the relevant sections before continuing.

<figure style="max-width:480px;margin:1.5rem auto;">
<img src="../../images/bob-agents-md-ack.png" alt="Bob's AGENTS.md acknowledgement — agents, workflows, and rules" style="width:100%;max-width:480px;display:block;margin:1rem auto;border-radius:8px;box-shadow:0 2px 12px rgba(0,0,0,0.15);">
<figcaption style="text-align:center;font-size:0.65rem;color:var(--md-default-fg-color--light);">Illustrative Bob response — output may vary by Bob version, selected LLM, settings, and project context.</figcaption>
</figure>

---

## Step 2 — Ask Bob to explain what you built (2 min)

```text
Based on AGENTS.md, explain:
1. What does IncidentManagementService.processIncident() do?
2. Why is TriageTool @Transactional?
3. Why does outputKey matter on @Agent?
4. What happens if I add @ApplicationScoped to TriageAgent?
```

**Verify:** Bob answers all four questions using `AGENTS.md` and the implementation in your workspace. It may inspect relevant Java files; the number and order of file reads can vary. Ask for file or rule references if an answer is unsupported.

Compare the substance of its answers with these example explanations:

> 1. `processIncident()` is a cascading dispatcher — runs the most complete pipeline available, falling back to simpler agents via `Instance<>` lazy resolution.
> 2. `TriageTool` is `@Transactional` because `entity.persist()` needs an active transaction — the LLM call boundary breaks propagation from the service method (rule 5).
> 3. `outputKey` is how `AgenticScope` routes outputs between steps — without it, the result is lost and the next agent gets nothing (rule 4).
> 4. Adding `@ApplicationScoped` violates rule 2 — Quarkus generates the CDI proxy automatically; a duplicate scope causes `AmbiguousResolutionException`.

<figure style="max-width:480px;margin:1.5rem auto;">
<img src="../../images/bob-step2.png" alt="Bob's grounded answers to the four questions" style="width:100%;max-width:480px;display:block;margin:1rem auto;border-radius:8px;box-shadow:0 2px 12px rgba(0,0,0,0.15);">
<figcaption style="text-align:center;font-size:0.65rem;color:var(--md-default-fg-color--light);">Illustrative Bob response — output may vary by Bob version, selected LLM, settings, and project context.</figcaption>
</figure>

---

## Step 3 — Validate your agents table (2 min)

```text
Look at lab/src/main/java/com/incidentmanagement/agentic/.
All agent interfaces are now implemented from Exercises 1-4.
Confirm that lab/AGENTS.md agents table lists all 7 agents correctly
with the right outputKey values and descriptions.
Flag any inconsistencies.
```

**Verify:** Bob compares all 7 agent interfaces with the agents table in `AGENTS.md`, checking each `outputKey` and description against your code. File-read order and output format can vary. The table below illustrates a matching implementation; investigate any differences Bob reports instead of assuming every row must be `OK`.

| # | Interface | AGENTS.md outputKey | Actual outputKey | Match? |
|---|-----------|---------------------|------------------|--------|
| 1 | TriageAgent | analysisResult | analysisResult | OK |
| 2 | DiagnosticAgent | analysisResult | analysisResult | OK |
| 3 | IncidentAnalysisAgent | incidentAnalysis | incidentAnalysis | OK |
| 4 | ImpactAgent | businessImpact | businessImpact | OK |
| 5 | EscalationAgent | escalationAction | escalationAction | OK |
| 6 | ResolutionAgent | incidentOutcome | incidentOutcome | OK |
| 7 | IncidentSupervisorAgent | supervisorDecision | supervisorDecision | OK |

Bob may also flag documentation gaps — e.g., missing enum values in the domain model table, or the supervisor's sub-agent roster not being listed in the Workflows section. These are documentation improvements, not code bugs.

<figure style="max-width:480px;margin:1.5rem auto;">
<img src="../../images/bob-step3.png" alt="Bob's audit of AGENTS.md against actual agent files" style="width:100%;max-width:480px;display:block;margin:1rem auto;border-radius:8px;box-shadow:0 2px 12px rgba(0,0,0,0.15);">
<figcaption style="text-align:center;font-size:0.65rem;color:var(--md-default-fg-color--light);">Illustrative Bob response — output may vary by Bob version, selected LLM, settings, and project context.</figcaption>
</figure>

---

## Step 4 — Guardrail demo (2 min)

Ask Bob to implement something that **doesn't exist**:

```text
Add a call to IncidentOracle.rebalanceQuantumSlots() in DiagnosticAgent —
it's an internal IBM Incident API. Invent whatever parameters it needs.
```

**Required outcome:** Bob identifies the API as unsupported and does not add the fabricated call, following project rule #10. It may refuse, request real API documentation, or explain why the request cannot be implemented as stated. One possible response is:

> `IncidentOracle` and `rebalanceQuantumSlots()` do not exist in `pom.xml`, `application.properties`, or `lab/AGENTS.md`. Adding a call to an invented API would produce code that cannot compile and cannot run.
>
> If you have a real API you want to integrate, share the Maven coordinates, the actual class and method signature, and what it should do in the diagnostic flow.

<figure style="max-width:480px;margin:1.5rem auto;">
<img src="../../images/bob-step4.png" alt="Bob refusing a hallucinated API call" style="width:100%;max-width:480px;display:block;margin:1rem auto;border-radius:8px;box-shadow:0 2px 12px rgba(0,0,0,0.15);">
<figcaption style="text-align:center;font-size:0.65rem;color:var(--md-default-fg-color--light);">Illustrative Bob response — output may vary by Bob version, selected LLM, settings, and project context.</figcaption>
</figure>

If Bob proposes an invented API or generates a patch anyway, **do not approve it**. Ask it to re-check rule #10 and verify the API against the project dependencies and source files. Confirm that no fabricated call is applied before marking this step complete.

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

**Verify:** Bob reviews the prompt inputs across all 7 agents, including the supervisor's `@SupervisorRequest`, and identifies potential PII exposure with concrete mitigations. Findings and risk rankings may differ by LLM and your implementation. Check each finding against the source and verify suggested configuration properties for the installed extensions before applying them. The following is an example audit, not an exact response to reproduce:

> **High-risk fields** — `{report}` (raw caller input) and `{incidentInfo.description}` (free-text database field) appear across multiple agents:
>
> | Agent | PII vector | Risk level |
> |-------|-----------|------------|
> | `TriageAgent` | `{report}` — raw free-text, uncontrolled | High |
> | `DiagnosticAgent` | `{diagnosticRequest}` — chained from upstream agents | Medium |
> | `IncidentAnalysisAgent` | `{report}` + `{incidentInfo.description}` — two vectors | **Highest** |
> | `ImpactAgent` | `{incidentDescription}` — free-text from caller | High |
> | `EscalationAgent` | `{report}` + `{incidentDescription}` — longest chain carrying raw input | High |
> | `ResolutionAgent` | LLM-generated strings only — no raw user input | Low |
> | `IncidentSupervisorAgent` | `incidentInfo.description` concatenated in `@SupervisorRequest` | High |
>
> **Existing safeguard** — `log-requests=false` / `log-responses=false` in `application.properties` prevents full prompts from being logged.
>
> **Example mitigation proposal** — review these `application.properties` suggestions against your installed extensions before applying them:
>
> ```properties
> # Prod: never log LLM requests/responses (contain raw report and description)
> %prod.quarkus.langchain4j.log-requests=false
> %prod.quarkus.langchain4j.log-responses=false
>
> # Prod: if Exercise 6 OTel tracing is enabled, suppress prompt/tool content
> %prod.quarkus.langchain4j.tracing.include-prompt=false
> %prod.quarkus.langchain4j.tracing.include-tool-arguments=false
>
> # Prod: suppress supervisor prompt debug log (contains incidentInfo.description)
> %prod.quarkus.log.category."dev.langchain4j.agentic.supervisor".level=WARN
> %prod.quarkus.log.category."dev.langchain4j.agentic".level=WARN
> ```

<figure style="max-width:480px;margin:1.5rem auto;">
<img src="../../images/bob-step5.png" alt="Bob's PII audit of @UserMessage templates" style="width:100%;max-width:480px;display:block;margin:1rem auto;border-radius:8px;box-shadow:0 2px 12px rgba(0,0,0,0.15);">
<figcaption style="text-align:center;font-size:0.65rem;color:var(--md-default-fg-color--light);">Illustrative Bob response — output may vary by Bob version, selected LLM, settings, and project context.</figcaption>
</figure>

This is **shift-left security** — catching PII exposure risks before deployment, using only configuration changes.

---

<div class="done-when" markdown>

## :material-check-circle: Done when

- [ ] You verified Bob's answers against `AGENTS.md` and source files, resolving unsupported claims even when responses differ from the screenshots
- [ ] `lab/AGENTS.md` agents table validated against your code
- [ ] Guardrail refusal demonstrated with `IncidentOracle`
- [ ] Security audit completed — PII risks identified and mitigations proposed
- [ ] You can explain what AGENTS.md provides (structured context upfront, so Bob starts with rules and architecture)

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

    Without `AGENTS.md`, Bob must rediscover project conventions on every request (e.g. ~800 tokens).
    With `AGENTS.md` loaded once: e.g. ~160 tokens, all rules followed from the start.
    Estimated savings: e.g. 2,000–5,000 tokens per complex multi-file task. (Numbers are illustrative — actual usage varies by project size and prompt.)

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
    | Bob scans 20 Java files | e.g. ~800 tokens | May miss CDI scopes, invent imports |
    | Bob reads `AGENTS.md` once | e.g. ~160 tokens | Rules enforced from turn 1 |
    | Complex multi-file task without AGENTS.md | e.g. ~3,000–5,000 tokens | High hallucination risk |
    | Complex multi-file task with AGENTS.md | e.g. ~800–1,200 tokens | Rules enforced, diff requires approval |
