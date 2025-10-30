package com.example.spas.repository;

import com.example.spas.model.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {

    // For Admin: check if membership name already exists (Feature 21)
    Optional<Membership> findByName(String name);
}