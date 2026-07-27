# Exercise 5 — MCP Remote Tools

**Timebox:** 10 minutes  
**Persona:** Sam — Integration architect  
**Story:** Sam's team owns weather capabilities as a shared platform service. Agents across multiple apps need it — but Sam can't inject weather code into every team's codebase. MCP is the answer.  
**Projects:**
- Client: [`exercises/05-mcp/solution`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/tree/main/exercises/05-mcp/solution)
- Server: [`exercises/05-mcp/weather-mcp-server`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/tree/main/exercises/05-mcp/weather-mcp-server)

**Upstream:** [section-1/step-08](https://quarkus.io/quarkus-workshop-langchain4j/section-1/step-08/)

![MCP](../images/mcp.png)

---

## MCP architecture

```
  Agent (Quarkus :8080)                     MCP Server (Quarkus :8081)
  ┌───────────────────────┐                 ┌──────────────────────────────┐
  │  WeatherAssistant     │                 │  WeatherMcpService           │
  │  @McpToolBox("weather")│                │  @Tool getForecast(lat, lon) │
  │                       │◄─── SSE/HTTP ──►│  @Tool getAlerts(area)       │
  │  application.properties│  tool_call     │                              │
  │  mcp.weather.url=     │  tool_result   │  Calls open-meteo.com API    │
  │  :8081/mcp/sse/       │                └──────────────────────────────┘
  └───────────────────────┘
```

**Local `@ToolBox` vs Remote `@McpToolBox`:**

| | `@ToolBox` (local) | `@McpToolBox` (remote) |
|--|-------------------|----------------------|
| Tool location | Same JVM | Separate Quarkus process |
| Team ownership | Same team | Different team's codebase |
| Reuse | This app only | Any MCP-compatible client |
| Tool discovery | Build-time (class reference) | Runtime (SSE tool-list from server) |
| Latency | In-process | HTTP round-trip per call |
| When to use | Tightly coupled, same release | Shared services, different lifecycle |

---

## Start (two terminals)

```bash
# Terminal 1 — MCP server
cd exercises/05-mcp/weather-mcp-server
./mvnw quarkus:dev
# Listens on :8081
# Dev UI: http://localhost:8081/q/dev
```

Wait for `weather-mcp-server started`. Then:

```bash
# Terminal 2 — MCP client (main app)
cd exercises/05-mcp/solution
./mvnw quarkus:dev
# Listens on :8080
```

---

## How MCP wires together

In `exercises/05-mcp/solution/src/main/resources/application.properties` (already set):

```properties
quarkus.langchain4j.mcp.weather.transport-type=http
quarkus.langchain4j.mcp.weather.url=http://localhost:8081/mcp/sse/
```

The `"weather"` key matches the `@McpToolBox` annotation:

```java
@McpToolBox("weather")   // binds to quarkus.langchain4j.mcp.weather.*
String chat(String userMessage);
```

At startup, the client connects to `:8081/mcp/sse/` and receives the tool list (SSE event). When the LLM decides to call `getForecast`, the client sends a `tool_call` message and waits for the `tool_result` event.

---

## Do

1. Confirm both ports are up: `:8080` (client) and `:8081` (server)
2. Open http://localhost:8080 (chatbot UI)
3. Send:

```text
My name is Speedy McWheels, booking id 2.
I'm picking up my rental in Denver next Tuesday.
Do I need snow chains for the trip? Check the weather and advise.
```

**Watch both terminals:**
- **Server (8081):** `[WeatherMcpService] getForecast called lat=39.7... lon=-104.9...`
- **Client (8080):** `[McpClient] tool_call getForecast → weather data received`
- **Client (8080):** LLM composes practical advice including snow chain recommendation

---

## Production security considerations

MCP servers in enterprise deployments should:
- **Authenticate:** Require an API key or mTLS on `/mcp/sse/` — currently open in dev
- **Authorize:** Scope tool visibility per client (not all clients should call all tools)
- **Audit:** Log every `tool_call` with caller identity + arguments for compliance

Quarkus Security + Vert.x route guards can enforce all three. The `quarkus-security` extension is already in the installed features list of the running app.

---

## Done when

- [ ] MCP server on `:8081` and client on `:8080` both running
- [ ] A `getForecast` tool invocation visible in **server** logs
- [ ] You can contrast MCP (tools/capabilities) vs A2A (agent-to-agent tasks) in one sentence
- [ ] You can name one production security requirement for MCP SSE endpoints
