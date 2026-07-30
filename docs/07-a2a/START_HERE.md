# Exercise 7 — A2A: Distributed Impact Assessment Agent

<span class="badge badge--run-read">Run + Read</span>

**Timebox:** 10 minutes  
**Persona:** Riley — SRE team lead  
**You work in:** `exercises/07-a2a/solution/` (run + read)

!!! tip "Reference solution"
    [`exercises/07-a2a/solution`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/tree/main/exercises/07-a2a/solution)

---

## The problem

In Exercise 4, `ImpactAgent` ran inside the main app — same process, same release cycle. But Riley's SRE team needs independent ownership: their own repo, their own release cadence, and the ability to serve other IBM systems beyond Apex Systems.

Solution: convert `ImpactAgent` into an **Agent-to-Agent (A2A)** remote service.

---

## Start (two terminals)

Stop any running Quarkus process first (`Ctrl+C`).

=== "Terminal 1 — impact assessment service (start first)"

    ```bash
    cd exercises/07-a2a/solution/remote-a2a-agent
    ./mvnw quarkus:dev   # port :8888
    ```

=== "Terminal 2 — main system"

    ```bash
    cd exercises/07-a2a/solution/multi-agent-system
    ./mvnw quarkus:dev   # port :8080
    ```

Verify the AgentCard:
```bash
curl http://localhost:8888/.well-known/agent.json
```

Expected:
```json
{
  "name": "impact-agent",
  "description": "Estimates business impact and SLA cost for incident escalation decisions",
  "url": "http://localhost:8888/a2a",
  "capabilities": ["impact-assessment", "sla-analysis"]
}
```

!!! tip "Agentic Dev UI"
    Open the [Agentic Dev UI](http://localhost:8080/q/dev-ui/quarkus-langchain4j-agentic/agents) on the main system (:8080). Notice `ImpactAgent` now shows as `@A2AClientAgent` instead of a local LLM agent — the framework transparently proxies calls to the remote service.

    Check the [topology](http://localhost:8080/q/dev-ui/quarkus-langchain4j-agentic/topology) — this is the final evolution of the agent tree. Compare it to Exercise 4: `ImpactAgent` is now an A2A remote node instead of a local sub-agent.

---

## The A2A architecture

```mermaid
%%{init: {'look':'handDrawn','theme':'neutral','themeVariables': {'lineColor':'#4A4035'}}}%%
flowchart LR
    subgraph main[":8080 — Main App"]
        IPW([IncidentProcessingWorkflow])
        ISA([IncidentSupervisorAgent])
        IA([ImpactAgent<br/>@A2AClientAgent])
        IPW --> ISA --> IA
    end

    IA -->|"JSON-RPC / HTTP<br/>POST /a2a/tasks/send"| AE

    subgraph remote[":8888 — Remote Impact Assessment"]
        AE([AgentExecutor])
        RIA([ImpactAgent<br/>local LLM call])
        LLM([LLM → HIGH / $50k/hr])
        AE --> RIA --> LLM
    end

    LLM -->|Task result| IA

    style IPW fill:#D4E6F1,stroke:#2E6B8A
    style ISA fill:#FFE4CC,stroke:#B87333
    style IA fill:#FFF8DC,stroke:#C4A000
    style AE fill:#E8DCC4,stroke:#6B5B45
    style RIA fill:#D8F0D8,stroke:#3D7A3D
    style LLM fill:#E8E0F0,stroke:#6B5B8A
    style main fill:#F5F5F0,stroke:#8B8070
    style remote fill:#FFF5F0,stroke:#A08070
```

**A2A concepts:**

| Concept | Meaning | Analogy |
|---------|---------|---------|
| `AgentCard` | Capability metadata — what can this agent do? | Service contract / OpenAPI spec |
| `AgentExecutor` | Request handler on the server — processes an incoming `Task` | JAX-RS endpoint for agents |
| `Task` | Long-running, stateful goal with input/output envelope | Async job submission |
| `Message` | Single synchronous exchange within a task | Synchronous REST call |

---

## Try it

Process an incident that requires impact assessment + escalation:
```text
Complete service outage, all API endpoints returning 503, cascading failures across dependent services
```

Correlate logs across **both** processes:
- **Client (8080):** `[A2AClient] sending task to http://localhost:8888/a2a`
- **Remote (8888):** `[AgentExecutor] received task, invoking ImpactAgent`
- **Remote (8888):** `[ImpactAgent] Business Impact: HIGH, Revenue Loss: $50,000/hr`
- **Client (8080):** `[A2AClient] task completed, result: HIGH / $50,000/hr`

---

## MCP vs A2A — when to use each

You haven't built an MCP integration in this lab, but the distinction matters for architecture decisions:

| | MCP (Model Context Protocol) | A2A (Agent-to-Agent) |
|--|-----|-----|
| **What crosses the wire** | Tool call (function + typed args) | Agent task (goal + natural language) |
| **Who reasons** | Local LLM uses remote tool | Remote LLM reasons independently |
| **Team ownership** | Shared capability (weather, search, DB lookup) | Autonomous team agent (impact assessment, legal review) |
| **State** | Stateless per call | Optionally stateful (task lifecycle) |
| **Best for** | Shared functionality any agent can call | Delegated decision-making by another team |
| **Quarkus annotation** | `@McpToolBox("name")` | `@A2AClientAgent` |

**Rule of thumb:** If the remote service just *does something* when told exactly what to do → MCP. If it *decides something* using its own reasoning → A2A.

!!! example "Stretch: Try MCP"
    The [`exercises/05-mcp/`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/tree/main/exercises/05-mcp) folder contains a working MCP weather server and client. Run both and see `@McpToolBox` in action as a stretch exercise.

---

## Trade-offs

| Factor | Local agent (Ex 4) | Remote A2A agent |
|--------|-------------|-----------------|
| Latency | In-process | +HTTP round-trip |
| Ownership | Shared codebase | Independent repo + release |
| Scaling | Scale whole app | Scale impact service independently |
| Failure mode | Shared crash domain | Network partition risk |
| Reuse | Single app | Any A2A-compatible client |

---

<div class="done-when" markdown>

## :material-check-circle: Done when

- [ ] A2A: impact assessment ran in `:8888` (confirmed in remote process logs)
- [ ] AgentCard verified via `curl`
- [ ] You can contrast MCP vs A2A in one sentence each
- [ ] You can explain when to keep an agent local vs make it remote

</div>
