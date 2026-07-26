# Exercises — completed Quarkus solutions

Each folder is a **finished** Quarkus project (or multi-module step) you can run with `./mvnw quarkus:dev`. Instructions are in [`../docs/`](../docs/).

| Exercise | Run from | Upstream workshop step |
|----------|----------|------------------------|
| 01 First agents | [`01-first-agents/solution`](01-first-agents/solution) | [section-2/step-01](https://github.com/quarkusio/quarkus-workshop-langchain4j/tree/main/section-2/step-01) |
| 02 Workflow patterns (sequence) | [`02-workflow-patterns/solution-sequence`](02-workflow-patterns/solution-sequence) | [section-2/step-02](https://github.com/quarkusio/quarkus-workshop-langchain4j/tree/main/section-2/step-02) |
| 02 Workflow patterns (composed) | [`02-workflow-patterns/solution-composed`](02-workflow-patterns/solution-composed) | [section-2/step-03](https://github.com/quarkusio/quarkus-workshop-langchain4j/tree/main/section-2/step-03) |
| 03 Supervisor | [`03-supervisor/solution`](03-supervisor/solution) | [section-2/step-04](https://github.com/quarkusio/quarkus-workshop-langchain4j/tree/main/section-2/step-04) |
| 04 IBM Bob (base app) | [`04-ibm-bob/solution`](04-ibm-bob/solution) | section-2/step-04 (same codebase; Bob prompts in docs) |
| 05 MCP client | [`05-mcp/solution`](05-mcp/solution) | [section-1/step-08](https://github.com/quarkusio/quarkus-workshop-langchain4j/tree/main/section-1/step-08) |
| 05 MCP weather server | [`05-mcp/weather-mcp-server`](05-mcp/weather-mcp-server) | [section-1/step-08-mcp-server](https://github.com/quarkusio/quarkus-workshop-langchain4j/tree/main/section-1/step-08-mcp-server) |
| 06 HITL | [`06-hitl-observability/solution`](06-hitl-observability/solution) | [section-2/step-05](https://github.com/quarkusio/quarkus-workshop-langchain4j/tree/main/section-2/step-05) |
| 06 Observability reference | [`06-hitl-observability/observability-reference`](06-hitl-observability/observability-reference) | [section-1/step-10](https://github.com/quarkusio/quarkus-workshop-langchain4j/tree/main/section-1/step-10) |
| 07 A2A | [`07-a2a/solution`](07-a2a/solution) (`multi-agent-system` + `remote-a2a-agent`) | [section-2/step-07](https://github.com/quarkusio/quarkus-workshop-langchain4j/tree/main/section-2/step-07) |

## Typical run

```bash
export OPENAI_API_KEY=sk-your-key-here
cd 01-first-agents/solution
./mvnw quarkus:dev
```

For MCP and A2A exercises, start **both** processes (see the matching `docs/0N-*/START_HERE.md`).
