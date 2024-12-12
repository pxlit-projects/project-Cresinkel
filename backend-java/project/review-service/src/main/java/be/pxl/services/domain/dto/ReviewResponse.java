package be.pxl.services.domain.dto;

import be.pxl.services.domain.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Long id;
    private String title;
    private String description;
    private String author;
    private LocalDateTime publicationDate;
    private LocalDateTime lastEditedDate;
    private boolean accepted;
    private String rejectionReason;

    public static ReviewResponse mapToReviewResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .title(review.getTitle())
                .description(review.getDescription())
                .author(review.getAuthor())
                .publicationDate(review.getPublicationDate())
                .lastEditedDate(review.getLastEditedDate())
                .accepted(review.isAccepted())
                .rejectionReason(review.getRejectionReason())
                .build();
    }
}
