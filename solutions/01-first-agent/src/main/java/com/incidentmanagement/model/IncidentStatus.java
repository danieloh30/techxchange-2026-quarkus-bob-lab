package com.incidentmanagement.model;

public enum IncidentStatus {
    OPEN, TRIAGING, IN_PROGRESS, ESCALATED, RESOLVED;

    @Override
    public String toString() {
        return name().toLowerCase().replace('_', ' ');
    }
}
