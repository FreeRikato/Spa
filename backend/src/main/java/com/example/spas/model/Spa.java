package com.example.spas.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.example.spas.model.enums.ApprovalStatus;

@Entity
@Table(name = "spas")
public class Spa implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String address;
    private String description;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus; // For Admin (Feature 18)

    // --- Relationships ---

    // The Client (User) who owns this spa
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id", nullable = false)
    private User owner;

    // Services offered by this spa
    @OneToMany(mappedBy = "spa", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Service> services = new HashSet<>();

    // Bookings for this spa
    @OneToMany(mappedBy = "spa", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Booking> bookings = new HashSet<>();

    // Reviews for this spa
    @OneToMany(mappedBy = "spa", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Review> reviews = new HashSet<>();



    // --- Constructors ---

    public Spa() {
    }

    public Spa(String name, String address, String description, User owner) {
        this.name = name;
        this.address = address;
        this.description = description;
        this.owner = owner;
        this.approvalStatus = ApprovalStatus.PENDING; // Default status
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

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Set<Service> getServices() {
        return services;
    }

    public void setServices(Set<Service> services) {
        this.services = services;
    }

    public Set<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(Set<Booking> bookings) {
        this.bookings = bookings;
    }

    public Set<Review> getReviews() {
        return reviews;
    }

    public void setReviews(Set<Review> reviews) {
        this.reviews = reviews;
    }



    // --- toString, equals, hashCode ---

    @Override
    public String toString() {
        return "Spa{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", approvalStatus=" + approvalStatus +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Spa spa = (Spa) o;
        return Objects.equals(id, spa.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}