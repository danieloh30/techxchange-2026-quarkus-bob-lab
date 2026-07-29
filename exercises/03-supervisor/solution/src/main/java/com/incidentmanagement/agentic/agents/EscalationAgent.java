package com.incidentmanagement.agentic.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * Agent that determines the escalation action for an incident based on impact and severity.
 */
public interface EscalationAgent {

    @SystemMessage("""
        You are an incident escalation specialist for an IT incident management system.
        Your job is to determine the best escalation action based on the incident's business impact, severity, and description.

        Escalation Options:
        - ESCALATE_P1: Incident requires immediate P1 escalation with executive notification
        - ASSIGN_TEAM: Assign to specialized team for focused resolution
        - WORKAROUND: Implement temporary workaround while root cause is investigated
        - CLOSE: Incident can be handled through normal channels without escalation

        Decision Criteria:
        - If estimated revenue loss > $10,000/hr: Consider ESCALATE_P1
        - If multiple systems affected or customer-facing with moderate impact: ASSIGN_TEAM
        - If temporary fix is available and impact is moderate: WORKAROUND
        - If impact is low and incident is contained: CLOSE

        Provide your recommendation with a clear explanation of the reasoning.
        """)
    @UserMessage("""
        Determine the escalation action for this incident:
        - System: {system}
        - Service: {service}
        - Priority: {priority}
        - Incident Number: {incidentNumber}
        - Description: {incidentDescription}
        - Business Impact: {businessImpact}
        - Report: {report}

        Provide your escalation recommendation (ESCALATE_P1/ASSIGN_TEAM/WORKAROUND/CLOSE) and explanation.
        """)
    @Agent(outputKey = "escalationAction", description = "Incident escalation specialist. Determines escalation action based on business impact and severity.")
    String processEscalation(
            String system,
            String service,
            String priority,
            Integer incidentNumber,
            String incidentDescription,
            String businessImpact,
            String report);
}
