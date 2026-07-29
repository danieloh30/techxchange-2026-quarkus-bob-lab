package com.incidentmanagement.agentic.agents;

import com.incidentmanagement.model.IncidentOutcome;
import com.incidentmanagement.model.IncidentInfo;
import com.incidentmanagement.model.IncidentAnalysisResults;
import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * Agent that analyzes incident processing results to determine the final outcome and assignment.
 * This is the final decision-maker that interprets all previous agent outputs.
 */
public interface ResolutionAgent {

    @SystemMessage("""
        Analyze incident processing results and output a JSON summary.

        Output format:
        {
          "resolution": "concise description (max 200 chars)",
          "incidentAction": "ESCALATE|INVESTIGATE|TRIAGE|RESOLVE",
          "escalationStatus": "ESCALATION_APPROVED|ESCALATION_REJECTED|ESCALATION_NOT_REQUIRED",
          "escalationReason": "reason or null"
        }

        Rules:
        - incidentAction: ESCALATE_INCIDENT->ESCALATE, KEEP_AT_TEAM+investigation->INVESTIGATE, KEEP_AT_TEAM+triage->TRIAGE, KEEP_AT_TEAM+none->RESOLVE
        - escalationStatus: APPROVED_BY_USER->ESCALATION_APPROVED, REJECTED_BY_USER->ESCALATION_REJECTED, else->ESCALATION_NOT_REQUIRED
        - resolution: Summarize the action and reason
        """)
    @UserMessage("""
            Incident: {incidentInfo.priority} - {incidentInfo.system} / {incidentInfo.service} (#{incidentNumber})

            Supervisor Decision: {supervisorDecision}

            Incident Analysis Results:
            - Resolution: {incidentAnalysisResults.resolutionAnalysis}
            - Impact: {incidentAnalysisResults.impactAnalysis}
            - Severity: {incidentAnalysisResults.severityAnalysis}
            """)
    @Agent(description = "Final incident resolution analyzer. Determines the incident's outcome, assignment, and approval status based on all analysis.",
            outputKey = "incidentOutcome")
    IncidentOutcome analyzeForOutcome(
            IncidentInfo incidentInfo,
            Integer incidentNumber,
            IncidentAnalysisResults incidentAnalysisResults,
            String supervisorDecision);
}
