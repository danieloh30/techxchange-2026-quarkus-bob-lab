# AGENTS.md — IBM Bob Project Context (Exercise 08-quarkus-flow/solution)

> This is the project-local copy of `AGENTS.md` for the Exercise 8 (bonus) solution.

**Quick reference for this exercise:**

- **Incident list:** `GET http://localhost:8080/incidents`
- **Report generation:** `POST http://localhost:8080/incident-report/{incidentId}`
- **Incident processing:** `POST http://localhost:8080/incident-management/process/{incidentNumber}`
- **Dev UI:** `http://localhost:8080/q/dev-ui`
- **Key agents:** `ReportDrafterAgent`, `ReportCriticAgent`, `IncidentReportFlow` (loop orchestrator)
- **`IncidentStatus` enum values:** `OPEN`, `TRIAGING`, `IN_PROGRESS`, `ESCALATED`, `RESOLVED`
