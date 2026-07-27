package com.carmanagement.agentic.agents;

import io.quarkiverse.langchain4j.ToolBox;

import com.carmanagement.agentic.tools.CleaningTool;
import com.carmanagement.model.CarInfo;
import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * Exercise 2 — declare CleaningAgent.
 *
 * See docs/02-workflow-patterns/START_HERE.md Step 1 for full instructions.
 *
 * Rules from AGENTS.md:
 *  - This MUST be an interface (never a class)
 *  - Do NOT add @ApplicationScoped or any CDI scope annotation
 *  - @Agent(description="...", outputKey="analysisResult") — outputKey is required for workflow
 *  - @ToolBox(CleaningTool.class) — lets the LLM invoke CleaningTool.requestCleaning()
 *  - @SystemMessage — sets the LLM role and the "CLEANING_NOT_REQUIRED" skip rule
 *  - @UserMessage — per-call prompt with {placeholder} substitution
 */
public interface CleaningAgent {

    // TODO Exercise 2 — Step 1: add the four annotations and method declaration below.
    //
    // 1. @SystemMessage(""" ... """)
    //    Content: cleaning intake role
    //    → "It is your job to submit a request to the provided requestCleaning function..."
    //    → "If no cleaning is needed based on the feedback, respond with "CLEANING_NOT_REQUIRED"."
    //
    // 2. @UserMessage(""" ... """)
    //    Placeholders: {carInfo.make}, {carInfo.model}, {carInfo.year}, {carNumber}, {feedback}
    //
    // 3. @Agent(description = "Cleaning specialist. Determines what cleaning services are needed.",
    //           outputKey = "analysisResult")
    //    ⚠ outputKey = "analysisResult" is REQUIRED — without it the workflow silently drops the result
    //
    // 4. @ToolBox(CleaningTool.class)
    //    Exposes CleaningTool.requestCleaning() to the LLM
    //
    // Method signature:
    //    String processCleaning(CarInfo carInfo, Integer carNumber, String feedback);
    //
    // Full code is in docs/02-workflow-patterns/START_HERE.md Step 1.

}
