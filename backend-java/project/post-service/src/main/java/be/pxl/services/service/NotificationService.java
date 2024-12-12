package be.pxl.services.service;

import be.pxl.services.domain.Notification;
import be.pxl.services.domain.dto.NotificationRequest;
import be.pxl.services.domain.dto.NotificationResponse;
import be.pxl.services.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService implements INotificationService {

    private final NotificationRepository notificationRepository;
    private final PostService postService;


    @Override
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    @Override
    public List<NotificationResponse> getNotifications(String user) {
        return notificationRepository.findAll().stream()
                .filter(n -> n.getUser().equals(user))
                .map(NotificationResponse::mapToNotificationResponse)
                .toList();
    }

    @Override
    public void createNotification(NotificationRequest notificationRequest) {
        System.out.println("Creating Notification: ");
        Notification notification = Notification.builder()
                .description(notificationRequest.getDescription())
                .user(postService.getUserOfPost(notificationRequest.getPostId()))
                .postId(notificationRequest.getPostId())
                .build();
        notificationRepository.save(notification);
    }
}
