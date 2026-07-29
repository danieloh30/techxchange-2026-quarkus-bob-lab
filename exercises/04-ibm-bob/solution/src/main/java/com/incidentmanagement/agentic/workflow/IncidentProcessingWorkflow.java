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
 * Workflow for processing incidents using a supervisor agent for complete orchestration.
 * The supervisor coordinates both incident analysis and action agents.
 */
public interface IncidentProcessingWorkflow {

    /**
     * Processes an incident by first analyzing the report, then using supervisor to coordinate actions.
     * IncidentAnalysisWorkflow analyzes the report in parallel and returns IncidentAnalysisResults via its @Output method.
     * IncidentSupervisorAgent uses these results to coordinate action agents.
     * ResolutionAgent determines the final incident assignment and outcome.
     */
    // --8<-- [start:sequence-agent]
    @SequenceAgent(outputKey = "incidentProcessingAgentResult",
            subAgents = { IncidentAnalysisWorkflow.class, IncidentSupervisorAgent.class, ResolutionAgent.class })
    // --8<-- [end:sequence-agent]
    IncidentOutcome processIncident(
            List<AnalysisTask> tasks,
            IncidentInfo incidentInfo,
            Integer incidentNumber,
            String feedback);

    @Output
    static IncidentOutcome output(IncidentOutcome incidentOutcome) {
        // ResolutionAgent handles all logic for determining
        // the final incident assignment and resolution description.
        Log.debug("IncidentOutcome: " + incidentOutcome.resolution() + " -> " + incidentOutcome.incidentAction());
        return incidentOutcome;
    }
}
