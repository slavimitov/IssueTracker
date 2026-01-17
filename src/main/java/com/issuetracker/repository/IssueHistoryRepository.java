package com.issuetracker.repository;

import com.issuetracker.model.IssueHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IssueHistoryRepository extends JpaRepository<IssueHistory, Long> {
    List<IssueHistory> findByIssueIdOrderByChangedAtDesc(Long issueId);
}
