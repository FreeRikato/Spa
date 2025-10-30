package com.example.spas.dto;

import java.time.LocalTime;
import java.util.List;

public class AvailabilityResponse {

    private List<LocalTime> availableSlots;

    // Constructors
    public AvailabilityResponse() {
    }

    public AvailabilityResponse(List<LocalTime> availableSlots) {
        this.availableSlots = availableSlots;
    }

    // Getters and Setters
    public List<LocalTime> getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(List<LocalTime> availableSlots) {
        this.availableSlots = availableSlots;
    }
}