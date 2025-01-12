package be.pxl.services.controller;

import be.pxl.services.domain.Post;
import be.pxl.services.domain.dto.*;
import be.pxl.services.service.IPostService;
import be.pxl.services.repository.PostRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureMockMvc
class PostControllerTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostRepository postRepository;

    @Container
    private static MySQLContainer mySQLContainer = new MySQLContainer("mysql:5.7.37");

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
    }

    @BeforeEach
    public void setup() {
        postRepository.deleteAll();
    }

    @Test
    void testCreatePost() throws Exception {
        PostRequest postRequest = new PostRequest("Title", "Description", "Author", true);

        mockMvc.perform(post("/api/post")
                        .header("Role", "redacteur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isCreated());

        assertEquals(1, postRepository.findAll().size());
    }

    @Test
    void testCreatePostForbidden() throws Exception {
        PostRequest postRequest = new PostRequest("Title", "Description", "Author", true);

        mockMvc.perform(post("/api/post")
                        .header("Role", "gebruiker")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isForbidden()); // Expect Forbidden

        assertEquals(0, postRepository.findAll().size());
    }

    @Test
    void testGetDrafts() throws Exception {
        postRepository.deleteAll();
        postRepository.save(Post.builder()
                .title("Title 1")
                .description("Description 1")
                .author("Author")
                .isDraft(true)
                .build());
        postRepository.save(Post.builder()
                .title("Title 2")
                .description("Description 2")
                .author("Author")
                .isDraft(true)
                .build());

        mockMvc.perform(post("/api/post/drafts")
                        .header("Role", "gebruiker")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"author\": \"Author\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    void testSendInDraft() throws Exception {
        Post draft = postRepository.save(Post.builder()
                .title("Title")
                .description("Description")
                .author("Author")
                .isDraft(true)
                .build());

        mockMvc.perform(post("/api/post/sendInDraft")
                        .header("Role", "gebruiker")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": " + draft.getId() + "}"))
                .andExpect(status().isAccepted());

        Post updatedPost = postRepository.findById(draft.getId()).get();
        assertEquals(false, updatedPost.isAccepted());
        assertEquals(false, updatedPost.isDraft());
    }

    @Test
    void testEditDraft() throws Exception {
        Post draft = postRepository.save(Post.builder()
                .title("Title")
                .description("Description")
                .author("Author")
                .isDraft(true)
                .build());

        mockMvc.perform(post("/api/post/editDraft")
                        .header("Role", "redacteur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": " + draft.getId() + ", \"title\": \"Updated Title\", \"description\": \"Updated Description\"}"))
                .andExpect(status().isAccepted());

        Post updatedPost = postRepository.findById(draft.getId()).get();
        assertEquals("Updated Title", updatedPost.getTitle());
        assertEquals("Updated Description", updatedPost.getDescription());
    }

    @Test
    void testGetPost() throws Exception {
        Post draft = postRepository.save(Post.builder()
                .title("Title")
                .description("Description")
                .author("Author")
                .isDraft(true)
                .build());

        mockMvc.perform(post("/api/post/draft")
                        .header("Role", "gebruiker")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": " + draft.getId() + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Title"));
    }

    @Test
    void testGetPosts() throws Exception {
        postRepository.save(Post.builder()
                .title("Title 1")
                .description("Description 1")
                .author("Author")
                .isDraft(false)
                .accepted(true)
                .build());
        postRepository.save(Post.builder()
                .title("Title 2")
                .description("Description 2")
                .author("Author")
                .isDraft(false)
                .accepted(true)
                .build());

        mockMvc.perform(get("/api/post/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }
}