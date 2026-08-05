# AGENTS.md — IBM Bob Project Context (Exercise 04-supervisor/solution)

> This is the project-local copy of `AGENTS.md` for the Exercise 4 solution.
> The authoritative version is the [`AGENTS.md`](../../../../AGENTS.md) at the repository root.
> Bob should read the root file first; this copy is here for IDE context loading.

See [../../../../AGENTS.md](../../../../AGENTS.md) for the full content.

**Quick reference for this exercise:**

- **Entry endpoint:** `POST http://localhost:8080/incident-management/process/{incidentNumber}`
- **Incident list:** `GET http://localhost:8080/incidents`
- **Dev UI:** `http://localhost:8080/q/dev-ui`
- **Key agents:** `IncidentSupervisorAgent`, `TriageAgent`, `DiagnosticAgent`, `EscalationAgent`, `ImpactAgent`
- **`IncidentStatus` enum values:** `OPEN`, `TRIAGING`, `IN_PROGRESS`, `ESCALATED`, `RESOLVED`
