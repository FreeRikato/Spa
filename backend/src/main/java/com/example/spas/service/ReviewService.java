package com.example.spas.service;

import com.example.spas.dto.ReviewRequest;
import com.example.spas.dto.ReviewView;
import com.example.spas.exception.ResourceNotFoundException;
import com.example.spas.model.*; // Import BookingStatus
import com.example.spas.model.enums.BookingStatus;
import com.example.spas.repository.BookingRepository;
import com.example.spas.repository.ReviewRepository;
import com.example.spas.repository.SpaRepository;
import com.example.spas.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final SpaRepository spaRepository;
    private final BookingRepository bookingRepository; // Added for check

    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository, 
                         SpaRepository spaRepository, BookingRepository bookingRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.spaRepository = spaRepository;
        this.bookingRepository = bookingRepository;
    }

    /**
     * Feature 8: User submits a review
     */
    public ReviewView submitReview(Long spaId, Long userId, ReviewRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        Spa spa = spaRepository.findById(spaId)
                .orElseThrow(() -> new ResourceNotFoundException("Spa not found with id: " + spaId));

        // --- EDGE CASE LOGIC (Business Rules) ---
        
        // 1. Check if user has a confirmed booking at this spa.
        boolean hasBooking = bookingRepository.existsByCustomerIdAndSpaIdAndStatus(
                userId, spaId, BookingStatus.CONFIRMED);

        if (!hasBooking) {
            throw new IllegalStateException("You must have a confirmed booking to review this spa.");
        }
        
        // 2. Check if user has already reviewed this spa.
        boolean hasReviewed = reviewRepository.existsByUserIdAndSpaId(userId, spaId);
        if (hasReviewed) {
            throw new IllegalStateException("You have already submitted a review for this spa.");
        }
        // --- END EDGE CASE ---
        
        Review review = new Review(
                request.getRating(),
                request.getComment(),
                user,
                spa
        );

        Review savedReview = reviewRepository.save(review);
        return mapToReviewView(savedReview);
    }

    // --- Helper ---

    private ReviewView mapToReviewView(Review review) {
        String userName = review.getUser().getFirstName() + " " + review.getUser().getLastName().charAt(0) + ".";
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