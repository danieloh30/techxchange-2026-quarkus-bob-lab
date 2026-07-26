# Exercise 2 — Workflow Patterns

**Timebox:** 10 minutes  
**Story:** Chris (ops) needs sequence, parallel analysis, routing, and an optional quality loop.  
**Solution projects:**
- Sequence: [`exercises/02-workflow-patterns/solution-sequence`](../../exercises/02-workflow-patterns/solution-sequence) ← [upstream step-02](https://quarkus.io/quarkus-workshop-langchain4j/section-2/step-02/)
- Composed (parallel + conditional): [`exercises/02-workflow-patterns/solution-composed`](../../exercises/02-workflow-patterns/solution-composed) ← [upstream step-03](https://quarkus.io/quarkus-workshop-langchain4j/section-2/step-03/)

![Parallel execution](../images/parallel-execution.png)

## Start (use composed solution for the full pattern set)

```bash
cd exercises/02-workflow-patterns/solution-composed
./mvnw quarkus:dev
```

Optional: compare with `solution-sequence` first if you want the simpler prompt-chaining step.

## Do

1. Identify `@SequenceAgent`, `@ParallelAgent`, `@ConditionalAgent` in the solution.
2. Trace `AgenticScope`: where does cleaning analysis write data that routing reads?
3. Process feedback: `Engine warning light is on and the cabin smells like smoke`
4. **Stretch:** discuss where a `@LoopAgent` would refine a condition summary until it mentions severity (pattern covered in the lab guide; composed solution focuses on parallel + conditional nesting).

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
