package com.example.spas.dto;

import com.example.spas.model.enums.BookingStatus;

public class BookingStatusUpdateRequest {

    private BookingStatus status;

    // Constructors
    public BookingStatusUpdateRequest() {
    }

    public BookingStatusUpdateRequest(BookingStatus status) {
        this.status = status;
    }

    // Getters and Setters
    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }
}