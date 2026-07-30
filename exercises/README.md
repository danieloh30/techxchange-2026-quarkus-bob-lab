# Lab Project + Reference Solutions

## Your hands-on project: `lab/`

This is where you work throughout Exercises 1–4. It contains:
- A running Quarkus app with UI, model, REST endpoints, and seeded data
- **Stub files** in `src/main/java/com/incidentmanagement/agentic/` — interfaces with `// TODO` markers
- `lab/AGENTS.md` — your project-level IBM Bob context file (used in Exercise 5)

```bash
cd lab
export OPENAI_API_KEY=sk-your-key-here
./mvnw quarkus:dev
```

Open http://localhost:8080 → follow the exercise guides in `docs/`.

---

## Reference solutions

Each folder is a **finished** Quarkus project you can run with `./mvnw quarkus:dev`.
**Only open these if you get stuck on a step** — the exercise guides link to the relevant solution at the top.

| Exercise | Solution folder | What it shows |
|----------|----------------|--------------|
| Ex 1 | [`01-first-agent/solution`](01-first-agent/solution) | `TriageAgent` + `TriageTool` |
| Ex 2 | [`03-parallel-workflow/solution`](03-parallel-workflow/solution) | `DiagnosticAgent` + full agent set |
| Ex 3 | [`03-parallel-workflow/solution`](03-parallel-workflow/solution) | `IncidentAnalysisWorkflow` + `@ParallelMapperAgent` |
| Ex 4 | [`04-supervisor/solution`](04-supervisor/solution) | Complete multi-agent supervisor pipeline |
| Ex 5 | [`04-supervisor/solution`](04-supervisor/solution) | Same project — Bob validates this codebase |
| Ex 6 | [`06-hitl-observability/solution`](06-hitl-observability/solution) | `@HumanInTheLoop` + OTel |
| Ex 7 | [`07-a2a/solution`](07-a2a/solution) (`multi-agent-system` + `remote-a2a-agent`) | `@A2AClientAgent` |

### Additional reference projects

| Folder | What it shows |
|--------|--------------|
| [`02-maintenance-agent/solution-sequence`](02-maintenance-agent/solution-sequence) | `@SequenceAgent` chain |
| [`02-maintenance-agent/solution-composed`](02-maintenance-agent/solution-composed) | Parallel + conditional workflows |
| [`05-ibm-bob/solution`](05-ibm-bob/solution) | MCP client with `@McpToolBox` (stretch exercise) |
| [`05-ibm-bob/weather-mcp-server`](05-ibm-bob/weather-mcp-server) | MCP SSE weather server |
| [`06-hitl-observability/observability-reference`](06-hitl-observability/observability-reference) | Prompt injection guard + observability |

## Versions

All projects target:
- **Java 25** (`maven.compiler.release`)
- **Quarkus** `3.37.4` (`quarkus.platform.version`)
- **Quarkus LangChain4j** `1.12.0` (`quarkus-langchain4j.version`)
