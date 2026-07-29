package com.incidentmanagement.agentic.agents;

import io.quarkiverse.langchain4j.ToolBox;

import com.incidentmanagement.agentic.tools.TriageTool;
import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * Agent that determines what triage actions to request for an incident.
 */
public interface TriageAgent {

    @SystemMessage("""
        You handle initial triage for an IT incident management system.
        """)
    @UserMessage("""
        Taking into account all provided information, determine if the incident needs triage.
        If the report indicates the incident requires initial assessment, classification, or routing,
        call the provided tool and recommend appropriate triage actions (assign on-call, notify stakeholders, create war room, link related incidents).
        Be specific about what actions are needed.
        If no specific triage request is provided, request a standard triage assessment.

        Incident Information:
        System: {system}
        Service: {service}
        Priority: {priority}
        Incident Number: {incidentNumber}

        Report:
        {report}
        """)
    @Agent(description = "Triage specialist. Determines what triage actions are needed.",
            outputKey = "analysisResult")
    @ToolBox(TriageTool.class)
    String processTriage(
            String system,
            String service,
            String priority,
            Integer incidentNumber,
            String report);
}
