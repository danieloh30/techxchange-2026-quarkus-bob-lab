package com.incidentmanagement.agentic.workflow;

import com.incidentmanagement.agentic.agents.ResolutionAgent;
import com.incidentmanagement.agentic.agents.IncidentSupervisorAgent;
import com.incidentmanagement.model.IncidentOutcome;
import com.incidentmanagement.model.IncidentInfo;
import com.incidentmanagement.model.AnalysisTask;
import dev.langchain4j.agentic.declarative.Output;
import dev.langchain4j.agentic.declarative.SequenceAgent;
import dev.langchain4j.agentic.observability.MonitoredAgent;
import io.quarkus.logging.Log;

import java.util.List;

/**
 * Workflow for processing incidents using a supervisor agent for complete orchestration.
 * The supervisor coordinates both incident analysis and action agents.
 */
public interface IncidentProcessingWorkflow extends MonitoredAgent {

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
        // ResolutionAgent now handles all the logic for determining
        // the final incident assignment, escalation status, and resolution description.
        // We simply pass through its result.

        Log.debug("DEBUG IncidentOutcome output method:");
        Log.debug("  resolution: " + incidentOutcome.resolution());
        Log.debug("  incidentAction: " + incidentOutcome.incidentAction());
        Log.debug("  escalationStatus: " + incidentOutcome.escalationStatus());
        Log.debug("  escalationReason: " + incidentOutcome.escalationReason());

        return incidentOutcome;
    }
}
