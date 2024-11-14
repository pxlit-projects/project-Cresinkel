package be.pxl.services.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class PostTests {

    @Test
    void testPostConstructor() {
        // Arrange
        Long id = 1L;
        String title = "Test Title";
        String description = "Test Description";
        String author = "Test Author";
        LocalDateTime publicationDate = LocalDateTime.now();

        // Act
        Post post = new Post(id, title, description, author, publicationDate);

        // Assert
        assertNotNull(post);
        assertEquals(id, post.getId());
        assertEquals(title, post.getTitle());
        assertEquals(description, post.getDescription());
        assertEquals(author, post.getAuthor());
        assertEquals(publicationDate, post.getPublicationDate());
    }

    @Test
    void testPostBuilder() {
        // Arrange
        String title = "Test Title";
        String description = "Test Description";
        String author = "Test Author";
        LocalDateTime publicationDate = LocalDateTime.now();

        // Act
        Post post = Post.builder()
                .title(title)
                .description(description)
                .author(author)
                .publicationDate(publicationDate)
                .build();

        // Assert
        assertNotNull(post);
        assertEquals(title, post.getTitle());
        assertEquals(description, post.getDescription());
        assertEquals(author, post.getAuthor());
        assertEquals(publicationDate, post.getPublicationDate());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        Post post1 = Post.builder()
                .id(1L)
                .title("Test Title")
                .description("Test Description")
                .author("Test Author")
                .publicationDate(LocalDateTime.now())
                .build();

        Post post2 = Post.builder()
                .id(1L)
                .title("Test Title")
                .description("Test Description")
                .author("Test Author")
                .publicationDate(post1.getPublicationDate())  // Same publication date
                .build();

        Post post3 = Post.builder()
                .id(2L)
                .title("Different Title")
                .description("Different Description")
                .author("Different Author")
                .publicationDate(LocalDateTime.now())
                .build();

        // Assert: post1 and post2 should be equal (same ID and same content)
        assertEquals(post1, post2);
        assertEquals(post1.hashCode(), post2.hashCode());

        // Assert: post1 and post3 should not be equal (different ID)
        assertNotEquals(post1, post3);
        assertNotEquals(post1.hashCode(), post3.hashCode());
    }

    @Test
    void testDefaultConstructor() {
        // Act
        Post post = new Post();

        // Assert
        assertNotNull(post);
        assertNull(post.getId());
        assertNull(post.getTitle());
        assertNull(post.getDescription());
        assertNull(post.getAuthor());
        assertNull(post.getPublicationDate());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        Post post = new Post();

        String title = "Test Title";
        String description = "Test Description";
        String author = "Test Author";
        LocalDateTime publicationDate = LocalDateTime.now();

        // Act
        post.setTitle(title);
        post.setDescription(description);
        post.setAuthor(author);
        post.setPublicationDate(publicationDate);

        // Assert
        assertEquals(title, post.getTitle());
        assertEquals(description, post.getDescription());
        assertEquals(author, post.getAuthor());
        assertEquals(publicationDate, post.getPublicationDate());
    }
}