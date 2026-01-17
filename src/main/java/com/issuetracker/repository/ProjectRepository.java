package com.issuetracker.repository;

import com.issuetracker.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findByKey(String key);

    boolean existsByKey(String key);

    List<Project> findByTeamId(Long teamId);

    List<Project> findByLeadId(Long leadId);
}
