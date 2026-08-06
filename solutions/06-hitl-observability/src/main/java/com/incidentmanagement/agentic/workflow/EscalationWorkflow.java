package com.incidentmanagement.agentic.workflow;

import com.incidentmanagement.agentic.agents.EscalationProposalAgent;
import com.incidentmanagement.agentic.agents.HumanApprovalAgent;
import com.incidentmanagement.model.IncidentInfo;
import dev.langchain4j.agentic.declarative.ActivationCondition;
import dev.langchain4j.agentic.declarative.ConditionalAgent;

public interface EscalationWorkflow {

    @ConditionalAgent(outputKey = "approvalDecision",
            subAgents = { EscalationProposalAgent.class, HumanApprovalAgent.class })
    String evaluateEscalation(
            IncidentInfo incidentInfo,
            Integer incidentNumber,
            String supervisorDecision,
            String report);

    @ActivationCondition(EscalationProposalAgent.class)
    static boolean shouldPropose(String supervisorDecision) {
        return supervisorDecision != null
                && supervisorDecision.toUpperCase().contains("IMPACT");
    }

    @ActivationCondition(HumanApprovalAgent.class)
    static boolean shouldAwaitApproval(String supervisorDecision) {
        return supervisorDecision != null
                && supervisorDecision.toUpperCase().contains("IMPACT");
    }
}
