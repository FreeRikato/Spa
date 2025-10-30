package com.example.spas.dto;

import com.example.spas.model.enums.BookingStatus;
import java.time.LocalDateTime;

public class BookingView {

    private Long id;
    private LocalDateTime bookingTime;
    private BookingStatus status;
    
    private Long customerId;
    private String customerName;
    
    private Long spaId;
    private String spaName;
    
    private Long serviceId;
    private String serviceName;
    private Double originalPrice; // Renamed from servicePrice
    private Double finalPrice;
    // Constructors
    public BookingView() {
    }
    public BookingView(Long id, LocalDateTime bookingTime, BookingStatus status, Long customerId, String customerName, Long spaId, String spaName, Long serviceId, String serviceName, Double originalPrice, Double finalPrice) {
        this.id = id;
        this.bookingTime = bookingTime;
        this.status = status;
        this.customerId = customerId;
        this.customerName = customerName;
        this.spaId = spaId;
        this.spaName = spaName;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.originalPrice = originalPrice;
        this.finalPrice = finalPrice;
    }
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(LocalDateTime bookingTime) {
        this.bookingTime = bookingTime;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Long getSpaId() {
        return spaId;
    }

    public void setSpaId(Long spaId) {
        this.spaId = spaId;
    }

    public String getSpaName() {
        return spaName;
    }

    public void setSpaName(String spaName) {
        this.spaName = spaName;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(Double finalPrice) {
        this.finalPrice = finalPrice;
    }
}