package com.incidentmanagement.agentic.tools;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import dev.langchain4j.agent.tool.Tool;
import io.quarkus.logging.Log;

import com.incidentmanagement.model.IncidentInfo;
import com.incidentmanagement.model.IncidentStatus;

/**
 * Exercise 1 — implement TriageTool.requestTriage().
 *
 * See docs/01-first-agent/START_HERE.md Step 2 for full instructions.
 *
 * Rules from AGENTS.md:
 *  - @ApplicationScoped is already set — NEVER change or remove it
 *  - @Tool("...") on the method — description shown to the LLM as a callable function
 *  - @Transactional on requestTriage() — required because incidentInfo.persist() is a JPA mutation
 *  - Return a String summary (the LLM reads this as the tool result)
 *  - Log incident number only — do NOT log full report text (PII risk — AGENTS.md rule 6)
 */
@ApplicationScoped
public class TriageTool {

    // TODO Exercise 1 — Step 2: add the requestTriage method below.
    //
    // Annotations:
    //   @Tool("Requests initial triage with the specified options")
    //   @Transactional
    //
    // Method signature:
    //   public String requestTriage(Integer incidentNumber, String system, String service,
    //                                Integer priority, boolean assignOnCall,
    //                                boolean notifyStakeholders, boolean createWarRoom,
    //                                boolean linkRelatedIncidents, String triageNotes)
    //
    // Body:
    //   1. IncidentInfo incidentInfo = IncidentInfo.findById(incidentNumber);
    //   2. if (incidentInfo != null) { incidentInfo.status = IncidentStatus.TRIAGING; incidentInfo.persist(); }
    //   3. Build and return a summary String (see guide for helper StringBuilder pattern)
    //   4. Log.info("  └─ TriageTool activated for incident #" + incidentNumber);
    //
    // Full code is in docs/01-first-agent/START_HERE.md Step 2.

}
