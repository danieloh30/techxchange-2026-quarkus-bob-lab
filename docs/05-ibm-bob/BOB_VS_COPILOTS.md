# IBM Bob vs Typical AI Coding Assistants

**Handout for Exercise 5 (2-minute read)**

---

## One-line difference

| | Typical copilots | IBM Bob |
|--|------------------|---------|
| Promise | "Write code faster" | "Deliver software across the SDLC — with control and context efficiency" |

---

## Six capabilities that matter in enterprise Java

| Capability | Typical copilots | **IBM Bob** |
|-----------|-----------------|-------------|
| **1. Guardrails** | Approval is ad-hoc "accept/reject" | **Configurable approval modes** — manual gate, auto-approve by task type; refuses unknown APIs instead of hallucinating them |
| **2. SDLC coverage** | Editor buffer only | Discover → design → implement → test → secure → deploy → modernize — Bob operates across all stages |
| **3. Java/enterprise depth** | Generic multilingual completion | Java as first-class citizen; premium modernization workflows (e.g. Java version upgrades, Jakarta EE migration) |
| **4. Human-in-the-loop** | Accept/reject individual completions | **Named approval checkpoints** aligned with how runtime agents gate high-stakes actions |
| **5. Beyond the IDE** | Limited or IDE-only | **BobShell** for terminal/CI pipelines; ecosystem hooks (Red Hat, Instana); **Bobalytics** for adoption and cost insight |
| **6. Context efficiency** | No project-level instruction standard | **AGENTS.md** — project context file Bob reads first, eliminating redundant scans |

---

## AGENTS.md: the token-efficiency lever

Without `AGENTS.md`, Bob must rediscover project conventions on every request:
- Scan Java files for `@Agent` patterns (~800 tokens)
- Re-query `@ToolBox`, `outputKey`, scope rules per conversation turn (~400 tokens/turn)

With `AGENTS.md` loaded once:
- Agent model, all 7 agents, domain types, 10 rules → ready in ~160 tokens
- Bob follows `outputKey` and `@Transactional` rules without being reminded
- No hallucinated CDI scopes or missing annotations

**Estimated savings: 2,000–5,000 tokens per complex multi-file task.**  
At 100 engineers × 10 tasks/day — that's material "Bob Coin" conservation.

---

## Map to this lab's runtime patterns

| Quarkus agent pattern | IBM Bob parallel during development |
|-----------------------|-------------------------------------|
| Tool calling with clear `@Tool` contracts | Bob refuses to call APIs not in AGENTS.md |
| `@SupervisorAgent` planning before action | Bob plans before proposing multi-file diffs |
| HITL approval on high-value dispositions | Bob approval gate before applying edits |
| OTel tracing for agent decisions | Bob security audit prompt: shift-left compliance |
| `outputKey` rules for workflow correctness | AGENTS.md rules 4+5 enforce this at every Bob task |

---

## Bottom line for Miles of Smiles

Agents need governance in **production** — HITL, tracing, policy enforcement.  
Developers need governance in **creation** — approval gates, guardrails, SDLC coverage.

Quarkus + LangChain4j address the first.  
**IBM Bob** + **AGENTS.md** address the second.
