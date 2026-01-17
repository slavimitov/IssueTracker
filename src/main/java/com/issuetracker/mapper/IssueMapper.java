package com.issuetracker.mapper;

import com.issuetracker.model.Issue;
import com.issuetracker.dto.CreateIssueRequest;
import com.issuetracker.dto.IssueDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IssueMapper {

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "projectName", source = "project.name")
    @Mapping(target = "assigneeId", source = "assignee.id")
    @Mapping(target = "assigneeName", source = "assignee.username")
    @Mapping(target = "reporterId", source = "reporter.id")
    @Mapping(target = "reporterName", source = "reporter.username")
    IssueDTO toDTO(Issue issue);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "project.id", source = "projectId")
    @Mapping(target = "reporter.id", source = "reporterId")
    @Mapping(target = "assignee", ignore = true) // Assignee not set on create usually
    @Mapping(target = "sprint", ignore = true)
    @Mapping(target = "labels", ignore = true)
    Issue toEntity(CreateIssueRequest request);
}
