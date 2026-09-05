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
import io.smallrye.mutiny.Uni;

import java.util.List;

import static dev.langchain4j.agentic.observability.HtmlReportGenerator.generateReport;

/**
 * Service for managing incident processing operations.
 * Uses async processing to handle Human-in-the-Loop workflow pauses.
 */
@ApplicationScoped
public class IncidentManagementService {

    @Inject
    IncidentProcessingWorkflow incidentProcessingWorkflow;

    /**
     * Process an incident report.
     * This method runs asynchronously to handle workflow pauses for human approval.
     *
     * @param incidentNumber The incident number
     * @param report The incident report details
     * @return Uni that completes with the result of the processing
     */
    public Uni<String> processIncident(Integer incidentNumber, String report) {

        return Uni.createFrom().item(() -> {
            IncidentInfo incidentInfo = findIncidentInfo(incidentNumber);
            if (incidentInfo == null) {
                return "Incident not found with number: " + incidentNumber;
            }

            // Create the list of analysis tasks for parallel analysis
            List<AnalysisTask> tasks = List.of(
                    AnalysisTask.severity(),
                    AnalysisTask.impact(),
                    AnalysisTask.resolution()
            );

            // Process the incident using the workflow with supervisor
            // This may PAUSE if human approval is needed
            IncidentOutcome incidentOutcome = incidentProcessingWorkflow.processIncident(
                    tasks,
                    incidentInfo,
                    incidentNumber,
                    report);

            Log.info("ResolutionAgent updating...");
            Log.infof("  └─ Incident #%d action: %s", incidentNumber, incidentOutcome.incidentAction());

            // Update the incident's description with the result from ResolutionAgent
            incidentInfo.description = incidentOutcome.resolution();

            incidentInfo.status = switch (incidentOutcome.incidentAction()) {
                case ESCALATE -> {
                    Log.info("Incident marked for escalation - awaiting final decision");
                    yield IncidentStatus.ESCALATED;
                }
                case INVESTIGATE -> IncidentStatus.IN_PROGRESS;
                case TRIAGE -> IncidentStatus.TRIAGING;
                case MONITOR -> incidentInfo.status;
                case RESOLVE -> IncidentStatus.RESOLVED;
            };

            // Persist the changes to the database in a separate transaction
            updateIncidentInfo(incidentInfo);

            return incidentOutcome.resolution();
        }).runSubscriptionOn(io.smallrye.mutiny.infrastructure.Infrastructure.getDefaultWorkerPool());
    }

    /**
     * Find incident info in a read-only transaction
     */
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    IncidentInfo findIncidentInfo(Integer incidentNumber) {
        return IncidentInfo.findById(incidentNumber);
    }

    /**
     * Update incident info in a separate transaction after workflow completes.
     * Uses merge to handle detached entity from the workflow.
     */
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    void updateIncidentInfo(IncidentInfo incidentInfo) {
        // Merge the detached entity back into the persistence context
        IncidentInfo.getEntityManager().merge(incidentInfo);
    }

    public String report() {
        return generateReport(incidentProcessingWorkflow.agentMonitor());
    }
}
