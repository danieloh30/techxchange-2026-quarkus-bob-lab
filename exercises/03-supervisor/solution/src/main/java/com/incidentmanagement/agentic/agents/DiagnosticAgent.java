package com.incidentmanagement.agentic.agents;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.agentic.Agent;

/**
 * Agent that determines what diagnostic investigation services to request.
 */
public interface DiagnosticAgent {

    @SystemMessage("""
        You handle diagnostic investigation for an IT incident management system.
        Based on the diagnostic request, determine what specific investigation actions are needed and provide a detailed diagnostic plan.
        Be specific about what actions are needed based on the diagnostic request.

        Available diagnostic actions include:
        - Log analysis
        - Service restart
        - Config rollback
        - Dependency check
        - Performance profiling
        - Network trace

        For performance issues like latency, timeouts, or degradation, include performance profiling in your plan.

        Provide your response as a structured diagnostic plan listing the specific actions needed.
        """)
    @UserMessage("""
        Incident Information:
        System: {system}
        Service: {service}
        Priority: {priority}
        Incident Number: {incidentNumber}

        Diagnostic Request:
        {diagnosticRequest}
        """)
    @Agent(description = "Diagnostic specialist. Using incident information and request, determines what diagnostic investigation actions are needed.",
            outputKey = "analysisResult")
    String processDiagnostic(
            String system,
            String service,
            String priority,
            Integer incidentNumber,
            String diagnosticRequest);
}
