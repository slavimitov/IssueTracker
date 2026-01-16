package com.issuetracker.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddTeamMemberRequest {

    @NotNull
    private Long userId;

    private String role;
}
