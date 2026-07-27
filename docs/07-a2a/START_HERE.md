# Exercise 7 — MCP + A2A: Remote Tools and Distributed Agents

**Timebox:** 10 minutes  
**Personas:** Sam (MCP), Riley (A2A)  
**You work in:** `exercises/05-mcp/` and `exercises/07-a2a/` (run + read)  
**Bob task:** extend your `lab/AGENTS.md` with MCP and A2A concepts

> 💡 **Reference solutions:**  
> MCP: [`exercises/05-mcp`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/tree/main/exercises/05-mcp)  
> A2A: [`exercises/07-a2a/solution`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/tree/main/exercises/07-a2a/solution)

---

## Part A — MCP (5 min)

**The problem:** Sam's team owns weather capabilities. They can't inject weather code into every team's app. Model Context Protocol (MCP) exposes tools over HTTP/SSE so any MCP-compatible agent can call them.

### Start (two terminals)

```bash
# Terminal 1 — MCP server
cd exercises/05-mcp/weather-mcp-server
./mvnw quarkus:dev   # port :8081
```
```bash
# Terminal 2 — MCP client
cd exercises/05-mcp/solution
./mvnw quarkus:dev   # port :8080
```

### What changed vs `@ToolBox`?

In the client `application.properties`:
```properties
quarkus.langchain4j.mcp.weather.transport-type=http
quarkus.langchain4j.mcp.weather.url=http://localhost:8081/mcp/sse/
```

In the AI service:
```java
@McpToolBox("weather")   // remote tool discovery at runtime
String chat(String userMessage);
```

The tool schema is fetched from the MCP server at startup via SSE — no compile-time dependency on the tool implementation.

### Test it

```text
My name is Speedy McWheels, booking id 2.
I'm picking up my rental in Denver next Tuesday.
Do I need snow chains? Check the weather and advise.
```

Watch both terminals — server logs show `getForecast` called; client shows LLM composing advice from tool result.

---

## Part B — A2A (5 min)

**The problem:** Riley's pricing team needs their own release cycle and independent scaling. Convert `PricingAgent` from a local in-process agent to a remote A2A service.

### Start (two terminals)

```bash
# Terminal 1 — pricing service FIRST
cd exercises/07-a2a/solution/remote-a2a-agent
./mvnw quarkus:dev   # port :8888
```
```bash
# Terminal 2 — main system
cd exercises/07-a2a/solution/multi-agent-system
./mvnw quarkus:dev   # port :8080
```

Verify the AgentCard:
```bash
curl http://localhost:8888/.well-known/agent.json
```

### The A2A contract

```
FleetSupervisorAgent (@A2AClientAgent) → POST /a2a/tasks/send :8888
                                              │
                                         AgentExecutor.execute(task)
                                              │
                                         PricingAgent (remote LLM call)
                                              │
                                         Task result → back to :8080
```

### MCP vs A2A — the decision

| | MCP | A2A |
|--|-----|-----|
| What crosses the wire | Tool call (function + typed args) | Agent task (goal + natural language) |
| Who reasons | Local LLM uses remote tool | Remote LLM reasons independently |
| Team ownership | Shared capability (weather, search) | Autonomous team agent (pricing, legal) |
| Best for | Shared functionality | Delegated decision-making |

---

## Bob: update AGENTS.md

```text
Based on what I just learned about MCP and A2A, suggest additions to
lab/AGENTS.md that document:
1. The @McpToolBox annotation pattern for remote tools
2. The @A2AClientAgent annotation for remote agents
3. When to use each vs local @ToolBox

Do NOT write code — update only AGENTS.md.
```

---

## Done when

- [ ] MCP: `getForecast` tool invocation visible in **server** logs
- [ ] A2A: pricing ran in `:8888` (confirmed in remote process logs)
- [ ] AgentCard verified via `curl`
- [ ] `lab/AGENTS.md` updated with MCP and A2A patterns
- [ ] You can contrast MCP vs A2A in one sentence each
