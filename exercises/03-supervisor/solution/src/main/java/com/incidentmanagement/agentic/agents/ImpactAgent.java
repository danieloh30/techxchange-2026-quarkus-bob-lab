package com.incidentmanagement.agentic.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * Agent that assesses the business impact of an incident.
 * Used by the supervisor to make escalation decisions.
 */
public interface ImpactAgent {

    @SystemMessage("""
        You are a business impact assessment specialist for IT incident management.

        Use these impact assessment guidelines:

        Revenue Impact Tiers:
        - Tier 1 (Revenue-Critical): Core payment, checkout, or order systems - $50,000-$100,000/hr loss
        - Tier 2 (High Impact): Customer-facing services, APIs, or authentication - $10,000-$50,000/hr loss
        - Tier 3 (Moderate Impact): Internal tools, batch processing, or reporting - $1,000-$10,000/hr loss
        - Tier 4 (Low Impact): Dev/staging environments, non-critical monitoring - <$1,000/hr loss

        Provide:
        1. Business impact level (HIGH/MEDIUM/LOW)
        2. Estimated revenue loss per hour (single dollar amount with comma separator)
        3. Brief justification (2-3 sentences explaining system criticality, customer impact, and severity factors)

        Format your response as:
        Business Impact: HIGH/MED/LOW
        Estimated Revenue Loss: $XX,XXX/hr
        Justification: [Your reasoning including system criticality and customer impact]
        """)
    @UserMessage("""
        Assess the business impact of this incident:
        - System: {system}
        - Service: {service}
        - Priority: {priority}
        - Description: {incidentDescription}
        """)
    @Agent(
        outputKey = "businessImpact",
        description = "Impact specialist that assesses business impact based on system, service, priority, and incident description"
    )
    String assessImpact(String system, String service, String priority, String incidentDescription);
}
