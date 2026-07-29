package com.incidentmanagement.model;

/**
 * Enum representing the type of possible incident actions for incident processing.
 */
public enum IncidentAssignment {
    ESCALATE,    // Incident needs to be escalated to higher authority
    INVESTIGATE, // Incident needs diagnostic investigation
    TRIAGE,      // Incident needs initial triage assessment
    RESOLVE      // Incident can be resolved/closed
}
