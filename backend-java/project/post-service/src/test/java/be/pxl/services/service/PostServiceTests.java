package be.pxl.services.service;

import be.pxl.services.domain.Post;
import be.pxl.services.domain.dto.*;
import be.pxl.services.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class PostServiceTests {
    @Mock
    private PostRepository postRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetDrafts() {
        // Given a drafts request and a list of drafts in the repository
        DraftsRequest draftsRequest = new DraftsRequest("Author");
        Post post = new Post(1L, "Draft Title", "Draft Description", "Author", LocalDateTime.now(), null, true, false, null);
        when(postRepository.findAll()).thenReturn(List.of(post));

        // When we fetch the drafts for the given author
        ResponseEntity<List<PostResponse>> response = postService.getDrafts(draftsRequest);

        // Verify that the correct list is returned
        assertNotNull(response);
        assertEquals(1, response.getBody().size());
        assertTrue(response.getBody().get(0).isDraft());
        assertEquals("Draft Title", response.getBody().get(0).getTitle());
    }

    @Test
    void testSendForReview() {
        // Given a draft request
        DraftRequest draftRequest = new DraftRequest(1L);
        Post post = new Post(1L, "Post title", "Post description", "Author", LocalDateTime.now(), null, true, false, null);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        // When sending the draft for review
        postService.sendForReview(draftRequest);

        // Verify that the post's draft status is updated to false
        assertFalse(post.isDraft());
        verify(postRepository, times(1)).save(post);

        // Verify that a review message is sent to the queue
        verify(rabbitTemplate, times(1)).convertAndSend(eq("ReviewQueue"), any(ReviewResponse.class));
    }

    @Test
    void testGetPosts() {
        // Given some posts
        Post post = new Post(1L, "Post title", "Post description", "Author", LocalDateTime.now(), LocalDateTime.now(), false, true, null);
        when(postRepository.findAll()).thenReturn(List.of(post));

        // When fetching posts
        ResponseEntity<List<PostResponse>> response = postService.getPosts();

        // Verify that the correct list is returned
        assertNotNull(response);
        assertEquals(1, response.getBody().size());
        assertFalse(response.getBody().get(0).isDraft());
        assertTrue(response.getBody().get(0).isAccepted());
    }

    @Test
    void testGetDraft() {
        // Given a draft request and a draft post
        DraftRequest draftRequest = new DraftRequest(1L);
        Post post = new Post(1L, "Post title", "Post description", "Author", LocalDateTime.now(), null, true, false, null);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        // When fetching the draft
        ResponseEntity<PostResponse> response = postService.getDraft(draftRequest);

        // Verify that the draft is returned correctly
        assertNotNull(response);
        assertTrue(response.getBody().isDraft());
        assertEquals("Post title", response.getBody().getTitle());
    }

    @Test
    void testEditDraft() {
        // Given a draft request and a draft post
        EditDraftRequest editDraftRequest = new EditDraftRequest(1L, "New title", "New description");
        Post post = new Post(1L, "Old title", "Old description", "Author", LocalDateTime.now(), null, true, false, null);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        // When editing the draft
        postService.editDraft(editDraftRequest);

        // Verify that the post is updated
        assertEquals("New title", post.getTitle());
        assertEquals("New description", post.getDescription());
        assertNotNull(post.getLastEditedDate());
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void testGetUserOfPost() {
        // Given a post and its ID
        Post post = new Post(1L, "Post title", "Post description", "Author", LocalDateTime.now(), null, false, false, null);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        // When fetching the author
        String author = postService.getUserOfPost(1L);

        // Verify that the correct author is returned
        assertEquals("Author", author);
    }

    @Test
    void testSendForAcceptation() {
        // Given a valid post ID that exists in the repository
        Long postId = 1L;
        Post post = new Post(postId, "Post title", "Post description", "Author", LocalDateTime.now(), LocalDateTime.now(), false, false, null);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // When sending the post for acceptation
        postService.sendForAcceptation(postId);

        // Verify that the post is sent to the review queue
        verify(rabbitTemplate, times(1)).convertAndSend(eq("ReviewQueue"), any(ReviewResponse.class));
    }

    @Test
    void testGetReviewAccepted() {
        // Given a review response with accepted status
        ReviewResponse reviewResponse = new ReviewResponse(1L, "Post title", "Post description", "Author", LocalDateTime.now(), LocalDateTime.now(), true, null);
        Post post = new Post(1L, "Post title", "Post description", "Author", LocalDateTime.now(), LocalDateTime.now(), false, false, null);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        // When processing the review response
        postService.getReview(reviewResponse);

        // Verify that the post is updated with the acceptance status
        assertTrue(post.isAccepted());
        assertNull(post.getRejectionReason());
        assertNotNull(post.getPublicationDate());
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void testGetReviewRejected() {
        // Given a review response with rejected status
        ReviewResponse reviewResponse = new ReviewResponse(1L, "Post title", "Post description", "Author", LocalDateTime.now(), LocalDateTime.now(), false, "Rejection reason");
        Post post = new Post(1L, "Post title", "Post description", "Author", LocalDateTime.now(), LocalDateTime.now(), false, false, null);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        // When processing the review response
        postService.getReview(reviewResponse);

        // Verify that the post is updated with the rejection status
        assertFalse(post.isAccepted());
        assertEquals("Rejection reason", post.getRejectionReason());
        assertTrue(post.isDraft());  // The post is reverted to draft
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void testGetDraftNotFound() {
        // Given a draft request with an ID that does not exist in the repository
        DraftRequest draftRequest = new DraftRequest(999L); // ID that doesn't exist
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        // When fetching the draft
        ResponseEntity<PostResponse> response = postService.getDraft(draftRequest);

        // Verify that the response is null (or handle it appropriately based on your implementation)
        assertNull(response);
    }

    @Test
    void testEditDraftNotFound() {
        // Given an edit draft request and a post ID that doesn't exist
        EditDraftRequest editDraftRequest = new EditDraftRequest(999L, "New title", "New description");
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        // When editing the draft
        postService.editDraft(editDraftRequest);

        // Verify that the repository's save method is not called since the post was not found
        verify(postRepository, never()).save(any(Post.class));
    }
}
