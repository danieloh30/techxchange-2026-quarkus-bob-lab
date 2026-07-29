package com.incidentmanagement.agentic.agents;

import io.quarkiverse.langchain4j.ToolBox;

import com.incidentmanagement.agentic.tools.TriageTool;
import com.incidentmanagement.model.IncidentInfo;
import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * Exercise 1 — declare TriageAgent.
 *
 * See docs/01-first-agent/START_HERE.md Step 1 for full instructions.
 *
 * Rules from AGENTS.md:
 *  - This MUST be an interface (never a class)
 *  - Do NOT add @ApplicationScoped or any CDI scope annotation
 *  - @Agent(description="...", outputKey="analysisResult") — outputKey is required for workflow
 *  - @ToolBox(TriageTool.class) — lets the LLM invoke TriageTool.requestTriage()
 *  - @SystemMessage — sets the LLM role and the "TRIAGE_NOT_REQUIRED" skip rule
 *  - @UserMessage — per-call prompt with {placeholder} substitution
 */
public interface TriageAgent {

    // TODO Exercise 1 — Step 1: add the four annotations and method declaration below.
    //
    // 1. @SystemMessage(""" ... """)
    //    Content: triage intake role
    //    → "It is your job to submit a request to the provided requestTriage function..."
    //    → "If no triage action is needed based on the report, respond with "TRIAGE_NOT_REQUIRED"."
    //
    // 2. @UserMessage(""" ... """)
    //    Placeholders: {incidentInfo.system}, {incidentInfo.service}, {incidentInfo.priority},
    //                  {incidentNumber}, {report}
    //
    // 3. @Agent(description = "Triage specialist. Determines initial triage and team assignment.",
    //           outputKey = "analysisResult")
    //    ⚠ outputKey = "analysisResult" is REQUIRED — without it the workflow silently drops the result
    //
    // 4. @ToolBox(TriageTool.class)
    //    Exposes TriageTool.requestTriage() to the LLM
    //
    // Method signature:
    //    String processTriage(IncidentInfo incidentInfo, Integer incidentNumber, String report);
    //
    // Full code is in docs/01-first-agent/START_HERE.md Step 1.

}
