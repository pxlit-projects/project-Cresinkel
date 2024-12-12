package be.pxl.services.domain.dto;

import be.pxl.services.domain.Notification;
import be.pxl.services.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long id;

    private String description;
    private String user;
    private Long postId;

    public static NotificationResponse mapToNotificationResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .description(notification.getDescription())
                .user(notification.getUser())
                .postId(notification.getPostId())
                .build();
    }
}
