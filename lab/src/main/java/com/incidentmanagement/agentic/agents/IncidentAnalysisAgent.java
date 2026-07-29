package com.incidentmanagement.agentic.agents;

import com.incidentmanagement.model.IncidentInfo;
import com.incidentmanagement.model.AnalysisTask;
import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * Exercise 3 — declare IncidentAnalysisAgent.
 *
 * See docs/03-parallel-workflow/START_HERE.md Step 1 for full instructions.
 *
 * This agent is parameterized: the same interface handles severity, impact,
 * AND resolution analysis. Which analysis it performs depends on the AnalysisTask
 * passed to it — specifically AnalysisTask.systemInstructions.
 *
 * The @ParallelMapperAgent in IncidentAnalysisWorkflow runs it 3× concurrently,
 * one per task in [SEVERITY, IMPACT, RESOLUTION].
 *
 * Key pattern: @SystemMessage("{task.systemInstructions}")
 *   The LLM system prompt is injected dynamically from the AnalysisTask record at runtime.
 *   One interface declaration → three different LLM roles.
 *
 * Rules from AGENTS.md:
 *  - Interface only, no CDI scope
 *  - @Agent(outputKey="incidentAnalysis") — this EXACT key is consumed by @ParallelMapperAgent
 */
public interface IncidentAnalysisAgent {

    // TODO Exercise 3 — Step 1: add the three annotations and method declaration below.
    //
    // 1. @SystemMessage("{task.systemInstructions}")
    //    ⚠ Use this EXACT string — the dynamic value comes from AnalysisTask at runtime
    //
    // 2. @UserMessage(""" ... """)
    //    Placeholders: {incidentInfo.system}, {incidentInfo.service}, {incidentInfo.priority},
    //                  {incidentInfo.description}, {report}
    //    Label: "Current Description: {incidentInfo.description}"
    //
    // 3. @Agent(description = "Incident analyzer. ...",
    //           outputKey = "incidentAnalysis")
    //    ⚠ outputKey MUST be "incidentAnalysis" — IncidentAnalysisWorkflow reads this exact key
    //
    // Method signature:
    //    String analyzeIncident(AnalysisTask task, IncidentInfo incidentInfo,
    //                           Integer incidentNumber, String report);
    //
    // Full code is in docs/03-parallel-workflow/START_HERE.md Step 1.

}
