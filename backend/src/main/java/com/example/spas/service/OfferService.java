package com.example.spas.service;

import com.example.spas.dto.ServiceCreateRequest;
import com.example.spas.dto.ServiceView;
import com.example.spas.exception.ResourceNotFoundException;
import com.example.spas.model.enums.ApprovalStatus;
import com.example.spas.model.enums.ServiceStatus;
import com.example.spas.model.Spa;
import com.example.spas.model.Service;
import com.example.spas.repository.ServiceRepository;
import com.example.spas.repository.SpaRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OfferService {

    private final ServiceRepository serviceRepository;
    private final SpaRepository spaRepository;

    public OfferService(ServiceRepository serviceRepository, SpaRepository spaRepository) {
        this.serviceRepository = serviceRepository;
        this.spaRepository = spaRepository;
    }
    
    
    public List<ServiceView> getAllServicesForAdmin(ApprovalStatus status) {
        List<com.example.spas.model.Service> services;

        if (status != null) {
            services = serviceRepository.findAllByApprovalStatus(status);
        } else {
            services = serviceRepository.findAll();
        }

        List<ServiceView> serviceViews = new ArrayList<>();
        for (com.example.spas.model.Service service : services) {
            serviceViews.add(mapToServiceView(service));
        }
        return serviceViews;
    }
    /**
     * Feature 11: Client adds a new Service to their Spa
     */
    public ServiceView addServiceToSpa(ServiceCreateRequest request, Long spaId, Long ownerId) {
        Spa spa = spaRepository.findById(spaId)
                .orElseThrow(() -> new ResourceNotFoundException("Spa not found with id: " + spaId));

        // Ownership Check
        if (!spa.getOwner().getId().equals(ownerId)) {
            throw new IllegalStateException("You do not have permission to add services to this spa.");
        }

        // --- EDGE CASE LOGIC ---
        // A client can only add services to a spa that is already APPROVED.
        if (spa.getApprovalStatus() != ApprovalStatus.APPROVED) {
            throw new IllegalStateException("Cannot add service: This spa is not yet approved by an admin.");
        }
        // --- END EDGE CASE ---

        Service service = new Service();
        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setPrice(request.getPrice());
        service.setDurationInMinutes(request.getDurationInMinutes());
        service.setSpa(spa);
        service.setApprovalStatus(ApprovalStatus.PENDING);
        service.setServiceStatus(ServiceStatus.UNAVAILABLE);

        Service savedService = serviceRepository.save(service);
        return mapToServiceView(savedService);
    }

    /**
     * Feature 12: Client changes the status of their service
     */
    public ServiceView updateServiceStatus(Long serviceId, ServiceStatus status, Long ownerId) {
        Service service = getServiceById(serviceId);

        // Ownership Check
        if (!service.getSpa().getOwner().getId().equals(ownerId)) {
            throw new IllegalStateException("You do not have permission to change this service.");
        }

        // --- EDGE CASE LOGIC ---
        // A client cannot make a service available if the parent spa is not approved.
        if (service.getSpa().getApprovalStatus() != ApprovalStatus.APPROVED) {
            throw new IllegalStateException("Cannot change service status: The parent spa is not approved.");
        }
        // --- END EDGE CASE ---

        service.setServiceStatus(status);
        Service updatedService = serviceRepository.save(service);
        return mapToServiceView(updatedService);
    }

    /**
     * Feature 19: Admin approves or rejects a Service
     */
    public ServiceView updateServiceApproval(Long serviceId, ApprovalStatus status) {
        Service service = getServiceById(serviceId);
        
        // Edge Case: Admin cannot approve a service for a spa that is not approved.
        if (status == ApprovalStatus.APPROVED && service.getSpa().getApprovalStatus() != ApprovalStatus.APPROVED) {
            throw new IllegalStateException("Cannot approve service: The parent spa must be approved first.");
        }
        
        service.setApprovalStatus(status);
        Service updatedService = serviceRepository.save(service);
        return mapToServiceView(updatedService);
    }
    
    /**
     * Helper to get all services for a client's spa
     */
    public List<ServiceView> getServicesForSpa(Long spaId) {
        List<Service> services = serviceRepository.findAllBySpaId(spaId);
        List<ServiceView> serviceViews = new ArrayList<>();
        
        for (Service service : services) {
            serviceViews.add(mapToServiceView(service));
        }
        return serviceViews;
    }

    // --- Helper Methods ---

    public Service getServiceById(Long serviceId) {
         return serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + serviceId));
    }

    public ServiceView mapToServiceView(Service service) {
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
}