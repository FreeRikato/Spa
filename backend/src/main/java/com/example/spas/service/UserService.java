package com.example.spas.service;

import com.example.spas.dto.LoginRequest;
import com.example.spas.dto.ProfileUpdateRequest;
import com.example.spas.dto.RegistrationRequest;
import com.example.spas.dto.ServiceView;
import com.example.spas.dto.UserView;
import com.example.spas.exception.ResourceNotFoundException;
import com.example.spas.model.enums.ApprovalStatus;
import com.example.spas.model.enums.MembershipStatus;
import com.example.spas.model.enums.Role;
import com.example.spas.model.enums.ServiceStatus;
import com.example.spas.model.User;
import com.example.spas.repository.ServiceRepository;
import com.example.spas.repository.UserRepository;
import com.example.spas.model.Service;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@org.springframework.stereotype.Service
public class UserService {

    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository; // <-- ADD THIS
    private final OfferService offerService;
    
    
    public UserService(UserRepository userRepository, ServiceRepository serviceRepository, OfferService offerService) {
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
        this.offerService = offerService;
    }
    /**
     * Feature 1: Register (No Hashing)
     */
    public UserView register(RegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }
//        
//        if (request.getRole() == Role.ADMIN) {
//             throw new IllegalArgumentException("Cannot register as ADMIN");
//        }

        // Edge Case: DTO validation should catch this, but we check again.
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // Storing plain text (NOT RECOMMENDED)
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());

        User savedUser = userRepository.save(user);
        return mapToUserView(savedUser);
    }

    /**
     * Feature 1: Login (Plain Text Check)
     */
    public User login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

        // Plain text password check
        if (!user.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password.");
        }
        return user;
    }

    /**
     * Feature 2 & 14: Update Profile
     */
    public UserView updateProfile(Long userId, ProfileUpdateRequest request) {
        User user = getUserById(userId);
        
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        
        User updatedUser = userRepository.save(user);
        return mapToUserView(updatedUser);
    }

    /**
     * Feature 20: View all Clients
     */
    public List<UserView> getAllClients() {
        List<User> clients = userRepository.findAllByRole(Role.CLIENT);
        List<UserView> clientViews = new ArrayList<>();
        
        for (User client : clients) {
            clientViews.add(mapToUserView(client));
        }
        return clientViews;
    }

    /**
     * Feature 23: View customers by membership status
     */
    public List<UserView> getUsersByMembershipStatus(MembershipStatus status) {
        List<User> users = userRepository.findAllByMembershipStatus(status);
        List<UserView> userViews = new ArrayList<>();

        for (User user : users) {
            userViews.add(mapToUserView(user));
        }
        return userViews;
    }

    /**
     * Feature 23: View customers by membership plan
     */
    public List<UserView> getUsersByMembershipId(Long membershipId) {
        List<User> users = userRepository.findAllByMembershipId(membershipId);
        List<UserView> userViews = new ArrayList<>();

        for (User user : users) {
            userViews.add(mapToUserView(user));
        }
        return userViews;
    }
    
    
    public void addToWishlist(Long userId, Long serviceId) {
        User user = getUserById(userId);
        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + serviceId));

        // You can only wishlist an approved and available service
        if (service.getApprovalStatus() != ApprovalStatus.APPROVED || service.getServiceStatus() != ServiceStatus.AVAILABLE) {
            throw new IllegalStateException("This service cannot be wishlisted as it is not available.");
        }
        
        if (user.getWishlist() == null) {
            user.setWishlist(new HashSet<>());
        }

        user.getWishlist().add(service);
        userRepository.save(user);
    }

    /**
     * Feature 15: Remove Service from Wishlist
     */
    public void removeFromWishlist(Long userId, Long serviceId) {
        User user = getUserById(userId);
        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + serviceId));

        if (user.getWishlist() != null) {
            user.getWishlist().remove(service);
            userRepository.save(user);
        }
    }
    
    
    

    /**
     * Feature 15: View Wishlist (now returns List<ServiceView>)
     */
    public List<ServiceView> getWishlist(Long userId) {
        User user = getUserById(userId);
        List<ServiceView> serviceViews = new ArrayList<>();

        if (user.getWishlist() != null) {
            for (Service service : user.getWishlist()) {
                // Use the public mapper from OfferService
                serviceViews.add(offerService.mapToServiceView(service));
            }
        }
        return serviceViews;
    }

    // --- Helper Methods ---

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }
    
    public UserView getUserView(Long userId) {
        return mapToUserView(getUserById(userId));
    }

    // This is the mapping function
    public UserView mapToUserView(User user) {
        String membershipName = (user.getMembership() != null) ? user.getMembership().getName() : null;
        
        return new UserView(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getRole(),
                membershipName,
                user.getMembershipStatus()
        );
    }
}