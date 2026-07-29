package com.incidentmanagement.agentic.agents;

import io.quarkiverse.langchain4j.ToolBox;

import com.incidentmanagement.agentic.tools.TriageTool;
import com.incidentmanagement.model.IncidentInfo;
import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * Agent that handles intake for the triage process of an IT incident management system.
 */
public interface TriageAgent {

    @SystemMessage("""
        You are an IT incident triage specialist for an incident management system.
        It is your job to submit a request to the provided requestTriage function
        to initiate triage for incoming incidents.
        Based on the report, determine the appropriate triage actions including
        whether to assign on-call personnel, notify stakeholders, create a war room,
        and link related incidents.
        If no triage action is needed based on the report, respond with "TRIAGE_NOT_REQUIRED".
        """)
    @UserMessage("""
        Taking into account the incident report, determine if the incident needs triage.
        If the report indicates service degradation, outages, errors, or any operational issues,
        call the provided tool and recommend appropriate triage actions.
        Be specific about what actions are needed.
        If no triage is needed based on the report, respond with "TRIAGE_NOT_REQUIRED".

        Incident Information:
        System: {incidentInfo.system}
        Service: {incidentInfo.service}
        Priority: {incidentInfo.priority}
        Incident Number: {incidentNumber}

        Triage Request:
        {triageRequest}
        """)
    @Agent(description = "Triage specialist. Determines initial triage and team assignment.",
            outputKey = "analysisResult")
    @ToolBox(TriageTool.class)
    String processTriage(
            IncidentInfo incidentInfo,
            Integer incidentNumber,
            String triageRequest);
}
