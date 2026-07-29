package com.incidentmanagement.agentic.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * Agent that determines how to escalate an incident based on impact, priority, and severity.
 */
public interface EscalationAgent {

    @SystemMessage("""
        You are an incident escalation specialist for an IT incident management system.
        Your job is to determine the best escalation action based on the incident's impact, priority, and affected systems.

        Escalation Options:
        - ESCALATE_TO_VP: Incident has severe business impact on revenue-critical systems
        - ESCALATE_TO_CTO: Incident involves critical infrastructure failure or security breach
        - KEEP_AT_TEAM_LEVEL: Incident can be handled by the current team
        - RESOLVE: Incident is minor and can be resolved immediately

        Decision Criteria:
        - If estimated revenue impact > $500,000: Consider ESCALATE_TO_VP or ESCALATE_TO_CTO
        - If incident involves security breach or data loss: ESCALATE_TO_CTO
        - If P1 on revenue-critical system with high impact: ESCALATE_TO_VP
        - If impact is moderate and team can handle: KEEP_AT_TEAM_LEVEL
        - If impact is minimal: RESOLVE

        Provide your recommendation with a clear explanation of the reasoning.
        """)
    @UserMessage("""
        Determine the escalation path for this incident:
        - System: {incidentSystem}
        - Service: {incidentService}
        - Priority: {incidentPriority}
        - Incident Number: {incidentNumber}
        - Current Description: {incidentDescription}
        - Estimated Revenue Impact: {revenueImpact}
        - Incident Report: {feedback}

        Provide your escalation recommendation (ESCALATE_TO_VP/ESCALATE_TO_CTO/KEEP_AT_TEAM_LEVEL/RESOLVE) and explanation.
        """)
    @Agent(outputKey = "escalationAction", description = "Incident escalation specialist. Determines how to escalate an incident based on impact and priority.")
    String processEscalation(
            String incidentSystem,
            String incidentService,
            String incidentPriority,
            Integer incidentNumber,
            String incidentDescription,
            String revenueImpact,
            String feedback);
}
