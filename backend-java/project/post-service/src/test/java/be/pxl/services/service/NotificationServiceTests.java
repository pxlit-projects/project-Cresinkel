package be.pxl.services.service;

import be.pxl.services.domain.Notification;
import be.pxl.services.domain.dto.NotificationRequest;
import be.pxl.services.domain.dto.NotificationResponse;
import be.pxl.services.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTests {
    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private PostService postService;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    public void testDeleteNotification() {
        Long notificationId = 1L;

        // Call the deleteNotification method
        notificationService.deleteNotification(notificationId);

        // Verify if deleteById was called on the repository
        verify(notificationRepository, times(1)).deleteById(notificationId);
    }

    @Test
    public void testGetNotifications() {
        String user = "testUser";
        Notification notification1 = new Notification(1L, "Description 1", user, 101L);
        Notification notification2 = new Notification(2L, "Description 2", user, 102L);
        List<Notification> notifications = Arrays.asList(notification1, notification2);

        // Mock the repository response
        when(notificationRepository.findAll()).thenReturn(notifications);

        // Call the getNotifications method
        List<NotificationResponse> result = notificationService.getNotifications(user);

        // Verify that the correct number of notifications is returned
        assertEquals(2, result.size());

        // Verify that findAll() is called once
        verify(notificationRepository, times(1)).findAll();
    }

    @Test
    public void testGetNotifications_NoNotifications() {
        String user = "testUser";

        // Mock an empty list of notifications
        when(notificationRepository.findAll()).thenReturn(Arrays.asList());

        // Call the getNotifications method
        List<NotificationResponse> result = notificationService.getNotifications(user);

        // Verify that no notifications are returned
        assertTrue(result.isEmpty());

        // Verify that findAll() is called once
        verify(notificationRepository, times(1)).findAll();
    }

    @Test
    public void testCreateNotification() {
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setPostId(101L);
        notificationRequest.setDescription("New post notification");

        // Mock the behavior of postService to return a user based on postId
        when(postService.getUserOfPost(notificationRequest.getPostId())).thenReturn("testUser");

        // Call the createNotification method
        notificationService.createNotification(notificationRequest);

        // Verify that the save method was called on notificationRepository with the correct notification
        verify(notificationRepository, times(1)).save(Mockito.any(Notification.class));

        // Verify the user of the notification is set correctly
        verify(postService, times(1)).getUserOfPost(notificationRequest.getPostId());
    }
}
