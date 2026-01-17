package com.issuetracker.service;

import com.issuetracker.exception.BadRequestException;
import com.issuetracker.exception.ResourceNotFoundException;
import com.issuetracker.model.Project;
import com.issuetracker.model.Team;
import com.issuetracker.model.User;
import com.issuetracker.repository.ProjectRepository;
import com.issuetracker.repository.TeamRepository;
import com.issuetracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProjectService projectService;

    private Project testProject;
    private Team testTeam;
    private User testUser;

    @BeforeEach
    void setUp() {
        testProject = Project.builder()
                .id(1L)
                .name("Test Project")
                .key("TEST")
                .description("Test project description")
                .build();

        testTeam = Team.builder()
                .id(1L)
                .name("Development Team")
                .build();

        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .build();
    }

    @Test
    void createProject_ShouldReturnSavedProject() {
        when(projectRepository.existsByKey("TEST")).thenReturn(false);
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        Project result = projectService.createProject(testProject);

        assertNotNull(result);
        assertEquals("TEST", result.getKey());
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    void createProject_WhenKeyExists_ShouldThrowException() {
        when(projectRepository.existsByKey("TEST")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> {
            projectService.createProject(testProject);
        });
    }

    @Test
    void getAllProjects_ShouldReturnProjectList() {
        when(projectRepository.findAll()).thenReturn(Arrays.asList(testProject));

        List<Project> result = projectService.getAllProjects();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(projectRepository, times(1)).findAll();
    }

    @Test
    void getProjectById_WhenProjectExists_ShouldReturnProject() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));

        Optional<Project> result = projectService.getProjectById(1L);

        assertTrue(result.isPresent());
        assertEquals("TEST", result.get().getKey());
    }

    @Test
    void getProjectById_WhenProjectNotExists_ShouldReturnEmpty() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Project> result = projectService.getProjectById(99L);

        assertFalse(result.isPresent());
    }

    @Test
    void getProjectByKey_WhenProjectExists_ShouldReturnProject() {
        when(projectRepository.findByKey("TEST")).thenReturn(Optional.of(testProject));

        Optional<Project> result = projectService.getProjectByKey("TEST");

        assertTrue(result.isPresent());
        assertEquals("TEST", result.get().getKey());
    }

    @Test
    void updateProject_ShouldReturnUpdatedProject() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(projectRepository.existsByKey("TEST")).thenReturn(true);
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        Project result = projectService.updateProject(testProject);

        assertNotNull(result);
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    void updateProject_WhenProjectNotExists_ShouldThrowException() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        Project project = Project.builder().id(99L).build();

        assertThrows(ResourceNotFoundException.class, () -> {
            projectService.updateProject(project);
        });
    }

    @Test
    void updateProject_WhenKeyChangedAndExists_ShouldThrowException() {
        Project existingProject = Project.builder()
                .id(1L)
                .key("OLD")
                .build();
        Project updatedProject = Project.builder()
                .id(1L)
                .key("NEW")
                .build();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(existingProject));
        when(projectRepository.existsByKey("NEW")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> {
            projectService.updateProject(updatedProject);
        });
    }

    @Test
    void deleteProject_ShouldDeleteProject() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        doNothing().when(projectRepository).delete(any(Project.class));

        projectService.deleteProject(1L);

        verify(projectRepository, times(1)).delete(any(Project.class));
    }

    @Test
    void deleteProject_WhenProjectNotExists_ShouldThrowException() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            projectService.deleteProject(99L);
        });
    }

    @Test
    void assignTeamToProject_ShouldAssignTeam() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(teamRepository.findById(1L)).thenReturn(Optional.of(testTeam));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        Project result = projectService.assignTeamToProject(1L, 1L);

        assertNotNull(result);
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    void assignTeamToProject_WhenProjectNotExists_ShouldThrowException() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            projectService.assignTeamToProject(99L, 1L);
        });
    }

    @Test
    void assignTeamToProject_WhenTeamNotExists_ShouldThrowException() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(teamRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            projectService.assignTeamToProject(1L, 99L);
        });
    }

    @Test
    void assignLeadToProject_ShouldAssignLead() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        Project result = projectService.assignLeadToProject(1L, 1L);

        assertNotNull(result);
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    void assignLeadToProject_WhenUserNotExists_ShouldThrowException() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            projectService.assignLeadToProject(1L, 99L);
        });
    }

    @Test
    void getProjectsByTeam_ShouldReturnProjectList() {
        when(projectRepository.findByTeamId(1L)).thenReturn(Arrays.asList(testProject));

        List<Project> result = projectService.getProjectsByTeam(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(projectRepository, times(1)).findByTeamId(1L);
    }

    @Test
    void getProjectsByLead_ShouldReturnProjectList() {
        when(projectRepository.findByLeadId(1L)).thenReturn(Arrays.asList(testProject));

        List<Project> result = projectService.getProjectsByLead(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(projectRepository, times(1)).findByLeadId(1L);
    }
}
