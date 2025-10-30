package com.example.spas.dto;

import com.example.spas.model.enums.ApprovalStatus;

public class ApprovalRequest {

    private ApprovalStatus status;

    // Constructors
    public ApprovalRequest() {
    }

    public ApprovalRequest(ApprovalStatus status) {
        this.status = status;
    }

    // Getters and Setters
    public ApprovalStatus getStatus() {
        return status;
    }

    public void setStatus(ApprovalStatus status) {
        this.status = status;
    }
}