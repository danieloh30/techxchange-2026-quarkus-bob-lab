package com.incidentmanagement.agentic.agents;

import dev.langchain4j.agentic.declarative.A2AClientAgent;

/**
 * Agent that estimates the business impact of an IT incident.
 * Delegates to the remote A2A impact assessment service.
 */
public interface ImpactAgent {

    @A2AClientAgent(a2aServerUrl = "http://localhost:8888",
        outputKey = "revenueImpact",
        description = "Impact assessment specialist that estimates business and revenue impact based on system, service, priority, and description")
    String assessImpact(String incidentSystem, String incidentService, String incidentPriority, String incidentDescription);
}
