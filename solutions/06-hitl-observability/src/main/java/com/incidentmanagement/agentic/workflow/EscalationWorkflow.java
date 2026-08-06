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
            String businessImpact,
            String report);

    @ActivationCondition(EscalationProposalAgent.class)
    static boolean shouldPropose(String businessImpact) {
        return businessImpact != null && !businessImpact.isEmpty();
    }

    @ActivationCondition(HumanApprovalAgent.class)
    static boolean shouldAwaitApproval(String businessImpact) {
        return businessImpact != null && !businessImpact.isEmpty();
    }
}
