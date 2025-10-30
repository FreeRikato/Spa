package com.example.spas.service;

import com.example.spas.dto.MembershipCreateRequest;
import com.example.spas.dto.MembershipView;
import com.example.spas.dto.UserView;
import com.example.spas.exception.ResourceNotFoundException;
import com.example.spas.model.Membership;
import com.example.spas.model.enums.MembershipStatus;
import com.example.spas.model.User;
import com.example.spas.repository.MembershipRepository;
import com.example.spas.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository;
    private final UserService userService; // For mapping User -> UserView

    public MembershipService(MembershipRepository membershipRepository, UserRepository userRepository, UserService userService) {
        this.membershipRepository = membershipRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    /**
     * Feature 21: Admin creates a new membership
     */
    public MembershipView createMembership(MembershipCreateRequest request) {
        if (membershipRepository.findByName(request.getName()).isPresent()) {
            throw new IllegalArgumentException("Membership name already exists");
        }

        // --- UPDATE THIS ---
        Membership membership = new Membership(
                request.getName(),
                request.getDescription(),
                request.getPricePerMonth(),
                request.getDiscountPercentage() // <-- Add this
        );
        // -----------------
        
        Membership savedMembership = membershipRepository.save(membership);
        return mapToMembershipView(savedMembership);
    }
    /**
     * Helper to see all memberships
     */
    public List<MembershipView> getAllMemberships() {
        List<Membership> memberships = membershipRepository.findAll();
        List<MembershipView> membershipViews = new ArrayList<>();
        
        for (Membership membership : memberships) {
            membershipViews.add(mapToMembershipView(membership));
        }
        return membershipViews;
    }

    /**
     * Feature 22: User subscribes to a membership
     * Edge Case: Checks if the user is already an active member.
     */
    public UserView subscribeToMembership(Long userId, Long membershipId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        Membership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found with id: " + membershipId));
        
        // --- EDGE CASE LOGIC ---
        // Check if user is already an active member of any plan.
        if (user.getMembership() != null && user.getMembershipStatus() == MembershipStatus.ACTIVE) {
            throw new IllegalStateException("You are already subscribed to a membership (" + user.getMembership().getName() + ").");
        }
        // --- END EDGE CASE ---
        
        user.setMembership(membership);
        user.setMembershipStatus(MembershipStatus.ACTIVE); // Simple: auto-approve

        User savedUser = userRepository.save(user);
        return userService.mapToUserView(savedUser);
    }

    /**
     * Feature 22: User rejects/cancels membership
     * Edge Case: Checks if the user has a membership to cancel.
     */
    public UserView rejectMembership(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        // --- EDGE CASE LOGIC ---
        if (user.getMembership() == null) {
            throw new IllegalStateException("You do not have an active membership to cancel.");
        }
        // --- END EDGE CASE ---
        
        user.setMembership(null);
        user.setMembershipStatus(MembershipStatus.INACTIVE);

        User savedUser = userRepository.save(user);
        return userService.mapToUserView(savedUser);
    }
    
    // --- Helper ---

    private MembershipView mapToMembershipView(Membership membership) {
        // --- UPDATE THIS ---
        return new MembershipView(
                membership.getId(),
                membership.getName(),
                membership.getDescription(),
                membership.getPricePerMonth(),
                membership.getDiscountPercentage() // <-- Add this
        );
        // -----------------
    }
}