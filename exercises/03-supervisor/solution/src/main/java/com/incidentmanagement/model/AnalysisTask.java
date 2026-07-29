package com.incidentmanagement.model;

/**
 * Record representing an incident analysis task with its configuration.
 * Contains the type of analysis and system instructions for the analysis.
 */
public record AnalysisTask(
        AnalysisType analysisType,
        String systemInstructions) {

    /**
     * Factory method for creating a severity analysis task.
     */
    public static AnalysisTask severity() {
        return new AnalysisTask(
                AnalysisType.SEVERITY,
                """
                You are a severity analyzer for an IT incident management system. Your job is to assess the severity level of an incident based on the report.
                Analyze the report and incident information to determine the appropriate severity classification.
                If the report mentions critical outages, data loss, security breaches, or service-wide impact, classify as high severity.
                Be specific about the severity level (P1-Critical, P2-High, P3-Medium, P4-Low).
                If no triage or severity assessment is needed based on the report, respond with "TRIAGE_NOT_REQUIRED".
                Include the reason for your assessment but keep your response short.
                """
        );
    }

    /**
     * Factory method for creating an impact analysis task.
     */
    public static AnalysisTask impact() {
        return new AnalysisTask(
                AnalysisType.IMPACT,
                """
                You are a business impact analyzer for an IT incident management system. Your job is to determine the business impact of an incident based on the report.
                Analyze the report and incident information to assess business impact.
                Impact assessment never includes triage or initial classification.
                If the report mentions revenue loss, customer-facing issues, SLA violations, performance degradation, or anything that suggests
                business impact, recommend appropriate investigation.
                Be specific about what type of investigation is needed (log analysis, service restart, config rollback, dependency check, performance profiling, network trace).
                If no investigation or diagnostic work is needed, respond with "DIAGNOSTIC_NOT_REQUIRED".
                Include the reason for your choice but keep your response short.
                """
        );
    }

    /**
     * Factory method for creating a resolution analysis task.
     */
    public static AnalysisTask resolution() {
        return new AnalysisTask(
                AnalysisType.RESOLUTION,
                """
                You are an escalation analyzer for an IT incident management system. Your job is to determine if an incident should be escalated.

                Analyze the report for SEVERE issues that would require escalation:
                - Service outages: "down", "offline", "unavailable", "outage"
                - Data issues: "data loss", "corruption", "inconsistency"
                - Security: "breach", "unauthorized", "vulnerability", "exploit"
                - Cascading failures: "cascading", "multiple systems", "widespread"

                If you detect ANY of these severe issues, respond with:
                "ESCALATION_REQUIRED: [brief description of the severe issue]"

                If the incident has only minor or moderate issues that can be handled normally, respond with:
                "ESCALATION_NOT_REQUIRED"

                Keep your response concise.
                """
        );
    }
}
