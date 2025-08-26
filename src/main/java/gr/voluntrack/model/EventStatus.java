package gr.voluntrack.model;

public enum EventStatus {
    PENDING,   // created by organization, waiting admin approval
    APPROVED,  // visible to volunteers
    REJECTED,  // rejected by admin
    CANCELLED
}
