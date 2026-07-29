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
        - ESCALATE_P1: Incident has severe business impact requiring executive attention
        - ASSIGN_TEAM: Incident needs a dedicated response team but not executive escalation
        - WORKAROUND: A temporary workaround can mitigate the issue while a fix is developed
        - CLOSE: Incident can be resolved with current resources or is a non-issue

        Decision Criteria:
        - If estimated revenue impact > $500,000 per hour: Consider ESCALATE_P1
        - If incident affects revenue-critical systems with high priority: ESCALATE_P1
        - If incident requires cross-team coordination: ASSIGN_TEAM
        - If a temporary fix can restore service: WORKAROUND
        - If impact is minimal and can be handled routinely: CLOSE

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

        Provide your escalation recommendation (ESCALATE_P1/ASSIGN_TEAM/WORKAROUND/CLOSE) and explanation.
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
