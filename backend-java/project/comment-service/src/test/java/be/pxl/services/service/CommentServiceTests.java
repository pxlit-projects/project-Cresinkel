package be.pxl.services.service;

import be.pxl.services.domain.Comment;
import be.pxl.services.domain.dto.CommentRequest;
import be.pxl.services.domain.dto.CommentResponse;
import be.pxl.services.domain.dto.UpdateCommentRequest;
import be.pxl.services.repository.CommentRepository;
import jakarta.ws.rs.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class CommentServiceTests {
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    private CommentRequest commentRequest;
    private UpdateCommentRequest updateCommentRequest;
    private Comment comment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup mock data
        commentRequest = new CommentRequest("This is a test comment", "author1", 1L);
        updateCommentRequest = new UpdateCommentRequest("Updated comment", 1L);
        comment = new Comment(1L, 1L, "This is a test comment", "author1");
    }

    @Test
    void getCommentsForPost_ShouldReturnComments() {
        // Given
        List<Comment> comments = List.of(comment);
        when(commentRepository.findByPostId(1L)).thenReturn(comments);

        // When
        List<CommentResponse> commentResponses = commentService.getCommentsForPost(1L);

        // Then
        assertNotNull(commentResponses);
        assertEquals(1, commentResponses.size());
        assertEquals("This is a test comment", commentResponses.get(0).getDescription());
    }

    @Test
    void createComment_ShouldSaveNewComment() {
        // Given
        Comment savedComment = Comment.builder()
                .description(commentRequest.getDescription())
                .author(commentRequest.getAuthor())
                .postId(commentRequest.getPostId())
                .build();

        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        // When
        commentService.createComment(commentRequest);

        // Then
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void deleteComment_ShouldDeleteComment_WhenCommentExistsAndAuthorMatches() {
        // Given
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        // When
        commentService.deleteComment(1L, "author1");

        // Then
        verify(commentRepository, times(1)).deleteById(1L);
    }

    @Test
    void updateComment_ShouldUpdateComment_WhenCommentExistsAndAuthorMatches() {
        // Given
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        // When
        commentService.updateComment(updateCommentRequest, "author1");

        // Then
        verify(commentRepository, times(1)).save(any(Comment.class));
        assertEquals("Updated comment", comment.getDescription());
    }
}
