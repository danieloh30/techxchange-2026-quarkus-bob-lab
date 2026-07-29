package com.incidentmanagement.agentic.agents;

import com.incidentmanagement.model.IncidentOutcome;
import com.incidentmanagement.model.IncidentInfo;
import com.incidentmanagement.model.IncidentAnalysisResults;
import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * Exercise 4 — Step 3: declare ResolutionAgent.
 *
 * See docs/04-supervisor/START_HERE.md Step 3 for full instructions.
 *
 * This is the FINAL agent in IncidentProcessingWorkflow (@SequenceAgent).
 * It reads all upstream results and returns a typed IncidentOutcome record.
 *
 * Important: the return type is IncidentOutcome (a record), not String.
 * Quarkus LangChain4j deserializes the LLM's JSON output into IncidentOutcome automatically.
 * The @SystemMessage must instruct the LLM to output well-formed JSON.
 *
 * Rules from AGENTS.md:
 *  - Interface only, no CDI scope
 *  - @Agent(outputKey="incidentOutcome")
 *  - Return IncidentOutcome (NOT String)
 */
public interface ResolutionAgent {

    // TODO Exercise 4 — Step 3: add the three annotations and method declaration.
    //
    // 1. @SystemMessage(""" ... """)
    //    Content: JSON output format + routing rules
    //    Output format:
    //      { "resolution": "...", "incidentAction": "ESCALATE|INVESTIGATE|TRIAGE|RESOLVE" }
    //    Rules:
    //      - ESCALATE if supervisorDecision mentions ESCALATE_P1/ASSIGN_TEAM (NOT CLOSE)
    //      - INVESTIGATE if resolutionAnalysis ≠ "ESCALATION_NOT_REQUIRED"
    //      - TRIAGE if severityAnalysis ≠ "SEVERITY_LOW"
    //      - RESOLVE otherwise
    //      - If EscalationAgent decided CLOSE → do NOT assign ESCALATE
    //
    // 2. @UserMessage(""" ... """)
    //    Placeholders: {incidentInfo.priority}, {incidentInfo.system}, {incidentInfo.service},
    //                  {incidentNumber},
    //                  {supervisorDecision},
    //                  {incidentAnalysisResults.resolutionAnalysis},
    //                  {incidentAnalysisResults.impactAnalysis},
    //                  {incidentAnalysisResults.severityAnalysis}
    //
    // 3. @Agent(description = "Final incident resolution analyzer...",
    //           outputKey = "incidentOutcome")
    //
    // Method signature:
    //    IncidentOutcome analyzeForResolution(IncidentInfo incidentInfo, Integer incidentNumber,
    //                                         IncidentAnalysisResults incidentAnalysisResults,
    //                                         String supervisorDecision);
    //
    // Full code is in docs/04-supervisor/START_HERE.md Step 3.

}
