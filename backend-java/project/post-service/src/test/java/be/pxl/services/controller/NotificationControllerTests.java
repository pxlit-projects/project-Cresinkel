package be.pxl.services.controller;

import be.pxl.services.domain.Post;
import be.pxl.services.domain.dto.NotificationRequest;
import be.pxl.services.domain.dto.NotificationResponse;
import be.pxl.services.domain.Notification;
import be.pxl.services.repository.NotificationRepository;
import be.pxl.services.repository.PostRepository;
import be.pxl.services.service.INotificationService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureMockMvc
public class NotificationControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NotificationRepository notificationRepository;

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
    void setUp() {
        notificationRepository.deleteAll();
        postRepository.deleteAll();
    }

    @Test
    void testCreateNotification() throws Exception {
        postRepository.save(Post.builder()
                .title("Title 1")
                .description("Description 1")
                .author("Author")
                .isDraft(true)
                .build());

        NotificationRequest notificationRequest = new NotificationRequest("New notification", 1L);

        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificationRequest)))
                .andExpect(status().isOk());

        assertEquals(1, notificationRepository.findAll().size());
    }

    @Test
    void testGetNotifications() throws Exception {
        Notification notification1 = notificationRepository.save(Notification.builder()
                .description("Notification 1")
                .user("User1")
                .postId(1L)
                .build());

        Notification notification2 = notificationRepository.save(Notification.builder()
                .description("Notification 2")
                .user("User1")
                .postId(2L)
                .build());

        mockMvc.perform(get("/api/notifications")
                        .param("author", "User1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2)) // Expecting a list with 2 notifications
                .andExpect(jsonPath("$[0].description").value("Notification 1"))
                .andExpect(jsonPath("$[1].description").value("Notification 2"));
    }

    @Test
    void testDeleteNotification() throws Exception {
        Notification notification = notificationRepository.save(Notification.builder()
                .description("Delete me")
                .user("User1")
                .postId(1L)
                .build());

        mockMvc.perform(delete("/api/notifications/{notificationId}", notification.getId()))
                .andExpect(status().isOk());

        assertEquals(0, notificationRepository.findAll().size());
    }
}
