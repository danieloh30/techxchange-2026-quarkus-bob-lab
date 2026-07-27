# Exercise 1 — IBM Bob Setup + AGENTS.md

**Timebox:** 12 minutes  
**Persona:** Jordan — Java platform engineer  
**You work in:** `lab/` (your hands-on project, not the solution)  
**This exercise produces:** a running Quarkus app + your `lab/AGENTS.md` file

> 💡 **Solution fallback:** [`exercises/04-ibm-bob/solution`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/tree/main/exercises/04-ibm-bob/solution) — open it only if you get stuck.

---

## Why Bob + AGENTS.md first?

Before writing a single agent, you establish **governed development context**. AGENTS.md is the project-level instruction file that Bob reads first on every request — eliminating redundant codebase scans (saves 2,000–5,000 tokens/task) and preventing annotation errors before they happen.

```
Without AGENTS.md:  Bob scans 20 Java files → ~800 tokens → risks wrong CDI scope
With AGENTS.md:     Bob reads one file → ~160 tokens → follows all 10 rules from the start
```

---

## Step 1 — Start the lab project

```bash
cd lab
export OPENAI_API_KEY=sk-your-lab-key-here
./mvnw quarkus:dev
```

Open **http://localhost:8080** — you'll see the Fleet Status UI with 8 seeded cars but no agent behavior yet (returns will fail — that's expected, you haven't wired the agents).

Open the Dev UI at **http://localhost:8080/q/dev-ui** — explore the CDI beans panel. Note: no agent beans registered yet.

---

## Step 2 — Open IBM Bob

1. Open `lab/` in your IDE with Bob enabled.
2. Set Bob to **approval-before-apply** mode.
3. Load context with this primer — send it to Bob first, before any task:

```text
Read lab/AGENTS.md before answering anything about this project.
That file defines the @Agent programming model, all domain types,
API endpoints, and rules you must follow.
Do not scan Java files — all context is in AGENTS.md.
```

> This is the AGENTS.md pattern. You will update `lab/AGENTS.md` as the lab progresses.

---

## Step 3 — Ask Bob to explain the project (no code)

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

## Step 4 — Ask Bob to validate the starter project

```text
Look at lab/src/main/java/com/carmanagement/agentic/.
All agent and workflow interfaces have TODO stubs.
Confirm which exercises map to which stubs and check that
my lab/AGENTS.md agents table lists all 7 agents correctly.
Flag any inconsistencies.
```

Bob should enumerate the stub files and confirm the exercise mapping matches AGENTS.md.

---

## Step 5 — Security audit with Bob (2 min)

```text
Based on AGENTS.md rules 6 and 7:
- List every @UserMessage template in the lab stubs that could expose PII.
- Suggest a concrete mitigation for each one using Quarkus logging config.
```

This demonstrates shift-left security — before a single line of agent code is written.

---

## Done when

- [ ] `./mvnw quarkus:dev` starts cleanly (UI visible, no agents yet)
- [ ] Bob answered all questions using AGENTS.md (no file scan needed)
- [ ] `lab/AGENTS.md` agents table is accurate
- [ ] You can explain the "AGENTS.md saves Bob Coins" principle in one sentence

> **Keep Quarkus running** — Exercise 2 adds the first agent with hot reload.
