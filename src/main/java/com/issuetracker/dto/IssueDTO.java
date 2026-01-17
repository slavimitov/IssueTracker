package com.issuetracker.dto;

import com.issuetracker.model.Issue.IssuePriority;
import com.issuetracker.model.Issue.IssueStatus;
import com.issuetracker.model.Issue.IssueType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class IssueDTO {
    private Long id;
    private String title;
    private String description;
    private IssueType type;
    private IssueStatus status;
    private IssuePriority priority;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    
    private Long projectId;
    private String projectName;
    
    // Assignee details
    private Long assigneeId;
    private String assigneeName;
    
    private Long reporterId;
    private String reporterName;
}
