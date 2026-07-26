# Exercise 4 — Pro-Coding with IBM Bob

**Timebox:** 12 minutes  
**Story:** Jordan ships agent code under enterprise governance.

## Why this exercise exists

The upstream Quarkus LangChain4j workshop teaches agent *runtime* patterns well — it does **not** teach how Java enterprise teams *author* that code with an AI assistant that understands **guardrails and SDLC**. TechXchange attendees should leave able to contrast Bob with typical copilots.

## Setup

1. Install / open **IBM Bob** — https://bob.ibm.com/
2. Open the lab repo (prefer `lab/03-supervisor/starter` or `lab/05-mcp/starter` as the working tree).
3. Set Bob to a mode that **requires approval before applying edits**.

## Tasks

### A — Plan only (no edits)

Paste from the main README §4.2 prompt. Review the plan for risks and test ideas.

### B — Implement with approval

Paste §4.3 prompt (`MaintenanceAgent` + tests). Approve diffs deliberately — narrate the gate.

### C — Guardrails trap

Paste §4.4 prompt asking for a nonexistent `FleetOracle.rebalanceQuantumSlots()` API.  
**Success = refusal or explicit “not in codebase,” not a fake implementation.**

### D — Optional SDLC

Outline CI/smoke via BobShell-oriented prompt (§4.5).

## Competitive talking points (use while waiting on generation)

1. **SDLC partner vs autocomplete** — plan, test, secure, modernize — not only complete the current line.
2. **Guardrails built in** — sensitive data scanning, policy, approval modes, red-teaming mindset.
3. **Java first-class** — modernization packages; enterprise frameworks as defaults.
4. **BobShell + Bobalytics** — same agent skills in CI/terminal; adoption and cost visibility.
5. **Human-in-the-loop for developers** — mirrors HITL you will build for fleet agents in Exercise 6.

## Done when

- [ ] You approved at least one Bob change (or walked FALLBACK.md)
- [ ] You saw a guardrail/refusal behavior
- [ ] You can give a 30-second “Bob vs copilots” answer to a colleague
