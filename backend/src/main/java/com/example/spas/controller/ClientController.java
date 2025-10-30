package com.example.spas.controller;

import com.example.spas.dto.*;
import com.example.spas.model.User;
import com.example.spas.model.enums.BookingStatus;
import com.example.spas.model.enums.Role;
import com.example.spas.service.BookingService;
import com.example.spas.service.OfferService;
import com.example.spas.service.SpaService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client")
public class ClientController extends BaseController {

    private final SpaService spaService;
    private final OfferService offerService;
    private final BookingService bookingService;

    public ClientController(
        SpaService spaService,
        OfferService offerService,
        BookingService bookingService
    ) {
        this.spaService = spaService;
        this.offerService = offerService;
        this.bookingService = bookingService;
    }

    /**
     * Feature 10: Get all spas owned by the logged-in client
     */
    @GetMapping("/spas")
    public ResponseEntity<List<SpaView>> getMySpas(HttpSession session) {
        User user = checkRole(session, Role.CLIENT);
        return ResponseEntity.ok(spaService.getClientSpas(user.getId()));
    }

    /**
     * Feature 10: Add Spa
     */
    @PostMapping("/spas")
    public ResponseEntity<SpaView> addSpa(
        HttpSession session,
        @Valid @RequestBody SpaCreateRequest request
    ) {
        User user = checkRole(session, Role.CLIENT);
        SpaView newSpa = spaService.addSpa(request, user.getId());
        return new ResponseEntity<>(newSpa, HttpStatus.CREATED);
    }

    /**
     * Feature 11: Add service to a spa
     * Edge Case: Service logic checks if client owns the spa and if spa is APPROVED.
     */
    @PostMapping("/spas/{spaId}/services")
    public ResponseEntity<ServiceView> addService(
        HttpSession session,
        @PathVariable Long spaId,
        @Valid @RequestBody ServiceCreateRequest request
    ) {
        User user = checkRole(session, Role.CLIENT);
        ServiceView newService = offerService.addServiceToSpa(
            request,
            spaId,
            user.getId()
        );
        return new ResponseEntity<>(newService, HttpStatus.CREATED);
    }

    /**
     * Feature 12: Change service status
     * Edge Case: Service logic checks if client owns the service and if spa is APPROVED.
     */
    @PutMapping("/services/{serviceId}/status")
    public ResponseEntity<ServiceView> updateServiceStatus(
        HttpSession session,
        @PathVariable Long serviceId,
        @Valid @RequestBody ServiceStatusUpdateRequest request
    ) {
        User user = checkRole(session, Role.CLIENT);
        ServiceView updatedService = offerService.updateServiceStatus(
            serviceId,
            request.getStatus(),
            user.getId()
        );
        return ResponseEntity.ok(updatedService);
    }

    /**
     * Feature 13: Confirm or decline booking
     * Edge Case: Service logic checks if client owns the spa, if spa is APPROVED,
     * and if booking is still in PENDING state.
     */
    @PutMapping("/bookings/{bookingId}/status")
    public ResponseEntity<BookingView> updateBookingStatus(
        HttpSession session,
        @PathVariable Long bookingId,
        @Valid @RequestBody BookingStatusUpdateRequest request
    ) {
        User user = checkRole(session, Role.CLIENT);
        BookingView updatedBooking = bookingService.updateBookingStatus(
            bookingId,
            request.getStatus(),
            user.getId()
        );
        return ResponseEntity.ok(updatedBooking);
    }

    /**
     * Feature 16: View all bookings for a spa
     * Edge Case: Service logic checks if client owns the spa.
     */
    @GetMapping("/spas/{spaId}/bookings")
    public ResponseEntity<List<BookingView>> getBookingsForSpa(
        HttpSession session,
        @PathVariable Long spaId
    ) {
        User user = checkRole(session, Role.CLIENT);
        List<BookingView> bookings = bookingService.getBookingsForSpa(
            spaId,
            user.getId()
        );
        return ResponseEntity.ok(bookings);
    }

    /**
     * Feature 17: View bookings (filter by status)
     * Edge Case: Service logic checks if client owns the spa.
     */
    @GetMapping("/spas/{spaId}/bookings/filter")
    public ResponseEntity<List<BookingView>> getBookingsByStatus(
        HttpSession session,
        @PathVariable Long spaId,
        @RequestParam BookingStatus status
    ) {
        User user = checkRole(session, Role.CLIENT);
        List<BookingView> bookings = bookingService.getBookingsForSpaByStatus(
            spaId,
            status,
            user.getId()
        );
        return ResponseEntity.ok(bookings);
    }
}
