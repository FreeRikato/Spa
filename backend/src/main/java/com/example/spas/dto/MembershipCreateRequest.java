package com.example.spas.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class MembershipCreateRequest {

	@NotEmpty(message = "Membership name cannot be empty")
    private String name;

    private String description;

    @NotNull(message = "Price cannot be null")
    @Min(value = 0, message = "Price cannot be negative")
    private Double pricePerMonth;
    
    @NotNull(message = "Discount cannot be null")
    @Min(value = 0, message = "Discount cannot be negative")
    @Max(value = 100, message = "Discount cannot be over 100")
    private Double discountPercentage;

    // Constructors
    public MembershipCreateRequest() {
    }

    public MembershipCreateRequest(String name, String description, Double pricePerMonth, Double discountPercentage) {
        this.name = name;
        this.description = description;
        this.pricePerMonth = pricePerMonth;
        this.discountPercentage = discountPercentage;
    }
    // Getters and Setters
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
    
    public Double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(Double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }
}