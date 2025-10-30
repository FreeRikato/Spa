package com.example.spas.dto;

import java.time.LocalDateTime;

public class ReviewView {

    private Long id;
    private Integer rating;
    private String comment;
    private LocalDateTime reviewDate;
    private Long userId;
    private String userName; // (e.g., "John D.")
    private Long spaId;

    // Constructors
    public ReviewView() {
    }

    public ReviewView(Long id, Integer rating, String comment, LocalDateTime reviewDate, Long userId, String userName, Long spaId) {
        this.id = id;
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = reviewDate;
        this.userId = userId;
        this.userName = userName;
        this.spaId = spaId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDateTime reviewDate) {
        this.reviewDate = reviewDate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getSpaId() {
        return spaId;
    }

    public void setSpaId(Long spaId) {
        this.spaId = spaId;
    }
}