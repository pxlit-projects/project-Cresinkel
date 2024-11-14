package be.pxl.services.domain.dto;

import jakarta.annotation.Nonnull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostRequest {
    @Nonnull
    private String title;
    @Nonnull
    private String description;
    @Nonnull
    private String author;
}
