package com.incidentmanagement.agentic.tools;

import jakarta.enterprise.context.ApplicationScoped;
import dev.langchain4j.agent.tool.Tool;
import io.quarkus.logging.Log;

/**
 * Tool for requesting incident triage operations.
 */
@ApplicationScoped
public class TriageTool {

    /**
     * Requests initial triage with the specified options.
     *
     * @param incidentNumber The incident number
     * @param system The affected system
     * @param service The affected service
     * @param priority The incident priority
     * @param assignOnCall Whether to assign on-call engineer
     * @param notifyStakeholders Whether to notify stakeholders
     * @param createWarRoom Whether to create a war room
     * @param linkRelatedIncidents Whether to link related incidents
     * @param triageNotes The triage notes
     * @return A summary of the triage request
     */
    @Tool("Requests initial triage with the specified options")
    public String requestTriage(
            Integer incidentNumber,
            String system,
            String service,
            String priority,
            boolean assignOnCall,
            boolean notifyStakeholders,
            boolean createWarRoom,
            boolean linkRelatedIncidents,
            String triageNotes) {

        // In a real implementation, this would make an API call to a triage service
        // or update a database with the triage request

        Log.info("  └─ TriageAgent activated");
        String result = generateTriageSummary(incidentNumber, system, service, priority,
                                              assignOnCall, notifyStakeholders, createWarRoom,
                                              linkRelatedIncidents, triageNotes);
        Log.debug("TriageTool result: " + result);
        return result;
    }

    private String generateTriageSummary(
            Integer incidentNumber,
            String system,
            String service,
            String priority,
            boolean assignOnCall,
            boolean notifyStakeholders,
            boolean createWarRoom,
            boolean linkRelatedIncidents,
            String triageNotes) {

        StringBuilder summary = new StringBuilder();
        summary.append("Triage requested for ").append(system).append(" - ")
               .append(service).append(" [").append(priority).append("], Incident #")
               .append(incidentNumber).append(":\n");

        if (assignOnCall) {
            summary.append("- Assign on-call engineer\n");
        }

        if (notifyStakeholders) {
            summary.append("- Notify stakeholders\n");
        }

        if (createWarRoom) {
            summary.append("- Create war room\n");
        }

        if (linkRelatedIncidents) {
            summary.append("- Link related incidents\n");
        }

        if (triageNotes != null && !triageNotes.isEmpty()) {
            summary.append("Additional notes: ").append(triageNotes);
        }

        return summary.toString();
    }
}
