package com.example.spas.service;

import com.example.spas.dto.AvailabilityResponse;
import com.example.spas.dto.BookingRequest;
import com.example.spas.dto.BookingView;
import com.example.spas.exception.ResourceNotFoundException;
import com.example.spas.model.*;
import com.example.spas.model.enums.ApprovalStatus;
import com.example.spas.model.enums.BookingStatus;
import com.example.spas.model.enums.MembershipStatus;
import com.example.spas.model.enums.ServiceStatus;
import com.example.spas.repository.BookingRepository;
import com.example.spas.repository.ServiceRepository;
import com.example.spas.repository.SpaRepository;
import com.example.spas.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final SpaRepository spaRepository;

    public BookingService(BookingRepository bookingRepository, UserRepository userRepository, 
                          ServiceRepository serviceRepository, SpaRepository spaRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
        this.spaRepository = spaRepository;
    }

    /**
     * Feature 4: User books a service
     * This is transactional to prevent race conditions.
     */
    @Transactional
    public BookingView createBooking(BookingRequest request, Long customerId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + customerId));
        
        com.example.spas.model.Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + request.getServiceId()));

        // ... (edge case checks for approval, availability, and slotTaken are the same) ...

        // --- NEW LOGIC: CALCULATE PRICE ONCE ---
        Double originalPrice = service.getPrice();
        Double priceToSave = originalPrice; // Default to full price

        // Check for active membership and apply discount
        if (customer.getMembership() != null && customer.getMembershipStatus() == MembershipStatus.ACTIVE) {
            Double discountPercent = customer.getMembership().getDiscountPercentage();
            if (discountPercent != null && discountPercent > 0) {
                double discountAmount = originalPrice * (discountPercent / 100.0);
                priceToSave = originalPrice - discountAmount;
            }
        }
        // --- END NEW LOGIC ---

        // --- UPDATE CONSTRUCTOR CALL ---
        Booking booking = new Booking(
                request.getBookingTime(),
                customer,
                service.getSpa(),
                service,
                priceToSave // <-- Save the locked-in price
        );
        // -----------------------------

        // ... (status setting and save are the same) ...

        Booking savedBooking = bookingRepository.save(booking);
        return mapToBookingView(savedBooking);
    }

    /**
     * Feature 6: User views their bookings
     */
    public List<BookingView> getUserBookings(Long customerId) {
        List<Booking> bookings = bookingRepository.findAllByCustomerId(customerId);
        List<BookingView> bookingViews = new ArrayList<>();
        
        for (Booking booking : bookings) {
            bookingViews.add(mapToBookingView(booking));
        }
        return bookingViews;
    }

    /**
     * Feature 7: User cancels a booking
     */
    public BookingView cancelBooking(Long bookingId, Long customerId) {
        Booking booking = getBookingById(bookingId);

        // Ownership check
        if (!booking.getCustomer().getId().equals(customerId)) {
            throw new IllegalStateException("You do not have permission to cancel this booking.");
        }
        
        // --- EDGE CASE LOGIC (State Check) ---
        // A user can only cancel a PENDING booking.
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("This booking cannot be cancelled (Status: " + booking.getStatus() + ").");
        }
        // --- END EDGE CASE ---
        
        booking.setStatus(BookingStatus.CANCELLED_BY_USER);
        Booking updatedBooking = bookingRepository.save(booking);
        return mapToBookingView(updatedBooking);
    }

    /**
     * Feature 9: Check Availability
     * Note: This is a simple 1-hour slot check. A real-world edge case
     * would be to check if a 90-minute service can fit, which this logic does not do.
     */
    public AvailabilityResponse checkAvailability(Long serviceId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        List<Booking> existingBookings = bookingRepository
                .findAllByServiceIdAndBookingTimeBetween(serviceId, startOfDay, endOfDay);
        
        Set<LocalTime> bookedTimes = new HashSet<>();
        for (Booking booking : existingBookings) {
            bookedTimes.add(booking.getBookingTime().toLocalTime());
        }

        List<LocalTime> availableSlots = new ArrayList<>();
        // Assuming spa hours are 9:00 to 17:00 (9am to 5pm)
        for (int hour = 9; hour <= 17; hour++) {
            LocalTime slot = LocalTime.of(hour, 0);
            if (!bookedTimes.contains(slot)) {
                availableSlots.add(slot);
            }
        }
        
        return new AvailabilityResponse(availableSlots);
    }

    /**
     * Feature 13: Client confirms or declines a booking
     */
    public BookingView updateBookingStatus(Long bookingId, BookingStatus status, Long ownerId) {
        Booking booking = getBookingById(bookingId);

        // Ownership check
        if (!booking.getSpa().getOwner().getId().equals(ownerId)) {
            throw new IllegalStateException("You do not have permission to update this booking.");
        }

        // --- EDGE CASE LOGIC (Business Rules) ---
        if (booking.getSpa().getApprovalStatus() != ApprovalStatus.APPROVED) {
            throw new IllegalStateException("Cannot manage bookings: This spa is not yet approved.");
        }
        
        // A client can only act on a PENDING booking.
        if (booking.getStatus() != BookingStatus.PENDING) {
             throw new IllegalStateException("This booking is no longer pending (Status: " + booking.getStatus() + ").");
        }
        
        // Client can only Confirm or Decline
        if (status != BookingStatus.CONFIRMED && status != BookingStatus.DECLINED_BY_CLIENT) {
             throw new IllegalArgumentException("Invalid status update. Can only CONFIRM or DECLINE.");
        }
        // --- END EDGE CASE ---

        booking.setStatus(status);
        Booking updatedBooking = bookingRepository.save(booking);
        return mapToBookingView(updatedBooking);
    }

    /**
     * Feature 16 & 17: Client views bookings (with/without filter)
     */
    public List<BookingView> getBookingsForSpa(Long spaId, Long ownerId) {
        Spa spa = spaRepository.findById(spaId)
             .orElseThrow(() -> new ResourceNotFoundException("Spa not found with id: " + spaId));
        
        if (!spa.getOwner().getId().equals(ownerId)) {
            throw new IllegalStateException("You do not own this spa.");
        }
        
        List<Booking> bookings = bookingRepository.findAllBySpaId(spaId);
        List<BookingView> bookingViews = new ArrayList<>();

        for (Booking booking : bookings) {
            bookingViews.add(mapToBookingView(booking));
        }
        return bookingViews;
    }

    public List<BookingView> getBookingsForSpaByStatus(Long spaId, BookingStatus status, Long ownerId) {
        Spa spa = spaRepository.findById(spaId)
             .orElseThrow(() -> new ResourceNotFoundException("Spa not found with id: " + spaId));
        
        if (!spa.getOwner().getId().equals(ownerId)) {
            throw new IllegalStateException("You do not own this spa.");
        }
        
        List<Booking> bookings = bookingRepository.findAllBySpaIdAndStatus(spaId, status);
        List<BookingView> bookingViews = new ArrayList<>();
        
        for (Booking booking : bookings) {
            bookingViews.add(mapToBookingView(booking));
        }
        return bookingViews;
    }


    // --- Helper Methods ---

    public Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
    }

    private BookingView mapToBookingView(Booking booking) {
        User customer = booking.getCustomer();
        Spa spa = booking.getSpa();
        com.example.spas.model.Service service = booking.getService();

   
     // --- LOGIC UPDATED ---
        // Get the original price from the service
        Double originalPrice = service.getPrice(); 
        // Get the final price directly from the booking
        Double finalPrice = booking.getFinalPrice(); 
        // --- END UPDATE ---
        
        
        return new BookingView(
                booking.getId(),
                booking.getBookingTime(),
                booking.getStatus(),
                customer.getId(),
                customer.getFirstName() + " " + customer.getLastName(),
                spa.getId(),
                spa.getName(),
                service.getId(),
                service.getName(),
                originalPrice, // <-- Pass original price
                finalPrice     // <-- Pass final (discounted) price
        );
    }
    
    
}