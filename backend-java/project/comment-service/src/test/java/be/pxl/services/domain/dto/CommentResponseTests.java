package be.pxl.services.domain.dto;

import be.pxl.services.domain.Comment;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CommentResponseTests {
    @Test
    void mapToCommentResponse_ShouldMapCommentToCommentResponse() {
        // Given
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setPostId(100L);
        comment.setAuthor("John Doe");
        comment.setDescription("This is a comment");

        // When
        CommentResponse commentResponse = CommentResponse.mapToCommentResponse(comment);

        // Then
        assertNotNull(commentResponse);
        assertEquals(comment.getId(), commentResponse.getCommentId());
        assertEquals(comment.getPostId(), commentResponse.getPostId());
        assertEquals(comment.getAuthor(), commentResponse.getAuthor());
        assertEquals(comment.getDescription(), commentResponse.getDescription());
    }

    @Test
    void mapToCommentResponse_ShouldReturnNullWhenCommentIsNull() {
        // Given
        Comment comment = null;

        // When
        CommentResponse commentResponse = CommentResponse.mapToCommentResponse(comment);

        // Then
        assertNull(commentResponse);
    }
}
