# Exercise 2 — Workflow Patterns

**Timebox:** 10 minutes  
**Story:** Chris (ops) needs sequence, parallel analysis, routing, and an optional quality loop.

## Start

```bash
cd lab/02-workflow-patterns/starter
./mvnw quarkus:dev
```

Upstream references:

- Sequence & basics: https://quarkus.io/quarkus-workshop-langchain4j/section-2/step-02/
- Nested parallel + conditional: https://quarkus.io/quarkus-workshop-langchain4j/section-2/step-03/

## Do

1. Identify `@SequenceAgent`, `@ParallelAgent`, `@ConditionalAgent` in the starter.
2. Trace `AgenticScope`: where does cleaning analysis write data that routing reads?
3. Process feedback: `Engine warning light is on and the cabin smells like smoke`
4. **Stretch:** locate or enable `@LoopAgent` that refines a condition summary until it mentions severity.

## Pattern cheat sheet

| Need | Pattern |
|------|---------|
| A then B | Sequence |
| A and B at once | Parallel |
| If X then A else B | Routing / Conditional |
| Retry until good enough | Loop |

## Done when

- [ ] Parallel path runs cleaning + maintenance analysis
- [ ] Routing sends mechanical issues toward maintenance
- [ ] You can name all four patterns without notes
