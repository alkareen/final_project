package com.example.taskmanager.mapper;

import com.example.taskmanager.dto.project.ProjectCreateRequestDto;
import com.example.taskmanager.dto.project.ProjectResponseDto;
import com.example.taskmanager.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Project toEntity(ProjectCreateRequestDto dto);

    @Mapping(source = "createdBy.id", target = "createdById")
    @Mapping(source = "createdBy.email", target = "createdByEmail")
    ProjectResponseDto toDto(Project entity);
}