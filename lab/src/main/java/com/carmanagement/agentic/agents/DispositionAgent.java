package com.carmanagement.agentic.agents;

// Imports you will need:
// import dev.langchain4j.agentic.Agent;
// import dev.langchain4j.service.SystemMessage;
// import dev.langchain4j.service.UserMessage;

/**
 * Exercise 5 — Step 2: declare DispositionAgent.
 *
 * Called by FleetSupervisorAgent AFTER PricingAgent.
 * Decides: SCRAP / SELL / DONATE / KEEP based on value + damage.
 *
 * Rules from AGENTS.md:
 *  - Interface only, no CDI scope
 *  - @Agent(outputKey="dispositionAction")
 */
public interface DispositionAgent {

    // TODO Exercise 5 — Step 2:
    //  Add @SystemMessage for disposition specialist.
    //    Options: SCRAP (beyond repair), SELL (aging/moderate damage),
    //             DONATE (low value but functional), KEEP (minor damage, worth keeping)
    //    Decision criteria: repair cost > 50% of value → SCRAP or SELL
    //
    //  Add @UserMessage with {carMake}, {carModel}, {carYear}, {carNumber},
    //    {carCondition}, {carValue}, {feedback}
    //
    //  Add @Agent(outputKey="dispositionAction", description="Car disposition specialist...")
    //
    //  Declare:
    //    String processDisposition(String carMake, String carModel, Integer carYear,
    //                              Integer carNumber, String carCondition,
    //                              String carValue, String feedback);

}
