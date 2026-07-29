package com.incidentmanagement.agentic.agents;

import com.incidentmanagement.model.IncidentOutcome;
import com.incidentmanagement.model.IncidentInfo;
import com.incidentmanagement.model.IncidentAnalysisResults;
import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * Agent that analyzes incident processing results to determine the final resolution and action.
 * This is the final decision-maker that interprets all previous agent outputs.
 */
public interface ResolutionAgent {

    @SystemMessage("""
        Analyze incident processing results and output a JSON summary.

        Output format:
        {
          "resolution": "concise description (max 200 chars)",
          "incidentAction": "ESCALATE|INVESTIGATE|TRIAGE|RESOLVE"
        }

        Rules:
        - incidentAction: Check the ACTUAL EscalationAgent decision in supervisorDecision, not just the analysis
        - If supervisorDecision mentions ESCALATE_P1/ASSIGN_TEAM/WORKAROUND (but NOT CLOSE) → ESCALATE
        - Else if impactAnalysis ≠ "DIAGNOSTIC_NOT_REQUIRED" → INVESTIGATE
        - Else if severityAnalysis ≠ "TRIAGE_NOT_REQUIRED" → TRIAGE
        - Else → RESOLVE
        - IMPORTANT: If EscalationAgent decided CLOSE, do NOT assign ESCALATE - check investigation/triage instead
        - resolution: Summarize the action and reason
        """)
    @UserMessage("""
            Incident: {incidentInfo.system} - {incidentInfo.service} [{incidentInfo.priority}] (#{incidentNumber})

            Supervisor Decision: {supervisorDecision}

            Incident Analysis Results:
            - Resolution: {incidentAnalysisResults.resolutionAnalysis}
            - Impact: {incidentAnalysisResults.impactAnalysis}
            - Severity: {incidentAnalysisResults.severityAnalysis}
            """)
    @Agent(description = "Final incident resolution analyzer. Determines the incident's resolution and action based on all analysis.",
            outputKey = "incidentOutcome")
    IncidentOutcome analyzeForResolution(
            IncidentInfo incidentInfo,
            Integer incidentNumber,
            IncidentAnalysisResults incidentAnalysisResults,
            String supervisorDecision);
}
