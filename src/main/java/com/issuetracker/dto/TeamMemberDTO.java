package com.issuetracker.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamMemberDTO {
    private Long id;
    private Long teamId;
    private String teamName;
    private Long userId;
    private String username;
    private String role;
    private LocalDateTime joinedAt;
}
