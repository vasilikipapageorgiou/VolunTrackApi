package gr.voluntrack.model;

public enum ParticipationStatus {
    PENDING,    // volunteer registered, waiting org confirmation
    APPROVED,   // organization approved participation
    REJECTED,   // organization rejected
    CHECKED_IN  // volunteer checked in at event
}
