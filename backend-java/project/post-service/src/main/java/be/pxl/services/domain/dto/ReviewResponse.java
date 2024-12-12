package be.pxl.services.domain.dto;

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
}
