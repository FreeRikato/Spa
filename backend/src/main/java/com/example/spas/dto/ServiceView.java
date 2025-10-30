package com.example.spas.dto;

import com.example.spas.model.enums.ApprovalStatus;
import com.example.spas.model.enums.ServiceStatus;

public class ServiceView {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer durationInMinutes;
    private ApprovalStatus approvalStatus;
    private ServiceStatus serviceStatus;
    private Long spaId;

    // Constructors
    public ServiceView() {
    }

    public ServiceView(Long id, String name, String description, Double price, Integer durationInMinutes, ApprovalStatus approvalStatus, ServiceStatus serviceStatus, Long spaId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.durationInMinutes = durationInMinutes;
        this.approvalStatus = approvalStatus;
        this.serviceStatus = serviceStatus;
        this.spaId = spaId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setDurationInMinutes(Integer durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public ServiceStatus getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(ServiceStatus serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public Long getSpaId() {
        return spaId;
    }

    public void setSpaId(Long spaId) {
        this.spaId = spaId;
    }
}