package com.issuetracker.service;

import com.issuetracker.model.Issue;
import com.issuetracker.model.Issue.IssuePriority;
import com.issuetracker.model.Issue.IssueStatus;
import com.issuetracker.model.User;
import com.issuetracker.repository.IssueHistoryRepository;
import com.issuetracker.repository.IssueRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IssueServiceTest {

    @Mock
    private IssueRepository issueRepository;

    @Mock
    private IssueHistoryRepository historyRepository;

    @InjectMocks
    private IssueService issueService;

    @Test
    void startIssue_ShouldSucceed_WhenValid() {
        // Arrange
        Long issueId = 1L;
        Issue issue = new Issue();
        issue.setId(issueId);
        issue.setStatus(IssueStatus.TODO);
        issue.setPriority(IssuePriority.MEDIUM);

        when(issueRepository.findById(issueId)).thenReturn(Optional.of(issue));
        when(issueRepository.save(any(Issue.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        Issue result = issueService.startIssue(issueId);

        // Assert
        assertEquals(IssueStatus.IN_PROGRESS, result.getStatus());
        verify(historyRepository, times(1)).save(any());
    }

    @Test
    void startIssue_ShouldThrow_WhenHighPriorityAndUnassigned() {
        // Arrange
        Long issueId = 1L;
        Issue issue = new Issue();
        issue.setId(issueId);
        issue.setStatus(IssueStatus.TODO);
        issue.setPriority(IssuePriority.HIGH);
        issue.setAssignee(null); // Unassigned

        when(issueRepository.findById(issueId)).thenReturn(Optional.of(issue));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> issueService.startIssue(issueId));
    }

    @Test
    void assignIssue_ShouldThrow_WhenOptimisticLockFails() {
        // Arrange
        Long issueId = 1L;
        User user = new User();
        user.setId(2L);
        
        Issue issue = new Issue();
        issue.setId(issueId);

        when(issueRepository.findById(issueId)).thenReturn(Optional.of(issue));
        when(issueRepository.save(any(Issue.class))).thenThrow(new OptimisticLockingFailureException("Simulated concurrency error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> issueService.assignIssue(issueId, user));
    }
}
