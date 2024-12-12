package be.pxl.services.service;

import be.pxl.services.domain.Review;
import be.pxl.services.domain.dto.ReviewRequest;
import be.pxl.services.domain.dto.ReviewResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IReviewService {

    ReviewResponse getReviewById(Long id);

    Review updateReviewStatus(Long id, ReviewRequest reviewRequest);

    List<ReviewResponse> getReviews();
}
