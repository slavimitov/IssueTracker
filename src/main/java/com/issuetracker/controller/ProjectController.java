package com.issuetracker.controller;

import com.issuetracker.dto.CreateProjectRequest;
import com.issuetracker.dto.ProjectDTO;
import com.issuetracker.exception.ResourceNotFoundException;
import com.issuetracker.model.Project;
import com.issuetracker.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        List<ProjectDTO> projects = projectService.getAllProjects().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable Long id) {
        return projectService.getProjectById(id)
                .map(project -> ResponseEntity.ok(toDTO(project)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/key/{key}")
    public ResponseEntity<ProjectDTO> getProjectByKey(@PathVariable String key) {
        return projectService.getProjectByKey(key)
                .map(project -> ResponseEntity.ok(toDTO(project)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProjectDTO> createProject(@Valid @RequestBody CreateProjectRequest request) {
        Project project = Project.builder()
                .name(request.getName())
                .key(request.getKey())
                .description(request.getDescription())
                .build();
        Project savedProject = projectService.createProject(project);
        if (request.getTeamId() != null) {
            savedProject = projectService.assignTeamToProject(savedProject.getId(), request.getTeamId());
        }
        if (request.getLeadId() != null) {
            savedProject = projectService.assignLeadToProject(savedProject.getId(), request.getLeadId());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(savedProject));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectDTO> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody CreateProjectRequest request) {
        Project project = projectService.getProjectById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id));
        project.setName(request.getName());
        project.setKey(request.getKey());
        project.setDescription(request.getDescription());
        Project updatedProject = projectService.updateProject(project);
        if (request.getTeamId() != null) {
            updatedProject = projectService.assignTeamToProject(updatedProject.getId(), request.getTeamId());
        }
        if (request.getLeadId() != null) {
            updatedProject = projectService.assignLeadToProject(updatedProject.getId(), request.getLeadId());
        }
        return ResponseEntity.ok(toDTO(updatedProject));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{projectId}/team/{teamId}")
    public ResponseEntity<ProjectDTO> assignTeam(
            @PathVariable Long projectId,
            @PathVariable Long teamId) {
        Project project = projectService.assignTeamToProject(projectId, teamId);
        return ResponseEntity.ok(toDTO(project));
    }

    @DeleteMapping("/{projectId}/team")
    public ResponseEntity<ProjectDTO> removeTeam(@PathVariable Long projectId) {
        Project project = projectService.getProjectById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        project.setTeam(null);
        Project updatedProject = projectService.updateProject(project);
        return ResponseEntity.ok(toDTO(updatedProject));
    }

    @PostMapping("/{projectId}/lead/{userId}")
    public ResponseEntity<ProjectDTO> assignLead(
            @PathVariable Long projectId,
            @PathVariable Long userId) {
        Project project = projectService.assignLeadToProject(projectId, userId);
        return ResponseEntity.ok(toDTO(project));
    }

    @DeleteMapping("/{projectId}/lead")
    public ResponseEntity<ProjectDTO> removeLead(@PathVariable Long projectId) {
        Project project = projectService.getProjectById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        project.setLead(null);
        Project updatedProject = projectService.updateProject(project);
        return ResponseEntity.ok(toDTO(updatedProject));
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<ProjectDTO>> getProjectsByTeam(@PathVariable Long teamId) {
        List<ProjectDTO> projects = projectService.getProjectsByTeam(teamId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/lead/{userId}")
    public ResponseEntity<List<ProjectDTO>> getProjectsByLead(@PathVariable Long userId) {
        List<ProjectDTO> projects = projectService.getProjectsByLead(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(projects);
    }

    private ProjectDTO toDTO(Project project) {
        return ProjectDTO.builder()
                .id(project.getId())
                .name(project.getName())
                .key(project.getKey())
                .description(project.getDescription())
                .teamId(project.getTeam() != null ? project.getTeam().getId() : null)
                .teamName(project.getTeam() != null ? project.getTeam().getName() : null)
                .leadId(project.getLead() != null ? project.getLead().getId() : null)
                .leadUsername(project.getLead() != null ? project.getLead().getUsername() : null)
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }
}
