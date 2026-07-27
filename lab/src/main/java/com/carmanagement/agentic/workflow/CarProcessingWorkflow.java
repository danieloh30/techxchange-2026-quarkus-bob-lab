package com.carmanagement.agentic.workflow;

import com.carmanagement.agentic.agents.CarConditionFeedbackAgent;
import com.carmanagement.agentic.agents.FleetSupervisorAgent;
import com.carmanagement.model.CarConditions;
import com.carmanagement.model.CarInfo;
import com.carmanagement.model.FeedbackTask;
import dev.langchain4j.agentic.declarative.Output;
import dev.langchain4j.agentic.declarative.SequenceAgent;
import io.quarkus.logging.Log;

import java.util.List;

/**
 * Exercise 5 — Step 5: declare CarProcessingWorkflow.
 *
 * See docs/05-mcp/START_HERE.md Step 5 for full instructions.
 *
 * Pattern: @SequenceAgent
 * Chains three sub-workflows/agents in order:
 *   1. FeedbackAnalysisWorkflow  — parallel analysis → FeedbackAnalysisResults
 *   2. FleetSupervisorAgent      — orchestrates action agents based on results
 *   3. CarConditionFeedbackAgent — produces final CarConditions (status + description)
 *
 * This is the top-level entry point called by CarManagementService.
 * The @Output method logs and returns the final CarConditions.
 *
 * outputKey note: every sub-agent already declares its own outputKey —
 *   FeedbackAnalysisWorkflow="feedbackAnalysisResults",
 *   FleetSupervisorAgent="supervisorDecision",
 *   CarConditionFeedbackAgent="carConditions".
 * The @SequenceAgent wires them automatically via AgenticScope.
 */
public interface CarProcessingWorkflow {

    // TODO Exercise 5 — Step 5a: @SequenceAgent method (replace this block).
    //
    // @SequenceAgent(outputKey = "carProcessingAgentResult",
    //         subAgents = { FeedbackAnalysisWorkflow.class,
    //                       FleetSupervisorAgent.class,
    //                       CarConditionFeedbackAgent.class })
    // CarConditions processCarReturn(List<FeedbackTask> tasks, CarInfo carInfo,
    //                                 Integer carNumber, String feedback);
    //
    // Full code is in docs/05-mcp/START_HERE.md Step 5.

    // TODO Exercise 5 — Step 5b: @Output static method (replace this block).
    //
    // @Output
    // static CarConditions output(CarConditions carConditions) {
    //     Log.debug("CarConditions: " + carConditions.generalCondition()
    //               + " → " + carConditions.carAssignment());
    //     return carConditions;
    // }
    //
    // The @Output method is NOT an LLM call — it's a final pass-through that logs
    // the result after all three sub-agents have completed.

}
