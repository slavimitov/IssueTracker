package com.issuetracker.mapper;

import com.issuetracker.dto.TeamDTO;
import com.issuetracker.model.Team;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TeamMapper {
    TeamDTO toDTO(Team team);
}
