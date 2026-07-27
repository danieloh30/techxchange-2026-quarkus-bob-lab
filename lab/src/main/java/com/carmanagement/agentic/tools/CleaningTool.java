package com.carmanagement.agentic.tools;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import dev.langchain4j.agent.tool.Tool;
import io.quarkus.logging.Log;

import com.carmanagement.model.CarInfo;
import com.carmanagement.model.CarStatus;

/**
 * Tool for requesting cleaning operations.
 *
 * Exercise 2 — Step 2: implement requestCleaning().
 * Rules from AGENTS.md:
 *  - @ApplicationScoped (already set — never change this)
 *  - @Transactional on any method that calls carInfo.persist()
 *  - Return a String summary (the LLM reads this as tool result)
 *  - Do NOT log full feedback text (PII risk — rule 6 in AGENTS.md)
 */
@ApplicationScoped
public class CleaningTool {

    // TODO Exercise 2 — Step 2:
    //  Add @Tool("Requests a cleaning with the specified options")
    //  Add @Transactional
    //  Implement requestCleaning(...) with these parameters:
    //    Integer carNumber, String carMake, String carModel, Integer carYear,
    //    boolean exteriorWash, boolean interiorCleaning, boolean detailing,
    //    boolean waxing, String requestText
    //  Body should:
    //    1. CarInfo carInfo = CarInfo.findById(carNumber);
    //    2. if (carInfo != null) { carInfo.status = CarStatus.AT_CLEANING; carInfo.persist(); }
    //    3. Build and return a summary string
    //    4. Log.info("  └─ CleaningTool activated for car #" + carNumber);

}
