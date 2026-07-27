package com.carmanagement.agentic.workflow;

import com.carmanagement.agentic.agents.CarConditionFeedbackAgent;
import com.carmanagement.agentic.agents.FleetSupervisorAgent;
import com.carmanagement.model.CarConditions;
import com.carmanagement.model.CarInfo;
import com.carmanagement.model.FeedbackTask;

import java.util.List;

// Imports you will need:
// import dev.langchain4j.agentic.declarative.Output;
// import dev.langchain4j.agentic.declarative.SequenceAgent;
// import io.quarkus.logging.Log;

/**
 * Exercise 5 — Step 5: declare CarProcessingWorkflow.
 *
 * Pattern: @SequenceAgent
 * Chains three sub-agents in order:
 *   1. FeedbackAnalysisWorkflow  — parallel analysis → FeedbackAnalysisResults
 *   2. FleetSupervisorAgent      — orchestrates action agents based on results
 *   3. CarConditionFeedbackAgent — produces final CarConditions (status + description)
 *
 * This is the top-level entry point called by CarManagementService.
 * The @Output method logs and returns the final CarConditions.
 *
 * ⚠ outputKey rule: every sub-agent in this sequence already declares its own
 *   outputKey — FeedbackAnalysisWorkflow="feedbackAnalysisResults",
 *   FleetSupervisorAgent="supervisorDecision",
 *   CarConditionFeedbackAgent="carConditions".
 *   The @SequenceAgent wires them automatically.
 */
public interface CarProcessingWorkflow {

    // TODO Exercise 5 — Step 5a: @SequenceAgent method
    //  Add @SequenceAgent(outputKey = "carProcessingAgentResult",
    //          subAgents = { FeedbackAnalysisWorkflow.class,
    //                        FleetSupervisorAgent.class,
    //                        CarConditionFeedbackAgent.class })
    //  Declare:
    //    CarConditions processCarReturn(List<FeedbackTask> tasks, CarInfo carInfo,
    //                                   Integer carNumber, String feedback);

    // TODO Exercise 5 — Step 5b: @Output static method
    //  Add @Output
    //  static CarConditions output(CarConditions carConditions) {
    //      Log.debug("CarConditions: " + carConditions.generalCondition()
    //                + " → " + carConditions.carAssignment());
    //      return carConditions;
    //  }

}
