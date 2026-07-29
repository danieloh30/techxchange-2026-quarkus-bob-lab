package com.incidentmanagement.agentic.agents;

import com.incidentmanagement.model.IncidentInfo;
import com.incidentmanagement.model.IncidentAnalysisResults;
import dev.langchain4j.agentic.declarative.SupervisorAgent;
import dev.langchain4j.agentic.declarative.SupervisorRequest;

/**
 * Supervisor agent that orchestrates the entire incident processing workflow.
 * Coordinates action agents based on analysis results of the incident.
 */
public interface IncidentSupervisorAgent {

    /**
     * Main method to coordinate incident processing based on analysis results.
     * This is the entry point for the supervisor agent.
     */
    @SupervisorAgent(
            outputKey = "supervisorDecision",
            subAgents = {
                    ImpactAgent.class,
                    EscalationAgent.class,
                    DiagnosticAgent.class,
                    TriageAgent.class
            }
    )
    String superviseIncidentProcessing(
            IncidentInfo incidentInfo,
            Integer incidentNumber,
            IncidentAnalysisResults incidentAnalysisResults
    );

    /**
     * Generates the supervisor request prompt based on incident analysis results.
     * This method examines the resolution analysis to determine if the incident requires
     * escalation and constructs appropriate instructions for the supervisor agent
     * to coordinate the necessary action agents.
     *
     * @param incidentInfo The incident information
     * @param incidentNumber The incident number
     * @param incidentAnalysisResults The results from parallel incident analysis
     * @return A formatted prompt instructing the supervisor which agents to invoke
     */
    @SupervisorRequest
    static String request(
            IncidentInfo incidentInfo,
            Integer incidentNumber,
            IncidentAnalysisResults incidentAnalysisResults
    ) {
        boolean escalationRequired = incidentAnalysisResults.resolutionAnalysis() != null &&
                incidentAnalysisResults.resolutionAnalysis().toUpperCase().contains("ESCALATION_REQUIRED");

        String noEscalationMessage = """
               No escalation has been requested.

                INSTRUCTIONS:
                - DO NOT invoke ImpactAgent
                - DO NOT invoke EscalationAgent
                - Only invoke DiagnosticAgent if investigation needed
                - Only invoke TriageAgent if triage needed
               """;

        // Escalation required - complex path
        String escalationMessage = """
            The incident requires escalation assessment.

            STEP 1: Invoke ImpactAgent to assess business impact
            STEP 2: Invoke EscalationAgent to decide escalation action (ESCALATE_P1/ASSIGN_TEAM/WORKAROUND/CLOSE)
            STEP 3: If EscalationAgent decides CLOSE:
                    - Invoke DiagnosticAgent if investigation needed
                    - Invoke TriageAgent if triage needed

            IMPORTANT: When invoking EscalationAgent:
            - Pass revenueImpact as a STRING with dollar sign (e.g., "$500,000" not 500000)
            - Use the EXACT format from ImpactAgent's response

            Follow the decision logic in your system message carefully.
            """;

        return String.format("""
            You are an incident supervisor for an IT incident management system. You coordinate action agents based on incident analysis.

            The incident has already been analyzed and you have these inputs:
            - severityAnalysis: What severity assessment was made (or "SEVERITY_ASSESSMENT_COMPLETE")
            - impactAnalysis: What business impact was found (or "IMPACT_MINIMAL")
            - resolutionAnalysis: Whether critical issues require escalation (or "ESCALATION_NOT_REQUIRED")

            Your job is to invoke the appropriate ACTION agents for this incident

            Incident: %s - %s / %s (#%d)
            Current Description: %s

            Severity Analysis: %s
            Impact Analysis: %s

            In particular, you have to follow these steps

            %s
            """,
                incidentInfo.priority, incidentInfo.system, incidentInfo.service, incidentNumber, incidentInfo.description,
                incidentAnalysisResults.severityAnalysis(),
                incidentAnalysisResults.impactAnalysis(),
                escalationRequired ? escalationMessage : noEscalationMessage);
    }
}
