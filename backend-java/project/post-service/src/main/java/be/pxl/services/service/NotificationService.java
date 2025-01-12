package be.pxl.services.service;

import be.pxl.services.domain.Notification;
import be.pxl.services.domain.dto.NotificationRequest;
import be.pxl.services.domain.dto.NotificationResponse;
import be.pxl.services.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService implements INotificationService {

    private final NotificationRepository notificationRepository;
    private final PostService postService;
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);


    @Override
    public void deleteNotification(Long notificationId) {
        log.info("Deleting notification with ID: {}", notificationId);
        notificationRepository.deleteById(notificationId);
        log.info("Notification with ID: {} deleted", notificationId);
    }

    @Override
    public List<NotificationResponse> getNotifications(String user) {
        log.info("Fetching notifications for user: {}", user);
        List<NotificationResponse> notifications = notificationRepository.findAll().stream()
                .filter(n -> n.getUser().equals(user))
                .map(NotificationResponse::mapToNotificationResponse)
                .toList();
        log.info("Fetched {} notifications for user: {}", notifications.size(), user);
        return notifications;
    }

    @Override
    public void createNotification(NotificationRequest notificationRequest) {
        log.info("Creating notification for post ID: {}", notificationRequest.getPostId());
        Notification notification = Notification.builder()
                .description(notificationRequest.getDescription())
                .user(postService.getUserOfPost(notificationRequest.getPostId()))
                .postId(notificationRequest.getPostId())
                .build();
        notificationRepository.save(notification);
        log.info("Notification created: {}", notification);
    }
}
