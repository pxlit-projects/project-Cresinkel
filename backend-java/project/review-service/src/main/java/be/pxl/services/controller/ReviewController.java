package be.pxl.services.controller;

import be.pxl.services.domain.Review;
import be.pxl.services.domain.dto.ReviewRequest;
import be.pxl.services.domain.dto.ReviewResponse;
import be.pxl.services.service.IReviewService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ReviewController {
    private final IReviewService reviewService;
    private static final Logger log = LoggerFactory.getLogger(ReviewController.class);

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> getPostById(@PathVariable Long id) {
        log.info("Fetching review with ID: {}", id);
        ReviewResponse reviewResponse = reviewService.getReviewById(id);
        log.info("Fetched review: {}", reviewResponse);
        return ResponseEntity.ok(reviewResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Review> updateReviewStatus(
            @PathVariable Long id,
            @RequestBody ReviewRequest reviewRequest,
            @RequestHeader("Role") String role) {
        log.info("Request to update review status for review ID: {} by role: {}", id, role);
        validateAdminRole(role);
        Review updatedReview = reviewService.updateReviewStatus(id, reviewRequest);
        log.info("Review updated: {}", updatedReview);
        return ResponseEntity.ok(updatedReview);
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getReviews(@RequestHeader("Role") String role) {
        log.info("Request to fetch all reviews by role: {}", role);
        validateAdminRole(role);
        List<ReviewResponse> reviews = reviewService.getReviews();
        log.info("Fetched {} reviews.", reviews.size());
        return ResponseEntity.ok(reviews);
    }

    private void validateAdminRole(String role) {
        if (!"admin".equalsIgnoreCase(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied: Admins only.");
        }
    }
}
