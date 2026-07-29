package com.incidentmanagement.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.incidentmanagement.agentic.workflow.IncidentProcessingWorkflow;
import com.incidentmanagement.model.IncidentOutcome;
import com.incidentmanagement.model.IncidentInfo;
import com.incidentmanagement.model.IncidentStatus;

/**
 * Service for managing IT incident processing.
 */
@ApplicationScoped
public class IncidentManagementService {
    @Inject
    IncidentProcessingWorkflow incidentProcessingWorkflow;

    /**
     * Process an incident report.
     *
     * @param incidentNumber The incident number
     * @param report Optional incident report
     * @return Result of the processing
     */
    @Transactional
    public String processIncident(Integer incidentNumber, String report) {
        IncidentInfo incidentInfo = IncidentInfo.findById(incidentNumber);
        if (incidentInfo == null) {
            return "Incident not found with number: " + incidentNumber;
        }

        // Process the incident using the workflow
        IncidentOutcome incidentOutcome = incidentProcessingWorkflow.processIncident(incidentInfo, incidentNumber, report);

        // Update the incident's description with the result from ResolutionAgent
        incidentInfo.description = incidentOutcome.resolution();

        // If triage was not required, mark the incident as resolved
        if (incidentOutcome.incidentAction() == com.incidentmanagement.model.IncidentAction.RESOLVE) {
            incidentInfo.status = IncidentStatus.RESOLVED;
        }

        incidentInfo.persist();

        return incidentOutcome.resolution();
    }
}
