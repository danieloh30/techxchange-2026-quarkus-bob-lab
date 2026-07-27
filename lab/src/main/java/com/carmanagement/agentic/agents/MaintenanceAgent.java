package com.carmanagement.agentic.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * Exercise 3 — declare MaintenanceAgent.
 *
 * See docs/03-supervisor/START_HERE.md Step 1 for full instructions.
 *
 * No tool is needed — MaintenanceAgent returns a maintenance plan as text.
 * The FleetSupervisorAgent reads this text plan during Exercise 5.
 *
 * Rules from AGENTS.md:
 *  - Interface only, no CDI scope
 *  - @Agent(outputKey="analysisResult") — same key name as CleaningAgent (different workflow slot)
 *  - NO @ToolBox — this agent returns text only, no JPA mutations
 *  - @SystemMessage: maintenance intake role + list of available services
 *  - @UserMessage: {carMake}, {carModel}, {carYear}, {carNumber}, {maintenanceRequest}
 */
public interface MaintenanceAgent {

    // TODO Exercise 3 — Step 1: add the three annotations and method declaration below.
    //
    // 1. @SystemMessage(""" ... """)
    //    Content:
    //    → Maintenance intake role
    //    → Available services: Oil change, Tire rotation, Brake service,
    //      Engine service, Transmission service, Body work (dent repair, paint, collision repair)
    //    → "If no maintenance is needed based on the request, respond with MAINTENANCE_NOT_REQUIRED."
    //
    // 2. @UserMessage(""" ... """)
    //    Placeholders: {carMake}, {carModel}, {carYear}, {carNumber}, {maintenanceRequest}
    //
    // 3. @Agent(description = "Car maintenance specialist. ...",
    //           outputKey = "analysisResult")
    //
    // Method signature:
    //    String processMaintenance(String carMake, String carModel,
    //                              Integer carYear, Integer carNumber,
    //                              String maintenanceRequest);
    //
    // Full code is in docs/03-supervisor/START_HERE.md Step 1.

}
