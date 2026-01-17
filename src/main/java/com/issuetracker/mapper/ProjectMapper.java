package com.issuetracker.mapper;

import com.issuetracker.dto.ProjectDTO;
import com.issuetracker.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(target = "teamId", expression = "java(project.getTeam() != null ? project.getTeam().getId() : null)")
    @Mapping(target = "teamName", expression = "java(project.getTeam() != null ? project.getTeam().getName() : null)")
    @Mapping(target = "leadId", expression = "java(project.getLead() != null ? project.getLead().getId() : null)")
    @Mapping(target = "leadUsername", expression = "java(project.getLead() != null ? project.getLead().getUsername() : null)")
    ProjectDTO toDTO(Project project);
}
