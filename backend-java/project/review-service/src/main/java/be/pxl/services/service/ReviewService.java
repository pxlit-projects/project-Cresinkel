package be.pxl.services.service;

import be.pxl.services.domain.Review;
import be.pxl.services.domain.dto.NotificationRequest;
import be.pxl.services.domain.dto.ReviewRequest;
import be.pxl.services.domain.dto.ReviewResponse;
import be.pxl.services.openFeign.NotificationClient;
import be.pxl.services.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
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

    @Override
    public ReviewResponse getReviewById(Long id) {
        Review review = reviewRepository.findById(id).orElseThrow();
        return ReviewResponse.mapToReviewResponse(review);
    }

    @RabbitListener(queues = "ReviewQueue")
    public void listenToPostService(ReviewResponse reviewResponse) {
        System.out.println("review got message: " + reviewResponse);
        Optional<Review> optReview = reviewRepository.findById(reviewResponse.getId());
        if (optReview.isEmpty()) {
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
            System.out.println("Review saved");
        }
    }

    @Override
    public Review updateReviewStatus(Long id, ReviewRequest reviewRequest) {
        Review review = reviewRepository.findById(id).orElseThrow();
        review.setAccepted(reviewRequest.isAccepted());
        review.setRejectionReason(reviewRequest.getRejectionReason());
        reviewRepository.save(review);
        ReviewResponse reviewResponse = ReviewResponse.mapToReviewResponse(review);
        rabbitTemplate.convertAndSend("ReviewQueue", reviewResponse);
        NotificationRequest notificationRequest = NotificationRequest.builder()
                .description("Your post " + review.getTitle() + " has been " + (reviewRequest.isAccepted() ? "accepted" : "denied"))
                .postId(review.getId())
                .build();
        notificationClient.sendNotification(notificationRequest);
        reviewRepository.delete(review);
        return review;
    }

    @Override
    public List<ReviewResponse> getReviews() {
        return reviewRepository.findAll().stream()
                .map(ReviewResponse::mapToReviewResponse)
                .toList();
    }
}
