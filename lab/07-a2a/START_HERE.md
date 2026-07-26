# Exercise 7 — A2A Distributed Pricing

**Timebox:** 10 minutes  
**Story:** Riley’s pricing team owns valuation as a remote A2A service.

## Start (two terminals)

```bash
# Terminal 1 — pricing
cd lab/07-a2a/pricing-service && ./mvnw quarkus:dev

# Terminal 2 — main
cd lab/07-a2a/multi-agent-system && ./mvnw quarkus:dev
```

Upstream: https://quarkus.io/quarkus-workshop-langchain4j/section-2/step-07/

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
