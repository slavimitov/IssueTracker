package com.issuetracker.controller;

import com.issuetracker.dto.AddTeamMemberRequest;
import com.issuetracker.dto.CreateTeamRequest;
import com.issuetracker.dto.TeamDTO;
import com.issuetracker.dto.TeamMemberDTO;
import com.issuetracker.model.Team;
import com.issuetracker.model.TeamMember;
import com.issuetracker.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @GetMapping
    public ResponseEntity<List<TeamDTO>> getAllTeams() {
        List<TeamDTO> teams = teamService.getAllTeams().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamDTO> getTeamById(@PathVariable Long id) {
        return teamService.getTeamById(id)
                .map(team -> ResponseEntity.ok(toDTO(team)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TeamDTO> createTeam(@Valid @RequestBody CreateTeamRequest request) {
        Team team = Team.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        Team savedTeam = teamService.createTeam(team);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(savedTeam));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{teamId}/members")
    public ResponseEntity<List<TeamMemberDTO>> getTeamMembers(@PathVariable Long teamId) {
        List<TeamMemberDTO> members = teamService.getTeamMembers(teamId).stream()
                .map(this::toMemberDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(members);
    }

    @PostMapping("/{teamId}/members")
    public ResponseEntity<TeamMemberDTO> addTeamMember(
            @PathVariable Long teamId,
            @Valid @RequestBody AddTeamMemberRequest request) {
        TeamMember member = teamService.addUserToTeam(teamId, request.getUserId(), request.getRole());
        return ResponseEntity.status(HttpStatus.CREATED).body(toMemberDTO(member));
    }

    @DeleteMapping("/{teamId}/members/{userId}")
    public ResponseEntity<Void> removeTeamMember(@PathVariable Long teamId, @PathVariable Long userId) {
        teamService.removeUserFromTeam(teamId, userId);
        return ResponseEntity.noContent().build();
    }

    private TeamDTO toDTO(Team team) {
        return TeamDTO.builder()
                .id(team.getId())
                .name(team.getName())
                .description(team.getDescription())
                .createdAt(team.getCreatedAt())
                .build();
    }

    private TeamMemberDTO toMemberDTO(TeamMember member) {
        return TeamMemberDTO.builder()
                .id(member.getId())
                .teamId(member.getTeam().getId())
                .teamName(member.getTeam().getName())
                .userId(member.getUser().getId())
                .username(member.getUser().getUsername())
                .role(member.getRole())
                .joinedAt(member.getJoinedAt())
                .build();
    }
}
