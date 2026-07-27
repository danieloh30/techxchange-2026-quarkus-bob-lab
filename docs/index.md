<img src="images/cover_page.png" alt="Lab cover" style="width:100%;max-width:960px;display:block;margin:0 auto 1.5rem auto;">

# Building Enterprise-Grade Agentic AI Systems with IBM Quarkus and Bob

**IBM TechXchange 2026 · Hands-On Lab**
**Duration:** 90 minutes (10 min intro · 80 min hands-on)

Build an agentic fleet-management system with **IBM Enterprise Build of Quarkus**, **Quarkus LangChain4j**, and **IBM Bob** — covering workflow patterns, supervisor orchestration, MCP, A2A, human-in-the-loop, and observability.

![Workshop architecture](images/global-architecture.png)

## Lab at a glance

| Block | Time | Focus |
|-------|------|--------|
| [Intro presentation](00-intro/SPEAKER_NOTES.md) | 10 min | Story, architecture, what you will build |
| [Exercise 1](01-first-agents/START_HERE.md) | 12 min | **IBM Bob setup + author `lab/AGENTS.md`** |
| [Exercise 2](02-workflow-patterns/START_HERE.md) | 10 min | First agent — `CleaningAgent` + `CleaningTool` |
| [Exercise 3](03-supervisor/START_HERE.md) | 10 min | `MaintenanceAgent` + `@SystemMessage` tuning |
| [Exercise 4](04-ibm-bob/START_HERE.md) | 10 min | Parallel workflow — `@ParallelMapperAgent` |
| [Exercise 5](05-mcp/START_HERE.md) | 15 min | Full multi-agent: supervisor + sequence workflow |
| [Exercise 6](06-hitl-observability/START_HERE.md) | 10 min | Human-in-the-loop + OpenTelemetry observability |
| [Exercise 7](07-a2a/START_HERE.md) | 10 min | MCP + A2A — remote tools and distributed agents |

Read the full narrative, timing sheet, and troubleshooting guide in the **[Full lab guide](LAB_GUIDE.md)**.

## Prerequisites

| Requirement | Notes |
|-------------|-------|
| **JDK 25** | `java -version` |
| Maven 3.9+ | or use `./mvnw` in each exercise |
| Quarkus **3.37.4** (IBM Enterprise Build) | Java 25 target |
| [IBM Bob](https://bob.ibm.com/) | Sign in before the lab starts |
| LLM API key | Provided in the room (`OPENAI_API_KEY`) |
| Free ports **8080**, **8081**, **8888** | One process per exercise set |
| Docker or Podman | For Quarkus Dev Services (PostgreSQL, LGTM) |

## Get the code

```bash
git clone https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab.git
cd techxchange-2026-quarkus-bob-lab
export OPENAI_API_KEY=sk-your-lab-key-here

# Your working project — start here for all exercises
cd lab
./mvnw quarkus:dev
```

Open http://localhost:8080 — Fleet Status UI with 8 seeded cars.
No agent behavior yet: that's Exercise 1.

> **Reference solutions** live in `exercises/*/solution`. Each exercise guide links to its solution at the top — use them only if you get stuck.

## AGENTS.md — project context for IBM Bob

The [`AGENTS.md`](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/blob/main/AGENTS.md) file in the repository root is a **token-efficient context file** for IBM Bob. Before any coding session, load it into Bob's context:

```text
Read AGENTS.md before answering anything about this project.
```

It contains: the `@Agent` declarative model, all 7 existing agents and their `outputKey` values, domain types (`CarInfo`, `CarStatus`, `FeedbackAnalysisResults`), API endpoints, and 10 project rules. This eliminates redundant codebase scans — estimated savings: **2,000–5,000 tokens per complex request**.

## Repository layout

| Path | Purpose |
|------|---------|
| `AGENTS.md` | IBM Bob project context (token efficiency) |
| `docs/` | These lab instructions (this site) |
| `exercises/` | Completed Quarkus solution projects |

## Upstream

Exercise solutions are adapted from the community [Quarkus LangChain4j Workshop](https://github.com/quarkusio/quarkus-workshop-langchain4j). See [Attribution](ATTRIBUTION.md).
