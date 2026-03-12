package com.example.ConnectDB.service;

import com.example.ConnectDB.model.Review;
import com.example.ConnectDB.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    public Optional<Review> findById(Long id) {
        return reviewRepository.findById(id);
    }

    public Review save(Review review) {
        // Check if user already reviewed this product
        Optional<Review> existingReview = reviewRepository.findByUserIdAndProductId(
                review.getUser().getId(), review.getProduct().getId());

        if (existingReview.isPresent()) {
            throw new RuntimeException("You have already reviewed this product");
        }

        return reviewRepository.save(review);
    }

    public Review update(Long id, Review reviewDetails) {
        return reviewRepository.findById(id)
                .map(review -> {
                    review.setRating(reviewDetails.getRating());
                    review.setComment(reviewDetails.getComment());
                    return reviewRepository.save(review);
                })
                .orElseThrow(() -> new RuntimeException("Review not found"));
    }

    public void delete(Long id) {
        reviewRepository.deleteById(id);
    }

    public List<Review> findByProductId(Long productId) {
        return reviewRepository.findByProductId(productId);
    }

    public List<Review> findByUserId(Long userId) {
        return reviewRepository.findByUserId(userId);
    }

    public Double getAverageRatingByProductId(Long productId) {
        return reviewRepository.findAverageRatingByProductId(productId);
    }
}