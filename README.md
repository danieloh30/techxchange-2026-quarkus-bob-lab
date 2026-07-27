![Lab cover](docs/images/cover_page.png)

# LAB-1219 - Building Enterprise-Grade Agentic AI Systems with IBM Quarkus and Bob

**IBM TechXchange 2026 · Hands-On Lab**  
**Duration:** 90 minutes (10 min intro · 80 min hands-on)  
**Lab site:** https://danieloh30.github.io/techxchange-2026-quarkus-bob-lab/

Build a multi-agent fleet-management system with **IBM Enterprise Build of Quarkus**, **Quarkus LangChain4j**, and **IBM Bob** — covering workflow patterns, supervisor orchestration, human-in-the-loop, observability, and A2A.

## Repository layout

| Path | Purpose |
|------|---------|
| [`lab/`](lab/) | Your hands-on Quarkus project (stub files with `// TODO`) |
| [`docs/`](docs/) | All lab instructions (Markdown) and images |
| [`exercises/`](exercises/) | Completed Quarkus solution projects for each exercise |

Start here:

1. **[docs/LAB_GUIDE.md](docs/LAB_GUIDE.md)** — full 90-minute guide (intro narrative + all exercises)
2. **[docs/index.md](docs/index.md)** — lab landing page with exercise table
3. **[exercises/README.md](exercises/README.md)** — exercise → solution mapping

## Quick start

```bash
git clone https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab.git
cd techxchange-2026-quarkus-bob-lab
export OPENAI_API_KEY=sk-your-key-here   # or lab-provided endpoint vars

# Start your working project for Exercise 1
cd lab
./mvnw quarkus:dev
```

Open http://localhost:8080

## Prerequisites

- **Java 25+**
- Maven 3.9+ (or use `./mvnw` in each exercise)
- Quarkus **3.37.4** (Java 25) — kept current via Dependabot
- IBM Bob ([bob.ibm.com](https://bob.ibm.com/))
- LLM API key (provided in the room)
- Free ports **8080**, **8888**
- Docker or Podman (for Quarkus Dev Services)

## Upstream attribution

Exercise solutions are adapted from the community [Quarkus LangChain4j Workshop](https://github.com/quarkusio/quarkus-workshop-langchain4j). See [docs/ATTRIBUTION.md](docs/ATTRIBUTION.md).
