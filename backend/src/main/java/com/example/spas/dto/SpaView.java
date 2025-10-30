package com.example.spas.dto;

import com.example.spas.model.enums.ApprovalStatus;

public class SpaView {

    private Long id;
    private String name;
    private String address;
    private String description;
    private ApprovalStatus approvalStatus;
    private Long ownerId;

    // Constructors
    public SpaView() {
    }

    public SpaView(Long id, String name, String address, String description, ApprovalStatus approvalStatus, Long ownerId) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.description = description;
        this.approvalStatus = approvalStatus;
        this.ownerId = ownerId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
}