package com.issuetracker.service;

import com.issuetracker.exception.BadRequestException;
import com.issuetracker.exception.ResourceNotFoundException;
import com.issuetracker.model.Project;
import com.issuetracker.model.Team;
import com.issuetracker.model.User;
import com.issuetracker.repository.ProjectRepository;
import com.issuetracker.repository.TeamRepository;
import com.issuetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    public Project createProject(Project project) {
        if (project.getKey() != null && projectRepository.existsByKey(project.getKey())) {
            throw new BadRequestException("Project key already exists: " + project.getKey());
        }
        return projectRepository.save(project);
    }

    @Transactional(readOnly = true)
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Project> getProjectByKey(String key) {
        return projectRepository.findByKey(key);
    }

    public Project updateProject(Project project) {
        Project existingProject = projectRepository.findById(project.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", project.getId()));

        if (project.getKey() != null && !project.getKey().equals(existingProject.getKey())) {
            if (projectRepository.existsByKey(project.getKey())) {
                throw new BadRequestException("Project key already exists: " + project.getKey());
            }
        }

        return projectRepository.save(project);
    }

    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id));
        projectRepository.delete(project);
    }

    public Project assignTeamToProject(Long projectId, Long teamId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId));
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team", teamId));
        project.setTeam(team);
        return projectRepository.save(project);
    }

    public Project assignLeadToProject(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        project.setLead(user);
        return projectRepository.save(project);
    }

    @Transactional(readOnly = true)
    public List<Project> getProjectsByTeam(Long teamId) {
        return projectRepository.findByTeamId(teamId);
    }

    @Transactional(readOnly = true)
    public List<Project> getProjectsByLead(Long userId) {
        return projectRepository.findByLeadId(userId);
    }
}
