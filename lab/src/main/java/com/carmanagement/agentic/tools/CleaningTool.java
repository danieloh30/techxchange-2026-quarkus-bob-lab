package com.carmanagement.agentic.tools;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import dev.langchain4j.agent.tool.Tool;
import io.quarkus.logging.Log;

import com.carmanagement.model.CarInfo;
import com.carmanagement.model.CarStatus;

/**
 * Exercise 2 — implement CleaningTool.requestCleaning().
 *
 * See docs/02-workflow-patterns/START_HERE.md Step 2 for full instructions.
 *
 * Rules from AGENTS.md:
 *  - @ApplicationScoped is already set — NEVER change or remove it
 *  - @Tool("...") on the method — description shown to the LLM as a callable function
 *  - @Transactional on requestCleaning() — required because carInfo.persist() is a JPA mutation
 *  - Return a String summary (the LLM reads this as the tool result)
 *  - Log car number only — do NOT log full feedback text (PII risk — AGENTS.md rule 6)
 */
@ApplicationScoped
public class CleaningTool {

    // TODO Exercise 2 — Step 2: add the requestCleaning method below.
    //
    // Annotations:
    //   @Tool("Requests a cleaning with the specified options")
    //   @Transactional
    //
    // Method signature:
    //   public String requestCleaning(Integer carNumber, String carMake, String carModel,
    //                                  Integer carYear, boolean exteriorWash,
    //                                  boolean interiorCleaning, boolean detailing,
    //                                  boolean waxing, String requestText)
    //
    // Body:
    //   1. CarInfo carInfo = CarInfo.findById(carNumber);
    //   2. if (carInfo != null) { carInfo.status = CarStatus.AT_CLEANING; carInfo.persist(); }
    //   3. Build and return a summary String (see guide for helper StringBuilder pattern)
    //   4. Log.info("  └─ CleaningTool activated for car #" + carNumber);
    //
    // Full code is in docs/02-workflow-patterns/START_HERE.md Step 2.

}
