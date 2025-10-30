package com.example.spas.model.enums;

public enum MembershipStatus {
    ACTIVE,
    PENDING,    // User has applied
    REJECTED,   // Admin has rejected
    INACTIVE    // Was active, but expired or cancelled
}