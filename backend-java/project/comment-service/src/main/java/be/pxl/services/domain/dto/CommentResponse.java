package be.pxl.services.domain.dto;

import be.pxl.services.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private Long commentId;
    private Long postId;
    private String description;
    private String author;

    public static CommentResponse mapToCommentResponse(Comment comment) {
        return CommentResponse.builder()
                .commentId(comment.getId())
                .postId(comment.getPostId())
                .author(comment.getAuthor())
                .description(comment.getDescription())
                .build();
    }
}
