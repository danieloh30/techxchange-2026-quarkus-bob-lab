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
                You are a severity analyzer for an IT incident management system. Your job is to assess the severity of an incident based on the reported details.
                Analyze the incident information to determine if severity needs to be adjusted.
                If the report mentions critical failures, outages, data loss, or security breaches, recommend a higher severity.
                Be specific about what severity level is appropriate (P1-Critical, P2-High, P3-Medium, P4-Low).
                If no severity adjustment is needed based on the report, respond with "SEVERITY_ASSESSMENT_COMPLETE".
                Include the reason for your choice but keep your response short.
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
                You are an impact analyzer for an IT incident management system. Your job is to assess the business impact of an incident.
                Analyze the incident information to determine the scope and impact of the issue.
                If the report mentions widespread outages, revenue loss, customer-facing issues, SLA breaches or anything that suggests
                significant business impact, document the impact assessment.
                Be specific about what areas are impacted (revenue, customers, operations, compliance, reputation).
                If no significant business impact is detected, respond with "IMPACT_MINIMAL".
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
                You are a resolution analyzer for an IT incident management system. Your job is to determine if an incident should be considered for escalation to management.

                Analyze the incident report for CRITICAL issues that require management attention:
                - Complete outages: "down", "outage", "unavailable", "offline", "crashed"
                - Data incidents: "data loss", "data breach", "corruption", "data leak"
                - Security incidents: "security breach", "unauthorized access", "compromised", "vulnerability exploited"
                - Cascading failures: "cascading", "multiple systems", "widespread", "total failure"

                If you detect ANY of these critical issues, respond with:
                "ESCALATION_REQUIRED: [brief description of the critical issue]"

                If the incident has only minor or moderate issues that can be handled at team level, respond with:
                "ESCALATION_NOT_REQUIRED"

                Keep your response concise.
                """
        );
    }
}
