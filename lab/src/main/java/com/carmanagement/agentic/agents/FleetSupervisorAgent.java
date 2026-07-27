package com.carmanagement.agentic.agents;

import com.carmanagement.model.CarInfo;
import com.carmanagement.model.FeedbackAnalysisResults;
import dev.langchain4j.agentic.declarative.SupervisorAgent;
import dev.langchain4j.agentic.declarative.SupervisorRequest;

/**
 * Exercise 5 — Step 4: declare FleetSupervisorAgent.
 *
 * See docs/05-mcp/START_HERE.md Step 4 for full instructions.
 *
 * The supervisor is an interface with TWO annotated members:
 *
 *  1. @SupervisorAgent method — declares sub-agents and the outputKey.
 *     The LLM decides WHICH sub-agents to call and in what order.
 *
 *  2. @SupervisorRequest static method — builds the natural-language prompt
 *     given to the supervisor LLM. This is where policy lives — NOT in if/else Java code.
 *     The only Java here is one boolean check; everything else is prose.
 *
 * Sub-agents declared: PricingAgent, DispositionAgent, MaintenanceAgent, CleaningAgent
 *
 * Rules from AGENTS.md:
 *  - Interface only, no CDI scope
 *  - @SupervisorAgent(outputKey="supervisorDecision", subAgents={...})
 *  - @SupervisorRequest is a static method returning String
 */
public interface FleetSupervisorAgent {

    // TODO Exercise 5 — Step 4a: @SupervisorAgent method (replace this block).
    //
    // @SupervisorAgent(
    //         outputKey = "supervisorDecision",
    //         subAgents = { PricingAgent.class, DispositionAgent.class,
    //                       MaintenanceAgent.class, CleaningAgent.class })
    // String superviseCarProcessing(CarInfo carInfo, Integer carNumber,
    //                                FeedbackAnalysisResults feedbackAnalysisResults);

    // TODO Exercise 5 — Step 4b: @SupervisorRequest static method (replace this block).
    //
    // @SupervisorRequest
    // static String request(CarInfo carInfo, Integer carNumber,
    //                       FeedbackAnalysisResults feedbackAnalysisResults) {
    //
    //     boolean dispositionRequired = feedbackAnalysisResults.dispositionAnalysis() != null &&
    //             feedbackAnalysisResults.dispositionAnalysis().toUpperCase().contains("DISPOSITION_REQUIRED");
    //
    //     String noDispositionMessage = """
    //             No disposition has been requested.
    //             INSTRUCTIONS:
    //             - DO NOT invoke PricingAgent
    //             - DO NOT invoke DispositionAgent
    //             - Only invoke MaintenanceAgent if maintenance needed
    //             - Only invoke CleaningAgent if cleaning needed
    //             """;
    //
    //     String dispositionMessage = """
    //             The car has to be disposed.
    //             STEP 1: Invoke PricingAgent to get car value
    //             STEP 2: Invoke DispositionAgent (pass carValue as "$XX,XXX")
    //             STEP 3: If KEEP → invoke Maintenance/Cleaning as needed
    //             """;
    //
    //     return String.format("""
    //             Fleet supervisor. Car: %d %s %s (#%d)  Condition: %s
    //             Cleaning Analysis: %s
    //             Maintenance Analysis: %s
    //             %s
    //             """, carInfo.year, carInfo.make, carInfo.model, carNumber, carInfo.condition,
    //             feedbackAnalysisResults.cleaningAnalysis(),
    //             feedbackAnalysisResults.maintenanceAnalysis(),
    //             dispositionRequired ? dispositionMessage : noDispositionMessage);
    // }
    //
    // Full code (with the complete prompt) is in docs/05-mcp/START_HERE.md Step 4.

}
