package com.incidentmanagement.agentic.agents;

import io.quarkiverse.langchain4j.ToolBox;

import com.incidentmanagement.agentic.tools.TriageTool;
import com.incidentmanagement.model.IncidentInfo;
import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * Agent that determines what triage actions to perform on an incident.
 */
public interface TriageAgent {

    @SystemMessage("""
        You handle initial triage for the incident management system.
        """)
    @UserMessage("""
        Taking into account all provided incident details, determine if the incident needs triage actions.
        If the report indicates the incident needs categorization, initial assessment, or priority validation,
        call the provided tool and recommend appropriate triage actions (categorize, assess impact, validate priority, assign team).
        Be specific about what actions are needed.
        If no specific triage request is provided, request a standard categorization and assessment.

        Incident Information:
        System: {incidentInfo.system}
        Service: {incidentInfo.service}
        Priority: {incidentInfo.priority}
        Incident Number: {incidentNumber}

        Triage Request:
        {triageRequest}
        """)
    @Agent(description = "Triage specialist. Determines what triage actions are needed.",
            outputKey = "analysisResult")
    @ToolBox(TriageTool.class)
    String processTriage(
            IncidentInfo incidentInfo,
            Integer incidentNumber,
            String triageRequest);
}
