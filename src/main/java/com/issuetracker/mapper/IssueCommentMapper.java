package com.issuetracker.mapper;

import com.issuetracker.model.IssueComment;
import com.issuetracker.dto.CommentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IssueCommentMapper {
    @Mapping(target = "authorName", source = "author.username")
    CommentDTO toDTO(IssueComment comment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "issue", ignore = true)
    @Mapping(target = "author", ignore = true) // Author set in service/controller
    IssueComment toEntity(CommentDTO dto);
}
