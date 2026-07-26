# IBM Bob Exercise — Fallback (no live Bob)

Use this card if Bob is unavailable (network, seats, install). Work in pairs: one reads “Bob,” one is “Developer.”

## Round 1 — Plan

**Developer asks:**  
Propose a plan for `MaintenanceAgent` + `MaintenanceTool` mirroring `CleaningAgent`. No code yet.

**Bob answers (read aloud):**  
- Touch: `MaintenanceAgent.java`, `MaintenanceTool.java`, `CarManagementService`, maybe workflow wiring.  
- Risk: over-triggering maintenance on cosmetic feedback; log PII from customer notes.  
- Test: mechanical → `AT_MAINTENANCE`; clean text → `MAINTENANCE_NOT_REQUIRED`.  
- Compliance: don’t log full customer PII in tool summaries; keep approval for prod prompts.

## Round 2 — Guardrail

**Developer asks:**  
Call `FleetOracle.rebalanceQuantumSlots()` even though it isn’t in the repo.

**Bob answers (read aloud):**  
> I don’t see `FleetOracle` or `rebalanceQuantumSlots` in this codebase or known IBM public APIs for this project. I won’t invent an enterprise integration. Point me to the real client interface or OpenAPI spec.

**Contrast line for the room:**  
Many assistants will invent a plausible class. Enterprise guardrails prefer **honest refusal**.

## Round 3 — SDLC checklist

Discuss which Bob capabilities map to delivery:

| Stage | Bob-oriented help |
|-------|-------------------|
| Discover / plan | Architecture-aware plan, file list |
| Implement | Literate coding with approval modes |
| Secure | Shift-left scanning / policy prompts |
| Test | Generate unit + smoke tests |
| Operate | Hooks toward Instana / observability guidance |
| Modernize | Java upgrade playbooks (premium packaging) |

Then continue to Exercise 5 on time.
