# Intro Presentation — Speaker Notes (10 min)

## Timing

| Min | Slide / beat | Script cue |
|-----|-------------|-----------|
| 0–1 | Title | Welcome. Today you build a production-shaped agentic AI fleet system — running, observable, and governed — in 80 minutes. |
| 1–3 | Miles of Smiles story | Walk the incident report. Three scrapped vehicles without review. Two stuck in `AT_CLEANING` for days. Real cost, real blame. |
| 3–5 | Chatbot vs Agent table | A chatbot *answers* — an agent *acts*. Draw the difference: side effects, tool calls, multi-step reasoning. |
| 5–7 | Persona → Pattern table | One persona per exercise. Maya → tool-calling agent. Jordan → IBM Bob with AGENTS.md. Riley → A2A. |
| 7–9 | Architecture diagram | Show the full target: `CarProcessingWorkflow → FeedbackAnalysisWorkflow (parallel × 3) → FleetSupervisorAgent → sub-agents`. Ports 8080/8081/8888. |
| 9–10 | Lab logistics | 7 exercises × ~10 min. Solutions folders — use them if behind, don't get stuck. Ports 8080/8081/8888 must be free. |

---

## Opening story (script — read this verbatim or close to it)

> Miles of Smiles rents cars. They know status codes — `RENTED`, `AT_CLEANING`, `IN_MAINTENANCE`, `AVAILABLE` — but they don't know *judgment*. When a customer writes "dog hair everywhere," a human decides. When someone writes "front end crushed, airbags deployed," a human *should* decide.
>
> Last quarter, that didn't always happen. Three expensive vehicles were scrapped by someone who typed the wrong status and nobody caught it. Two Mercedes-Benz and BMW rentals sat in `IN_MAINTENANCE` for three days waiting for a decision that could have been made in seconds with the right information.
>
> Today we build the judgment layer: **agentic AI** on **IBM Enterprise Build of Quarkus**. Agents will reason, call tools, collaborate in workflows, ask humans when the stakes are high, and talk to other teams' services over **MCP** and **A2A**.
>
> And because enterprise teams don't only need agents in production — they need to *build them safely* — we'll use **IBM Bob** as our AI development partner. With an `AGENTS.md` context file, Bob knows our project model upfront, so every coding request is accurate, governed, and token-efficient.

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

> **Instructor tip:** Use Car #5 (Ford Focus, 2014) for clean/dirty demos in Exercises 1–3 — it's low value and always `RENTED`. Use Car #1 (Mercedes-Benz C-Class) for HITL demos in Exercise 6.

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

## Why AGENTS.md (mention here, teach in Exercise 4)

> "Before we start, notice there's an `AGENTS.md` in the root of this repo. When Jordan opens IBM Bob in Exercise 4, the first thing they'll do is point Bob at that file. It tells Bob the entire `@Agent` programming model, all existing agents, and 10 rules — in one file. No background scans. No re-discovery. Fewer 'Bob Coins' spent. We'll come back to this in Exercise 4."

---

## Demo hygiene

- Pre-start Exercise 1 solution at port 8080 before doors open — run `./mvnw quarkus:dev` from `exercises/01-first-agents/solution`. Takes ~15s cold, ~3s warm.
- Have Bob signed in on the presenter laptop and `AGENTS.md` loaded in context before Exercise 4.
- Keep terminal 2 ready but blank — don't pre-start MCP server. Demonstrate the two-terminal startup live in Exercise 5.
- If the room has slow networks, pre-pull `docker.io/library/postgres:18` before the session: `docker pull postgres:18`.

---

## Time buffers (call these out loud)

| After exercise | If on time | If behind by 2+ min |
|----------------|-----------|---------------------|
| Ex 1 | Proceed | Skip stretch (tighten `@SystemMessage`) |
| Ex 2 | Proceed | Skip loop stretch (section 2.6) |
| Ex 3 | Proceed | Skip "try minor return" at the end |
| Ex 4 | Proceed | Skip Task D (SDLC CI) |
| Ex 5 | Proceed | Skip security discussion (5.5) |
| Ex 6 | Proceed | Skip Grafana span walk — just show HITL approve/reject |
| Ex 7 | Proceed | Skip trade-offs table — just correlate logs |
