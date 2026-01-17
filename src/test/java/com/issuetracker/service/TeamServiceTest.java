package com.issuetracker.service;

import com.issuetracker.model.Team;
import com.issuetracker.model.TeamMember;
import com.issuetracker.model.User;
import com.issuetracker.repository.TeamMemberRepository;
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
class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
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
                .password("password")
                .build();

        testTeamMember = TeamMember.builder()
                .id(1L)
                .team(testTeam)
                .user(testUser)
                .role("DEVELOPER")
                .build();
    }

    @Test
    void createTeam_ShouldReturnSavedTeam() {
        when(teamRepository.save(any(Team.class))).thenReturn(testTeam);

        Team result = teamService.createTeam(testTeam);

        assertNotNull(result);
        assertEquals("Development Team", result.getName());
        verify(teamRepository, times(1)).save(any(Team.class));
    }

    @Test
    void getAllTeams_ShouldReturnTeamList() {
        when(teamRepository.findAll()).thenReturn(Arrays.asList(testTeam));

        List<Team> result = teamService.getAllTeams();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(teamRepository, times(1)).findAll();
    }

    @Test
    void getTeamById_WhenTeamExists_ShouldReturnTeam() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(testTeam));

        Optional<Team> result = teamService.getTeamById(1L);

        assertTrue(result.isPresent());
        assertEquals("Development Team", result.get().getName());
    }

    @Test
    void getTeamById_WhenTeamNotExists_ShouldReturnEmpty() {
        when(teamRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Team> result = teamService.getTeamById(99L);

        assertFalse(result.isPresent());
    }

    @Test
    void getTeamByName_WhenExists_ShouldReturnTeam() {
        when(teamRepository.findByName("Development Team")).thenReturn(Optional.of(testTeam));

        Optional<Team> result = teamService.getTeamByName("Development Team");

        assertTrue(result.isPresent());
        assertEquals("Development Team", result.get().getName());
    }

    @Test
    void getTeamByName_WhenNotExists_ShouldReturnEmpty() {
        when(teamRepository.findByName("Unknown")).thenReturn(Optional.empty());

        Optional<Team> result = teamService.getTeamByName("Unknown");

        assertFalse(result.isPresent());
    }

    @Test
    void updateTeam_ShouldReturnUpdatedTeam() {
        testTeam.setDescription("Updated description");
        when(teamRepository.save(any(Team.class))).thenReturn(testTeam);

        Team result = teamService.updateTeam(testTeam);

        assertNotNull(result);
        assertEquals("Updated description", result.getDescription());
        verify(teamRepository, times(1)).save(any(Team.class));
    }

    @Test
    void deleteTeam_ShouldCallRepositoryDelete() {
        doNothing().when(teamRepository).deleteById(1L);

        teamService.deleteTeam(1L);

        verify(teamRepository, times(1)).deleteById(1L);
    }

    @Test
    void addUserToTeam_ShouldCreateTeamMember() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(testTeam));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(teamMemberRepository.existsByTeamIdAndUserId(1L, 1L)).thenReturn(false);
        when(teamMemberRepository.save(any(TeamMember.class))).thenAnswer(invocation -> {
            TeamMember tm = invocation.getArgument(0);
            tm.setId(1L);
            return tm;
        });

        TeamMember result = teamService.addUserToTeam(1L, 1L, "DEVELOPER");

        assertNotNull(result);
        assertEquals("DEVELOPER", result.getRole());
        verify(teamMemberRepository, times(1)).save(any(TeamMember.class));
    }

    @Test
    void addUserToTeam_WithNullRole_ShouldUseMemberAsDefault() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(testTeam));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(teamMemberRepository.existsByTeamIdAndUserId(1L, 1L)).thenReturn(false);
        when(teamMemberRepository.save(any(TeamMember.class))).thenAnswer(invocation -> {
            TeamMember tm = invocation.getArgument(0);
            tm.setId(1L);
            return tm;
        });

        TeamMember result = teamService.addUserToTeam(1L, 1L, null);

        assertNotNull(result);
        assertEquals("MEMBER", result.getRole());
    }

    @Test
    void addUserToTeam_WhenAlreadyMember_ShouldThrowException() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(testTeam));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(teamMemberRepository.existsByTeamIdAndUserId(1L, 1L)).thenReturn(true);

        assertThrows(RuntimeException.class, () -> {
            teamService.addUserToTeam(1L, 1L, "DEVELOPER");
        });
    }

    @Test
    void addUserToTeam_WhenTeamNotFound_ShouldThrowException() {
        when(teamRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            teamService.addUserToTeam(99L, 1L, "DEVELOPER");
        });
    }

    @Test
    void addUserToTeam_WhenUserNotFound_ShouldThrowException() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(testTeam));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            teamService.addUserToTeam(1L, 99L, "DEVELOPER");
        });
    }

    @Test
    void removeUserFromTeam_ShouldDeleteTeamMember() {
        when(teamMemberRepository.findByTeamIdAndUserId(1L, 1L)).thenReturn(Optional.of(testTeamMember));
        doNothing().when(teamMemberRepository).delete(any(TeamMember.class));

        teamService.removeUserFromTeam(1L, 1L);

        verify(teamMemberRepository, times(1)).delete(any(TeamMember.class));
    }

    @Test
    void removeUserFromTeam_WhenNotMember_ShouldThrowException() {
        when(teamMemberRepository.findByTeamIdAndUserId(1L, 99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            teamService.removeUserFromTeam(1L, 99L);
        });
    }

    @Test
    void getTeamMembers_ShouldReturnMembersList() {
        when(teamMemberRepository.findByTeamId(1L)).thenReturn(Arrays.asList(testTeamMember));

        List<TeamMember> result = teamService.getTeamMembers(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("DEVELOPER", result.get(0).getRole());
    }

    @Test
    void getTeamMembers_WhenNoMembers_ShouldReturnEmptyList() {
        when(teamMemberRepository.findByTeamId(99L)).thenReturn(Arrays.asList());

        List<TeamMember> result = teamService.getTeamMembers(99L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getUserTeams_ShouldReturnUserMemberships() {
        when(teamMemberRepository.findByUserId(1L)).thenReturn(Arrays.asList(testTeamMember));

        List<TeamMember> result = teamService.getUserTeams(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Development Team", result.get(0).getTeam().getName());
    }

    @Test
    void getUserTeams_WhenNoMemberships_ShouldReturnEmptyList() {
        when(teamMemberRepository.findByUserId(99L)).thenReturn(Arrays.asList());

        List<TeamMember> result = teamService.getUserTeams(99L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
