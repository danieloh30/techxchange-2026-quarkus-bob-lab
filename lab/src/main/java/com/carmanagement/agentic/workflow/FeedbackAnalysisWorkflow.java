package com.carmanagement.agentic.workflow;

import com.carmanagement.agentic.agents.FeedbackAnalysisAgent;
import com.carmanagement.model.CarInfo;
import com.carmanagement.model.FeedbackAnalysisResults;
import com.carmanagement.model.FeedbackTask;
import dev.langchain4j.agentic.scope.AgenticScope;

import java.util.List;

// Imports you will need:
// import dev.langchain4j.agentic.declarative.Output;
// import dev.langchain4j.agentic.declarative.ParallelMapperAgent;

/**
 * Exercise 4 — Step 2: declare FeedbackAnalysisWorkflow.
 *
 * Pattern: @ParallelMapperAgent
 * Runs FeedbackAnalysisAgent once per FeedbackTask in the input list — concurrently.
 * Three tasks in: [CLEANING, MAINTENANCE, DISPOSITION] → three results out.
 *
 * The @Output static method transforms the List<String> results into a typed
 * FeedbackAnalysisResults record that downstream agents read from AgenticScope.
 *
 * Wall-clock time ≈ slowest single call (not sum of all three).
 */
public interface FeedbackAnalysisWorkflow {

    // TODO Exercise 4 — Step 2a: @ParallelMapperAgent method
    //  Add @ParallelMapperAgent(
    //          description = "Analyzes car feedback in parallel for cleaning, maintenance, disposition",
    //          outputKey = "feedbackAnalysisResults",
    //          subAgent = FeedbackAnalysisAgent.class,
    //          itemsProvider = "tasks")
    //  Declare:
    //    FeedbackAnalysisResults analyzeFeedback(List<FeedbackTask> tasks,
    //                                            CarInfo carInfo,
    //                                            Integer carNumber,
    //                                            String feedback);

    // TODO Exercise 4 — Step 2b: @Output static method
    //  Add @Output
    //  static FeedbackAnalysisResults output(AgenticScope scope,
    //                                        List<String> feedbackAnalysisResults) {
    //      return new FeedbackAnalysisResults(
    //              feedbackAnalysisResults.get(0),   // cleaningAnalysis
    //              feedbackAnalysisResults.get(1),   // maintenanceAnalysis
    //              feedbackAnalysisResults.get(2)    // dispositionAnalysis
    //      );
    //  }

}
