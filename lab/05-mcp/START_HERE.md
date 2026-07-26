# Exercise 5 — MCP Remote Tools

**Timebox:** 10 minutes  
**Story:** Sam exposes weather as a shared MCP server; agents consume it remotely.

## Start (two terminals)

```bash
# Terminal 1
cd lab/05-mcp/weather-mcp-server && ./mvnw quarkus:dev

# Terminal 2
cd lab/05-mcp/starter && ./mvnw quarkus:dev
```

Upstream: https://quarkus.io/quarkus-workshop-langchain4j/section-1/step-08/

## Do

1. Confirm MCP server on `:8081` and client URL `http://localhost:8081/mcp/sse/`.
2. Ask the assistant about booking weather / snow chains (see main README sample dialog).
3. Watch MCP traffic logs on the server.

## Done when

- [ ] A remote MCP tool invocation appears in server logs
- [ ] You can contrast MCP (tools) with A2A (agents) in one sentence
