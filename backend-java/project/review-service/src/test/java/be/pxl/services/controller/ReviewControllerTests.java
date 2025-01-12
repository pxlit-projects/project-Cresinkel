package be.pxl.services.controller;

import be.pxl.services.domain.Review;
import be.pxl.services.domain.dto.ReviewRequest;
import be.pxl.services.domain.dto.ReviewResponse;
import be.pxl.services.openFeign.NotificationClient;
import be.pxl.services.repository.ReviewRepository;
import be.pxl.services.service.IReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureMockMvc
public class ReviewControllerTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReviewRepository reviewRepository;

    @MockBean
    private NotificationClient notificationClient;

    @Container
    private static MySQLContainer mySQLContainer = new MySQLContainer("mysql:5.7.37");

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
    }

    @BeforeEach
    public void setUp() {
        reviewRepository.deleteAll();
        doNothing().when(notificationClient).sendNotification(any());
    }

    @Test
    public void testGetReviewById() throws Exception {
        Review savedReview = reviewRepository.save(new Review(1L, "Title", "Description", "Author", null, null, false, null));

        mockMvc.perform(get("/api/review/{id}", savedReview.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Title"))
                .andExpect(jsonPath("$.description").value("Description"));
    }

    @Test
    public void testUpdateReviewStatus_AdminRole() throws Exception {
        Review savedReview = reviewRepository.save(new Review(1L, "Title", "Description", "Author", null, null, false, null));
        ReviewRequest reviewRequest = new ReviewRequest(true, null);

        mockMvc.perform(put("/api/review/{id}", savedReview.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(reviewRequest))
                        .header("Role", "admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Title"))
                .andExpect(jsonPath("$.accepted").value(true));
    }

    @Test
    public void testUpdateReviewStatus_NotAdminRole() throws Exception {
        Review savedReview = reviewRepository.save(new Review(1L, "Title", "Description", "Author", null, null, false, null));
        ReviewRequest reviewRequest = new ReviewRequest(true, null);

        mockMvc.perform(put("/api/review/{id}", savedReview.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(reviewRequest))
                        .header("Role", "user"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetReviews_AdminRole() throws Exception {
        Review review1 = reviewRepository.save(new Review(1L, "Title 1", "Description 1", "Author", null, null, false, null));
        Review review2 = reviewRepository.save(new Review(2L, "Title 2", "Description 2", "Author", null, null, false, null));

        mockMvc.perform(get("/api/review")
                        .header("Role", "admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].title").value("Title 1"))
                .andExpect(jsonPath("$[1].title").value("Title 2"));
    }

    @Test
    public void testGetReviews_NotAdminRole() throws Exception {
        mockMvc.perform(get("/api/review")
                        .header("Role", "user"))
                .andExpect(status().isForbidden());
    }
}
