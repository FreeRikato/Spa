package com.example.spas.dto;

import java.util.List;

public class SpaDetailView {

    private Long id;
    private String name;
    private String address;
    private String description;
    private Long ownerId;

    // Nested lists of other DTOs
    private List<ServiceView> services;
    private List<ReviewView> reviews;

    // Constructors
    public SpaDetailView() {
    }

    public SpaDetailView(Long id, String name, String address, String description, Long ownerId, List<ServiceView> services, List<ReviewView> reviews) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.description = description;
        this.ownerId = ownerId;
        this.services = services;
        this.reviews = reviews;
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

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public List<ServiceView> getServices() {
        return services;
    }

    public void setServices(List<ServiceView> services) {
        this.services = services;
    }

    public List<ReviewView> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewView> reviews) {
        this.reviews = reviews;
    }
}