package com.issuetracker.repository;

import com.issuetracker.model.Issue;
import com.issuetracker.model.Issue.IssueStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Long> {

    // Custom query for searching issues by project, status, and text (title or description)
    @Query("SELECT i FROM Issue i WHERE i.project.id = :projectId " +
           "AND (:status IS NULL OR i.status = :status) " +
           "AND (:text IS NULL OR LOWER(i.title) LIKE LOWER(CONCAT('%', :text, '%')) " +
           "OR LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%')))")
    List<Issue> searchIssues(@Param("projectId") Long projectId,
                             @Param("status") IssueStatus status,
                             @Param("text") String text);

    // Report query: Top users by closed issues in a date range
    // Returning Object[] for simplicity, could be mapped to an interface or DTO
    @Query("SELECT i.assignee.username, COUNT(i) as closedCount " +
           "FROM Issue i " +
           "WHERE i.status = 'DONE' " +
           "AND i.assignee IS NOT NULL " +
           "AND (:startDate IS NULL OR i.createdAt >= :startDate) " +
           "AND (:endDate IS NULL OR i.createdAt <= :endDate) " +
           "GROUP BY i.assignee.username " +
           "ORDER BY closedCount DESC " +
           "LIMIT 5")
    List<Object[]> findTopPerformers(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);
}
