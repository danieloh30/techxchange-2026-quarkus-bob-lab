package com.carmanagement.agentic.agents;

import com.carmanagement.model.CarConditions;
import com.carmanagement.model.CarInfo;
import com.carmanagement.model.FeedbackAnalysisResults;

// Imports you will need:
// import dev.langchain4j.agentic.Agent;
// import dev.langchain4j.service.SystemMessage;
// import dev.langchain4j.service.UserMessage;

/**
 * Exercise 5 — Step 3: declare CarConditionFeedbackAgent.
 *
 * This is the FINAL agent in CarProcessingWorkflow (@SequenceAgent).
 * It reads everything produced so far and returns a typed CarConditions record.
 *
 * Important: the return type is CarConditions (a record), not String.
 * Quarkus LangChain4j will deserialize the LLM's JSON output into CarConditions.
 *
 * Rules from AGENTS.md:
 *  - Interface only, no CDI scope
 *  - @Agent(outputKey="carConditions")
 *  - Return CarConditions (not String)
 */
public interface CarConditionFeedbackAgent {

    // TODO Exercise 5 — Step 3:
    //  Add @SystemMessage that instructs the LLM to output JSON:
    //    { "generalCondition": "...", "carAssignment": "DISPOSITION|MAINTENANCE|CLEANING|NONE" }
    //  Rules:
    //    - DISPOSITION if supervisorDecision mentions SCRAP/SELL/DONATE (not KEEP)
    //    - MAINTENANCE if maintenanceAnalysis != "MAINTENANCE_NOT_REQUIRED"
    //    - CLEANING if cleaningAnalysis != "CLEANING_NOT_REQUIRED"
    //    - NONE otherwise
    //
    //  Add @UserMessage with {carInfo.*}, {carNumber}, {supervisorDecision},
    //    {feedbackAnalysisResults.dispositionAnalysis},
    //    {feedbackAnalysisResults.maintenanceAnalysis},
    //    {feedbackAnalysisResults.cleaningAnalysis}
    //
    //  Add @Agent(description="Final car condition analyzer...", outputKey="carConditions")
    //
    //  Declare:
    //    CarConditions analyzeForCondition(CarInfo carInfo, Integer carNumber,
    //                                      FeedbackAnalysisResults feedbackAnalysisResults,
    //                                      String supervisorDecision);

}
