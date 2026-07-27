package com.carmanagement.agentic.agents;

import com.carmanagement.model.CarInfo;
import com.carmanagement.model.FeedbackAnalysisResults;

// Imports you will need:
// import dev.langchain4j.agentic.declarative.SupervisorAgent;
// import dev.langchain4j.agentic.declarative.SupervisorRequest;

/**
 * Exercise 5 — Step 4: declare FleetSupervisorAgent.
 *
 * The supervisor is an interface with TWO annotated members:
 *
 *  1. @SupervisorAgent method — declares sub-agents and the outputKey.
 *     The LLM decides WHICH sub-agents to call and in what order.
 *
 *  2. @SupervisorRequest static method — builds the natural-language prompt
 *     given to the supervisor LLM. This is where policy lives (not in if/else code).
 *
 * Sub-agents declared: PricingAgent, DispositionAgent, MaintenanceAgent, CleaningAgent
 *
 * Rules from AGENTS.md:
 *  - Interface only, no CDI scope
 *  - @SupervisorAgent(outputKey="supervisorDecision", subAgents={...})
 *  - @SupervisorRequest is a static method returning String
 */
public interface FleetSupervisorAgent {

    // TODO Exercise 5 — Step 4a: @SupervisorAgent method
    //  Add @SupervisorAgent(
    //          outputKey = "supervisorDecision",
    //          subAgents = { PricingAgent.class, DispositionAgent.class,
    //                        MaintenanceAgent.class, CleaningAgent.class })
    //  Declare:
    //    String superviseCarProcessing(CarInfo carInfo, Integer carNumber,
    //                                  FeedbackAnalysisResults feedbackAnalysisResults);

    // TODO Exercise 5 — Step 4b: @SupervisorRequest static method
    //  Add @SupervisorRequest
    //  static String request(CarInfo carInfo, Integer carNumber,
    //                        FeedbackAnalysisResults feedbackAnalysisResults) {
    //
    //    boolean dispositionRequired = feedbackAnalysisResults.dispositionAnalysis()
    //            .toUpperCase().contains("DISPOSITION_REQUIRED");
    //
    //    String noDispositionMessage = """
    //        No disposition requested.
    //        - DO NOT invoke PricingAgent
    //        - DO NOT invoke DispositionAgent
    //        - Invoke MaintenanceAgent only if maintenance needed
    //        - Invoke CleaningAgent only if cleaning needed
    //        """;
    //
    //    String dispositionMessage = """
    //        Car must be disposed.
    //        STEP 1: Invoke PricingAgent to get car value
    //        STEP 2: Invoke DispositionAgent (pass carValue as "$XX,XXX")
    //        STEP 3: if KEEP → invoke Maintenance/Cleaning as needed
    //        """;
    //
    //    return String.format("""
    //        Fleet supervisor. Car: %d %s %s (#%d)
    //        Cleaning Analysis: %s
    //        Maintenance Analysis: %s
    //        %s
    //        """, carInfo.year, carInfo.make, carInfo.model, carNumber,
    //        feedbackAnalysisResults.cleaningAnalysis(),
    //        feedbackAnalysisResults.maintenanceAnalysis(),
    //        dispositionRequired ? dispositionMessage : noDispositionMessage);
    //  }

}
