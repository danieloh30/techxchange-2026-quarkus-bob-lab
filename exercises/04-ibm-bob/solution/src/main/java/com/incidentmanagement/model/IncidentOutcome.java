package com.incidentmanagement.model;

/**
 * Record representing the outcome of incident processing.
 *
 * @param resolution          A description of the incident resolution
 * @param incidentAction      Indicates the action required
 */
public record IncidentOutcome(String resolution, IncidentAssignment incidentAction) {
}
