package com.example.spas.controller;

import com.example.spas.dto.ApprovalRequest;
import com.example.spas.dto.MembershipCreateRequest;
import com.example.spas.dto.MembershipView;
import com.example.spas.dto.ServiceView;
import com.example.spas.dto.SpaView;
import com.example.spas.dto.UserView;
import com.example.spas.model.enums.ApprovalStatus;
import com.example.spas.model.enums.MembershipStatus;
import com.example.spas.model.enums.Role;
import com.example.spas.service.MembershipService;
import com.example.spas.service.OfferService;
import com.example.spas.service.SpaService;
import com.example.spas.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController extends BaseController {
    
    private final SpaService spaService;
    private final OfferService offerService;
    private final UserService userService;
    private final MembershipService membershipService;

    public AdminController(SpaService spaService, OfferService offerService, UserService userService, MembershipService membershipService) {
        this.spaService = spaService;
        this.offerService = offerService;
        this.userService = userService;
        this.membershipService = membershipService;
    }

    /**
     * Feature 18: Approve or reject spa
     * Edge Case: Service logic cascades a REJECT decision to all pending services.
     */
    @PutMapping("/spas/{spaId}/approve")
    public ResponseEntity<?> approveSpa(HttpSession session, @PathVariable Long spaId, @Valid @RequestBody ApprovalRequest request) {
        checkRole(session, Role.ADMIN);
        return ResponseEntity.ok(spaService.updateSpaApproval(spaId, request.getStatus()));
    }
    
    /**
     * Feature 19: Approve or reject service
     * Edge Case: Service logic blocks approving a service for an unapproved spa.
     */
    @PutMapping("/services/{serviceId}/approve")
    public ResponseEntity<?> approveService(HttpSession session, @PathVariable Long serviceId, @Valid @RequestBody ApprovalRequest request) {
        checkRole(session, Role.ADMIN);
        return ResponseEntity.ok(offerService.updateServiceApproval(serviceId, request.getStatus()));
    }
    
    /**
     * Feature 20: View all clients
     */
    @GetMapping("/clients")
    public ResponseEntity<List<UserView>> getAllClients(HttpSession session) {
        checkRole(session, Role.ADMIN);
        return ResponseEntity.ok(userService.getAllClients());
    }
    
    
    /**
     * Gets all spas, with an optional filter for status.
     * e.g., GET /api/admin/spas?status=PENDING
     */
    @GetMapping("/spas")
    public ResponseEntity<List<SpaView>> getAllSpas(
            HttpSession session, 
            @RequestParam(required = false) ApprovalStatus status) 
    {
        checkRole(session, Role.ADMIN);
        return ResponseEntity.ok(spaService.getSpasForAdmin(status));
    }
    
    
    /**
     * Gets all services, with an optional filter for status.
     * e.g., GET /api/admin/services?status=PENDING
     */
    @GetMapping("/services")
    public ResponseEntity<List<ServiceView>> getAllServices(
            HttpSession session, 
            @RequestParam(required = false) ApprovalStatus status) 
    {
        checkRole(session, Role.ADMIN);
        return ResponseEntity.ok(offerService.getAllServicesForAdmin(status));
    }
    
    /**
     * Feature 21: Create membership
     * Edge Case: Service logic checks for duplicate membership names.
     */
    @PostMapping("/memberships")
    public ResponseEntity<MembershipView> createMembership(HttpSession session, @Valid @RequestBody MembershipCreateRequest request) {
        checkRole(session, Role.ADMIN);
        MembershipView newMembership = membershipService.createMembership(request);
        return new ResponseEntity<>(newMembership, HttpStatus.CREATED);
    }
    
    /**
     * Feature 23: View customers by membership status
     */
    @GetMapping("/users/filter/status")
    public ResponseEntity<List<UserView>> getUsersByStatus(HttpSession session, @RequestParam MembershipStatus status) {
        checkRole(session, Role.ADMIN);
        return ResponseEntity.ok(userService.getUsersByMembershipStatus(status));
    }

    /**
     * Feature 23: View customers by membership plan
     */
    @GetMapping("/users/filter/membership")
    public ResponseEntity<List<UserView>> getUsersByMembership(HttpSession session, @RequestParam Long membershipId) {
        checkRole(session, Role.ADMIN);
        return ResponseEntity.ok(userService.getUsersByMembershipId(membershipId));
    }
}