package com.carmanagement.agentic.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * Exercise 5 — Step 1: declare PricingAgent.
 *
 * See docs/05-mcp/START_HERE.md Step 1 for full instructions.
 *
 * Called by FleetSupervisorAgent when disposition may be required.
 * Returns a dollar value string, e.g.:
 *   "Estimated Value: $42,000\nJustification: [reasoning]"
 *
 * Rules from AGENTS.md:
 *  - Interface only, no CDI scope
 *  - @Agent(outputKey="carValue") — supervisor reads this key
 *  - {current_date} is a built-in LangChain4j variable — resolves to today's date automatically
 */
public interface PricingAgent {

    // TODO Exercise 5 — Step 1: add the three annotations and method declaration.
    //
    // 1. @SystemMessage(""" ... """)
    //    Content:
    //    → Vehicle pricing specialist role
    //    → "{current_date}" for age calculation
    //    → Brand Base Values table (Luxury $50k-70k, Mainstream $28k-42k, Economy $22k-35k)
    //    → Depreciation schedule (age 1yr = -12%, 2yr = -27% total, etc.)
    //    → Condition adjustments (Excellent +5%, Fair -10%, Poor -20%)
    //    → Output format: "Estimated Value: $XX,XXX\nJustification: ..."
    //
    // 2. @UserMessage(""" ... """)
    //    Placeholders: {carMake}, {carModel}, {carYear}, {carCondition}
    //
    // 3. @Agent(outputKey = "carValue",
    //           description = "Pricing specialist that estimates vehicle market value...")
    //
    // Method signature:
    //    String estimateValue(String carMake, String carModel,
    //                         Integer carYear, String carCondition);
    //
    // Full code is in docs/05-mcp/START_HERE.md Step 1.

}
