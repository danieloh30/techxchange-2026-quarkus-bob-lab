package com.incidentmanagement.model;

/**
 * Record representing the outcome of incident processing.
 *
 * @param resolution          A description of the incident resolution
 * @param incidentAction      Indicates the action required (ESCALATE, INVESTIGATE, TRIAGE, or RESOLVE)
 * @param escalationStatus    Status of escalation decision (ESCALATION_APPROVED, ESCALATION_REJECTED, or ESCALATION_NOT_REQUIRED)
 * @param escalationReason    Reason for escalation decision
 */
public record IncidentOutcome(
    String resolution,
    IncidentAssignment incidentAction,
    String escalationStatus,
    String escalationReason
) {
    /**
     * Constructor for backward compatibility without escalation fields.
     */
    public IncidentOutcome(String resolution, IncidentAssignment incidentAction) {
        this(resolution, incidentAction, "ESCALATION_NOT_REQUIRED", null);
    }
}
