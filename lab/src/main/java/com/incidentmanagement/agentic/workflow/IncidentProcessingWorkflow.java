package com.incidentmanagement.agentic.workflow;

import com.incidentmanagement.agentic.agents.ResolutionAgent;
import com.incidentmanagement.agentic.agents.IncidentSupervisorAgent;
import com.incidentmanagement.model.IncidentOutcome;
import com.incidentmanagement.model.IncidentInfo;
import com.incidentmanagement.model.AnalysisTask;
import dev.langchain4j.agentic.declarative.Output;
import dev.langchain4j.agentic.declarative.SequenceAgent;
import io.quarkus.logging.Log;

import java.util.List;

/**
 * Exercise 4 — Step 5: declare IncidentProcessingWorkflow.
 *
 * See docs/04-supervisor/START_HERE.md Step 5 for full instructions.
 *
 * Pattern: @SequenceAgent
 * Chains three sub-workflows/agents in order:
 *   1. IncidentAnalysisWorkflow  — parallel analysis → IncidentAnalysisResults
 *   2. IncidentSupervisorAgent   — orchestrates action agents based on results
 *   3. ResolutionAgent           — produces final IncidentOutcome (status + description)
 *
 * This is the top-level entry point called by IncidentManagementService.
 * The @Output method logs and returns the final IncidentOutcome.
 *
 * outputKey note: every sub-agent already declares its own outputKey —
 *   IncidentAnalysisWorkflow="incidentAnalysisResults",
 *   IncidentSupervisorAgent="supervisorDecision",
 *   ResolutionAgent="incidentOutcome".
 * The @SequenceAgent wires them automatically via AgenticScope.
 */
public interface IncidentProcessingWorkflow {

    // TODO Exercise 4 — Step 5a: @SequenceAgent method (replace this block).
    //
    // @SequenceAgent(outputKey = "incidentProcessingAgentResult",
    //         subAgents = { IncidentAnalysisWorkflow.class,
    //                       IncidentSupervisorAgent.class,
    //                       ResolutionAgent.class })
    // IncidentOutcome processIncident(List<AnalysisTask> tasks, IncidentInfo incidentInfo,
    //                                  Integer incidentNumber, String report);
    //
    // Full code is in docs/04-supervisor/START_HERE.md Step 5.

    // TODO Exercise 4 — Step 5b: @Output static method (replace this block).
    //
    // @Output
    // static IncidentOutcome output(IncidentOutcome incidentOutcome) {
    //     Log.debug("IncidentOutcome: " + incidentOutcome.resolution()
    //               + " → " + incidentOutcome.incidentAction());
    //     return incidentOutcome;
    // }
    //
    // The @Output method is NOT an LLM call — it's a final pass-through that logs
    // the result after all three sub-agents have completed.

}
