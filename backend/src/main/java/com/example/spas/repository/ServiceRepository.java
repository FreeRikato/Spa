package com.example.spas.repository;

import com.example.spas.model.enums.ApprovalStatus;
import com.example.spas.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {

    // For User/Client: view all services for a specific spa (Feature 5, 11)
    List<Service> findAllBySpaId(Long spaId);

    // For User: view only APPROVED services for a spa (Feature 5)
    List<Service> findAllBySpaIdAndApprovalStatus(Long spaId, ApprovalStatus status);

    // For Admin: view all services awaiting approval (Feature 19)
    List<Service> findAllByApprovalStatus(ApprovalStatus status);
}