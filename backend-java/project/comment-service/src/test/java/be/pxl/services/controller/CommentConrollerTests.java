package be.pxl.services.controller;

import be.pxl.services.domain.Comment;
import be.pxl.services.domain.dto.CommentRequest;
import be.pxl.services.domain.dto.CommentResponse;
import be.pxl.services.domain.dto.UpdateCommentRequest;
import be.pxl.services.repository.CommentRepository;
import be.pxl.services.service.ICommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureMockMvc
public class CommentConrollerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CommentRepository commentRepository;

    @Container
    private static MySQLContainer mySQLContainer = new MySQLContainer("mysql:5.7.37");

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
    }

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
    }

    @Test
    void createPost_ShouldCreateComment_WhenValidRequest() throws Exception {
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setPostId(1L);
        commentRequest.setAuthor("author1");
        commentRequest.setDescription("This is a comment");

        mockMvc.perform(post("/api/comment")
                        .header("Role", "gebruiker")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void createPost_ShouldReturnForbidden_WhenInvalidRole() throws Exception {
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setPostId(1L);
        commentRequest.setAuthor("author1");
        commentRequest.setDescription("This is a comment");

        mockMvc.perform(post("/api/comment")
                        .header("Role", "admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getCommentsByPostId_ShouldReturnComments_WhenPostExists() throws Exception {
        Comment comment = new Comment();
        comment.setPostId(1L);
        comment.setAuthor("author1");
        comment.setDescription("This is a comment");
        commentRepository.save(comment);

        mockMvc.perform(get("/api/comment/{postId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("This is a comment"));
    }

    @Test
    void updateComment_ShouldUpdateComment_WhenValidRequest() throws Exception {
        Comment comment = new Comment();
        comment.setPostId(1L);
        comment.setAuthor("author1");
        comment.setDescription("Original description");
        Comment savedComment = commentRepository.save(comment);

        UpdateCommentRequest updateRequest = new UpdateCommentRequest();
        updateRequest.setCommentId(savedComment.getId());
        updateRequest.setDescription("Updated comment");

        mockMvc.perform(put("/api/comment/{author}", "author1")
                        .header("Role", "redacteur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

        Comment updatedComment = commentRepository.findById(savedComment.getId()).orElseThrow();
        assert updatedComment.getDescription().equals("Updated comment");
    }

    @Test
    void deleteComment_ShouldDeleteComment_WhenValidRequest() throws Exception {
        Comment comment = new Comment();
        comment.setPostId(1L);
        comment.setAuthor("author1");
        comment.setDescription("This is a comment");
        Comment savedComment = commentRepository.save(comment);

        mockMvc.perform(delete("/api/comment/{commentId}/{author}", savedComment.getId(), "author1")
                        .header("Role", "gebruiker"))
                .andExpect(status().isOk());

        boolean exists = commentRepository.existsById(savedComment.getId());
        assert !exists;
    }
}
