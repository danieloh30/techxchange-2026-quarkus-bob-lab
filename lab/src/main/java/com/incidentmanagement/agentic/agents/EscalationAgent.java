package com.incidentmanagement.agentic.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * Exercise 4 — Step 2: declare EscalationAgent.
 *
 * See docs/04-supervisor/START_HERE.md Step 2 for full instructions.
 *
 * Called by IncidentSupervisorAgent AFTER ImpactAgent.
 * Decides: ESCALATE_P1 / ASSIGN_TEAM / WORKAROUND / CLOSE based on impact + severity.
 *
 * Rules from AGENTS.md:
 *  - Interface only, no CDI scope
 *  - @Agent(outputKey="escalationAction")
 *  - {businessImpact} placeholder in @UserMessage receives ImpactAgent's output from AgenticScope
 */
public interface EscalationAgent {

    // TODO Exercise 4 — Step 2: add the three annotations and method declaration.
    //
    // 1. @SystemMessage(""" ... """)
    //    Content:
    //    → Escalation specialist role
    //    → Options: ESCALATE_P1 (critical, needs VP/exec attention),
    //               ASSIGN_TEAM (route to specific engineering team),
    //               WORKAROUND (apply temporary mitigation),
    //               CLOSE (resolved or no action needed)
    //    → Decision Criteria: revenue loss > $10k/hr → ESCALATE_P1 or ASSIGN_TEAM
    //
    // 2. @UserMessage(""" ... """)
    //    Placeholders: {system}, {service}, {priority}, {incidentNumber},
    //                  {incidentDescription}, {businessImpact}, {report}
    //    Note: {businessImpact} is passed by the supervisor from ImpactAgent's AgenticScope output
    //
    // 3. @Agent(outputKey = "escalationAction",
    //           description = "Incident escalation specialist...")
    //
    // Method signature:
    //    String processEscalation(String system, String service, Integer priority,
    //                              Integer incidentNumber, String incidentDescription,
    //                              String businessImpact, String report);
    //
    // Full code is in docs/04-supervisor/START_HERE.md Step 2.

}
