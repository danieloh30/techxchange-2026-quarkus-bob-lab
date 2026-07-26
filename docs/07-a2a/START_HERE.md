# Exercise 7 — A2A Distributed Pricing

**Timebox:** 10 minutes  
**Story:** Riley’s pricing team owns valuation as a remote A2A service.  
**Solution:** [`exercises/07-a2a/solution`](../../exercises/07-a2a/solution)
- Main app: `multi-agent-system` (port 8080)
- Pricing A2A service: `remote-a2a-agent` (port 8888)

**Upstream:** [section-2/step-07](https://quarkus.io/quarkus-workshop-langchain4j/section-2/step-07/)

## Start (two terminals)

```bash
# Terminal 1 — pricing
cd exercises/07-a2a/solution/remote-a2a-agent && ./mvnw quarkus:dev

# Terminal 2 — main
cd exercises/07-a2a/solution/multi-agent-system && ./mvnw quarkus:dev
```

## Do

1. Confirm `:8888` (pricing) and `:8080` (main).
2. Process a return that requires valuation + disposition.
3. Correlate client `@A2AClientAgent` logs with remote `AgentExecutor` logs.
4. Discuss: what stayed local (HITL, disposition) vs what moved remote (pricing)?

## Concepts to name

- **AgentCard** — capability metadata  
- **AgentExecutor** — request handler  
- **Task** vs **Message** — long-running goal vs single exchange  

## Done when

- [ ] Pricing ran in the remote process
- [ ] You can explain one trade-off of distribution (ownership/scale vs complexity)
