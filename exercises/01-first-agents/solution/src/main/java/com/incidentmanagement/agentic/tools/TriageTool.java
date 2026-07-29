package com.incidentmanagement.agentic.tools;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import com.incidentmanagement.model.IncidentInfo;
import com.incidentmanagement.model.IncidentStatus;
import dev.langchain4j.agent.tool.Tool;

// --8<-- [start:TriageTool]
/**
 * Tool for requesting triage operations on IT incidents.
 */
@ApplicationScoped
public class TriageTool {

    /**
     * Requests initial triage for an incident based on the provided parameters.
     *
     * @param incidentNumber The incident number
     * @param system The affected system
     * @param service The affected service
     * @param priority The incident priority level
     * @param assignOnCall Whether to assign on-call engineer
     * @param notifyStakeholders Whether to notify stakeholders
     * @param createWarRoom Whether to create a war room
     * @param linkRelatedIncidents Whether to link related incidents
     * @param triageNotes The triage notes
     * @return A summary of the triage request
     */
    @Tool("Requests initial triage with the specified options")
    @Transactional
    public String requestTriage(
            Integer incidentNumber,
            String system,
            String service,
            Integer priority,
            boolean assignOnCall,
            boolean notifyStakeholders,
            boolean createWarRoom,
            boolean linkRelatedIncidents,
            String triageNotes) {

        // Update incident status to TRIAGING
        IncidentInfo incidentInfo = IncidentInfo.findById(incidentNumber);
        if (incidentInfo != null) {
            incidentInfo.status = IncidentStatus.TRIAGING;
            incidentInfo.persist();
        }

        var result = generateTriageSummary(incidentNumber, system, service, priority,
                                            assignOnCall, notifyStakeholders, createWarRoom,
                                            linkRelatedIncidents, triageNotes);
        System.out.println("  TriageTool result: " + result);
        return result;
    }
// --8<-- [end:TriageTool]

    private String generateTriageSummary(
            Integer incidentNumber,
            String system,
            String service,
            Integer priority,
            boolean assignOnCall,
            boolean notifyStakeholders,
            boolean createWarRoom,
            boolean linkRelatedIncidents,
            String triageNotes) {

        var summary = new StringBuilder();
        summary.append("Triage requested for ").append(system).append("/")
               .append(service).append(" (P").append(priority).append("), Incident #")
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
            summary.append("Triage notes: ").append(triageNotes);
        }

        return summary.toString();
    }
}
