package com.incidentmanagement.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.incidentmanagement.agentic.workflow.IncidentProcessingWorkflow;
import com.incidentmanagement.model.IncidentOutcome;
import com.incidentmanagement.model.IncidentInfo;
import com.incidentmanagement.model.IncidentStatus;
import com.incidentmanagement.model.AnalysisTask;
import io.quarkus.logging.Log;

import java.util.List;

@ApplicationScoped
public class IncidentManagementService {

    @Inject
    IncidentProcessingWorkflow incidentProcessingWorkflow;

    @Transactional
    public String processIncident(Integer incidentNumber, String report) {
        IncidentInfo incidentInfo = IncidentInfo.findById(incidentNumber);
        if (incidentInfo == null) {
            return "Incident not found with number: " + incidentNumber;
        }

        List<AnalysisTask> tasks = List.of(
                AnalysisTask.severity(),
                AnalysisTask.impact(),
                AnalysisTask.resolution()
        );

        IncidentOutcome incidentOutcome = incidentProcessingWorkflow.processIncident(
                tasks,
                incidentInfo,
                incidentNumber,
                report);

        Log.info("ResolutionAgent updating...");

        incidentInfo.description = incidentOutcome.resolution();

        switch (incidentOutcome.incidentAction()) {
            case ESCALATE:
                incidentInfo.status = IncidentStatus.ESCALATED;
                Log.info("Incident marked for escalation - awaiting management decision");
                break;
            case INVESTIGATE:
                incidentInfo.status = IncidentStatus.IN_PROGRESS;
                break;
            case TRIAGE:
                incidentInfo.status = IncidentStatus.TRIAGING;
                break;
            case MONITOR:
                break;
            case RESOLVE:
                incidentInfo.status = IncidentStatus.RESOLVED;
                break;
        }

        incidentInfo.persist();

        return incidentOutcome.resolution();
    }
}
