# IBM Bob vs Typical AI Coding Assistants

**Handout for Exercise 4 (2 minutes read)**

## One-line difference

| | Typical copilots | IBM Bob |
|--|------------------|---------|
| Promise | “Write code faster” | “Deliver software across the SDLC — with control” |

## What to remember for TechXchange Q&A

1. **Guardrails** — approval modes, sensitive-data scanning, policy enforcement; refuses unknown APIs instead of hallucinating enterprise integrations.
2. **SDLC coverage** — discover, design, implement, test, secure, deploy, modernize — not only the editor buffer.
3. **Java / enterprise depth** — Java as a first-class citizen; premium modernization workflows (e.g. version upgrades) vs generic snippets.
4. **Human-in-the-loop** — configurable checkpoints (manual → auto-approve by task type), aligned with how we govern *runtime* agents.
5. **Beyond IDE** — BobShell for terminal/CI; ecosystem connectivity (e.g. Red Hat, Instana); Bobalytics for adoption and cost insight.
6. **Agentic development** — role-based agents/subagents and reusable skills/playbooks for long-running, governed work.

## Map to this lab’s runtime patterns

| You build in Quarkus agents | You practice with Bob while coding |
|-----------------------------|--------------------------------------|
| Tool calling with clear contracts | Don’t invent missing APIs |
| Supervisor orchestration | Plan before multi-file edits |
| Human-in-the-loop on dispositions | Approval before applying diffs |
| Observability / audit | Security & compliance prompts shift-left |

**Bottom line for Miles of Smiles:** agents need governance in *production*; developers need governance in *creation*. Quarkus + LangChain4j address the first; **Bob** addresses the second.
