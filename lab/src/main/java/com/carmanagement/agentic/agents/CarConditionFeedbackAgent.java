package com.carmanagement.agentic.agents;

import com.carmanagement.model.CarConditions;
import com.carmanagement.model.CarInfo;
import com.carmanagement.model.FeedbackAnalysisResults;
import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * Exercise 5 — Step 3: declare CarConditionFeedbackAgent.
 *
 * See docs/05-mcp/START_HERE.md Step 3 for full instructions.
 *
 * This is the FINAL agent in CarProcessingWorkflow (@SequenceAgent).
 * It reads all upstream results and returns a typed CarConditions record.
 *
 * Important: the return type is CarConditions (a record), not String.
 * Quarkus LangChain4j deserializes the LLM's JSON output into CarConditions automatically.
 * The @SystemMessage must instruct the LLM to output well-formed JSON.
 *
 * Rules from AGENTS.md:
 *  - Interface only, no CDI scope
 *  - @Agent(outputKey="carConditions")
 *  - Return CarConditions (NOT String)
 */
public interface CarConditionFeedbackAgent {

    // TODO Exercise 5 — Step 3: add the three annotations and method declaration.
    //
    // 1. @SystemMessage(""" ... """)
    //    Content: JSON output format + routing rules
    //    Output format:
    //      { "generalCondition": "...", "carAssignment": "DISPOSITION|MAINTENANCE|CLEANING|NONE" }
    //    Rules:
    //      - DISPOSITION if supervisorDecision mentions SCRAP/SELL/DONATE (NOT KEEP)
    //      - MAINTENANCE if maintenanceAnalysis ≠ "MAINTENANCE_NOT_REQUIRED"
    //      - CLEANING if cleaningAnalysis ≠ "CLEANING_NOT_REQUIRED"
    //      - NONE otherwise
    //      - If DispositionAgent decided KEEP → do NOT assign DISPOSITION
    //
    // 2. @UserMessage(""" ... """)
    //    Placeholders: {carInfo.year}, {carInfo.make}, {carInfo.model}, {carNumber},
    //                  {supervisorDecision},
    //                  {feedbackAnalysisResults.dispositionAnalysis},
    //                  {feedbackAnalysisResults.maintenanceAnalysis},
    //                  {feedbackAnalysisResults.cleaningAnalysis}
    //
    // 3. @Agent(description = "Final car condition analyzer...",
    //           outputKey = "carConditions")
    //
    // Method signature:
    //    CarConditions analyzeForCondition(CarInfo carInfo, Integer carNumber,
    //                                      FeedbackAnalysisResults feedbackAnalysisResults,
    //                                      String supervisorDecision);
    //
    // Full code is in docs/05-mcp/START_HERE.md Step 3.

}
