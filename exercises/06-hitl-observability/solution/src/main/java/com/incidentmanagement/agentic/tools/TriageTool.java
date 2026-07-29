package com.incidentmanagement.agentic.tools;

import jakarta.enterprise.context.ApplicationScoped;
import dev.langchain4j.agent.tool.Tool;
import io.quarkus.logging.Log;

/**
 * Tool for requesting triage operations on incidents.
 */
@ApplicationScoped
public class TriageTool {

    /**
     * Requests a triage action based on the provided parameters.
     *
     * @param incidentNumber The incident number
     * @param incidentSystem The affected system
     * @param incidentService The affected service
     * @param incidentPriority The incident priority
     * @param categorize Whether to categorize the incident
     * @param assessImpact Whether to assess impact
     * @param validatePriority Whether to validate priority
     * @param assignTeam Whether to assign a response team
     * @param requestText The triage request text
     * @return A summary of the triage request
     */
    @Tool("Requests a triage action with the specified options")
    public String requestTriage(
            Integer incidentNumber,
            String incidentSystem,
            String incidentService,
            String incidentPriority,
            boolean categorize,
            boolean assessImpact,
            boolean validatePriority,
            boolean assignTeam,
            String requestText) {

        // In a real implementation, this would make an API call to a ticketing system
        // or update a database with the triage request

        Log.info("  TriageAgent activated");
        String result = generateTriageSummary(incidentNumber, incidentSystem, incidentService, incidentPriority,
                                              categorize, assessImpact, validatePriority,
                                              assignTeam, requestText);
        Log.debug("TriageTool result: " + result);
        return result;
    }

    private String generateTriageSummary(
            Integer incidentNumber,
            String incidentSystem,
            String incidentService,
            String incidentPriority,
            boolean categorize,
            boolean assessImpact,
            boolean validatePriority,
            boolean assignTeam,
            String requestText) {

        StringBuilder summary = new StringBuilder();
        summary.append("Triage requested for ").append(incidentSystem).append(" / ")
               .append(incidentService).append(" [").append(incidentPriority).append("], Incident #")
               .append(incidentNumber).append(":\n");

        if (categorize) {
            summary.append("- Categorize incident\n");
        }

        if (assessImpact) {
            summary.append("- Assess impact\n");
        }

        if (validatePriority) {
            summary.append("- Validate priority\n");
        }

        if (assignTeam) {
            summary.append("- Assign response team\n");
        }

        if (requestText != null && !requestText.isEmpty()) {
            summary.append("Additional notes: ").append(requestText);
        }

        return summary.toString();
    }
}
