package com.carmanagement.agentic.agents;

import com.carmanagement.model.CarInfo;
import com.carmanagement.model.FeedbackTask;
import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * Exercise 4 — declare FeedbackAnalysisAgent.
 *
 * See docs/04-ibm-bob/START_HERE.md Step 1 for full instructions.
 *
 * This agent is parameterized: the same interface handles cleaning, maintenance,
 * AND disposition analysis. Which analysis it performs depends on the FeedbackTask
 * passed to it — specifically FeedbackTask.systemInstructions.
 *
 * The @ParallelMapperAgent in FeedbackAnalysisWorkflow runs it 3× concurrently,
 * one per task in [CLEANING, MAINTENANCE, DISPOSITION].
 *
 * Key pattern: @SystemMessage("{task.systemInstructions}")
 *   The LLM system prompt is injected dynamically from the FeedbackTask record at runtime.
 *   One interface declaration → three different LLM roles.
 *
 * Rules from AGENTS.md:
 *  - Interface only, no CDI scope
 *  - @Agent(outputKey="feedbackAnalysis") — this EXACT key is consumed by @ParallelMapperAgent
 */
public interface FeedbackAnalysisAgent {

    // TODO Exercise 4 — Step 1: add the three annotations and method declaration below.
    //
    // 1. @SystemMessage("{task.systemInstructions}")
    //    ⚠ Use this EXACT string — the dynamic value comes from FeedbackTask at runtime
    //
    // 2. @UserMessage(""" ... """)
    //    Placeholders: {carInfo.make}, {carInfo.model}, {carInfo.year},
    //                  {carInfo.condition}, {feedback}
    //    Label: "Previous Condition: {carInfo.condition}"
    //
    // 3. @Agent(description = "Feedback analyzer. ...",
    //           outputKey = "feedbackAnalysis")
    //    ⚠ outputKey MUST be "feedbackAnalysis" — FeedbackAnalysisWorkflow reads this exact key
    //
    // Method signature:
    //    String analyzeFeedback(FeedbackTask task, CarInfo carInfo,
    //                           Integer carNumber, String feedback);
    //
    // Full code is in docs/04-ibm-bob/START_HERE.md Step 1.

}
