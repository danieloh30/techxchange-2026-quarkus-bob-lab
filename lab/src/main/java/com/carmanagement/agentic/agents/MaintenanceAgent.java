package com.carmanagement.agentic.agents;

// Imports you will need:
// import dev.langchain4j.agentic.Agent;
// import dev.langchain4j.service.SystemMessage;
// import dev.langchain4j.service.UserMessage;

/**
 * Exercise 3 — Step 1: declare MaintenanceAgent.
 *
 * No tool is needed — MaintenanceAgent returns a maintenance plan as text.
 * The FleetSupervisorAgent reads this text as part of its orchestration.
 *
 * Rules from AGENTS.md:
 *  - Interface only, no CDI scope
 *  - @Agent(outputKey="analysisResult") — same outputKey as CleaningAgent (different workflow slot)
 *  - @SystemMessage: maintenance intake role, list of services available
 *  - @UserMessage: {carMake}, {carModel}, {carYear}, {carNumber}, {maintenanceRequest}
 */
public interface MaintenanceAgent {

    // TODO Exercise 3 — Step 1:
    //  Add @SystemMessage for maintenance intake role.
    //    Available services: oil change, tire rotation, brake service,
    //    engine service, transmission service, body work.
    //    If no maintenance needed: return "MAINTENANCE_NOT_REQUIRED".
    //
    //  Add @UserMessage with car info + {maintenanceRequest} placeholder.
    //
    //  Add @Agent(description="Car maintenance specialist...", outputKey="analysisResult")
    //
    //  Declare:
    //    String processMaintenance(String carMake, String carModel,
    //                              Integer carYear, Integer carNumber,
    //                              String maintenanceRequest);

}
