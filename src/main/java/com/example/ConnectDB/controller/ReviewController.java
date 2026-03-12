package com.example.ConnectDB.controller;

import com.example.ConnectDB.model.Review;
import com.example.ConnectDB.service.ReviewService;
import com.example.ConnectDB.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody Review review) {
        review.setUser(authService.getCurrentUser());
        Review savedReview = reviewService.save(review);
        return ResponseEntity.ok(savedReview);
    }

    @GetMapping("/my")
    public ResponseEntity<List<Review>> getMyReviews() {
        Long userId = authService.getCurrentUser().getId();
        List<Review> reviews = reviewService.findByUserId(userId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Review>> getProductReviews(@PathVariable Long productId) {
        List<Review> reviews = reviewService.findByProductId(productId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable Long id) {
        return reviewService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Review> updateReview(@PathVariable Long id, @RequestBody Review reviewDetails) {
        try {
            Review updatedReview = reviewService.update(id, reviewDetails);
            return ResponseEntity.ok(updatedReview);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        try {
            reviewService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/product/{productId}/average")
    public ResponseEntity<Double> getProductAverageRating(@PathVariable Long productId) {
        Double averageRating = reviewService.getAverageRatingByProductId(productId);
        return ResponseEntity.ok(averageRating);
    }
}