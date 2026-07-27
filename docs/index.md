# Building Enterprise-Grade Agentic AI Systems with IBM Quarkus and Bob

**IBM TechXchange 2026 · Hands-On Lab**  
**Duration:** 90 minutes (10 min intro · 80 min hands-on)

Build an agentic fleet-management system with **IBM Enterprise Build of Quarkus**, **Quarkus LangChain4j**, and **IBM Bob** — covering workflow patterns, supervisor orchestration, MCP, A2A, human-in-the-loop, and observability.

![Workshop architecture](images/global-architecture.png)

## Lab at a glance

| Block | Duration | Focus |
|-------|----------|--------|
| [Intro presentation](00-intro/SPEAKER_NOTES.md) | 10 min | Story, architecture, what you will build |
| [Exercise 1](01-first-agents/START_HERE.md) | ~10 min | Your first AI agents |
| [Exercise 2](02-workflow-patterns/START_HERE.md) | ~10 min | Sequence, parallel, routing & loop workflows |
| [Exercise 3](03-supervisor/START_HERE.md) | ~10 min | Supervisor pattern |
| [Exercise 4](04-ibm-bob/START_HERE.md) | ~12 min | Pro-coding with IBM Bob |
| [Exercise 5](05-mcp/START_HERE.md) | ~10 min | MCP — remote tools for agents |
| [Exercise 6](06-hitl-observability/START_HERE.md) | ~10 min | Human-in-the-loop + observability |
| [Exercise 7](07-a2a/START_HERE.md) | ~10 min | A2A — distributed agents |

Read the full narrative and timing sheet in the **[Full lab guide](LAB_GUIDE.md)**.

## Prerequisites

- **JDK 25**
- Maven 3.9+ (or use `./mvnw` in each exercise)
- Quarkus **3.37.4** (Java 25)
- [IBM Bob](https://bob.ibm.com/)
- LLM API key (provided in the room)
- Free ports **8080**, **8081**, **8888**

## Get the code

```bash
git clone https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab.git
cd techxchange-2026-quarkus-bob-lab
export OPENAI_API_KEY=sk-your-key-here

cd exercises/01-first-agents/solution
./mvnw quarkus:dev
```

Open http://localhost:8080

Completed Quarkus solutions live in the repo under [`exercises/`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/tree/main/exercises).

## Repository layout

| Path | Purpose |
|------|---------|
| `docs/` | These lab instructions (this site) |
| `exercises/` | Completed Quarkus solution projects |

## Upstream

Exercise solutions are adapted from the community [Quarkus LangChain4j Workshop](https://github.com/quarkusio/quarkus-workshop-langchain4j). See [Attribution](ATTRIBUTION.md).
