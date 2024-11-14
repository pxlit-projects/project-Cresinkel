package be.pxl.services.controller;

import be.pxl.services.domain.dto.PostRequest;
import be.pxl.services.service.IPostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@WebMvcTest(PostController.class)
class PostControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IPostService postService; // Mocking the service

    private PostRequest postRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
        postRequest = new PostRequest();
        postRequest.setTitle("Test Title");
        postRequest.setDescription("Test Content");
        postRequest.setAuthor("Author");
    }

    @Test
    void testCreatePost_Success() throws Exception {
        // Given: a valid PostRequest object, and the service is expected to do nothing
        doNothing().when(postService).createPost(any(PostRequest.class));

        // When: we send a POST request to create a post
        mockMvc.perform(post("/api/post")
                        .contentType("application/json")
                        .content("{\"title\":\"Test Title\",\"content\":\"Test Content\"}"))
                // Then: the response status should be CREATED (201)
                .andExpect(status().is(HttpStatus.CREATED.value()));

        // Verify that the service method was called once with the postRequest object
        verify(postService, times(1)).createPost(any(PostRequest.class));
    }

    @Test
    void testCreatePost_MissingTitle() throws Exception {
        // Simulate invalid PostRequest (e.g., missing title)
        String invalidRequestJson = "{\"title\":null,\"description\":\"Test Description\",\"author\":\"Test Author\"}";

        // When: we send a POST request with missing or invalid data (title is null)
        mockMvc.perform(post("/api/post")
                        .contentType("application/json")
                        .content(invalidRequestJson))
                // Then: the response status should be BAD_REQUEST (400) due to missing title
                .andExpect(status().isBadRequest());

        // Verify that the service method was not called
        verify(postService, times(0)).createPost(any(PostRequest.class));
    }
}
