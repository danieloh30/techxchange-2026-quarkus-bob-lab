package com.incidentmanagement.agentic.agents;

import com.incidentmanagement.model.ReportCritique;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RegisterAiService
public interface ReportCriticAgent {

    // TODO Exercise 08 — Step 1b
    // Replace the placeholder @UserMessage below with the @SystemMessage and @UserMessage
    // annotations from docs/08-quarkus-flow/START_HERE.md.

    @UserMessage("Evaluate this post-incident report for {system}/{service} ({priority}): {report}")
    ReportCritique critiqueReport(@V("system") String system, @V("service") String service,
                                  @V("priority") String priority, @V("report") String report);
}
