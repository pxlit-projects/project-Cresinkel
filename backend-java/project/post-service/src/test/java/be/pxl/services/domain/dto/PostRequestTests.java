package be.pxl.services.domain.dto;

import be.pxl.services.domain.dto.PostRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PostRequestTests {

    @Test
    void testDefaultConstructor() {
        // Act
        PostRequest postRequest = new PostRequest();

        // Assert
        assertNotNull(postRequest);
        assertNull(postRequest.getTitle());
        assertNull(postRequest.getDescription());
        assertNull(postRequest.getAuthor());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        String title = "Test Title";
        String description = "Test Description";
        String author = "Test Author";

        // Act
        PostRequest postRequest = new PostRequest(title, description, author, false);

        // Assert
        assertNotNull(postRequest);
        assertEquals(title, postRequest.getTitle());
        assertEquals(description, postRequest.getDescription());
        assertEquals(author, postRequest.getAuthor());
    }

    @Test
    void testBuilder() {
        // Arrange
        String title = "Test Title";
        String description = "Test Description";
        String author = "Test Author";

        // Act
        PostRequest postRequest = PostRequest.builder()
                .title(title)
                .description(description)
                .author(author)
                .build();

        // Assert
        assertNotNull(postRequest);
        assertEquals(title, postRequest.getTitle());
        assertEquals(description, postRequest.getDescription());
        assertEquals(author, postRequest.getAuthor());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        PostRequest postRequest = new PostRequest();

        String title = "Test Title";
        String description = "Test Description";
        String author = "Test Author";

        // Act
        postRequest.setTitle(title);
        postRequest.setDescription(description);
        postRequest.setAuthor(author);

        // Assert
        assertEquals(title, postRequest.getTitle());
        assertEquals(description, postRequest.getDescription());
        assertEquals(author, postRequest.getAuthor());
    }
}
