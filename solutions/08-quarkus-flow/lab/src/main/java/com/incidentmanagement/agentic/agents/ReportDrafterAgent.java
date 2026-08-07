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
    // Replace the placeholder @UserMessage below with the @SystemMessage and @UserMessage
    // annotations from docs/08-quarkus-flow/START_HERE.md.

    @UserMessage("Draft a post-incident report for {system}/{service} ({priority}): {description} — status: {status}. Feedback: {feedback}")
    String draftReport(@V("system") String system, @V("service") String service,
                       @V("priority") String priority, @V("description") String description,
                       @V("status") String status, @V("feedback") String feedback);
}
