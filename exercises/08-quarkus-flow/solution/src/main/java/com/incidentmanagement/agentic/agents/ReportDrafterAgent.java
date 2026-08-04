package com.incidentmanagement.agentic.agents;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RegisterAiService
public interface ReportDrafterAgent {

    // TODO Exercise 08 — Step 1a
    // Paste the @SystemMessage and @UserMessage annotations below.
    // See docs/08-quarkus-flow/START_HERE.md for the full prompt text.

    String draftReport(@V("system") String system, @V("service") String service,
                       @V("priority") String priority, @V("description") String description,
                       @V("status") String status, @V("feedback") String feedback);
}
