package be.pxl.services.domain.dto;

import be.pxl.services.domain.Post;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class PostResponseTests {

    @Test
    void testDefaultConstructor() {
        // Act
        PostResponse postResponse = new PostResponse();

        // Assert
        assertNotNull(postResponse);
        assertNull(postResponse.getId());
        assertNull(postResponse.getTitle());
        assertNull(postResponse.getDescription());
        assertNull(postResponse.getAuthor());
        assertNull(postResponse.getPublicationDate());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        Long id = 1L;
        String title = "Test Title";
        String description = "Test Description";
        String author = "Test Author";
        LocalDateTime publicationDate = LocalDateTime.now();
        LocalDateTime lastEditedDate = LocalDateTime.now();

        // Act
        PostResponse postResponse = new PostResponse(id, title, description, author, publicationDate, lastEditedDate, false, true, "");

        // Assert
        assertNotNull(postResponse);
        assertEquals(id, postResponse.getId());
        assertEquals(title, postResponse.getTitle());
        assertEquals(description, postResponse.getDescription());
        assertEquals(author, postResponse.getAuthor());
        assertEquals(publicationDate, postResponse.getPublicationDate());
    }

    @Test
    void testBuilder() {
        // Arrange
        String title = "Test Title";
        String description = "Test Description";
        String author = "Test Author";
        LocalDateTime publicationDate = LocalDateTime.now();

        // Act
        PostResponse postResponse = PostResponse.builder()
                .title(title)
                .description(description)
                .author(author)
                .publicationDate(publicationDate)
                .build();

        // Assert
        assertNotNull(postResponse);
        assertEquals(title, postResponse.getTitle());
        assertEquals(description, postResponse.getDescription());
        assertEquals(author, postResponse.getAuthor());
        assertEquals(publicationDate, postResponse.getPublicationDate());
    }

    @Test
    void testMapToPostResponse() {
        // Arrange
        Post post = new Post();
        post.setId(1L);
        post.setTitle("Test Title");
        post.setDescription("Test Description");
        post.setAuthor("Test Author");
        post.setPublicationDate(LocalDateTime.now());

        // Act
        PostResponse postResponse = PostResponse.mapToPostResponse(post);

        // Assert
        assertNotNull(postResponse);
        assertEquals(post.getId(), postResponse.getId());
        assertEquals(post.getTitle(), postResponse.getTitle());
        assertEquals(post.getDescription(), postResponse.getDescription());
        assertEquals(post.getAuthor(), postResponse.getAuthor());
        assertEquals(post.getPublicationDate(), postResponse.getPublicationDate());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        PostResponse postResponse = new PostResponse();

        String title = "Test Title";
        String description = "Test Description";
        String author = "Test Author";
        LocalDateTime publicationDate = LocalDateTime.now();

        // Act
        postResponse.setTitle(title);
        postResponse.setDescription(description);
        postResponse.setAuthor(author);
        postResponse.setPublicationDate(publicationDate);

        // Assert
        assertEquals(title, postResponse.getTitle());
        assertEquals(description, postResponse.getDescription());
        assertEquals(author, postResponse.getAuthor());
        assertEquals(publicationDate, postResponse.getPublicationDate());
    }
}
