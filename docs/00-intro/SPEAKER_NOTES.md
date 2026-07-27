# Intro Presentation — Speaker Notes (10 min)

## Timing

| Min | Slide / beat | Script cue |
|-----|-------------|-----------|
| 0–1 | Title | Welcome. Today you build a production-shaped agentic AI fleet system — running, observable, and governed — in 80 minutes. |
| 1–3 | Miles of Smiles story | Walk the incident report. Three scrapped vehicles without review. Two stuck in `AT_CLEANING` for days. Real cost, real blame. |
| 3–5 | Chatbot vs Agent table | A chatbot *answers* — an agent *acts*. Draw the difference: side effects, tool calls, multi-step reasoning. |
| 5–7 | Persona → Pattern table | One persona per exercise. Maya → tool-calling agent. Chris → parallel workflows. Priya → supervisor. Jordan → IBM Bob. |
| 7–9 | Architecture diagram | Show the full target: `CarProcessingWorkflow → FeedbackAnalysisWorkflow (parallel × 3) → FleetSupervisorAgent → sub-agents`. Ports 8080/8888. |
| 9–10 | Lab logistics | 7 exercises. Start coding immediately in Exercise 1. Solutions folders — use them if behind, don't get stuck. |

---

## Opening story (script — read this verbatim or close to it)

> Miles of Smiles rents cars. They know status codes — `RENTED`, `AT_CLEANING`, `IN_MAINTENANCE`, `AVAILABLE` — but they don't know *judgment*. When a customer writes "dog hair everywhere," a human decides. When someone writes "front end crushed, airbags deployed," a human *should* decide.
>
> Last quarter, that didn't always happen. Three expensive vehicles were scrapped by someone who typed the wrong status and nobody caught it. Two Mercedes-Benz and BMW rentals sat in `IN_MAINTENANCE` for three days waiting for a decision that could have been made in seconds with the right information.
>
> Today we build the judgment layer: **agentic AI** on **IBM Enterprise Build of Quarkus**. Agents will reason, call tools, collaborate in workflows, ask humans when the stakes are high, and talk to other teams' services over **A2A**.
>
> And because enterprise teams don't only need agents in production — they need to *build them safely* — we'll use **IBM Bob** as our AI development partner. In Exercise 5, after you've built the full agent system, you'll use Bob with an `AGENTS.md` context file to validate and govern what you created.

---

## The seeded fleet (visible at http://localhost:8080 after Exercise 1 starts)

| Car ID | Make | Model | Year | Status | HITL trigger? |
|--------|------|-------|------|--------|--------------|
| 1 | Mercedes-Benz | C-Class | 2024 | RENTED | Yes (high value) |
| 2 | BMW | X5 | 2025 | IN_MAINTENANCE | Yes (high value) |
| 3 | Audi | Q4 | 2025 | RENTED | Yes (high value) |
| 4 | Nissan | Altima | 2018 | AT_CLEANING | No |
| 5 | Ford | Focus | 2014 | RENTED | No (low value, high mileage) |
| 6 | Toyota | Corolla | 2023 | RENTED | No |
| 7 | Honda | Civic | 2022 | RENTED | No |
| 8 | Ford | F-150 | 2024 | IN_MAINTENANCE | No |

> **Instructor tip:** Use Car #5 (Ford Focus, 2014) for dirty-car demos in Exercise 1. Use Car #6 (Toyota Corolla) for clean-car demos. Use Car #1 (Mercedes-Benz C-Class) for HITL demos in Exercise 6 and severe damage in Exercise 4.

---

## API endpoints (for live demos and troubleshooting)

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `http://localhost:8080/cars` | Full fleet list (JSON) |
| `GET` | `http://localhost:8080/cars/{id}` | Single car |
| `POST` | `http://localhost:8080/car-management/return/{carNumber}` | Return a car (triggers agentic workflow) |

The UI at `http://localhost:8080` wraps these endpoints — participants don't need to use `curl` unless they want to.

---

## Chatbot vs Agent — flip card (say this, then show the table)

> "If I ask a chatbot 'does this car need cleaning?' it tells me. If I ask an agent, it *decides*, calls the cleaning system, and changes the database. Same question — completely different architecture."

| Chatbot | Agent |
|---------|-------|
| Answers text | Takes actions |
| One LLM call | Multiple LLM rounds + tool calls |
| No side effects | Mutates state (`CarStatus → AT_CLEANING`) |
| Stateless | Shares state via `AgenticScope` across workflow |
| Single model | Composed specialists (cleaning, maintenance, pricing) |

---

## Why AGENTS.md (mention here, teach in Exercise 5)

> "Before we start, notice there's an `AGENTS.md` in the root of this repo. It tells IBM Bob the entire `@Agent` programming model, all existing agents, and 10 rules — in one file. No background scans. No re-discovery. Fewer 'Bob Coins' spent. You'll use this in Exercise 5 after you've built the full agent system — documenting and governing what you created."

---

## Demo hygiene

- Have `lab/` ready to start at port 8080 — first thing students do in Exercise 1 is `cd lab && ./mvnw quarkus:dev`. Takes ~15s cold, ~3s warm.
- Have Bob signed in on the presenter laptop and `AGENTS.md` loaded in context before Exercise 5.
- Keep terminal 2 ready but blank — demonstrate the two-terminal startup live in Exercise 7.
- If the room has slow networks, pre-pull `docker.io/library/postgres:18` before the session: `docker pull postgres:18`.

---

## Time buffers (call these out loud)

| After exercise | If on time | If behind by 2+ min |
|----------------|-----------|---------------------|
| Ex 1 | Proceed | Skip clean-car test; just show dirty-car path |
| Ex 2 | Proceed | Skip @SystemMessage tuning experiment |
| Ex 3 | Proceed | Skip Dev UI CDI bean check |
| Ex 4 | Proceed | Skip Path 1 (clean return); keep Paths 2+3 |
| Ex 5 | Proceed | Keep guardrail demo; skip security audit |
| Ex 6 | Proceed | Skip Grafana span walk — just show HITL approve/reject |
| Ex 7 | Proceed | Skip trade-offs table — just correlate logs |
