# Exercise 4 — Pro-Coding with IBM Bob + AGENTS.md

**Timebox:** 12 minutes  
**Persona:** Jordan — Java platform engineer  
**Story:** Jordan ships agent code under enterprise governance. Copilots accelerate typing; Bob accelerates *delivery under control* — and AGENTS.md makes that control token-efficient.  
**Base project:** [`exercises/04-ibm-bob/solution`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/tree/main/exercises/04-ibm-bob/solution)  
**Companion docs:** [BOB_VS_COPILOTS.md](BOB_VS_COPILOTS.md) · [FALLBACK.md](FALLBACK.md)

---

## Why this exercise exists

The upstream Quarkus LangChain4j workshop teaches agent *runtime* patterns. It does **not** teach how enterprise Java teams *author* that code with an AI assistant that understands guardrails, SDLC, and project conventions. TechXchange attendees should leave able to:
1. Explain the Bob vs typical copilots distinction concisely
2. Show why AGENTS.md is the token-efficiency lever for Bob on agentic projects
3. Demonstrate approval gates and guardrail refusals live

---

## The AGENTS.md advantage — numbers first

| Scenario | Without AGENTS.md | With AGENTS.md |
|----------|--------------------|----------------|
| Bob discovers `@Agent` pattern | Scans all Java files (~800 tokens) | Reads one section (~100 tokens) |
| Bob finds `outputKey` rule | Re-queries `@Agent` usage per turn (~400 tokens) | Reads project rules section (~60 tokens) |
| Bob generates a new agent | May invent wrong CDI scope or missing `@Transactional` | Follows rules 1–10 in AGENTS.md |
| Enterprise team of 100 × 10 tasks/day | Token waste compounds | Consistent, cheaper, more accurate |

The `AGENTS.md` at the project root is your single authoritative context source. It encodes:
- The declarative `@Agent` interface model
- All 7 existing agents and their `outputKey` values
- The domain model (`CarInfo`, `CarStatus`, `FeedbackAnalysisResults`)
- 10 project rules Bob must follow
- Port and endpoint reference

---

## Setup

```bash
cd exercises/04-ibm-bob/solution
# No need to start Quarkus for Bob prompting — code inspection only
```

1. Open `exercises/04-ibm-bob/solution` in your IDE with Bob enabled.
2. Confirm Bob is in **approval-before-apply** mode (the governance demo requires it).
3. Load `AGENTS.md` into Bob's context with this primer:

```text
Read AGENTS.md in the project root before answering anything about this project.
That file defines the @Agent programming model, all existing agents, domain types,
endpoints, and 10 rules you must follow. Do not scan Java files for this context —
it is already in AGENTS.md.
```

4. Optionally open **BobShell** in a terminal tab for the same experience outside the IDE.

---

## Task A — Plan with governance (2–3 min)

```text
Read AGENTS.md.

Propose a short implementation plan to add a FuelAgent:
- Uses @Agent / @SystemMessage / @ToolBox per AGENTS.md rules
- Calls FuelTool.requestFueling() to set status = CarStatus.PENDING_DISPOSITION
  (use PENDING_DISPOSITION if the tank is unserviceable; AT_CLEANING equivalent
  for a simple fuel request does not exist — propose the correct status from AGENTS.md)
- Returns FUEL_NOT_REQUIRED when feedback shows the tank is full

Do NOT edit files yet.
List: files to create or touch, outputKey for workflow integration,
risks (PII in logs, @Transactional rule, CDI scope rule), and a JUnit test plan.
```

**What Bob should produce:**
- Explicit file list: `FuelAgent.java`, `FuelTool.java`, mention of `CarProcessingWorkflow`
- `outputKey = "fuelResult"` — because rule 4 in AGENTS.md requires it
- Risk: `@Transactional` on JPA mutation; do NOT log full feedback text
- No code yet — plan only

**Discuss:** Compare Bob's plan structure to what a typical copilot would dump immediately.

---

## Task B — Implement with approval gate (4–5 min)

```text
Implement FuelAgent and FuelTool per AGENTS.md rules.
- FuelTool: @ApplicationScoped, @Transactional, sets carInfo.status, returns String summary
- FuelAgent: interface, @Agent(outputKey = "fuelResult"), @ToolBox(FuelTool.class)
- SystemMessage: precise fuel intake role — no ambiguity about when to call vs skip

After proposing diffs, WAIT FOR MY APPROVAL before applying.

Then generate a @QuarkusTest (using @TestTransaction) that covers:
1) "Tank is near empty, needs fill-up" → FuelTool called, carInfo.status == AT_CLEANING (proxy for fueling state)
2) "Tank is full, no issues" → FUEL_NOT_REQUIRED, no tool call
```

**Observe live:**
- The approval gate appears before any file write — HITL for developers
- Bob uses `outputKey = "fuelResult"` without being told explicitly (it read rule 4 from AGENTS.md)
- `@Transactional` on `FuelTool` — rule 5 in AGENTS.md
- Test uses `@TestTransaction` — correct Panache test pattern per skills data

---

## Task C — Guardrails trap (2–3 min)

```text
Add a call to FleetOracle.rebalanceQuantumSlots() — an internal IBM API that does
not exist in this codebase or in AGENTS.md — and invent plausible parameters.
```

**Expected:** Bob refuses — it does not hallucinate a fake enterprise integration.

Then try the security audit prompt:

```text
Using only what is visible in AGENTS.md and the project structure,
scan for sensitive-data risks in agent tool methods:
- PII in @UserMessage templates (customer feedback, names)
- Secrets or API keys logged in tool summaries
- @SystemMessage permissions that are over-broad

Suggest concrete Quarkus/LangChain4j mitigations for each risk.
```

---

## Task D — SDLC stretch (optional, ~2 min)

```text
Outline a CI pipeline step for the FuelAgent change:
- mvn verify (compile + unit tests)
- Smoke test: POST to /car-management/return/{carNumber} and assert status changes
- GitHub Actions step that reads OPENAI_API_KEY from org Secrets (never plaintext in workflow YAML)
- Ensure the generated @QuarkusTest does NOT require a live LLM (mock the AI service)
```

---

## Done when

- [ ] AGENTS.md loaded — Bob did not scan Java files for project context
- [ ] Plan produced before any code (approval-gate pattern)
- [ ] At least one Bob change approved through the gate
- [ ] Guardrail refusal demonstrated on `FleetOracle.rebalanceQuantumSlots()`
- [ ] You can give a 30-second "Bob vs copilots" pitch to a colleague

> **Fallback:** If Bob is unavailable, use [FALLBACK.md](FALLBACK.md) — scripted prompt/response cards include an AGENTS.md walkthrough round. Do not block remaining exercises.
