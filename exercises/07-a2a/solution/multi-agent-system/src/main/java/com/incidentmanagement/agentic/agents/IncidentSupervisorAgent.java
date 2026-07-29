package com.incidentmanagement.agentic.agents;

import com.incidentmanagement.model.IncidentInfo;
import com.incidentmanagement.model.IncidentAnalysisResults;
import dev.langchain4j.agentic.declarative.SupervisorAgent;
import dev.langchain4j.agentic.declarative.SupervisorRequest;

public interface IncidentSupervisorAgent {

    @SupervisorAgent(
            outputKey = "supervisorDecision",
            subAgents = {
                    ImpactAgent.class,
                    EscalationAgent.class,
                    DiagnosticAgent.class,
                    TriageAgent.class
            })
    String superviseIncidentProcessing(IncidentInfo incidentInfo, Integer incidentNumber,
                                        IncidentAnalysisResults incidentAnalysisResults);

    @SupervisorRequest
    static String request(IncidentInfo incidentInfo, Integer incidentNumber,
                          IncidentAnalysisResults incidentAnalysisResults) {

        boolean escalationRequired = incidentAnalysisResults.resolutionAnalysis() != null &&
                incidentAnalysisResults.resolutionAnalysis().toUpperCase().contains("ESCALATION_REQUIRED");

        String noEscalationMessage = """
                No escalation has been requested.

                INSTRUCTIONS:
                - DO NOT invoke ImpactAgent
                - DO NOT invoke EscalationAgent
                - Only invoke DiagnosticAgent if root cause analysis needed
                - Only invoke TriageAgent if re-triage needed
                """;

        String escalationMessage = """
                The incident requires escalation.

                STEP 1: Invoke ImpactAgent to assess business impact
                STEP 2: Invoke EscalationAgent to decide escalation action (ESCALATE_P1/ASSIGN_TEAM/WORKAROUND/CLOSE)
                STEP 3: If EscalationAgent decides CLOSE:
                        - Invoke DiagnosticAgent if root cause analysis needed
                        - Invoke TriageAgent if re-triage needed

                IMPORTANT: When invoking EscalationAgent:
                - Pass businessImpact as a STRING with the full assessment
                - Use the EXACT format from ImpactAgent's response

                Follow the decision logic in your system message carefully.
                """;

        return String.format("""
                You are an incident supervisor for an IT incident management system. You coordinate action agents based on incident analysis.

                The incident has already been analyzed and you have these inputs:
                - severityAnalysis: Severity classification (or "SEVERITY_LOW")
                - impactAnalysis: Business impact assessment (or "IMPACT_MINIMAL")
                - resolutionAnalysis: Whether critical issues require escalation (or "ESCALATION_NOT_REQUIRED")

                Your job is to invoke the appropriate ACTION agents for this incident.

                Incident: P%d %s/%s (#%d)
                Current Description: %s

                Severity Analysis: %s
                Impact Analysis: %s

                In particular, you have to follow these steps:

                %s
                """,
                incidentInfo.priority, incidentInfo.system, incidentInfo.service,
                incidentNumber, incidentInfo.description,
                incidentAnalysisResults.severityAnalysis(),
                incidentAnalysisResults.impactAnalysis(),
                escalationRequired ? escalationMessage : noEscalationMessage);
    }
}
