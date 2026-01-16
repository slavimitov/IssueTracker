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
    void addUserToTeam_WhenAlreadyMember_ShouldThrowException() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(testTeam));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(teamMemberRepository.existsByTeamIdAndUserId(1L, 1L)).thenReturn(true);

        assertThrows(RuntimeException.class, () -> {
            teamService.addUserToTeam(1L, 1L, "DEVELOPER");
        });
    }
}
