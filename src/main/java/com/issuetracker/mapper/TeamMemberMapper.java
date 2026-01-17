package com.issuetracker.mapper;

import com.issuetracker.dto.TeamMemberDTO;
import com.issuetracker.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TeamMemberMapper {

    @Mapping(target = "teamId", expression = "java(member.getTeam() != null ? member.getTeam().getId() : null)")
    @Mapping(target = "teamName", expression = "java(member.getTeam() != null ? member.getTeam().getName() : null)")
    @Mapping(target = "userId", expression = "java(member.getUser() != null ? member.getUser().getId() : null)")
    @Mapping(target = "username", expression = "java(member.getUser() != null ? member.getUser().getUsername() : null)")
    TeamMemberDTO toDTO(TeamMember member);
}
