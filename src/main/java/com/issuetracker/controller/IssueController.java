package com.issuetracker.controller;

import com.issuetracker.model.Issue;
import com.issuetracker.model.Issue.IssueStatus;
import com.issuetracker.model.IssueComment;
import com.issuetracker.model.User;
import com.issuetracker.service.UserService;
import com.issuetracker.service.IssueService;
import com.issuetracker.dto.CommentDTO;
import com.issuetracker.dto.CreateIssueRequest;
import com.issuetracker.dto.IssueDTO;
import com.issuetracker.mapper.IssueCommentMapper;
import com.issuetracker.mapper.IssueMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Issues", description = "Issue management APIs")
public class IssueController {

    private final IssueService issueService;
    private final UserService userService;
    private final IssueMapper issueMapper;
    private final IssueCommentMapper commentMapper;

    @PostMapping("/projects/{projectId}/issues")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new issue")
    public IssueDTO createIssue(@PathVariable Long projectId,
            @Valid @RequestBody CreateIssueRequest request) {
        // Enforce projectId from path
        request.setProjectId(projectId);
        Issue issue = issueMapper.toEntity(request);
        Issue savedIssue = issueService.createIssue(issue);
        return issueMapper.toDTO(savedIssue);
    }

    @PutMapping("/issues/{id}/start")
    @Operation(summary = "Start progress on an issue (TODO -> IN_PROGRESS)")
    public IssueDTO startIssue(@PathVariable Long id) {
        return issueMapper.toDTO(issueService.startIssue(id));
    }

    @PutMapping("/issues/{id}/complete")
    @Operation(summary = "Complete an issue (IN_PROGRESS -> DONE)")
    public IssueDTO completeIssue(@PathVariable Long id) {
        return issueMapper.toDTO(issueService.completeIssue(id));
    }

    @PutMapping("/issues/{id}/assign")
    @Operation(summary = "Assign an issue to a user")
    public IssueDTO assignIssue(@PathVariable Long id, @RequestParam Long userId) {
        User assignee = userService.getUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        return issueMapper.toDTO(issueService.assignIssue(id, assignee));
    }

    @GetMapping("/projects/{projectId}/issues")
    @Operation(summary = "Search issues in a project")
    public List<IssueDTO> searchIssues(@PathVariable Long projectId,
            @RequestParam(required = false) IssueStatus status,
            @RequestParam(required = false) String text) {
        return issueService.searchIssues(projectId, status, text).stream()
                .map(issueMapper::toDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/issues/{id}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a comment to an issue")
    public CommentDTO addComment(@PathVariable Long id, @RequestBody CommentDTO commentDTO, @RequestParam Long userId) {
        User author = userService.getUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        IssueComment comment = commentMapper.toEntity(commentDTO);
        comment.setAuthor(author);

        return commentMapper.toDTO(issueService.addComment(id, comment));
    }

    @GetMapping("/issues/reports/top-performers")
    @Operation(summary = "Get top 5 users by closed issues in last 30 days")
    public List<Object[]> getTopPerformers() {
        return issueService.getTopPerformers();
    }
}
