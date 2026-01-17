package com.issuetracker.service;

import com.issuetracker.exception.BadRequestException;
import com.issuetracker.exception.ResourceNotFoundException;
import com.issuetracker.model.Project;
import com.issuetracker.model.Sprint;
import com.issuetracker.repository.ProjectRepository;
import com.issuetracker.repository.SprintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SprintService {

    private final SprintRepository sprintRepository;
    private final ProjectRepository projectRepository;

    public Sprint createSprint(Sprint sprint) {
        if (sprint.getStartDate().isAfter(sprint.getEndDate())) {
            throw new BadRequestException("Start date must be before or equal to end date");
        }

        Project project = projectRepository.findById(sprint.getProject().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", sprint.getProject().getId()));

        List<Sprint> overlappingSprints = sprintRepository.findOverlappingSprints(
                project.getId(), sprint.getStartDate(), sprint.getEndDate());
        if (!overlappingSprints.isEmpty()) {
            throw new BadRequestException("Sprint overlaps with existing sprint in the project");
        }

        return sprintRepository.save(sprint);
    }

    @Transactional(readOnly = true)
    public List<Sprint> getAllSprints() {
        return sprintRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Sprint> getSprintById(Long id) {
        return sprintRepository.findById(id);
    }

    public Sprint updateSprint(Sprint sprint) {
        Sprint existingSprint = sprintRepository.findById(sprint.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Sprint", sprint.getId()));

        if (sprint.getStartDate().isAfter(sprint.getEndDate())) {
            throw new BadRequestException("Start date must be before or equal to end date");
        }

        if (!existingSprint.getProject().getId().equals(sprint.getProject().getId())) {
            Project project = projectRepository.findById(sprint.getProject().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project", sprint.getProject().getId()));
        }

        List<Sprint> overlappingSprints = sprintRepository.findOverlappingSprints(
                sprint.getProject().getId(), sprint.getStartDate(), sprint.getEndDate());
        boolean hasOverlap = overlappingSprints.stream()
                .anyMatch(s -> !s.getId().equals(sprint.getId()));
        if (hasOverlap) {
            throw new BadRequestException("Sprint overlaps with existing sprint in the project");
        }

        return sprintRepository.save(sprint);
    }

    public void deleteSprint(Long id) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint", id));
        sprintRepository.delete(sprint);
    }

    @Transactional(readOnly = true)
    public List<Sprint> getSprintsByProject(Long projectId) {
        return sprintRepository.findByProjectId(projectId);
    }

    @Transactional(readOnly = true)
    public List<Sprint> getActiveSprintsByProject(Long projectId) {
        return sprintRepository.findActiveSprintsByProjectAndDate(projectId, LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<Sprint> getSprintsByProjectOrderByStartDate(Long projectId) {
        return sprintRepository.findByProjectIdOrderByStartDateDesc(projectId);
    }
}
