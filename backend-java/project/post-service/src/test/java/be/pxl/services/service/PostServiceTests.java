package be.pxl.services.service;

import be.pxl.services.domain.Post;
import be.pxl.services.domain.dto.PostRequest;
import be.pxl.services.domain.dto.PostResponse;
import be.pxl.services.repository.PostRepository;
import be.pxl.services.service.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTests {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    private PostRequest postRequest;

    @BeforeEach
    void setUp() {
        postRequest = new PostRequest();
        postRequest.setTitle("Test Title");
        postRequest.setDescription("Test Description");
        postRequest.setAuthor("Test Author");
    }

    @Test
    void testCreatePost() {
        // Arrange
        Post post = new Post();
        post.setTitle(postRequest.getTitle());
        post.setDescription(postRequest.getDescription());
        post.setAuthor(postRequest.getAuthor());
        post.setPublicationDate(LocalDateTime.now());

        // Act
        postService.createPost(postRequest);

        // Assert
        verify(postRepository, times(1)).save(any(Post.class)); // Verify that the save method was called exactly once
    }

    @Test
    void testCreatePost_savesPostCorrectly() {
        // Arrange
        PostRequest request = new PostRequest();
        request.setTitle("Title");
        request.setDescription("Description");
        request.setAuthor("Author");

        // Act
        postService.createPost(request);

        // Assert
        verify(postRepository, times(1)).save(Mockito.any(Post.class));
    }

    @Test
    void testCreatePost_withNullValues() {
        // Arrange
        PostRequest request = new PostRequest();
        request.setTitle(null);
        request.setDescription(null);
        request.setAuthor(null);

        // Act
        postService.createPost(request);

        // Assert
        verify(postRepository, times(1)).save(Mockito.any(Post.class));
    }

    @Test
    void testCreatePost_Validations() {
        // Arrange
        PostRequest request = new PostRequest();
        request.setTitle("");
        request.setDescription("Description");
        request.setAuthor("Author");

        // Act
        postService.createPost(request);

        // Assert
        verify(postRepository, times(1)).save(Mockito.any(Post.class)); // Still save even if title is empty
    }

}
