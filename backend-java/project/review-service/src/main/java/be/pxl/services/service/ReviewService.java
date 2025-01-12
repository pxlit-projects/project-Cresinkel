package be.pxl.services.service;

import be.pxl.services.domain.Review;
import be.pxl.services.domain.dto.NotificationRequest;
import be.pxl.services.domain.dto.ReviewRequest;
import be.pxl.services.domain.dto.ReviewResponse;
import be.pxl.services.openFeign.NotificationClient;
import be.pxl.services.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService implements IReviewService {

    private final ReviewRepository reviewRepository;
    private final NotificationClient notificationClient;
    private final RabbitTemplate rabbitTemplate;
    private static final Logger log = LoggerFactory.getLogger(ReviewService.class);

    @Override
    public ReviewResponse getReviewById(Long id) {
        log.info("Fetching review with ID: {}", id);
        Review review = reviewRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Review not found"));
        ReviewResponse reviewResponse = ReviewResponse.mapToReviewResponse(review);
        log.info("Fetched review: {}", reviewResponse);
        return reviewResponse;
    }

    @RabbitListener(queues = "ReviewQueue")
    public void listenToPostService(ReviewResponse reviewResponse) {
        log.info("Received review message: {}", reviewResponse);
        Optional<Review> optReview = reviewRepository.findById(reviewResponse.getId());
        if (optReview.isEmpty()) {
            log.info("Review not found, creating new review for ID: {}", reviewResponse.getId());
            Review review = Review.builder()
                    .accepted(reviewResponse.isAccepted())
                    .publicationDate(reviewResponse.getPublicationDate())
                    .title(reviewResponse.getTitle())
                    .author(reviewResponse.getAuthor())
                    .lastEditedDate(reviewResponse.getLastEditedDate())
                    .rejectionReason(reviewResponse.getRejectionReason())
                    .description(reviewResponse.getDescription())
                    .id(reviewResponse.getId())
                    .build();
            reviewRepository.save(review);
            log.info("New review saved: {}", review);
        } else {
            log.info("Review already exists with ID: {}", reviewResponse.getId());
        }
    }

    @Override
    public Review updateReviewStatus(Long id, ReviewRequest reviewRequest) {
        log.info("Updating review status for review ID: {}", id);
        Review review = reviewRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Review not found"));
        review.setAccepted(reviewRequest.isAccepted());
        review.setRejectionReason(reviewRequest.getRejectionReason());
        reviewRepository.save(review);

        ReviewResponse reviewResponse = ReviewResponse.mapToReviewResponse(review);
        log.info("Sending updated review to ReviewQueue: {}", reviewResponse);
        rabbitTemplate.convertAndSend("ReviewQueue", reviewResponse);

        NotificationRequest notificationRequest = NotificationRequest.builder()
                .description("Your post '" + review.getTitle() + "' has been " + (reviewRequest.isAccepted() ? "accepted" : "denied"))
                .postId(review.getId())
                .build();
        log.info("Sending notification: {}", notificationRequest);
        notificationClient.sendNotification(notificationRequest);

        reviewRepository.delete(review);
        log.info("Deleted review with ID: {}", id);

        return review;
    }

    @Override
    public List<ReviewResponse> getReviews() {
        log.info("Fetching all reviews");
        List<ReviewResponse> reviews = reviewRepository.findAll().stream()
                .map(ReviewResponse::mapToReviewResponse)
                .toList();
        log.info("Fetched {} reviews", reviews.size());
        return reviews;
    }
}
