package com.issuetracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.issuetracker.dto.CreateSprintRequest;
import com.issuetracker.model.Project;
import com.issuetracker.model.Sprint;
import com.issuetracker.service.SprintService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings({"null", "removal"})
@WebMvcTest(SprintController.class)
class SprintControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SprintService sprintService;

    private Project testProject;
    private Sprint testSprint;

    @BeforeEach
    void setUp() {
        testProject = Project.builder()
                .id(1L)
                .name("Project One")
                .key("P1")
                .build();

        testSprint = Sprint.builder()
                .id(1L)
                .name("Sprint 1")
                .startDate(LocalDate.of(2026, 1, 1))
                .endDate(LocalDate.of(2026, 1, 14))
                .project(testProject)
                .build();
    }

    @Test
    void getAllSprints_ShouldReturnSprintList() throws Exception {
        when(sprintService.getAllSprints()).thenReturn(Arrays.asList(testSprint));

        mockMvc.perform(get("/api/sprints"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Sprint 1"))
                .andExpect(jsonPath("$[0].projectName").value("Project One"));
    }

    @Test
    void getSprintById_WhenExists_ShouldReturnSprint() throws Exception {
        when(sprintService.getSprintById(1L)).thenReturn(Optional.of(testSprint));

        mockMvc.perform(get("/api/sprints/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sprint 1"))
                .andExpect(jsonPath("$.projectKey").value("P1"));
    }

    @Test
    void getSprintById_WhenNotExists_ShouldReturn404() throws Exception {
        when(sprintService.getSprintById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/sprints/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createSprint_WithValidData_ShouldReturnCreatedSprint() throws Exception {
        CreateSprintRequest request = CreateSprintRequest.builder()
                .name("Sprint 1")
                .startDate(LocalDate.of(2026, 1, 1))
                .endDate(LocalDate.of(2026, 1, 14))
                .projectId(1L)
                .build();

        when(sprintService.createSprint(any(Sprint.class))).thenReturn(testSprint);

        mockMvc.perform(post("/api/sprints")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Sprint 1"))
                .andExpect(jsonPath("$.projectId").value(1L));
    }

    @Test
    void createSprint_WithInvalidData_ShouldReturn400() throws Exception {
        CreateSprintRequest request = CreateSprintRequest.builder()
                .name("")
                .projectId(1L)
                .build();

        mockMvc.perform(post("/api/sprints")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateSprint_WithValidData_ShouldReturnUpdatedSprint() throws Exception {
        CreateSprintRequest request = CreateSprintRequest.builder()
                .name("Sprint 1 Updated")
                .startDate(LocalDate.of(2026, 1, 1))
                .endDate(LocalDate.of(2026, 1, 14))
                .projectId(1L)
                .build();

        Sprint updatedSprint = Sprint.builder()
                .id(1L)
                .name("Sprint 1 Updated")
                .startDate(LocalDate.of(2026, 1, 1))
                .endDate(LocalDate.of(2026, 1, 14))
                .project(testProject)
                .build();

        when(sprintService.getSprintById(1L)).thenReturn(Optional.of(testSprint));
        when(sprintService.updateSprint(any(Sprint.class))).thenReturn(updatedSprint);

        mockMvc.perform(put("/api/sprints/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sprint 1 Updated"));
    }

    @Test
    void deleteSprint_ShouldReturnNoContent() throws Exception {
        doNothing().when(sprintService).deleteSprint(1L);

        mockMvc.perform(delete("/api/sprints/1"))
                .andExpect(status().isNoContent());

        verify(sprintService, times(1)).deleteSprint(1L);
    }

    @Test
    void getSprintsByProject_ShouldReturnSprints() throws Exception {
        when(sprintService.getSprintsByProject(1L)).thenReturn(Arrays.asList(testSprint));

        mockMvc.perform(get("/api/sprints/project/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].projectId").value(1L));
    }

    @Test
    void getActiveSprintsByProject_ShouldReturnActiveSprints() throws Exception {
        when(sprintService.getActiveSprintsByProject(1L)).thenReturn(Arrays.asList(testSprint));

        mockMvc.perform(get("/api/sprints/project/1/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Sprint 1"));
    }

    @Test
    void getSprintsByProject_WhenEmpty_ShouldReturnEmptyList() throws Exception {
        when(sprintService.getSprintsByProject(1L)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/sprints/project/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getActiveSprintsByProject_WhenEmpty_ShouldReturnEmptyList() throws Exception {
        when(sprintService.getActiveSprintsByProject(1L)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/sprints/project/1/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
