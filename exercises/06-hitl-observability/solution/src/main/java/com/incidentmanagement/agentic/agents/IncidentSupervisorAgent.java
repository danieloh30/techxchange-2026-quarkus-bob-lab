package com.incidentmanagement.agentic.agents;

import com.incidentmanagement.model.IncidentInfo;
import com.incidentmanagement.model.IncidentAnalysisResults;
import dev.langchain4j.agentic.declarative.SupervisorAgent;
import dev.langchain4j.agentic.declarative.SupervisorRequest;

/**
 * Supervisor agent that orchestrates the entire incident processing workflow.
 * Coordinates analysis agents and action agents based on incident severity and impact.
 * Implements human-in-the-loop pattern for P1/P2 incidents on revenue-critical systems.
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
        String feedback,
        IncidentAnalysisResults incidentAnalysisResults
    );

    @SupervisorRequest()
    static String request(
        IncidentInfo incidentInfo,
        Integer incidentNumber,
        String feedback,
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
           2. IF P1/P2 on revenue-critical system (HIGH-PRIORITY):
              - Invoke EscalationProposalAgent -> HumanApprovalAgent (workflow pauses)
              - APPROVED: Use AI recommendation -> KEEP_AT_TEAM->"KEEP_AT_TEAM", ESCALATE->"ESCALATE_INCIDENT"
              - REJECTED: Opposite of AI -> KEEP_AT_TEAM->"ESCALATE_INCIDENT", ESCALATE->"KEEP_AT_TEAM"
           3. IF P3/P4 or non-critical system (LOWER-PRIORITY):
              - Invoke EscalationAgent directly
              - KEEP_AT_TEAM_LEVEL/RESOLVE->"KEEP_AT_TEAM", ESCALATE_TO_VP/ESCALATE_TO_CTO->"ESCALATE_INCIDENT"
           4. IF "KEEP_AT_TEAM": Invoke DiagnosticAgent/TriageAgent as needed

           CRITICAL: End with KEEP_AT_TEAM or ESCALATE_INCIDENT
           """;

        return """
            You are an incident supervisor for an IT incident management system. You coordinate action agents based on incident analysis.

            The incident has already been analyzed and you have these inputs:
            - severityAnalysis: What severity assessment was made (or "SEVERITY_ASSESSMENT_COMPLETE")
            - impactAnalysis: What business impact was found (or "IMPACT_MINIMAL")
            - resolutionAnalysis: Whether critical issues require escalation (or "ESCALATION_NOT_REQUIRED")

            Your job is to invoke the appropriate ACTION agents for this incident

            Incident: """ + incidentInfo.priority + " - " + incidentInfo.system + " / " + incidentInfo.service + " (#" + incidentNumber + ")" + """

            Current Description: """ + incidentInfo.description + """

            Report: """ + feedback + """

            Severity Analysis: """ + incidentAnalysisResults.severityAnalysis() + """

            Impact Analysis: """ + incidentAnalysisResults.impactAnalysis() + """

            Resolution Analysis: """ + (escalationRequired ? escalationMessage : noEscalationMessage);
    }
}
