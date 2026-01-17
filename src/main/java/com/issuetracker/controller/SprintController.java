package com.issuetracker.controller;

import com.issuetracker.dto.CreateSprintRequest;
import com.issuetracker.dto.SprintDTO;
import com.issuetracker.exception.ResourceNotFoundException;
import com.issuetracker.model.Project;
import com.issuetracker.model.Sprint;
import com.issuetracker.service.SprintService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sprints")
@RequiredArgsConstructor
public class SprintController {

    private final SprintService sprintService;

    @GetMapping
    public ResponseEntity<List<SprintDTO>> getAllSprints() {
        List<SprintDTO> sprints = sprintService.getAllSprints().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(sprints);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SprintDTO> getSprintById(@PathVariable Long id) {
        return sprintService.getSprintById(id)
                .map(sprint -> ResponseEntity.ok(toDTO(sprint)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SprintDTO> createSprint(@Valid @RequestBody CreateSprintRequest request) {
        Project project = Project.builder()
                .id(request.getProjectId())
                .build();
        Sprint sprint = Sprint.builder()
                .name(request.getName())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .project(project)
                .build();
        Sprint savedSprint = sprintService.createSprint(sprint);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(savedSprint));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SprintDTO> updateSprint(
            @PathVariable Long id,
            @Valid @RequestBody CreateSprintRequest request) {
        Sprint sprint = sprintService.getSprintById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint", id));
        Project project = Project.builder()
                .id(request.getProjectId())
                .build();
        sprint.setName(request.getName());
        sprint.setStartDate(request.getStartDate());
        sprint.setEndDate(request.getEndDate());
        sprint.setProject(project);
        Sprint updatedSprint = sprintService.updateSprint(sprint);
        return ResponseEntity.ok(toDTO(updatedSprint));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSprint(@PathVariable Long id) {
        sprintService.deleteSprint(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<SprintDTO>> getSprintsByProject(@PathVariable Long projectId) {
        List<SprintDTO> sprints = sprintService.getSprintsByProject(projectId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(sprints);
    }

    @GetMapping("/project/{projectId}/active")
    public ResponseEntity<List<SprintDTO>> getActiveSprintsByProject(@PathVariable Long projectId) {
        List<SprintDTO> sprints = sprintService.getActiveSprintsByProject(projectId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(sprints);
    }

    private SprintDTO toDTO(Sprint sprint) {
        return SprintDTO.builder()
                .id(sprint.getId())
                .name(sprint.getName())
                .startDate(sprint.getStartDate())
                .endDate(sprint.getEndDate())
                .projectId(sprint.getProject().getId())
                .projectName(sprint.getProject().getName())
                .projectKey(sprint.getProject().getKey())
                .createdAt(sprint.getCreatedAt())
                .updatedAt(sprint.getUpdatedAt())
                .build();
    }
}
