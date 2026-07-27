# AGENTS.md — IBM Bob Project Context (Exercise 04-ibm-bob/solution)

> This is the project-local copy of `AGENTS.md` for the Exercise 4 solution.
> The authoritative version is the [`AGENTS.md`](../../../../AGENTS.md) at the repository root.
> Bob should read the root file first; this copy is here for IDE context loading.

See [../../../../AGENTS.md](../../../../AGENTS.md) for the full content.

**Quick reference for this exercise:**

- **Entry endpoint:** `POST http://localhost:8080/car-management/return/{carNumber}`
- **Fleet list:** `GET http://localhost:8080/cars`
- **Dev UI:** `http://localhost:8080/q/dev-ui`
- **Key agents:** `FleetSupervisorAgent`, `CleaningAgent`, `MaintenanceAgent`, `DispositionAgent`, `PricingAgent`
- **`CarStatus` enum values:** `RENTED`, `AVAILABLE`, `AT_CLEANING`, `IN_MAINTENANCE`, `PENDING_DISPOSITION`
