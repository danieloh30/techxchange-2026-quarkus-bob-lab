package com.incidentmanagement.agentic.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * Exercise 2 — declare DiagnosticAgent.
 *
 * See docs/02-maintenance-agent/START_HERE.md Step 1 for full instructions.
 *
 * No tool is needed — DiagnosticAgent returns a root cause analysis as text.
 * The IncidentSupervisorAgent reads this text plan during Exercise 4.
 *
 * Rules from AGENTS.md:
 *  - Interface only, no CDI scope
 *  - @Agent(outputKey="analysisResult") — same key name as TriageAgent (different workflow slot)
 *  - NO @ToolBox — this agent returns text only, no JPA mutations
 *  - @SystemMessage: diagnostic intake role + list of available actions
 *  - @UserMessage: {system}, {service}, {priority}, {incidentNumber}, {diagnosticRequest}
 */
public interface DiagnosticAgent {

    // TODO Exercise 2 — Step 1: add the three annotations and method declaration below.
    //
    // 1. @SystemMessage(""" ... """)
    //    Content:
    //    → Diagnostic intake role
    //    → Available actions: Log analysis, Service restart, Config rollback,
    //      Dependency check, Performance profiling, Network trace (packet capture, DNS, connectivity)
    //    → "If no diagnostic action is needed based on the request, respond with DIAGNOSTIC_NOT_REQUIRED."
    //
    // 2. @UserMessage(""" ... """)
    //    Placeholders: {system}, {service}, {priority}, {incidentNumber}, {diagnosticRequest}
    //
    // 3. @Agent(description = "Incident diagnostic specialist. ...",
    //           outputKey = "analysisResult")
    //
    // Method signature:
    //    String processDiagnostic(String system, String service,
    //                              Integer priority, Integer incidentNumber,
    //                              String diagnosticRequest);
    //
    // Full code is in docs/02-maintenance-agent/START_HERE.md Step 1.

}
