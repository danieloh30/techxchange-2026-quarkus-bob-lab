package com.carmanagement.agentic.agents;

// Imports you will need:
// import dev.langchain4j.agentic.Agent;
// import dev.langchain4j.service.SystemMessage;
// import dev.langchain4j.service.UserMessage;

/**
 * Exercise 5 — Step 1: declare PricingAgent.
 *
 * Used by FleetSupervisorAgent when a car may need disposition.
 * Returns a dollar value string like "Estimated Value: $42,000".
 *
 * Rules from AGENTS.md:
 *  - Interface only, no CDI scope
 *  - @Agent(outputKey="carValue") — supervisor reads this key
 */
public interface PricingAgent {

    // TODO Exercise 5 — Step 1:
    //  Add @SystemMessage for vehicle pricing specialist.
    //    Include brand base values and depreciation guidelines.
    //    Format: "Estimated Value: $XX,XXX\nJustification: [reasoning]"
    //
    //  Add @UserMessage with {carMake}, {carModel}, {carYear}, {carCondition}
    //
    //  Add @Agent(outputKey="carValue", description="Pricing specialist...")
    //
    //  Declare:
    //    String estimateValue(String carMake, String carModel,
    //                         Integer carYear, String carCondition);

}
