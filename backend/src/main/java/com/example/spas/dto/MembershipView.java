package com.example.spas.dto;

public class MembershipView {

    private Long id;
    private String name;
    private String description;
    private Double pricePerMonth;
 // --- ADD THIS ---
    private Double discountPercentage;
    // ---------------

    // Constructors
    public MembershipView() {
    }

    // --- UPDATE CONSTRUCTOR ---
    public MembershipView(Long id, String name, String description, Double pricePerMonth, Double discountPercentage) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.pricePerMonth = pricePerMonth;
        this.discountPercentage = discountPercentage;
    }    // Getters and Setters
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

    public Double getPricePerMonth() {
        return pricePerMonth;
    }

    public void setPricePerMonth(Double pricePerMonth) {
        this.pricePerMonth = pricePerMonth;
    }
}