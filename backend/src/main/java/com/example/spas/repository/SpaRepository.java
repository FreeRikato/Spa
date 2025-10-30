package com.example.spas.repository;

import com.example.spas.model.enums.ApprovalStatus;
import com.example.spas.model.Spa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpaRepository extends JpaRepository<Spa, Long> {

    // For User: find spa by name (Feature 3)
    // We also only want to show APPROVED spas
    List<Spa> findByNameContainingIgnoreCaseAndApprovalStatus(String name, ApprovalStatus status);
    
    // For User: find all approved spas (Feature 3)
    List<Spa> findAllByApprovalStatus(ApprovalStatus status);

    // For Client: view their own spas (part of Feature 10)
    List<Spa> findAllByOwnerId(Long ownerId);


}