package com.issuetracker.mapper;

import com.issuetracker.dto.RoleDTO;
import com.issuetracker.model.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleDTO toDTO(Role role);
}
