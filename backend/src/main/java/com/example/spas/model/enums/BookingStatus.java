package com.example.spas.model.enums;

public enum BookingStatus {
    PENDING,            // User has requested
    CONFIRMED,          // Client has confirmed
    CANCELLED_BY_USER,  // User has cancelled
    DECLINED_BY_CLIENT  // Client has declined
}