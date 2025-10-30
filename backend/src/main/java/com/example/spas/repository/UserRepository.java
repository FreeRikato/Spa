package com.example.spas.repository;

import com.example.spas.model.enums.MembershipStatus;
import com.example.spas.model.enums.Role;
import com.example.spas.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // For login (Feature 1)
    Optional<User> findByEmail(String email);

    // For registration check (Feature 1)
    Boolean existsByEmail(String email);

    // For Admin: view all clients (Feature 20)
    List<User> findAllByRole(Role role);

    // For Admin: view customers by membership status (Feature 23)
    List<User> findAllByMembershipStatus(MembershipStatus status);
    
    // For Admin: view customers by membership plan (Feature 23)
    List<User> findAllByMembershipId(Long membershipId);
}