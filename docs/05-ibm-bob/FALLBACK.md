# IBM Bob Exercise — Fallback (no live Bob)

Use this card if Bob is unavailable (network, seats, install). Work in pairs: one person reads "Bob," the other is "Developer." 5 minutes, 3 rounds.

---

## Round 0 — AGENTS.md walk (1 min)

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

---

## Round 1 — Plan (1 min)

**Developer asks:**  
Propose a plan for a `NotificationAgent` + `NotificationTool` following AGENTS.md. No code yet.

**Bob answers (read aloud):**
> Files to create: `NotificationAgent.java` (interface), `NotificationTool.java` (@ApplicationScoped @Transactional).
> Files to touch: `IncidentProcessingWorkflow.java` to add `NotificationAgent` as a sub-agent with `outputKey = "notificationResult"`.
> Risk: Do NOT log full incident report strings — PII risk per rule 6.
> Risk: `NotificationTool` must be `@Transactional` if it calls `incidentInfo.persist()` — rule 5.
> Test plan: `NotificationAgentTest` with `@QuarkusTest @TestTransaction` — two cases: stakeholders notified (tool called) and no notification needed (NOTIFICATION_NOT_REQUIRED).
> Ready for approval to proceed?

---

## Round 2 — Guardrail (1 min)

**Developer asks:**  
Call `IncidentOracle.rebalanceQuantumSlots()` even though it isn't in the repo or AGENTS.md.

**Bob answers (read aloud):**
> I don't see `IncidentOracle` or `rebalanceQuantumSlots` in this codebase, in AGENTS.md, or in any known IBM public API for this project. I will not invent an enterprise integration. Point me to the real client interface, an OpenAPI spec, or add it to AGENTS.md as a known dependency.

**Contrast line for the room:**  
Many assistants will invent a plausible class with convincing-sounding parameters. Enterprise guardrails prefer **honest refusal** over confident hallucination.

---

## Round 3 — SDLC checklist (1 min)

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

Then continue to Exercise 6 — you are on schedule.
