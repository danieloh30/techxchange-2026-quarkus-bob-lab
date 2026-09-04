package com.incidentmanagement.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.incidentmanagement.agentic.workflow.TriageWorkflow;
import com.incidentmanagement.model.IncidentInfo;
import com.incidentmanagement.model.IncidentStatus;

/**
 * Service for managing IT incident processing.
 */
@ApplicationScoped
public class IncidentManagementService {

    // --8<-- [start:processIncident]
    @Inject
    TriageWorkflow triageWorkflow;

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

        // Process the incident through the triage workflow
        String result = triageWorkflow.processTriage(incidentInfo, incidentNumber, report);

        if (result.toUpperCase().contains("TRIAGE_NOT_REQUIRED")) {
            incidentInfo.status = IncidentStatus.RESOLVED;
            incidentInfo.persist();
        }

        return result;
    }
    // --8<-- [end:processIncident]
}
