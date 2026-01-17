package com.issuetracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.issuetracker.dto.AddTeamMemberRequest;
import com.issuetracker.dto.CreateTeamRequest;
import com.issuetracker.model.Team;
import com.issuetracker.model.TeamMember;
import com.issuetracker.model.User;
import com.issuetracker.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TeamController.class)
class TeamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TeamService teamService;

    private Team testTeam;
    private User testUser;
    private TeamMember testTeamMember;

    @BeforeEach
    void setUp() {
        testTeam = Team.builder()
                .id(1L)
                .name("Development Team")
                .description("Main development team")
                .build();

        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();

        testTeamMember = TeamMember.builder()
                .id(1L)
                .team(testTeam)
                .user(testUser)
                .role("DEVELOPER")
                .build();
    }

    @Test
    void getAllTeams_ShouldReturnTeamList() throws Exception {
        when(teamService.getAllTeams()).thenReturn(Arrays.asList(testTeam));

        mockMvc.perform(get("/api/teams"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Development Team"))
                .andExpect(jsonPath("$[0].description").value("Main development team"));
    }

    @Test
    void getAllTeams_WhenNoTeams_ShouldReturnEmptyList() throws Exception {
        when(teamService.getAllTeams()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/teams"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getTeamById_WhenExists_ShouldReturnTeam() throws Exception {
        when(teamService.getTeamById(1L)).thenReturn(Optional.of(testTeam));

        mockMvc.perform(get("/api/teams/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Development Team"));
    }

    @Test
    void getTeamById_WhenNotExists_ShouldReturn404() throws Exception {
        when(teamService.getTeamById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/teams/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createTeam_WithValidData_ShouldReturnCreatedTeam() throws Exception {
        CreateTeamRequest request = CreateTeamRequest.builder()
                .name("New Team")
                .description("A new team")
                .build();

        Team savedTeam = Team.builder()
                .id(2L)
                .name("New Team")
                .description("A new team")
                .build();

        when(teamService.createTeam(any(Team.class))).thenReturn(savedTeam);

        mockMvc.perform(post("/api/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Team"));
    }

    @Test
    void deleteTeam_ShouldReturnNoContent() throws Exception {
        doNothing().when(teamService).deleteTeam(1L);

        mockMvc.perform(delete("/api/teams/1"))
                .andExpect(status().isNoContent());

        verify(teamService, times(1)).deleteTeam(1L);
    }

    @Test
    void getTeamMembers_ShouldReturnMembersList() throws Exception {
        when(teamService.getTeamMembers(1L)).thenReturn(Arrays.asList(testTeamMember));

        mockMvc.perform(get("/api/teams/1/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testuser"))
                .andExpect(jsonPath("$[0].role").value("DEVELOPER"));
    }

    @Test
    void getTeamMembers_WhenNoMembers_ShouldReturnEmptyList() throws Exception {
        when(teamService.getTeamMembers(1L)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/teams/1/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void addTeamMember_WithValidData_ShouldReturnCreatedMember() throws Exception {
        AddTeamMemberRequest request = AddTeamMemberRequest.builder()
                .userId(1L)
                .role("DEVELOPER")
                .build();

        when(teamService.addUserToTeam(eq(1L), eq(1L), eq("DEVELOPER"))).thenReturn(testTeamMember);

        mockMvc.perform(post("/api/teams/1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.role").value("DEVELOPER"));
    }

    @Test
    void removeTeamMember_ShouldReturnNoContent() throws Exception {
        doNothing().when(teamService).removeUserFromTeam(1L, 1L);

        mockMvc.perform(delete("/api/teams/1/members/1"))
                .andExpect(status().isNoContent());

        verify(teamService, times(1)).removeUserFromTeam(1L, 1L);
    }
}
