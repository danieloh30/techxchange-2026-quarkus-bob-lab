package com.incidentmanagement.agentic.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * Exercise 4 — Step 1: declare ImpactAgent.
 *
 * See docs/04-supervisor/START_HERE.md Step 1 for full instructions.
 *
 * Called by IncidentSupervisorAgent when escalation may be required.
 * Returns a business impact assessment string, e.g.:
 *   "Business Impact: HIGH\nEstimated Revenue Loss: $15,000/hr\nJustification: [reasoning]"
 *
 * Rules from AGENTS.md:
 *  - Interface only, no CDI scope
 *  - @Agent(outputKey="businessImpact") — supervisor reads this key
 *  - {current_date} is a built-in LangChain4j variable — resolves to today's date automatically
 */
public interface ImpactAgent {

    // TODO Exercise 4 — Step 1: add the three annotations and method declaration.
    //
    // 1. @SystemMessage(""" ... """)
    //    Content:
    //    → Business impact assessment specialist role
    //    → "{current_date}" for SLA calculation
    //    → System Criticality tiers (Tier 1: revenue-critical $50k-100k/hr,
    //         Tier 2: customer-facing $10k-50k/hr, Tier 3: internal $1k-10k/hr,
    //         Tier 4: non-critical <$1k/hr)
    //    → Impact multipliers (P1 = 4x, P2 = 2x, P3 = 1x, P4 = 0.5x)
    //    → SLA breach penalties (P1 >1hr = $25k, P2 >4hr = $10k, etc.)
    //    → Output format: "Business Impact: HIGH/MEDIUM/LOW\nEstimated Revenue Loss: $XX,XXX/hr\nJustification: ..."
    //
    // 2. @UserMessage(""" ... """)
    //    Placeholders: {system}, {service}, {priority}, {incidentDescription}
    //
    // 3. @Agent(outputKey = "businessImpact",
    //           description = "Impact assessment specialist that estimates business impact...")
    //
    // Method signature:
    //    String assessImpact(String system, String service,
    //                         Integer priority, String incidentDescription);
    //
    // Full code is in docs/04-supervisor/START_HERE.md Step 1.

}
