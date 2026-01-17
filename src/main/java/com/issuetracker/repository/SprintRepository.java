package com.issuetracker.repository;

import com.issuetracker.model.Sprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SprintRepository extends JpaRepository<Sprint, Long> {

    @Query("SELECT s FROM Sprint s JOIN FETCH s.project WHERE s.project.id = :projectId")
    List<Sprint> findByProjectId(@Param("projectId") Long projectId);

    @Query("SELECT s FROM Sprint s JOIN FETCH s.project")
    List<Sprint> findAllWithProject();

    @Query("SELECT s FROM Sprint s JOIN FETCH s.project WHERE s.id = :id")
    Optional<Sprint> findByIdWithProject(@Param("id") Long id);

    @Query("SELECT s FROM Sprint s JOIN FETCH s.project WHERE s.project.id = :projectId AND s.startDate <= :date AND s.endDate >= :date")
    List<Sprint> findActiveSprintsByProjectAndDate(@Param("projectId") Long projectId, @Param("date") LocalDate date);

    @Query("SELECT s FROM Sprint s WHERE s.project.id = :projectId AND " +
            "((s.startDate <= :startDate AND s.endDate >= :startDate) OR " +
            "(s.startDate <= :endDate AND s.endDate >= :endDate) OR " +
            "(s.startDate >= :startDate AND s.endDate <= :endDate))")
    List<Sprint> findOverlappingSprints(@Param("projectId") Long projectId,
                                        @Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);

    @Query("SELECT s FROM Sprint s JOIN FETCH s.project WHERE s.project.id = :projectId ORDER BY s.startDate DESC")
    List<Sprint> findByProjectIdOrderByStartDateDesc(@Param("projectId") Long projectId);
}
