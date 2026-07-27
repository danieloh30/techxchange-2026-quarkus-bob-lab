package com.carmanagement.agentic.agents;

import com.carmanagement.model.CarInfo;
import com.carmanagement.model.FeedbackTask;

// Imports you will need:
// import dev.langchain4j.agentic.Agent;
// import dev.langchain4j.service.SystemMessage;
// import dev.langchain4j.service.UserMessage;

/**
 * Exercise 4 — Step 1: declare FeedbackAnalysisAgent.
 *
 * This agent is parameterized: the same interface handles cleaning, maintenance,
 * AND disposition analysis depending on the FeedbackTask passed to it.
 * The @ParallelMapperAgent in FeedbackAnalysisWorkflow runs it 3× concurrently.
 *
 * Key pattern: @SystemMessage("{task.systemInstructions}") — the LLM system prompt
 * is injected dynamically from the FeedbackTask record at runtime.
 *
 * Rules from AGENTS.md:
 *  - Interface only, no CDI scope
 *  - @Agent(outputKey="feedbackAnalysis") — this key is used by @ParallelMapperAgent
 */
public interface FeedbackAnalysisAgent {

    // TODO Exercise 4 — Step 1:
    //  Add @SystemMessage("{task.systemInstructions}")
    //    — note the dynamic placeholder: the system instructions come from the FeedbackTask
    //
    //  Add @UserMessage(""" ... """)
    //    Include: Make={carInfo.make}, Model={carInfo.model}, Year={carInfo.year},
    //             Previous Condition={carInfo.condition}, Feedback={feedback}
    //
    //  Add @Agent(description="Feedback analyzer...", outputKey="feedbackAnalysis")
    //    ⚠ outputKey MUST be "feedbackAnalysis" — FeedbackAnalysisWorkflow expects this key
    //
    //  Declare:
    //    String analyzeFeedback(FeedbackTask task, CarInfo carInfo,
    //                           Integer carNumber, String feedback);

}
