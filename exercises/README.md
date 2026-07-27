# Lab Project + Reference Solutions

## Your hands-on project: `lab/`

This is where you work throughout all exercises. It contains:
- A running Quarkus app with UI, model, REST endpoints, and seeded data
- **Stub files** in `src/main/java/com/carmanagement/agentic/` — interfaces with `// TODO` markers
- `lab/AGENTS.md` — your project-level IBM Bob context file

```bash
cd lab
export OPENAI_API_KEY=sk-your-key-here
./mvnw quarkus:dev
```

Open IBM Bob → load `lab/AGENTS.md` → follow the exercise guides in `docs/`.

---

## Reference solutions

Each folder is a **finished** Quarkus project you can run with `./mvnw quarkus:dev`.
**Only open these if you get stuck on a step** — the exercise guides link to the relevant solution at the top.

| Exercise | Solution folder | What it shows |
|----------|----------------|--------------|
| Ex 1 + 2 | [`01-first-agents/solution`](01-first-agents/solution) | `CleaningAgent` + `CleaningTool` |
| Ex 2 (sequence) | [`02-workflow-patterns/solution-sequence`](02-workflow-patterns/solution-sequence) | `@SequenceAgent` prompt chain |
| Ex 2 (composed) | [`02-workflow-patterns/solution-composed`](02-workflow-patterns/solution-composed) | Parallel + conditional |
| Ex 3 + 4 | [`03-supervisor/solution`](03-supervisor/solution) | `MaintenanceAgent` + `@SupervisorAgent` |
| Ex 5 (full) | [`04-ibm-bob/solution`](04-ibm-bob/solution) | Complete multi-agent pipeline |
| Ex 6 MCP client | [`05-mcp/solution`](05-mcp/solution) | `@McpToolBox` + weather client |
| Ex 6 MCP server | [`05-mcp/weather-mcp-server`](05-mcp/weather-mcp-server) | MCP SSE server |
| Ex 6 HITL | [`06-hitl-observability/solution`](06-hitl-observability/solution) | `@HumanInTheLoop` + OTel |
| Ex 7 A2A | [`07-a2a/solution`](07-a2a/solution) (`multi-agent-system` + `remote-a2a-agent`) | `@A2AClientAgent` |

## Versions

All projects target:
- **Java 25** (`maven.compiler.release`)
- **Quarkus** `3.37.4` (`quarkus.platform.version`)
- **Quarkus LangChain4j** `1.12.0` (`quarkus-langchain4j.version`)
