package com.example.spas.controller;

import com.example.spas.dto.*;
import com.example.spas.model.enums.Role;
import com.example.spas.model.User;
import com.example.spas.service.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController extends BaseController {

    private final UserService userService;
    private final SpaService spaService;
    private final BookingService bookingService;
    private final ReviewService reviewService;
    private final MembershipService membershipService;

    public UserController(UserService userService, SpaService spaService, BookingService bookingService, ReviewService reviewService, MembershipService membershipService) {
        this.userService = userService;
        this.spaService = spaService;
        this.bookingService = bookingService;
        this.reviewService = reviewService;
        this.membershipService = membershipService;
    }

    /**
     * Feature 2 & 14: Update Profile
     * Edge Case: Allows ANY logged-in user (USER, CLIENT, or ADMIN).
     */
    @PutMapping("/profile")
    public ResponseEntity<UserView> updateProfile(HttpSession session, @Valid @RequestBody ProfileUpdateRequest request) {
        // getSessionUser() just checks for login, not a specific role.
        User user = getSessionUser(session); 
        UserView updatedUser = userService.updateProfile(user.getId(), request);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Feature 4: Book a service
     * Edge Case: Auth check for USER role. @Valid for input.
     * Service logic checks for availability, concurrency, and service status.
     */
    @PostMapping("/bookings")
    public ResponseEntity<BookingView> createBooking(HttpSession session, @Valid @RequestBody BookingRequest request) {
        User user = checkRole(session, Role.USER); // Only USERS can book
        BookingView newBooking = bookingService.createBooking(request, user.getId());
        return new ResponseEntity<>(newBooking, HttpStatus.CREATED);
    }

    /**
     * Feature 6: View my bookings
     */
    @GetMapping("/bookings")
    public ResponseEntity<List<BookingView>> getMyBookings(HttpSession session) {
        User user = checkRole(session, Role.USER);
        return ResponseEntity.ok(bookingService.getUserBookings(user.getId()));
    }

    /**
     * Feature 7: Cancel booking
     * Edge Case: Service logic checks that user owns the booking and that
     * booking is still in a cancellable state (PENDING).
     */
    @PutMapping("/bookings/{bookingId}/cancel")
    public ResponseEntity<BookingView> cancelBooking(HttpSession session, @PathVariable Long bookingId) {
        User user = checkRole(session, Role.USER);
        BookingView cancelledBooking = bookingService.cancelBooking(bookingId, user.getId());
        return ResponseEntity.ok(cancelledBooking);
    }
    
    /**
     * Feature 9: Check Availability
     */
    @PostMapping("/services/{serviceId}/availability")
    public ResponseEntity<AvailabilityResponse> checkAvailability(HttpSession session, @PathVariable Long serviceId, @Valid @RequestBody AvailabilityRequest request) {
        checkRole(session, Role.USER); // Only users check availability
        return ResponseEntity.ok(bookingService.checkAvailability(serviceId, request.getDate()));
    }

    /**
     * Feature 8: Submit review
     * Edge Case: Service logic checks that user had a CONFIRMED booking
     * and has not already reviewed this spa.
     */
    @PostMapping("/spas/{spaId}/reviews")
    public ResponseEntity<ReviewView> submitReview(HttpSession session, @PathVariable Long spaId, @Valid @RequestBody ReviewRequest request) {
        User user = checkRole(session, Role.USER);
        ReviewView newReview = reviewService.submitReview(spaId, user.getId(), request);
        return new ResponseEntity<>(newReview, HttpStatus.CREATED);
    }

    /**
     * Feature 15: Add to wishlist
     */
    /**
     * Feature 15: Add Service to wishlist
     */
    @PostMapping("/wishlist/service/{serviceId}") // <-- Path updated
    public ResponseEntity<Void> addToWishlist(HttpSession session, @PathVariable Long serviceId) { // <-- Param updated
        User user = checkRole(session, Role.USER);
        userService.addToWishlist(user.getId(), serviceId); // <-- Service call updated
        return ResponseEntity.ok().build();
    }
    
    /**
     * Feature 15: View wishlist
     */
    @GetMapping("/wishlist")
    public ResponseEntity<List<ServiceView>> getWishlist(HttpSession session) { // <-- Return type updated
        User user = checkRole(session, Role.USER);
        return ResponseEntity.ok(userService.getWishlist(user.getId())); // <-- Service call updated
    }

    /**
     * Feature 15: Remove Service from wishlist
     */
    @DeleteMapping("/wishlist/service/{serviceId}") // <-- Path updated
    public ResponseEntity<Void> removeFromWishlist(HttpSession session, @PathVariable Long serviceId) { // <-- Param updated
        User user = checkRole(session, Role.USER);
        userService.removeFromWishlist(user.getId(), serviceId); // <-- Service call updated
        return ResponseEntity.noContent().build();
    }
    /**
     * Feature 22: Subscribe to membership
     * Edge Case: Service logic checks if user is already subscribed.
     */
    @PostMapping("/membership/subscribe")
    public ResponseEntity<UserView> subscribeToMembership(HttpSession session, @Valid @RequestBody MembershipSubscribeRequest request) {
        User user = checkRole(session, Role.USER);
        UserView updatedUser = membershipService.subscribeToMembership(user.getId(), request.getMembershipId());
        return ResponseEntity.ok(updatedUser);
    }
    
    /**
     * Feature 22: Reject/Cancel membership
     * Edge Case: Service logic checks if user has a membership to cancel.
     */
    @PostMapping("/membership/cancel")
    public ResponseEntity<UserView> cancelMembership(HttpSession session) {
        User user = checkRole(session, Role.USER);
        UserView updatedUser = membershipService.rejectMembership(user.getId());
        return ResponseEntity.ok(updatedUser);
    }
}