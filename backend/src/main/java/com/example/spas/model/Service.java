package com.example.spas.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.example.spas.model.enums.ApprovalStatus;
import com.example.spas.model.enums.ServiceStatus;

@Entity
@Table(name = "services")
public class Service implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private Double price;

    private Integer durationInMinutes;

    // Admin approval (Feature 19)
    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;

    // Client availability (Feature 12)
    @Enumerated(EnumType.STRING)
    private ServiceStatus serviceStatus;
    
 // The set of users who have wishlisted this specific service.
    @ManyToMany(mappedBy = "wishlist")
    private Set<User> wishlistedBy = new HashSet<>();

    // --- Relationships ---

    // The Spa this service belongs to
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spa_id", nullable = false)
    private Spa spa;

    // All bookings for this specific service
    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Booking> bookings = new HashSet<>();

    // --- Constructors ---

    public Service() {
    }

    public Service(String name, String description, Double price, Integer durationInMinutes, Spa spa) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.durationInMinutes = durationInMinutes;
        this.spa = spa;
        this.approvalStatus = ApprovalStatus.PENDING; // Default for admin
        this.serviceStatus = ServiceStatus.UNAVAILABLE; // Default for client
    }

    // --- Getters and Setters ---

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

    public Spa getSpa() {
        return spa;
    }

    public void setSpa(Spa spa) {
        this.spa = spa;
    }

    public Set<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(Set<Booking> bookings) {
        this.bookings = bookings;
    }
    
    
    public Set<User> getWishlistedBy() {
        return wishlistedBy;
    }

    public void setWishlistedBy(Set<User> wishlistedBy) {
        this.wishlistedBy = wishlistedBy;
    }

    // --- toString, equals, hashCode ---

    @Override
    public String toString() {
        return "Service{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Service service = (Service) o;
        return Objects.equals(id, service.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}