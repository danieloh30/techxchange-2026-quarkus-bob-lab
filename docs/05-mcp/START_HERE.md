# Exercise 5 — MCP Remote Tools

**Timebox:** 10 minutes  
**Story:** Sam exposes weather as a shared MCP server; agents consume it remotely.  
**Projects:**
- Client: [`exercises/05-mcp/solution`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/tree/main/exercises/05-mcp/solution)
- Server: [`exercises/05-mcp/weather-mcp-server`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/tree/main/exercises/05-mcp/weather-mcp-server)

**Upstream:** [section-1/step-08](https://quarkus.io/quarkus-workshop-langchain4j/section-1/step-08/)

![MCP](../images/mcp.png)

## Start (two terminals)

```bash
# Terminal 1
cd exercises/05-mcp/weather-mcp-server && ./mvnw quarkus:dev

# Terminal 2
cd exercises/05-mcp/solution && ./mvnw quarkus:dev
```

## Do

1. Confirm MCP server on `:8081` and client URL `http://localhost:8081/mcp/sse/`.
2. Ask the assistant about booking weather / snow chains (see [LAB_GUIDE.md](../LAB_GUIDE.md) sample dialog).
3. Watch MCP traffic logs on the server.

## Done when

- [ ] A remote MCP tool invocation appears in server logs
- [ ] You can contrast MCP (tools) with A2A (agents) in one sentence
