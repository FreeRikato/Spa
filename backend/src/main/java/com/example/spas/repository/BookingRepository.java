package com.example.spas.repository;

import com.example.spas.model.Booking;
import com.example.spas.model.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // For User: view all their bookings (Feature 6)
    List<Booking> findAllByCustomerId(Long customerId);

    // For Client: view all bookings for their specific spa (Feature 16)
    List<Booking> findAllBySpaId(Long spaId);

    // For Client: filter bookings by status for their spa (Feature 17)
    List<Booking> findAllBySpaIdAndStatus(Long spaId, BookingStatus status);

    // For Client: view all bookings for ALL their spas
    List<Booking> findAllBySpaOwnerId(Long ownerId);

    // For User: check availability for a service on a specific day (Feature 9)
    List<Booking> findAllByServiceIdAndBookingTimeBetween(Long serviceId, LocalDateTime startOfDay, LocalDateTime endOfDay);
    
    boolean existsByServiceIdAndBookingTime(Long serviceId, LocalDateTime bookingTime);

    /**
     * This is the method you need to add.
     * It checks if a row exists in the Booking table that matches all three conditions.
     */
    boolean existsByCustomerIdAndSpaIdAndStatus(Long customerId, Long spaId, BookingStatus status);
}