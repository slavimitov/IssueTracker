package com.issuetracker.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SprintDTO {
    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long projectId;
    private String projectName;
    private String projectKey;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
