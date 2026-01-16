package com.issuetracker.service;

import com.issuetracker.model.Team;
import com.issuetracker.model.TeamMember;
import com.issuetracker.model.User;
import com.issuetracker.repository.TeamMemberRepository;
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
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;

    public Team createTeam(Team team) {
        return teamRepository.save(team);
    }

    @Transactional(readOnly = true)
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Team> getTeamById(Long id) {
        return teamRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Team> getTeamByName(String name) {
        return teamRepository.findByName(name);
    }

    public Team updateTeam(Team team) {
        return teamRepository.save(team);
    }

    public void deleteTeam(Long id) {
        teamRepository.deleteById(id);
    }

    public TeamMember addUserToTeam(Long teamId, Long userId, String role) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (teamMemberRepository.existsByTeamIdAndUserId(teamId, userId)) {
            throw new RuntimeException("User is already a member of this team");
        }

        TeamMember teamMember = TeamMember.builder()
                .team(team)
                .user(user)
                .role(role != null ? role : "MEMBER")
                .build();

        return teamMemberRepository.save(teamMember);
    }

    public void removeUserFromTeam(Long teamId, Long userId) {
        TeamMember teamMember = teamMemberRepository.findByTeamIdAndUserId(teamId, userId)
                .orElseThrow(() -> new RuntimeException("Team member not found"));
        teamMemberRepository.delete(teamMember);
    }

    @Transactional(readOnly = true)
    public List<TeamMember> getTeamMembers(Long teamId) {
        return teamMemberRepository.findByTeamId(teamId);
    }

    @Transactional(readOnly = true)
    public List<TeamMember> getUserTeams(Long userId) {
        return teamMemberRepository.findByUserId(userId);
    }
}
