package com.example.ecommerce.controller;

import com.example.ecommerce.model.Review;
import com.example.ecommerce.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @GetMapping("/{productId}/reviews")
    public ResponseEntity<List<Review>> getProductReviews(@PathVariable String productId) {
        return ResponseEntity.ok(reviewRepository.findByProductIdOrderByCreatedAtDesc(productId));
    }

    @org.springframework.security.access.prepost.PreAuthorize("isAuthenticated()")
    @PostMapping("/{productId}/reviews")
    public ResponseEntity<Review> addReview(@PathVariable String productId, @RequestBody Review review) {
        review.setProductId(productId);
        review.setCreatedAt(LocalDateTime.now());
        Review saved = reviewRepository.save(review);
        return ResponseEntity.ok(saved);
    }
}
