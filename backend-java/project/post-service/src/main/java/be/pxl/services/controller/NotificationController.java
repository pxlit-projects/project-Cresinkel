package be.pxl.services.controller;

import be.pxl.services.domain.dto.NotificationRequest;
import be.pxl.services.domain.dto.NotificationResponse;
import be.pxl.services.service.INotificationService;
import be.pxl.services.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class NotificationController {
    private final INotificationService notificationService;
    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);

    @PostMapping
    public void createNotification(@RequestBody NotificationRequest notificationRequest) {
        log.info("Creating notification: {}", notificationRequest);
        notificationService.createNotification(notificationRequest);
        log.info("Notification created: {}", notificationRequest);
    }

    @GetMapping
    public List<NotificationResponse> getNotifications(@RequestParam String author) {
        log.info("Fetching notifications for user: {}", author);
        List<NotificationResponse> notifications = notificationService.getNotifications(author);
        log.info("Fetched notifications for user {}: {}", author, notifications.size());
        return notifications;
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long notificationId) {
        log.info("Deleting notification with ID: {}", notificationId);
        notificationService.deleteNotification(notificationId);
        log.info("Notification with ID: {} deleted", notificationId);
        return ResponseEntity.ok().build();
    }
}
