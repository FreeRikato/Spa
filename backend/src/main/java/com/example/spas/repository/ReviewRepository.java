package com.example.spas.repository;

import com.example.spas.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // For User: view all reviews for a spa (Feature 5, 8)
    List<Review> findAllBySpaId(Long spaId);

    // For User: view all reviews they have written
    List<Review> findAllByUserId(Long userId);
    
    boolean existsByUserIdAndSpaId(Long userId, Long spaId);
}