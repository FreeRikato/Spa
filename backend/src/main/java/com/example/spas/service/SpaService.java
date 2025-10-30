package com.example.spas.service;

import com.example.spas.dto.ReviewView;
import com.example.spas.dto.ServiceView;
import com.example.spas.dto.SpaCreateRequest;
import com.example.spas.dto.SpaDetailView;
import com.example.spas.dto.SpaView;
import com.example.spas.exception.ResourceNotFoundException;
import com.example.spas.model.*;
import com.example.spas.model.enums.ApprovalStatus;
import com.example.spas.repository.ReviewRepository;
import com.example.spas.repository.ServiceRepository;
import com.example.spas.repository.SpaRepository;
import com.example.spas.repository.UserRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SpaService {

    private final SpaRepository spaRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final ReviewRepository reviewRepository;

    public SpaService(
        SpaRepository spaRepository,
        UserRepository userRepository,
        ServiceRepository serviceRepository,
        ReviewRepository reviewRepository
    ) {
        this.spaRepository = spaRepository;
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
        this.reviewRepository = reviewRepository;
    }

    // --- ADD THIS METHOD ---
    /**
     * For Admin: Get all spas, optionally filtered by status.
     */
    public List<SpaView> getSpasForAdmin(ApprovalStatus status) {
        List<Spa> spas;

        if (status != null) {
            // If status is provided (e.g., PENDING), filter by it
            spas = spaRepository.findAllByApprovalStatus(status);
        } else {
            // If no status is provided, get all spas
            spas = spaRepository.findAll();
        }

        List<SpaView> spaViews = new ArrayList<>();
        for (Spa spa : spas) {
            spaViews.add(mapToSpaView(spa));
        }
        return spaViews;
    }

    /**
     * For Client: Get all spas owned by the client.
     */
    public List<SpaView> getClientSpas(Long ownerId) {
        List<Spa> spas = spaRepository.findAllByOwnerId(ownerId);
        List<SpaView> spaViews = new ArrayList<>();

        for (Spa spa : spas) {
            spaViews.add(mapToSpaView(spa));
        }
        return spaViews;
    }

    /**
     * Feature 10: Client adds a new Spa
     */
    public SpaView addSpa(SpaCreateRequest request, Long ownerId) {
        User owner = userRepository
            .findById(ownerId)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Owner (User) not found with id: " + ownerId
                )
            );

        Spa spa = new Spa();
        spa.setName(request.getName());
        spa.setAddress(request.getAddress());
        spa.setDescription(request.getDescription());
        spa.setOwner(owner);
        spa.setApprovalStatus(ApprovalStatus.PENDING); // Default

        Spa savedSpa = spaRepository.save(spa);
        return mapToSpaView(savedSpa);
    }

    /**
     * Feature 3: Find Spas (all approved)
     */
    public List<SpaView> findAllApprovedSpas() {
        List<Spa> spas = spaRepository.findAllByApprovalStatus(
            ApprovalStatus.APPROVED
        );
        List<SpaView> spaViews = new ArrayList<>();

        for (Spa spa : spas) {
            spaViews.add(mapToSpaView(spa));
        }
        return spaViews;
    }

    /**
     * Feature 3: Find Spas (by name)
     */
    public List<SpaView> findSpasByName(String name) {
        List<Spa> spas =
            spaRepository.findByNameContainingIgnoreCaseAndApprovalStatus(
                name,
                ApprovalStatus.APPROVED
            );
        List<SpaView> spaViews = new ArrayList<>();

        for (Spa spa : spas) {
            spaViews.add(mapToSpaView(spa));
        }
        return spaViews;
    }

    /**
     * Feature 5: View a single Spa's details
     */
    public SpaDetailView getSpaDetails(Long spaId) {
        Spa spa = spaRepository
            .findById(spaId)
            .orElseThrow(() ->
                new ResourceNotFoundException("Spa not found with id: " + spaId)
            );

        // Get entity lists
        List<com.example.spas.model.Service> services =
            serviceRepository.findAllBySpaIdAndApprovalStatus(
                spaId,
                ApprovalStatus.APPROVED
            );
        List<Review> reviews = reviewRepository.findAllBySpaId(spaId);

        // Map Services to ServiceViews
        List<ServiceView> serviceViews = new ArrayList<>();
        for (com.example.spas.model.Service service : services) {
            serviceViews.add(mapToServiceView(service));
        }

        // Map Reviews to ReviewViews
        List<ReviewView> reviewViews = new ArrayList<>();
        for (Review review : reviews) {
            reviewViews.add(mapToReviewView(review));
        }

        return new SpaDetailView(
            spa.getId(),
            spa.getName(),
            spa.getAddress(),
            spa.getDescription(),
            spa.getOwner().getId(),
            serviceViews,
            reviewViews
        );
    }

    /**
     * Feature 18: Admin approves or rejects a Spa
     * Edge Case: If Spa is rejected, reject all its pending services.
     */
    @Transactional
    public SpaView updateSpaApproval(Long spaId, ApprovalStatus status) {
        Spa spa = spaRepository
            .findById(spaId)
            .orElseThrow(() ->
                new ResourceNotFoundException("Spa not found with id: " + spaId)
            );

        spa.setApprovalStatus(status);

        // --- EDGE CASE LOGIC ---
        // If Admin REJECTS a spa, find all its PENDING services and REJECT them too.
        if (status == ApprovalStatus.REJECTED) {
            List<com.example.spas.model.Service> services =
                serviceRepository.findAllBySpaId(spaId);
            for (com.example.spas.model.Service service : services) {
                if (service.getApprovalStatus() == ApprovalStatus.PENDING) {
                    service.setApprovalStatus(ApprovalStatus.REJECTED);
                    serviceRepository.save(service);
                }
            }
        }
        // --- END EDGE CASE ---

        Spa updatedSpa = spaRepository.save(spa);
        return mapToSpaView(updatedSpa);
    }

    // --- Helper Mappers ---

    private SpaView mapToSpaView(Spa spa) {
        return new SpaView(
            spa.getId(),
            spa.getName(),
            spa.getAddress(),
            spa.getDescription(),
            spa.getApprovalStatus(),
            spa.getOwner().getId()
        );
    }

    private ServiceView mapToServiceView(
        com.example.spas.model.Service service
    ) {
        return new ServiceView(
            service.getId(),
            service.getName(),
            service.getDescription(),
            service.getPrice(),
            service.getDurationInMinutes(),
            service.getApprovalStatus(),
            service.getServiceStatus(),
            service.getSpa().getId()
        );
    }

    private ReviewView mapToReviewView(Review review) {
        String userName =
            review.getUser().getFirstName() +
            " " +
            review.getUser().getLastName().charAt(0) +
            ".";
        return new ReviewView(
            review.getId(),
            review.getRating(),
            review.getComment(),
            review.getReviewDate(),
            review.getUser().getId(),
            userName,
            review.getSpa().getId()
        );
    }
}
