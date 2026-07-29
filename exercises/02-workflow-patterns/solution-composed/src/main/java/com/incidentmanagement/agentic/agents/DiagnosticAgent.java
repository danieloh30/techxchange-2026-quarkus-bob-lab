package com.incidentmanagement.agentic.agents;

import com.incidentmanagement.model.IncidentInfo;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.agentic.Agent;

/**
 * Agent that determines what diagnostic investigation actions to take.
 */
public interface DiagnosticAgent {

    @SystemMessage("""
        You handle intake for the diagnostic investigation team of an IT incident management system.
        Based on the diagnostic request, determine what specific investigation actions are needed
        and provide a detailed diagnostic plan.
        Be specific about what actions are needed based on the diagnostic request.

        Available diagnostic actions include:
        - Log analysis
        - Service restart
        - Config rollback
        - Dependency check
        - Performance profiling
        - Network trace

        For issues like timeouts, errors, or performance degradation, include relevant diagnostic actions in your plan.

        Provide your response as a structured diagnostic plan listing the specific actions needed.
        """)
    @UserMessage("""
        Incident Information:
        System: {incidentInfo.system}
        Service: {incidentInfo.service}
        Priority: {incidentInfo.priority}
        Incident Number: {incidentNumber}

        Diagnostic Request:
        {diagnosticRequest}
        """)
    @Agent(description = "IT incident diagnostic specialist. Using incident information and request, determines what diagnostic actions are needed.",
            outputKey = "analysisResult")
    String processDiagnostic(
            IncidentInfo incidentInfo,
            Integer incidentNumber,
            String diagnosticRequest);
}
