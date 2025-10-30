package com.example.spas.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

public class AvailabilityRequest {

	@NotNull(message = "Date cannot be null")
    @FutureOrPresent(message = "Cannot check availability for a past date")
    private LocalDate date;
    // Constructors
    public AvailabilityRequest() {
    }

    public AvailabilityRequest(LocalDate date) {
        this.date = date;
    }

    // Getters and Setters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}