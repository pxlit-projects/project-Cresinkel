package be.pxl.services.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "review")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    private Long id;

    private String title;
    private String description;
    private String author;
    private LocalDateTime publicationDate;
    private LocalDateTime lastEditedDate;
    private boolean accepted = false;
    private String rejectionReason = "";
}
