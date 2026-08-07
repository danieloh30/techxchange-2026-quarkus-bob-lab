![Lab cover](docs/images/cover_page.png)

[![Deploy docs to GitHub Pages](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/workflows/Deploy%20docs%20to%20GitHub%20Pages/badge.svg)](https://github.com/danieloh30/techxchange-2026-quarkus-bob-lab/actions/workflows/docs.yml)

# LAB-1219 - Building Enterprise-Grade Agentic AI Systems with IBM Enterprise Build of Quarkus and Bob

**IBM TechXchange 2026 · Hands-On Lab**  
**Duration:** 90 minutes (10 min intro · 80 min hands-on)  
**Lab site:** https://danieloh30.github.io/techxchange-2026-quarkus-bob-lab/ (short URL: [bit.ly/lab-1219](https://bit.ly/lab-1219))  
**Intro deck:** [lab-1219-intro-deck.pdf](docs/images/lab-1219-intro-deck.pdf)

Build a multi-agent incident management system with **IBM Enterprise Build of Quarkus**, **Quarkus LangChain4j**, and **IBM Bob** — covering workflow patterns, supervisor orchestration, human-in-the-loop, observability, and A2A.

## Repository layout

| Path | Purpose |
|------|---------|
| [`lab/`](lab/) | Your hands-on Quarkus project (stub files with `// TODO`) |
| [`docs/`](docs/) | All lab instructions (Markdown) and images |
| [`solutions/`](solutions/) | Reference solution projects for each exercise |

Start here:

1. **[docs/index.md](docs/index.md)** — lab landing page with exercise table
2. **[solutions/README.md](solutions/README.md)** — exercise → solution mapping

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

## Reset labs

To revert all working code back to the original TODO stubs:

```bash
./reset-labs.sh          # Reset both lab/ (Ex 1-4) and Exercise 08
./reset-labs.sh lab      # Reset root lab only (Exercises 1-4)
./reset-labs.sh ex08     # Reset Exercise 08 lab only
```

## Prerequisites

- **Java 25+**
- Maven 3.9+ (or use `./mvnw` in each exercise)
- IBM Enterprise Build of Quarkus — kept current via Dependabot
- IBM Bob ([bob.ibm.com](https://bob.ibm.com/))
- LLM API key (provided in the room)
- Free ports **8080**, **8888**
- Docker or Podman (for Quarkus Dev Services)

