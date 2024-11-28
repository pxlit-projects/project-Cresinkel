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
public class EditDraftRequest {
    @Nonnull
    private Long id;
    @Nonnull
    private String title;
    @Nonnull
    private String description;
}
