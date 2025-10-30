package com.example.spas.dto;

import jakarta.validation.constraints.NotEmpty;

public class SpaCreateRequest {

	@NotEmpty(message = "Spa name cannot be empty")
    private String name;

    @NotEmpty(message = "Address cannot be empty")
    private String address;
    private String description;

    // Constructors
    public SpaCreateRequest() {
    }

    public SpaCreateRequest(String name, String address, String description) {
        this.name = name;
        this.address = address;
        this.description = description;
    }

    // Getters and Setters
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
}