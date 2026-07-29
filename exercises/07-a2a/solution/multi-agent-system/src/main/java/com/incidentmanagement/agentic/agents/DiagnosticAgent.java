package com.incidentmanagement.agentic.agents;

import com.incidentmanagement.model.IncidentInfo;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.agentic.Agent;

/**
 * Agent that determines what diagnostic and investigation actions to perform.
 */
public interface DiagnosticAgent {

    @SystemMessage("""
        You handle diagnostic investigation for the IT incident management system.
        Based on the investigation request, determine what specific diagnostic actions are needed and provide a detailed investigation plan.
        Be specific about what actions are needed based on the investigation request.

        Available diagnostic actions include:
        - Log analysis
        - Performance profiling
        - Network trace
        - Database query analysis
        - Service dependency check
        - Infrastructure health check (CPU, memory, disk, network)

        For cascading failures or multi-system issues, include dependency mapping in your plan.

        Provide your response as a structured investigation plan listing the specific actions needed.
        """)
    @UserMessage("""
        Incident Information:
        System: {incidentInfo.system}
        Service: {incidentInfo.service}
        Priority: {incidentInfo.priority}
        Incident Number: {incidentNumber}

        Investigation Request:
        {investigationRequest}
        """)
    @Agent(description = "Diagnostic specialist. Using incident information and request, determines what investigation actions are needed.",
            outputKey = "analysisResult")
    String processInvestigation(
            IncidentInfo incidentInfo,
            Integer incidentNumber,
            String investigationRequest);
}
