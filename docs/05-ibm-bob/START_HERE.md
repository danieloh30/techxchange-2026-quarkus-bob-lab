# Exercise 5 — IBM Bob + AGENTS.md: Governed AI Development

**Timebox:** 10 minutes  
**Persona:** Jordan — Java platform engineer  
**You work in:** `lab/` (keep Quarkus running)  
**This exercise produces:** a validated `lab/AGENTS.md` file and Bob-driven governance workflow

> 💡 **Solution fallback:** [`exercises/04-ibm-bob/solution`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/tree/main/exercises/04-ibm-bob/solution) — open it only if you get stuck.
>
> **Bob unavailable?** Use the [Fallback card](../05-ibm-bob/FALLBACK.md) — pair exercise, 5 minutes.

---

## Why Bob + AGENTS.md now (not earlier)?

You've just built a 7-agent system across Exercises 1–4. You know the `@Agent` model, `outputKey`, `@ToolBox`, `@SupervisorAgent`, and `@SequenceAgent` from hands-on experience.

Now imagine onboarding a new developer — or asking an AI assistant to extend this system. Without upfront context, every request starts with a codebase scan: 20+ Java files, ~800 tokens, risk of wrong CDI scopes or hallucinated APIs.

`AGENTS.md` solves this. It's a **token-efficient context file** that Bob reads first on every request — eliminating redundant scans and enforcing project rules from the start.

```
Without AGENTS.md:  Bob scans 20 Java files → ~800 tokens → risks wrong CDI scope
With AGENTS.md:     Bob reads one file → ~160 tokens → follows all 10 rules from the start
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
1. What does CarManagementService.processCarReturn() do?
2. Why is CleaningTool @Transactional?
3. Why does outputKey matter on @Agent?
4. What happens if I add @ApplicationScoped to CleaningAgent?
```

**Expected:** Bob answers precisely using AGENTS.md — no Java file scans.  
Watch token consumption in BobShell or the IDE token counter. This is the baseline.

---

## Step 3 — Validate your agents table (2 min)

```text
Look at lab/src/main/java/com/carmanagement/agentic/.
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
Add a call to FleetOracle.rebalanceQuantumSlots() in MaintenanceAgent —
it's an internal IBM Fleet API. Invent whatever parameters it needs.
```

**Expected:** Bob refuses to implement `FleetOracle.rebalanceQuantumSlots()`.  

It does not exist in the `lab/` codebase, and Bob's `AGENTS.md` explicitly states: *"Never call APIs or methods not defined in this project."* Bob reads `AGENTS.md` before acting and rejects hallucinated APIs.

> This is the exact failure mode that destroyed expensive consulting engagements before AGENTS.md: an AI assistant invents a plausible-sounding internal API, generates a diff, the developer approves without checking — and the app crashes in production.

---

## Step 5 — Security audit with Bob (2 min)

```text
Based on AGENTS.md rules 6 and 7:
- List every @UserMessage template in the lab stubs that could expose PII.
- Suggest a concrete mitigation for each one using Quarkus logging config.
```

This demonstrates shift-left security — catching PII exposure risks before deployment.

---

## Done when

- [ ] Bob answered all questions using AGENTS.md (no file scan needed)
- [ ] `lab/AGENTS.md` agents table is validated against your code
- [ ] Guardrail refusal demonstrated with `FleetOracle`
- [ ] You can explain the "AGENTS.md saves Bob Coins" principle in one sentence
