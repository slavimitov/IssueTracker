package com.issuetracker.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDTO {
    private Long id;
    private String name;
    private String key;
    private String description;
    private Long teamId;
    private String teamName;
    private Long leadId;
    private String leadUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
