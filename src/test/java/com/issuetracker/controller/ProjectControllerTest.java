package com.issuetracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.issuetracker.dto.CreateProjectRequest;
import com.issuetracker.dto.ProjectDTO;
import com.issuetracker.mapper.ProjectMapper;
import com.issuetracker.model.Project;
import com.issuetracker.model.Team;
import com.issuetracker.model.User;
import com.issuetracker.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings({"null", "removal"})
@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private ProjectMapper projectMapper;

    @Test
    void getAllProjects_ShouldReturnProjectList() throws Exception {
        Project project = Project.builder()
                .id(1L)
                .name("Test Project")
                .key("TEST")
                .description("Test description")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(projectService.getAllProjects()).thenReturn(Arrays.asList(project));
        when(projectMapper.toDTO(any(Project.class))).thenReturn(ProjectDTO.builder()
                .id(1L)
                .name("Test Project")
                .key("TEST")
                .build());

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Project"))
                .andExpect(jsonPath("$[0].key").value("TEST"));
    }

    @Test
    void getProjectById_WhenExists_ShouldReturnProject() throws Exception {
        Project project = Project.builder()
                .id(1L)
                .name("Test Project")
                .key("TEST")
                .description("Test description")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(projectService.getProjectById(1L)).thenReturn(Optional.of(project));
        when(projectMapper.toDTO(any(Project.class))).thenReturn(ProjectDTO.builder()
                .id(1L)
                .name("Test Project")
                .key("TEST")
                .build());

        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Project"))
                .andExpect(jsonPath("$.key").value("TEST"));
    }

    @Test
    void getProjectById_WhenNotExists_ShouldReturn404() throws Exception {
        when(projectService.getProjectById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/projects/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getProjectByKey_WhenExists_ShouldReturnProject() throws Exception {
        Project project = Project.builder()
                .id(1L)
                .name("Test Project")
                .key("TEST")
                .description("Test description")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(projectService.getProjectByKey("TEST")).thenReturn(Optional.of(project));
        when(projectMapper.toDTO(any(Project.class))).thenReturn(ProjectDTO.builder()
                .id(1L)
                .name("Test Project")
                .key("TEST")
                .build());

        mockMvc.perform(get("/api/projects/key/TEST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("TEST"));
    }

    @Test
    void createProject_WithValidData_ShouldReturnCreatedProject() throws Exception {
        CreateProjectRequest request = CreateProjectRequest.builder()
                .name("New Project")
                .key("NEW")
                .description("New project description")
                .build();

        Project savedProject = Project.builder()
                .id(1L)
                .name("New Project")
                .key("NEW")
                .description("New project description")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(projectService.createProject(any(Project.class))).thenReturn(savedProject);
        when(projectMapper.toDTO(any(Project.class))).thenReturn(ProjectDTO.builder()
                .id(1L)
                .name("New Project")
                .key("NEW")
                .build());

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Project"))
                .andExpect(jsonPath("$.key").value("NEW"));
    }

    @Test
    void createProject_WithInvalidData_ShouldReturn400() throws Exception {
        CreateProjectRequest request = CreateProjectRequest.builder()
                .name("")
                .key("")
                .build();

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateProject_WithValidData_ShouldReturnUpdatedProject() throws Exception {
        CreateProjectRequest request = CreateProjectRequest.builder()
                .name("Updated Project")
                .key("UPD")
                .description("Updated description")
                .build();

        Project existingProject = Project.builder()
                .id(1L)
                .name("Test Project")
                .key("TEST")
                .build();

        Project updatedProject = Project.builder()
                .id(1L)
                .name("Updated Project")
                .key("UPD")
                .description("Updated description")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(projectService.getProjectById(1L)).thenReturn(Optional.of(existingProject));
        when(projectService.updateProject(any(Project.class))).thenReturn(updatedProject);
        when(projectMapper.toDTO(any(Project.class))).thenReturn(ProjectDTO.builder()
                .id(1L)
                .name("Updated Project")
                .key("UPD")
                .build());

        mockMvc.perform(put("/api/projects/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Project"));
    }

    @Test
    void deleteProject_ShouldReturn204() throws Exception {
        Project project = Project.builder()
                .id(1L)
                .build();

        when(projectService.getProjectById(1L)).thenReturn(Optional.of(project));
        doNothing().when(projectService).deleteProject(1L);

        mockMvc.perform(delete("/api/projects/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void assignTeam_ShouldReturnUpdatedProject() throws Exception {
        Project project = Project.builder()
                .id(1L)
                .name("Test Project")
                .key("TEST")
                .team(Team.builder().id(1L).name("Team").build())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(projectService.assignTeamToProject(1L, 1L)).thenReturn(project);
        when(projectMapper.toDTO(any(Project.class))).thenReturn(ProjectDTO.builder()
                .id(1L)
                .name("Test Project")
                .teamId(1L)
                .build());

        mockMvc.perform(post("/api/projects/1/team/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamId").value(1L));
    }

    @Test
    void assignLead_ShouldReturnUpdatedProject() throws Exception {
        Project project = Project.builder()
                .id(1L)
                .name("Test Project")
                .key("TEST")
                .lead(User.builder().id(1L).username("user").build())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(projectService.assignLeadToProject(1L, 1L)).thenReturn(project);
        when(projectMapper.toDTO(any(Project.class))).thenReturn(ProjectDTO.builder()
                .id(1L)
                .name("Test Project")
                .leadId(1L)
                .build());

        mockMvc.perform(post("/api/projects/1/lead/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.leadId").value(1L));
    }

    @Test
    void getProjectsByTeam_ShouldReturnProjectList() throws Exception {
        Project project = Project.builder()
                .id(1L)
                .name("Test Project")
                .key("TEST")
                .team(Team.builder().id(1L).name("Team").build())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(projectService.getProjectsByTeam(1L)).thenReturn(Arrays.asList(project));
        when(projectMapper.toDTO(any(Project.class))).thenReturn(ProjectDTO.builder()
                .id(1L)
                .name("Test Project")
                .teamId(1L)
                .build());

        mockMvc.perform(get("/api/projects/team/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].teamId").value(1L));
    }

    @Test
    void getProjectsByLead_ShouldReturnProjectList() throws Exception {
        Project project = Project.builder()
                .id(1L)
                .name("Test Project")
                .key("TEST")
                .lead(User.builder().id(1L).username("user").build())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(projectService.getProjectsByLead(1L)).thenReturn(Arrays.asList(project));
        when(projectMapper.toDTO(any(Project.class))).thenReturn(ProjectDTO.builder()
                .id(1L)
                .name("Test Project")
                .leadId(1L)
                .build());

        mockMvc.perform(get("/api/projects/lead/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].leadId").value(1L));
    }
}
