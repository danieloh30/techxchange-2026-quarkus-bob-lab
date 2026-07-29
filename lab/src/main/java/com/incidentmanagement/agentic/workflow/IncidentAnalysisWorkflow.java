package com.incidentmanagement.agentic.workflow;

import com.incidentmanagement.agentic.agents.IncidentAnalysisAgent;
import com.incidentmanagement.model.IncidentInfo;
import com.incidentmanagement.model.IncidentAnalysisResults;
import com.incidentmanagement.model.AnalysisTask;
import dev.langchain4j.agentic.declarative.Output;
import dev.langchain4j.agentic.declarative.ParallelMapperAgent;
import dev.langchain4j.agentic.scope.AgenticScope;

import java.util.List;

/**
 * Exercise 3 — declare IncidentAnalysisWorkflow.
 *
 * See docs/03-parallel-workflow/START_HERE.md Step 2 for full instructions.
 *
 * Pattern: @ParallelMapperAgent
 * Runs IncidentAnalysisAgent once per AnalysisTask in the input list — concurrently.
 * Three tasks in: [SEVERITY, IMPACT, RESOLUTION] → three String results out.
 *
 * The @Output static method transforms the List<String> results into a typed
 * IncidentAnalysisResults record that downstream agents read from AgenticScope.
 *
 * Wall-clock time ≈ slowest single call (not sum of all three).
 *
 * Key concepts:
 *  - itemsProvider = "tasks": names the method parameter that holds the list to fan out
 *  - outputKey = "incidentAnalysisResults": the key IncidentSupervisorAgent reads from scope
 *  - @Output: post-processing step (not an LLM call) — maps List[0,1,2] → typed record
 */
public interface IncidentAnalysisWorkflow {

    // TODO Exercise 3 — Step 2a: add the @ParallelMapperAgent annotation and method.
    //
    // @ParallelMapperAgent(
    //         description = "Analyzes incident reports in parallel for severity, impact, and resolution needs",
    //         outputKey = "incidentAnalysisResults",
    //         subAgent = IncidentAnalysisAgent.class,
    //         itemsProvider = "tasks")
    // IncidentAnalysisResults analyzeIncident(List<AnalysisTask> tasks,
    //                                         IncidentInfo incidentInfo,
    //                                         Integer incidentNumber,
    //                                         String report);
    //
    // Full code is in docs/03-parallel-workflow/START_HERE.md Step 2.

    // TODO Exercise 3 — Step 2b: add the @Output static method.
    //
    // @Output
    // static IncidentAnalysisResults output(AgenticScope scope,
    //                                       List<String> incidentAnalysisResults) {
    //     return new IncidentAnalysisResults(
    //             incidentAnalysisResults.get(0),  // severityAnalysis
    //             incidentAnalysisResults.get(1),  // impactAnalysis
    //             incidentAnalysisResults.get(2)   // resolutionAnalysis
    //     );
    // }
    //
    // Note: the List<String> parameter is positional — index 0 = SEVERITY task result,
    // index 1 = IMPACT task result, index 2 = RESOLUTION task result.
    // This order matches the AnalysisTask list created in IncidentManagementService.

}
