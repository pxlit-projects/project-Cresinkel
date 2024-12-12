package be.pxl.services.domain.dto;

import be.pxl.services.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
    private Long id;
    private String title;
    private String description;
    private String author;
    private LocalDateTime publicationDate;
    private LocalDateTime lastEditedDate;
    private boolean isDraft;
    private boolean accepted;
    private String rejectionReason;

    public static PostResponse mapToPostResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .description(post.getDescription())
                .author(post.getAuthor())
                .publicationDate(post.getPublicationDate())
                .lastEditedDate(post.getLastEditedDate())
                .isDraft(post.isDraft())
                .accepted(post.isAccepted())
                .rejectionReason(post.getRejectionReason())
                .build();
    }
}
