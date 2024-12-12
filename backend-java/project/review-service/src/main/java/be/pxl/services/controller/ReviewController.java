package be.pxl.services.controller;

import be.pxl.services.domain.Review;
import be.pxl.services.domain.dto.ReviewRequest;
import be.pxl.services.domain.dto.ReviewResponse;
import be.pxl.services.service.IReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ReviewController {
    private final IReviewService reviewService;

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.getReviewById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Review> updateReviewStatus(@PathVariable Long id, @RequestBody ReviewRequest reviewRequest) {
        System.out.println("Updating review: ");
        return ResponseEntity.ok(reviewService.updateReviewStatus(id, reviewRequest));
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getReviews() {
        return ResponseEntity.ok(reviewService.getReviews());
    }
}
