package com.issuetracker.mapper;

import com.issuetracker.dto.SprintDTO;
import com.issuetracker.model.Sprint;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SprintMapper {

    @Mapping(target = "projectId", expression = "java(sprint.getProject() != null ? sprint.getProject().getId() : null)")
    @Mapping(target = "projectName", expression = "java(sprint.getProject() != null ? sprint.getProject().getName() : null)")
    @Mapping(target = "projectKey", expression = "java(sprint.getProject() != null ? sprint.getProject().getKey() : null)")
    SprintDTO toDTO(Sprint sprint);
}
