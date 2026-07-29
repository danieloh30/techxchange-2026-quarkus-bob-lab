package com.incidentmanagement.agentic.agents;

import com.incidentmanagement.model.ApprovalProposal;
import com.incidentmanagement.service.ApprovalService;
import dev.langchain4j.agentic.Agent;
import dev.langchain4j.agentic.declarative.HumanInTheLoop;
import io.quarkus.arc.Arc;
import io.quarkus.logging.Log;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public interface HumanApprovalAgent {

    @Agent(outputKey = "approvalDecision", description = "Coordinates human approval for high-impact incident escalations using the requestHumanApproval tool")
    @HumanInTheLoop(outputKey = "approvalDecision", description = "Coordinates human approval for high-impact incident escalations using the requestHumanApproval tool")
    static String reviewEscalationProposal(
            String incidentSystem,
            String incidentService,
            String incidentPriority,
            Integer incidentNumber,
            String revenueImpact,
            String escalationProposal,
            String escalationReason,
            String incidentDescription,
            String report
    ) {

        Log.infof("HITL Tool: Creating approval proposal for incident %d - %s / %s [%s]",
                incidentNumber, incidentSystem, incidentService, incidentPriority);
        Log.info("WORKFLOW PAUSED - Waiting for human approval decision via UI");

        ApprovalService approvalService = Arc.container().instance(ApprovalService.class).get();

        try {
            CompletableFuture<ApprovalProposal> approvalFuture =
                    approvalService.createProposalAndWaitForDecision(
                            incidentNumber, incidentSystem, incidentService, incidentPriority, revenueImpact,
                            escalationProposal, escalationReason, incidentDescription, report
                    );

            ApprovalProposal result = approvalFuture.get(5, TimeUnit.MINUTES);

            Log.infof("WORKFLOW RESUMED - Human decision received: %s", result.decision);

            return String.format("""
                Human Decision: %s
                Reason: %s
                Approved By: %s
                Decision Time: %s
                """,
                    result.decision,
                    result.approvalReason != null ? result.approvalReason : "No reason provided",
                    result.approvedBy != null ? result.approvedBy : "Unknown",
                    result.decidedAt != null ? result.decidedAt.toString() : "Unknown"
            );

        } catch (TimeoutException e) {
            Log.error("TIMEOUT: No human decision received within 5 minutes, defaulting to REJECTED");
            return """
                Human Decision: REJECTED
                Reason: Timeout - No human decision received within 5 minutes. Defaulting to rejection for safety.
                Approved By: System (Timeout)
                """;
        } catch (Exception e) {
            Log.errorf(e, "ERROR: Failed to get human approval for incident %d", incidentNumber);
            return String.format("""
                Human Decision: REJECTED
                Reason: Error occurred while waiting for human approval: %s
                Approved By: System (Error)
                """, e.getMessage());
        }
    }
}
