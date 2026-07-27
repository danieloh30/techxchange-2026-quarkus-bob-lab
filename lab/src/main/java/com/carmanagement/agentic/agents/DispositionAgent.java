package com.carmanagement.agentic.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * Exercise 5 — Step 2: declare DispositionAgent.
 *
 * See docs/05-mcp/START_HERE.md Step 2 for full instructions.
 *
 * Called by FleetSupervisorAgent AFTER PricingAgent.
 * Decides: SCRAP / SELL / DONATE / KEEP based on value + damage.
 *
 * Rules from AGENTS.md:
 *  - Interface only, no CDI scope
 *  - @Agent(outputKey="dispositionAction")
 *  - {carValue} placeholder in @UserMessage receives PricingAgent's output from AgenticScope
 */
public interface DispositionAgent {

    // TODO Exercise 5 — Step 2: add the three annotations and method declaration.
    //
    // 1. @SystemMessage(""" ... """)
    //    Content:
    //    → Disposition specialist role
    //    → Options: SCRAP (beyond repair), SELL (aging/moderate damage),
    //               DONATE (low value but functional), KEEP (minor damage, worth keeping)
    //    → Decision Criteria: repair cost > 50% of value → SCRAP or SELL
    //
    // 2. @UserMessage(""" ... """)
    //    Placeholders: {carMake}, {carModel}, {carYear}, {carNumber},
    //                  {carCondition}, {carValue}, {feedback}
    //    Note: {carValue} is passed by the supervisor from PricingAgent's AgenticScope output
    //
    // 3. @Agent(outputKey = "dispositionAction",
    //           description = "Car disposition specialist...")
    //
    // Method signature:
    //    String processDisposition(String carMake, String carModel, Integer carYear,
    //                              Integer carNumber, String carCondition,
    //                              String carValue, String feedback);
    //
    // Full code is in docs/05-mcp/START_HERE.md Step 2.

}
