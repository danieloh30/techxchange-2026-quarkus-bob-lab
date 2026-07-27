package com.carmanagement.agentic.workflow;

import com.carmanagement.agentic.agents.FeedbackAnalysisAgent;
import com.carmanagement.model.CarInfo;
import com.carmanagement.model.FeedbackAnalysisResults;
import com.carmanagement.model.FeedbackTask;
import dev.langchain4j.agentic.declarative.Output;
import dev.langchain4j.agentic.declarative.ParallelMapperAgent;
import dev.langchain4j.agentic.scope.AgenticScope;

import java.util.List;

/**
 * Exercise 4 — declare FeedbackAnalysisWorkflow.
 *
 * See docs/04-ibm-bob/START_HERE.md Step 2 for full instructions.
 *
 * Pattern: @ParallelMapperAgent
 * Runs FeedbackAnalysisAgent once per FeedbackTask in the input list — concurrently.
 * Three tasks in: [CLEANING, MAINTENANCE, DISPOSITION] → three String results out.
 *
 * The @Output static method transforms the List<String> results into a typed
 * FeedbackAnalysisResults record that downstream agents read from AgenticScope.
 *
 * Wall-clock time ≈ slowest single call (not sum of all three).
 *
 * Key concepts:
 *  - itemsProvider = "tasks": names the method parameter that holds the list to fan out
 *  - outputKey = "feedbackAnalysisResults": the key FleetSupervisorAgent reads from scope
 *  - @Output: post-processing step (not an LLM call) — maps List[0,1,2] → typed record
 */
public interface FeedbackAnalysisWorkflow {

    // TODO Exercise 4 — Step 2a: add the @ParallelMapperAgent annotation and method.
    //
    // @ParallelMapperAgent(
    //         description = "Analyzes car feedback in parallel for cleaning, maintenance, and disposition needs",
    //         outputKey = "feedbackAnalysisResults",
    //         subAgent = FeedbackAnalysisAgent.class,
    //         itemsProvider = "tasks")
    // FeedbackAnalysisResults analyzeFeedback(List<FeedbackTask> tasks,
    //                                         CarInfo carInfo,
    //                                         Integer carNumber,
    //                                         String feedback);
    //
    // Full code is in docs/04-ibm-bob/START_HERE.md Step 2.

    // TODO Exercise 4 — Step 2b: add the @Output static method.
    //
    // @Output
    // static FeedbackAnalysisResults output(AgenticScope scope,
    //                                       List<String> feedbackAnalysisResults) {
    //     return new FeedbackAnalysisResults(
    //             feedbackAnalysisResults.get(0),  // cleaningAnalysis
    //             feedbackAnalysisResults.get(1),  // maintenanceAnalysis
    //             feedbackAnalysisResults.get(2)   // dispositionAnalysis
    //     );
    // }
    //
    // Note: the List<String> parameter is positional — index 0 = CLEANING task result,
    // index 1 = MAINTENANCE task result, index 2 = DISPOSITION task result.
    // This order matches the FeedbackTask list created in CarManagementService.

}
