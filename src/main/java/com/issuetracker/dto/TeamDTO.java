package com.issuetracker.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
}
