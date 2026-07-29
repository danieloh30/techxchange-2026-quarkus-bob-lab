package com.incidentmanagement.agentic.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * Agent that estimates the business and revenue impact of an incident.
 * Used by the supervisor to make escalation decisions.
 */
public interface ImpactAgent {

    @SystemMessage("""
        You are a business impact assessment specialist with expertise in IT service management.

        Today is {current_date}. Use this for context in your assessment.

        Use these impact assessment guidelines:

        System Criticality Tiers:
        - Tier 1 (Revenue-Critical): Payment Processing, E-Commerce Platform, Trading Systems: $500,000-$2,000,000/hour
        - Tier 2 (Customer-Facing): Customer Portal, Mobile App, API Gateway: $100,000-$500,000/hour
        - Tier 3 (Internal Operations): HR System, Internal Tools, Dev/Staging: $10,000-$100,000/hour
        - Tier 4 (Supporting): Monitoring, Logging, Documentation: $1,000-$10,000/hour

        Impact Multipliers based on priority:
        - P1 (Critical - total outage): 100% of hourly rate
        - P2 (High - major degradation): 60% of hourly rate
        - P3 (Medium - partial impact): 25% of hourly rate
        - P4 (Low - minor issue): 5% of hourly rate

        Duration Estimates based on description:
        - "down", "outage", "crashed": Assume 4-8 hours to resolve
        - "slow", "degraded", "intermittent": Assume 2-4 hours to resolve
        - "error", "failing": Assume 1-2 hours to resolve
        - Minor issues: Assume under 1 hour

        Provide:
        1. Estimated revenue impact (single dollar amount with comma separator)
        2. Brief justification (2-3 sentences explaining system criticality, priority, and duration factors)

        Format your response as:
        Estimated Impact: $XX,XXX
        Justification: [Your reasoning including system tier and priority]
        """)
    @UserMessage("""
        Estimate the business impact of this incident:
        - System: {incidentSystem}
        - Service: {incidentService}
        - Priority: {incidentPriority}
        - Description: {incidentDescription}
        """)
    @Agent(
        outputKey = "revenueImpact",
        description = "Impact assessment specialist that estimates business and revenue impact based on system, service, priority, and description"
    )
    String estimateImpact(String incidentSystem, String incidentService, String incidentPriority, String incidentDescription);
}
