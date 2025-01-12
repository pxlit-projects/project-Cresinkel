package be.pxl.services.controller;

import be.pxl.services.domain.Review;
import be.pxl.services.domain.dto.ReviewRequest;
import be.pxl.services.domain.dto.ReviewResponse;
import be.pxl.services.service.IReviewService;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.getReviewById(id));
    }


    @PutMapping("/{id}")
    public ResponseEntity<Review> updateReviewStatus(
            @PathVariable Long id,
            @RequestBody ReviewRequest reviewRequest,
            @RequestHeader("Role") String role) {
        validateAdminRole(role);
        System.out.println("Updating review: ");
        return ResponseEntity.ok(reviewService.updateReviewStatus(id, reviewRequest));
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getReviews(@RequestHeader("Role") String role) {
        validateAdminRole(role);
        return ResponseEntity.ok(reviewService.getReviews());
    }

    private void validateAdminRole(String role) {
        if (!"admin".equalsIgnoreCase(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied: Admins only.");
        }
    }
}
