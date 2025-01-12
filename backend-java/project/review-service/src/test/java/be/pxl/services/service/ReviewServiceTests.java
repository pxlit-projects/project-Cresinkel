package be.pxl.services.service;

import be.pxl.services.domain.Review;
import be.pxl.services.domain.dto.NotificationRequest;
import be.pxl.services.domain.dto.ReviewRequest;
import be.pxl.services.domain.dto.ReviewResponse;
import be.pxl.services.openFeign.NotificationClient;
import be.pxl.services.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTests {
    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private NotificationClient notificationClient;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private ReviewService reviewService;

    private Review mockReview;

    @BeforeEach
    public void setUp() {
        mockReview = new Review(1L, "Title", "Description", "Author", null, null, false, null);
    }

    @Test
    public void testGetReviewById_Success() {
        Long reviewId = 1L;
        ReviewResponse reviewResponse = ReviewResponse.builder()
                .id(1L)
                .title("Title")
                .description("Description")
                .author("Author")
                .build();

        // Mock the repository to return the mock review
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(mockReview));

        // Call the service method
        ReviewResponse result = reviewService.getReviewById(reviewId);

        // Verify the result
        assertNotNull(result);
        assertEquals("Title", result.getTitle());
        verify(reviewRepository, times(1)).findById(reviewId);
    }

    @Test
    public void testGetReviewById_NotFound() {
        Long reviewId = 999L;

        // Mock the repository to return empty
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        // Call the service method and expect an exception
        assertThrows(IllegalArgumentException.class, () -> reviewService.getReviewById(reviewId));

        verify(reviewRepository, times(1)).findById(reviewId);
    }

    @Test
    public void testListenToPostService_NewReview() {
        ReviewResponse reviewResponse = ReviewResponse.builder()
                .id(1L)
                .title("Title")
                .description("Description")
                .author("Author")
                .build();

        // Mock the repository to return empty for the given ID
        when(reviewRepository.findById(reviewResponse.getId())).thenReturn(Optional.empty());

        // Call the service method
        reviewService.listenToPostService(reviewResponse);

        // Verify that a new review is saved
        verify(reviewRepository, times(1)).save(Mockito.any(Review.class));
    }

    @Test
    public void testListenToPostService_ExistingReview() {
        ReviewResponse reviewResponse = ReviewResponse.builder()
                .id(1L)
                .title("Title")
                .description("Description")
                .author("Author")
                .build();

        // Mock the repository to return the existing review
        when(reviewRepository.findById(reviewResponse.getId())).thenReturn(Optional.of(mockReview));

        // Call the service method
        reviewService.listenToPostService(reviewResponse);

        // Verify that no new review is saved
        verify(reviewRepository, times(0)).save(Mockito.any(Review.class));
    }

    @Test
    public void testUpdateReviewStatus() {
        Long reviewId = 1L;
        ReviewRequest reviewRequest = new ReviewRequest(true, "No issues");
        Review updatedReview = new Review(1L, "Updated Title", "Updated Description", "Author", null, null, true, "No issues");

        // Mock the repository to return the existing review and save the updated one
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(mockReview));
        when(reviewRepository.save(Mockito.any(Review.class))).thenReturn(updatedReview);

        // Call the service method
        Review result = reviewService.updateReviewStatus(reviewId, reviewRequest);

        // Verify the result
        assertNotNull(result);
        assertTrue(result.isAccepted());
        verify(reviewRepository, times(1)).findById(reviewId);
        verify(reviewRepository, times(1)).save(Mockito.any(Review.class));
        verify(rabbitTemplate, times(1)).convertAndSend(eq("ReviewQueue"), Mockito.any(ReviewResponse.class));
        verify(notificationClient, times(1)).sendNotification(Mockito.any(NotificationRequest.class));
        verify(reviewRepository, times(1)).delete(mockReview);
    }

    @Test
    public void testGetReviews() {
        ReviewResponse reviewResponse1 = new ReviewResponse(1L, "Title 1", "Description 1", "Author", null, null, false, null);
        ReviewResponse reviewResponse2 = new ReviewResponse(2L, "Title 2", "Description 2", "Author", null, null, false, null);
        List<ReviewResponse> reviews = Arrays.asList(reviewResponse1, reviewResponse2);

        // Mock the repository to return a list of reviews
        when(reviewRepository.findAll()).thenReturn(Arrays.asList(mockReview));

        // Call the service method
        List<ReviewResponse> result = reviewService.getReviews();

        // Verify the result
        assertNotNull(result);
        assertEquals(1, result.size());  // We mocked only one review
        verify(reviewRepository, times(1)).findAll();
    }
}
