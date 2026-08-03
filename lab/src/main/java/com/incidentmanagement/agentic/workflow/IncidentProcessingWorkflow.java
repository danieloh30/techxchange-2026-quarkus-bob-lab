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

public interface IncidentProcessingWorkflow extends MonitoredAgent {

    // TODO Exercise 4 — Step 5a: @SequenceAgent method — See docs/04-supervisor/START_HERE.md

    // TODO Exercise 4 — Step 5b: @Output static method — See docs/04-supervisor/START_HERE.md

}
