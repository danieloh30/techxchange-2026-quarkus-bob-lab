package com.incidentmanagement.agentic.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * Agent that creates escalation proposals for incidents requiring escalation.
 * This agent analyzes the incident and creates a proposal that will be reviewed
 * by the HumanApprovalAgent if the business impact exceeds the threshold.
 */
public interface EscalationProposalAgent {

    @SystemMessage("""
        You are an incident escalation specialist for an IT incident management system.
        Your job is to create an escalation proposal based on the incident's impact, priority, scope, and business cost.

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

        Your response must include:
        1. Proposed Action with unique marker: __ESCALATE_P1__ or __ASSIGN_TEAM__ or __WORKAROUND__ or __CLOSE__
        2. Reasoning: Clear explanation of your recommendation

        Format your response as:
        Proposed Action: __[ESCALATE_P1/ASSIGN_TEAM/WORKAROUND/CLOSE]__
        Reasoning: [Your detailed explanation]

        CRITICAL: Use double underscores around the action (e.g., __CLOSE__ not CLOSE)
        """)
    @UserMessage("""
        Create an escalation proposal for this incident:
        - System: {incidentSystem}
        - Service: {incidentService}
        - Priority: {incidentPriority}
        - Incident Number: {incidentNumber}
        - Current Description: {incidentDescription}
        - Estimated Revenue Impact: {businessImpact}
        - Incident Report: {report}

        Provide your escalation proposal with clear reasoning.
        """)
    @Agent(outputKey = "escalationProposal", description = "Creates escalation proposals for incidents requiring escalation")
    String createEscalationProposal(
            String incidentSystem,
            String incidentService,
            String incidentPriority,
            Integer incidentNumber,
            String incidentDescription,
            String businessImpact,
            String report);
}
