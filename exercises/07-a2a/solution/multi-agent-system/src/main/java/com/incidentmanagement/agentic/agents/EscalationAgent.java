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
        - ESCALATE_P1: Incident requires immediate P1 escalation to senior engineering and management
        - ASSIGN_TEAM: Incident should be assigned to a specialized team for resolution
        - WORKAROUND: A temporary workaround can be applied while root cause is investigated
        - CLOSE: Incident can be resolved or closed with current information

        Decision Criteria:
        - If estimated revenue impact > $10,000/hour: Consider ESCALATE_P1
        - If incident affects multiple services or has security implications: ESCALATE_P1
        - If incident is contained to one service but needs specialized expertise: ASSIGN_TEAM
        - If a temporary fix can restore service while investigation continues: WORKAROUND
        - If incident is minor and can be resolved with standard procedures: CLOSE

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
        - Incident Report: {report}

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
            String report);
}
