package com.incidentmanagement.agentic.workflow;

import com.incidentmanagement.agentic.agents.EscalationProposalAgent;
import com.incidentmanagement.agentic.agents.HumanApprovalAgent;
import com.incidentmanagement.agentic.agents.ResolutionAgent;
import com.incidentmanagement.agentic.agents.IncidentSupervisorAgent;
import com.incidentmanagement.model.IncidentOutcome;
import com.incidentmanagement.model.IncidentInfo;
import com.incidentmanagement.model.AnalysisTask;
import dev.langchain4j.agentic.declarative.Output;
import dev.langchain4j.agentic.declarative.SequenceAgent;
import dev.langchain4j.agentic.observability.MonitoredAgent;
import dev.langchain4j.data.message.ImageContent;
import io.quarkus.logging.Log;

import java.util.List;

public interface IncidentProcessingWorkflow extends MonitoredAgent {

    @SequenceAgent(outputKey = "incidentProcessingAgentResult",
            subAgents = {
                          // TODO (Bonus — Multimodal log analysis): add IncidentLogAnalysisAgent.class
                          //      as the FIRST sub-agent here so it enriches "report" before the
                          //      analysis runs. Remember to import it. See 07-a2a/START_HERE.md.
                          IncidentAnalysisWorkflow.class,
                          IncidentSupervisorAgent.class,
                          EscalationProposalAgent.class,
                          HumanApprovalAgent.class,
                          ResolutionAgent.class })
    IncidentOutcome processIncident(List<AnalysisTask> tasks, IncidentInfo incidentInfo,
                                     Integer incidentNumber, String report,
                                     ImageContent logImage);

    @Output
    static IncidentOutcome output(IncidentOutcome incidentOutcome) {
        Log.debug("IncidentOutcome: " + incidentOutcome.resolution()
                  + " → " + incidentOutcome.incidentAction());
        return incidentOutcome;
    }
}
