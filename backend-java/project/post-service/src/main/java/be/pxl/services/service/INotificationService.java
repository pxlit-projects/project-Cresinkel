package be.pxl.services.service;

import be.pxl.services.domain.dto.NotificationRequest;
import be.pxl.services.domain.dto.NotificationResponse;

import java.util.List;

public interface INotificationService {
    void deleteNotification(Long notificationId);
    List<NotificationResponse> getNotifications(String user);
    void createNotification(NotificationRequest notificationRequest);
}
