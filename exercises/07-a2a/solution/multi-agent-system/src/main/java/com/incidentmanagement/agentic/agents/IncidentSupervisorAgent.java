package com.incidentmanagement.agentic.agents;

import com.incidentmanagement.model.IncidentInfo;
import com.incidentmanagement.model.IncidentAnalysisResults;
import dev.langchain4j.agentic.declarative.SupervisorAgent;
import dev.langchain4j.agentic.declarative.SupervisorRequest;

/**
 * Supervisor agent that orchestrates the entire incident processing workflow.
 * Coordinates analysis agents and action agents based on incident severity.
 * Implements human-in-the-loop pattern for high-impact incident escalations.
 */
public interface IncidentSupervisorAgent {

    @SupervisorAgent(
        outputKey = "supervisorDecision",
        subAgents = {
            ImpactAgent.class,
            EscalationProposalAgent.class,
            HumanApprovalAgent.class,
            EscalationAgent.class,
            DiagnosticAgent.class,
            TriageAgent.class
        }
    )
    String superviseIncidentProcessing(
        IncidentInfo incidentInfo,
        Integer incidentNumber,
        String report,
        IncidentAnalysisResults incidentAnalysisResults
    );

    @SupervisorRequest()
    static String request(
        IncidentInfo incidentInfo,
        Integer incidentNumber,
        String report,
        IncidentAnalysisResults incidentAnalysisResults
    ) {
        boolean escalationRequired = incidentAnalysisResults.resolutionAnalysis() != null &&
                                     incidentAnalysisResults.resolutionAnalysis().toUpperCase().contains("ESCALATION_REQUIRED");

        String noEscalationMessage = """
            Escalation is not required.
            Proceed with normal investigation and triage workflow.
            If investigation or triage is required, invoke the appropriate agents.
                """;

        String escalationMessage = """
           ESCALATION_REQUIRED

           Follow these steps:

           1. Get revenue impact from ImpactAgent (keep $ format)
           2. IF impact > $15,000 (HIGH-IMPACT):
              - Invoke EscalationProposalAgent -> HumanApprovalAgent (workflow pauses)
              - APPROVED: Use AI recommendation -> CLOSE->"RESOLVE_INCIDENT", ESCALATE->"ESCALATE_INCIDENT"
              - REJECTED: Opposite of AI -> CLOSE->"ESCALATE_INCIDENT", ESCALATE->"RESOLVE_INCIDENT"
           3. IF impact <= $15,000 (LOW-IMPACT):
              - Invoke EscalationAgent directly
              - CLOSE->"RESOLVE_INCIDENT", ESCALATE_P1/ASSIGN_TEAM->"ESCALATE_INCIDENT"
           4. IF "RESOLVE_INCIDENT": Invoke DiagnosticAgent/TriageAgent as needed

           CRITICAL: End with RESOLVE_INCIDENT or ESCALATE_INCIDENT
           """;

        return """
            You are an incident supervisor for an IT incident management system. You coordinate action agents based on incident analysis.

            The incident has already been analyzed and you have these inputs:
            - severityAnalysis: What severity assessment was made (or "SEVERITY_LOW")
            - impactAnalysis: What business impact was found (or "IMPACT_MINIMAL")
            - resolutionAnalysis: Whether critical issues require escalation (or "ESCALATION_NOT_REQUIRED")

            Your job is to invoke the appropriate ACTION agents for this incident

            Incident: """ + incidentInfo.priority + " - " + incidentInfo.system + " / " + incidentInfo.service + " (#" + incidentNumber + ")" + """

            Current Description: """ + incidentInfo.description + """

            Report: """ + report + """

            Severity Analysis: """ + incidentAnalysisResults.severityAnalysis() + """

            Impact Analysis: """ + incidentAnalysisResults.impactAnalysis() + """

            Resolution Analysis: """ + (escalationRequired ? escalationMessage : noEscalationMessage);
    }
}
