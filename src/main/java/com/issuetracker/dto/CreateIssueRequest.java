package com.issuetracker.dto;

import com.issuetracker.model.Issue.IssuePriority;
import com.issuetracker.model.Issue.IssueType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateIssueRequest {
    @NotBlank
    private String title;
    
    private String description;
    
    @NotNull
    private IssueType type;
    
    @NotNull
    private IssuePriority priority;
    
    private LocalDateTime dueDate;
    
    @NotNull
    private Long projectId;
    
    private Long reporterId;
}
