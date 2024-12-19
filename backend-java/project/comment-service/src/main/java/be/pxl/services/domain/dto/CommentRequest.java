package be.pxl.services.domain.dto;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {
    @Nonnull
    private String description;
    @Nonnull
    private String author;
    @Nonnull
    private Long postId;
}
