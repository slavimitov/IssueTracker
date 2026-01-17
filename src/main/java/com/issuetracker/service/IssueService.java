package com.issuetracker.service;

import com.issuetracker.model.*;
import com.issuetracker.model.Issue.IssuePriority;
import com.issuetracker.model.Issue.IssueStatus;
import com.issuetracker.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IssueService {

    private final IssueRepository issueRepository;
    private final IssueHistoryRepository historyRepository;
    private final IssueCommentRepository commentRepository;
    
    // Developer 1 & 3 repositories would be injected here
    // For now we assume we have access to stubs or we'd need their repos
    // private final UserRepository userRepository; 
    // private final ProjectRepository projectRepository;

    @Transactional
    public Issue createIssue(Issue issue) {
        issue.setStatus(IssueStatus.TODO);
        issue.setCreatedAt(LocalDateTime.now());
        // In a real scenario, we'd validate project and reporter existence here
        return issueRepository.save(issue);
    }

    @Transactional
    public Issue startIssue(Long issueId) {
        Issue issue = getIssueOrThrow(issueId);

        // Workflow Rule: TODO -> IN_PROGRESS
        if (issue.getStatus() != IssueStatus.TODO) {
            throw new IllegalStateException("Issue must be in TODO status to start.");
        }

        // SLA Rule: High Priority must have Assignee
        if (issue.getPriority() == IssuePriority.HIGH && issue.getAssignee() == null) {
            throw new IllegalStateException("High priority issues must be assigned before starting.");
        }

        IssueStatus oldStatus = issue.getStatus();
        issue.setStatus(IssueStatus.IN_PROGRESS);
        Issue savedIssue = issueRepository.save(issue);
        
        logHistory(savedIssue, "STATUS", oldStatus.name(), IssueStatus.IN_PROGRESS.name(), null); // Assuming 'null' user for now, or get from SecurityContext
        
        return savedIssue;
    }

    @Transactional
    public Issue completeIssue(Long issueId) {
        Issue issue = getIssueOrThrow(issueId);

        // Workflow Rule: IN_PROGRESS -> DONE
        if (issue.getStatus() != IssueStatus.IN_PROGRESS) {
            throw new IllegalStateException("Issue must be in IN_PROGRESS status to complete.");
        }

        IssueStatus oldStatus = issue.getStatus();
        issue.setStatus(IssueStatus.DONE);
        Issue savedIssue = issueRepository.save(issue);
        
        logHistory(savedIssue, "STATUS", oldStatus.name(), IssueStatus.DONE.name(), null);
        
        return savedIssue;
    }

    @Transactional
    public Issue assignIssue(Long issueId, User assignee) {
        try {
            Issue issue = getIssueOrThrow(issueId);
            String oldAssigneeName = issue.getAssignee() != null ? issue.getAssignee().getUsername() : "Unassigned";
            
            issue.setAssignee(assignee);
            Issue savedIssue = issueRepository.save(issue);

            logHistory(savedIssue, "ASSIGNEE", oldAssigneeName, assignee.getUsername(), null);
            return savedIssue;
        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("Issue was updated by another user. Please refresh and try again.");
        }
    }

    @Transactional
    public IssueComment addComment(Long issueId, IssueComment comment) {
        Issue issue = getIssueOrThrow(issueId);
        comment.setIssue(issue);
        comment.setCreatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    public List<Issue> searchIssues(Long projectId, IssueStatus status, String text) {
        String searchText = text == null ? "" : text;
        return issueRepository.searchIssues(projectId, status, searchText);
    }
    
    public List<Object[]> getTopPerformers() {
        // Default to last 30 days if no range provided, flexible implementation
        return issueRepository.findTopPerformers(LocalDateTime.now().minusDays(30), LocalDateTime.now());
    }

    private Issue getIssueOrThrow(Long id) {
        return issueRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Issue found with id: " + id));
    }

    private void logHistory(Issue issue, String field, String oldValue, String newValue, User changedBy) {
        IssueHistory history = new IssueHistory();
        history.setIssue(issue);
        history.setOldStatus(field.equals("STATUS") ? oldValue : null); // Simple mapping for now
        history.setNewStatus(field.equals("STATUS") ? newValue : null);
        history.setOldAssigneeName(field.equals("ASSIGNEE") ? oldValue : null);
        history.setNewAssigneeName(field.equals("ASSIGNEE") ? newValue : null);
        history.setChangedBy(changedBy);
        history.setChangedAt(LocalDateTime.now());
        historyRepository.save(history);
    }
}
