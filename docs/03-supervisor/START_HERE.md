# Exercise 3 — Supervisor Pattern

**Timebox:** 10 minutes  
**Story:** Priya needs adaptive disposition — not another `if/else` tree.  
**Solution project:** [`exercises/03-supervisor/solution`](../../exercises/03-supervisor/solution)  
**Upstream:** [section-2/step-04](https://quarkus.io/quarkus-workshop-langchain4j/section-2/step-04/)

## Start

```bash
cd exercises/03-supervisor/solution
./mvnw quarkus:dev
```

## Do

1. Find `FleetSupervisorAgent` (`@SupervisorAgent`) and the parallel mapper feedback workflow.
2. Return a car with severe damage: `Front end crushed after collision; airbags deployed; not driveable`
3. In logs, note which sub-agents the supervisor invoked (pricing / disposition / maintenance / cleaning).
4. Compare mentally to Exercise 2 conditional routing — what decisions would be painful to hardcode?

## Done when

- [ ] Supervisor invoked more than one specialist for severe damage
- [ ] You can state one reason to prefer supervisors over pure conditionals
