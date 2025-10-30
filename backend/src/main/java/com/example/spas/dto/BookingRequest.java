package com.example.spas.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

public class BookingRequest {

	@NotNull(message = "Service ID cannot be null")
    private Long serviceId;

    @NotNull(message = "Booking time cannot be null")
    @Future(message = "Booking time must be in the future")
    private LocalDateTime bookingTime;

    // Constructors
    public BookingRequest() {
    }

    public BookingRequest(Long serviceId, LocalDateTime bookingTime) {
        this.serviceId = serviceId;
        this.bookingTime = bookingTime;
    }

    // Getters and Setters
    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(LocalDateTime bookingTime) {
        this.bookingTime = bookingTime;
    }
}