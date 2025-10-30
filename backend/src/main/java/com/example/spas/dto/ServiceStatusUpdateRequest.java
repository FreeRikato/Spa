package com.example.spas.dto;

import com.example.spas.model.enums.ServiceStatus;

public class ServiceStatusUpdateRequest {

    private ServiceStatus status;

    // Constructors
    public ServiceStatusUpdateRequest() {
    }

    public ServiceStatusUpdateRequest(ServiceStatus status) {
        this.status = status;
    }

    // Getters and Setters
    public ServiceStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceStatus status) {
        this.status = status;
    }
}