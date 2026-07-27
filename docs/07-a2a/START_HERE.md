# Exercise 7 — A2A Distributed Pricing

**Timebox:** 10 minutes  
**Persona:** Riley — Pricing team lead  
**Story:** Valuation is a separate team's responsibility. They need their own release cycle, their own scaling budget, and the ability to be reused by other IBM systems. Solution: A2A remote agent.  
**Solution:** [`exercises/07-a2a/solution`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/tree/main/exercises/07-a2a/solution)
- Main app: `multi-agent-system` (port **8080**)
- Pricing A2A service: `remote-a2a-agent` (port **8888**)

**Upstream:** [section-2/step-07](https://quarkus.io/quarkus-workshop-langchain4j/section-2/step-07/)

---

## Start (two terminals)

```bash
# Terminal 1 — pricing service FIRST
cd exercises/07-a2a/solution/remote-a2a-agent
./mvnw quarkus:dev
# port :8888 — wait for "started"
```

```bash
# Terminal 2 — main system
cd exercises/07-a2a/solution/multi-agent-system
./mvnw quarkus:dev
# port :8080
```

> **Order matters:** Start `:8888` before `:8080`. The main app fetches the AgentCard on startup.

---

## A2A concepts deep-dive

| Concept | Technical meaning | Analogy |
|---------|------------------|---------|
| **AgentCard** | JSON capability manifest served at `GET /.well-known/agent.json` | OpenAPI spec for an agent service |
| **AgentExecutor** | Server-side request handler that processes an incoming A2A `Task` | Jakarta REST resource for agent tasks |
| **Task** | Stateful long-running goal with input/output envelope + lifecycle | Async job / workflow instance |
| **Message** | Single synchronous exchange within a task session | Synchronous REST call |

Verify the AgentCard is live:
```bash
curl http://localhost:8888/.well-known/agent.json
```

Expected response (abbreviated):
```json
{
  "name": "pricing-agent",
  "description": "Estimates car market value for fleet disposition decisions",
  "url": "http://localhost:8888/a2a"
}
```

---

## Architecture

```
CarProcessingWorkflow (main app :8080)
  └─► FleetSupervisorAgent
           └─► PricingAgent  @A2AClientAgent
                    │
                    │  JSON-RPC / HTTP
                    │  POST /a2a/tasks/send
                    │  { "input": { "carMake": "BMW", "carModel": "X5", ... } }
                    │
                    ▼
             remote-a2a-agent :8888
               AgentExecutor.execute(task)
                 └─► PricingAgent (local AI service)
                          └─► LLM call → "$38,500"
               Task result sent back to caller via JSON-RPC response
```

---

## MCP vs A2A — the key distinction

| Dimension | MCP | A2A |
|-----------|-----|-----|
| What travels | **Tool calls** (functions, typed args) | **Agent tasks** (goals, natural language) |
| Who executes | Tool server — runs a function | Remote agent — LLM + tools on their side |
| Team ownership | Shared capability (weather, search) | Autonomous team agent (pricing, legal) |
| State | Stateless per tool call | Optionally stateful (task lifecycle: pending → running → done) |
| Best for | Shared reusable functionality | Delegated decision-making to another team |

---

## Do

1. Return Car **#1** (Mercedes-Benz C-Class, RENTED) with:
```text
High-value luxury vehicle, minor damage, needs disposition decision
```

2. Correlate logs across **both** processes:
   - **Client (8080):** `[A2AClient] sending task to http://localhost:8888/a2a`
   - **Remote (8888):** `[AgentExecutor] received task, invoking PricingAgent`
   - **Remote (8888):** LLM call → `"$42,000"`
   - **Client (8080):** `[A2AClient] task completed: $42,000`

3. Verify the AgentCard endpoint:
```bash
curl http://localhost:8888/.well-known/agent.json | python3 -m json.tool
```

---

## Trade-offs to discuss

| Factor | Local agent | Remote A2A agent |
|--------|-------------|-----------------|
| Latency | In-process call | +HTTP + agent startup overhead |
| Ownership | Shared codebase/repo | Independent repo, release train, team |
| Scaling | Scale whole app | Scale pricing service independently |
| Failure mode | Shared crash domain | Network partition risk — needs timeout/retry |
| Reuse | One app only | Any A2A-compatible client in the enterprise |
| Testing | Unit-testable in isolation | Needs contract testing (AgentCard validation) |

---

## Done when

- [ ] Pricing ran in the remote process `:8888` (not `:8080`)
- [ ] Cross-process log correlation shows request ID matching on both sides
- [ ] AgentCard returns valid JSON from `GET /.well-known/agent.json`
- [ ] You can explain MCP vs A2A in one sentence each
- [ ] You can state one trade-off of distribution you would raise in a design review
