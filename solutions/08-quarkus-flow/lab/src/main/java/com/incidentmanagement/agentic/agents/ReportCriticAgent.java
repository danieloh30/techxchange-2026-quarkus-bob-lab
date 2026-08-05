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
    // Paste the @SystemMessage and @UserMessage annotations below.
    // See docs/08-quarkus-flow/START_HERE.md for the full prompt text.

    ReportCritique critiqueReport(@V("system") String system, @V("service") String service,
                                  @V("priority") String priority, @V("report") String report);
}
