package com.carmanagement.agentic.agents;

import io.quarkiverse.langchain4j.ToolBox;

import com.carmanagement.agentic.tools.CleaningTool;
import com.carmanagement.model.CarInfo;

// Imports you will need:
// import dev.langchain4j.agentic.Agent;
// import dev.langchain4j.service.SystemMessage;
// import dev.langchain4j.service.UserMessage;

/**
 * Exercise 2 — Step 1: declare CleaningAgent.
 *
 * Rules from AGENTS.md:
 *  - This MUST be an interface (never a class)
 *  - Do NOT add @ApplicationScoped or any CDI scope annotation
 *  - @Agent(description="...", outputKey="analysisResult") — outputKey required for workflow
 *  - @ToolBox(CleaningTool.class) — lets the LLM call CleaningTool.requestCleaning()
 *  - @SystemMessage — role + hard rules for the LLM
 *  - @UserMessage — per-call prompt with {placeholder} substitution
 */
public interface CleaningAgent {

    // TODO Exercise 2 — Step 1:
    //  Add @SystemMessage(""" ... """)
    //    Role: "You handle intake for the cleaning department of a car rental company."
    //    Hard rule: "Submit a request to the provided requestCleaning function."
    //    Skip rule: "If no cleaning is needed, respond with CLEANING_NOT_REQUIRED."
    //
    //  Add @UserMessage(""" ... """)
    //    Include: Make={carInfo.make}, Model={carInfo.model}, Year={carInfo.year},
    //             Car Number={carNumber}, Feedback={feedback}
    //
    //  Add @Agent(description="Cleaning specialist...", outputKey="analysisResult")
    //  Add @ToolBox(CleaningTool.class)
    //
    //  Declare: String processCleaning(CarInfo carInfo, Integer carNumber, String feedback);

}
