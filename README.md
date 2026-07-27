![Lab cover](docs/images/cover_page.png)

# Building Enterprise-Grade Agentic AI Systems with IBM Quarkus and Bob

**IBM TechXchange 2026 · Hands-On Lab**  
**Duration:** 90 minutes (10 min intro · 80 min hands-on)  
**Lab site:** https://danieloh30.github.io/techxchange-2026-quarkus-bob-lab/

Agentic AI lab on **IBM Enterprise Build of Quarkus**, **Quarkus LangChain4j**, and **IBM Bob** — covering workflow patterns, supervisor orchestration, MCP, A2A, human-in-the-loop, and observability.

## Repository layout

| Path | Purpose |
|------|---------|
| [`docs/`](docs/) | All lab instructions (Markdown) and images |
| [`exercises/`](exercises/) | Completed Quarkus solution projects for each exercise |

Start here:

1. **[docs/LAB_GUIDE.md](docs/LAB_GUIDE.md)** — full 90-minute guide (intro narrative + all exercises)
2. **[docs/README.md](docs/README.md)** — docs index
3. **[exercises/README.md](exercises/README.md)** — exercise → workshop source mapping

## Quick start

```bash
git clone https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab.git
cd techxchange-2026-quarkus-bob-lab
export OPENAI_API_KEY=sk-your-key-here   # or lab-provided endpoint vars

cd exercises/01-first-agents/solution
./mvnw quarkus:dev
```

Open http://localhost:8080

## Prerequisites

- **Java 25+**
- Maven 3.9+ (or use `./mvnw` in each exercise)
- Quarkus **3.37.4** (Java 25) — kept current via Dependabot
- IBM Bob ([bob.ibm.com](https://bob.ibm.com/))
- LLM API key (provided in the room)
- Free ports **8080**, **8081**, **8888**

## Upstream attribution

Exercise solutions are adapted from the community [Quarkus LangChain4j Workshop](https://github.com/quarkusio/quarkus-workshop-langchain4j). See [docs/ATTRIBUTION.md](docs/ATTRIBUTION.md).
