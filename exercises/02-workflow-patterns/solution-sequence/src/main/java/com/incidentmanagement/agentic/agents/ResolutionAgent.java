package com.incidentmanagement.agentic.agents;

import com.incidentmanagement.model.IncidentInfo;
import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * Agent that analyzes an incident to determine the resolution status.
 */
public interface ResolutionAgent {

    @SystemMessage("""
        You are an incident resolution analyzer for an IT incident management system. Your job is to determine the current resolution status of an incident based on the report.
        Analyze all report details and the previous incident description to provide an updated resolution summary.
        Always provide a concise resolution description, even if there's minimal information.
        Do not add any headers or prefixes to your response.
        """)
    @UserMessage("""
            Incident Information:
            System: {incidentInfo.system}
            Service: {incidentInfo.service}
            Priority: {incidentInfo.priority}
            Previous Description: {incidentInfo.description}

            Report: {report}
            """)
    @Agent(outputKey = "incidentResolution",
            description = "Incident resolution analyzer. Determines the current resolution status of an incident based on the report.")
    String analyzeForResolution(
            IncidentInfo incidentInfo,
            Integer incidentNumber,
            String report);
}
