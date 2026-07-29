package com.incidentmanagement.agentic.agents;

import com.incidentmanagement.model.IncidentInfo;
import com.incidentmanagement.model.IncidentAnalysisResults;
import dev.langchain4j.agentic.declarative.SupervisorAgent;
import dev.langchain4j.agentic.declarative.SupervisorRequest;

/**
 * Exercise 4 — Step 4: declare IncidentSupervisorAgent.
 *
 * See docs/04-supervisor/START_HERE.md Step 4 for full instructions.
 *
 * The supervisor is an interface with TWO annotated members:
 *
 *  1. @SupervisorAgent method — declares sub-agents and the outputKey.
 *     The LLM decides WHICH sub-agents to call and in what order.
 *
 *  2. @SupervisorRequest static method — builds the natural-language prompt
 *     given to the supervisor LLM. This is where policy lives — NOT in if/else Java code.
 *     The only Java here is one boolean check; everything else is prose.
 *
 * Sub-agents declared: ImpactAgent, EscalationAgent, DiagnosticAgent, TriageAgent
 *
 * Rules from AGENTS.md:
 *  - Interface only, no CDI scope
 *  - @SupervisorAgent(outputKey="supervisorDecision", subAgents={...})
 *  - @SupervisorRequest is a static method returning String
 */
public interface IncidentSupervisorAgent {

    // TODO Exercise 4 — Step 4a: @SupervisorAgent method (replace this block).
    //
    // @SupervisorAgent(
    //         outputKey = "supervisorDecision",
    //         subAgents = { ImpactAgent.class, EscalationAgent.class,
    //                       DiagnosticAgent.class, TriageAgent.class })
    // String superviseIncidentProcessing(IncidentInfo incidentInfo, Integer incidentNumber,
    //                                     IncidentAnalysisResults incidentAnalysisResults);

    // TODO Exercise 4 — Step 4b: @SupervisorRequest static method (replace this block).
    //
    // @SupervisorRequest
    // static String request(IncidentInfo incidentInfo, Integer incidentNumber,
    //                       IncidentAnalysisResults incidentAnalysisResults) {
    //
    //     boolean escalationRequired = incidentAnalysisResults.resolutionAnalysis() != null &&
    //             incidentAnalysisResults.resolutionAnalysis().toUpperCase().contains("ESCALATION_REQUIRED");
    //
    //     String noEscalationMessage = """
    //             No escalation has been requested.
    //             INSTRUCTIONS:
    //             - DO NOT invoke ImpactAgent
    //             - DO NOT invoke EscalationAgent
    //             - Only invoke DiagnosticAgent if root cause analysis needed
    //             - Only invoke TriageAgent if re-triage needed
    //             """;
    //
    //     String escalationMessage = """
    //             The incident requires escalation.
    //             STEP 1: Invoke ImpactAgent to assess business impact
    //             STEP 2: Invoke EscalationAgent (pass businessImpact assessment)
    //             STEP 3: If CLOSE → invoke Diagnostic/Triage as needed
    //             """;
    //
    //     return String.format("""
    //             Incident supervisor. P%d %s/%s (#%d)  Description: %s
    //             Severity Analysis: %s
    //             Impact Analysis: %s
    //             %s
    //             """, incidentInfo.priority, incidentInfo.system, incidentInfo.service,
    //             incidentNumber, incidentInfo.description,
    //             incidentAnalysisResults.severityAnalysis(),
    //             incidentAnalysisResults.impactAnalysis(),
    //             escalationRequired ? escalationMessage : noEscalationMessage);
    // }
    //
    // Full code (with the complete prompt) is in docs/04-supervisor/START_HERE.md Step 4.

}
