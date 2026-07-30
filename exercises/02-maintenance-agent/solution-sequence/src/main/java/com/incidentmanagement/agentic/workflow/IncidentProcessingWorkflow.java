package com.incidentmanagement.agentic.workflow;

import com.incidentmanagement.agentic.agents.ResolutionAgent;
import com.incidentmanagement.agentic.agents.TriageAgent;
import com.incidentmanagement.model.IncidentAction;
import com.incidentmanagement.model.IncidentOutcome;
import com.incidentmanagement.model.IncidentInfo;
import dev.langchain4j.agentic.declarative.Output;
import dev.langchain4j.agentic.declarative.SequenceAgent;
import dev.langchain4j.agentic.observability.MonitoredAgent;

/**
 * Workflow for processing incidents using a sequence of agents.
 */
public interface IncidentProcessingWorkflow extends MonitoredAgent {

    /**
     * Processes an incident by running triage and then resolution analysis.
     */
    @SequenceAgent(
            outputKey = "incidentOutcome",
            subAgents = { TriageAgent.class, ResolutionAgent.class })
    IncidentOutcome processIncident(
            IncidentInfo incidentInfo,
            Integer incidentNumber,
            String report);

    @Output
    static IncidentOutcome output(String incidentResolution, String analysisResult) {
        boolean triageRequired = !analysisResult.toUpperCase().contains("NOT_REQUIRED");
        return new IncidentOutcome(incidentResolution,
                triageRequired ? IncidentAction.TRIAGE : IncidentAction.RESOLVE);
    }
}
