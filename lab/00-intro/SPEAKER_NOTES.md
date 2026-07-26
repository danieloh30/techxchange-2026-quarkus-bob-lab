# Intro Presentation — Speaker Notes (10 min)

## Timing

| Min | Slide / beat | Say this |
|-----|--------------|----------|
| 0–1 | Title | Welcome to *Building Enterprise-Grade Agentic AI Systems with IBM Quarkus and Bob*. |
| 1–3 | Miles of Smiles story | Introduce the rental company, sticky-note ops, costly scrap mistakes. Mandate: automate with control. |
| 3–5 | Personas table | Walk Maya → Chris → Priya → Jordan → Sam → Alex → Riley. Each maps to an exercise. |
| 5–7 | Architecture diagram | Show growth from single CleaningAgent to supervisor + MCP + A2A + HITL. |
| 7–9 | Why Quarkus + Bob | Quarkus = enterprise Java runtime for agents. Bob = SDLC partner with guardrails — not just autocomplete. |
| 9–10 | Lab logistics | 7 exercises ~10 min; ports 8080/8081/8888; ask for help early; solutions folders if behind. |

## Opening story (script)

> Miles of Smiles rents cars. Their fleet software knows *status codes* — RENTED, CLEANING, AVAILABLE — but not *judgment*. When a customer writes “dog hair everywhere,” a human decides. When someone writes “airbags deployed,” a human *should* decide — and last quarter, that didn’t always happen.
>
> Today we build the judgment layer: **agentic AI** on **IBM Enterprise Build of Quarkus**. Agents will reason, call tools, collaborate in workflows, ask humans when the stakes are high, and talk to other teams’ services over **MCP** and **A2A**.
>
> And because enterprise teams don’t only need agents in production — they need to *build* them safely — we’ll use **IBM Bob** as our AI development partner: guardrails, approvals, and full SDLC support that go beyond typical coding copilots.

## Demo hygiene

- Pre-start one Quarkus app on 8080 before doors open (Exercise 1 solution) for a 30-second live return demo during the story.
- Have Bob signed in on the presenter laptop for Exercise 4.
- Keep a second terminal ready for MCP/A2A later — don’t live-debug networking in the intro.
