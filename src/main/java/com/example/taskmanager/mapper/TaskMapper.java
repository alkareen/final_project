package com.example.taskmanager.mapper;

import com.example.taskmanager.dto.task.TaskCreateRequestDto;
import com.example.taskmanager.dto.task.TaskResponseDto;
import com.example.taskmanager.entity.Task;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface TaskMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "status", constant = "NEW")
    @Mapping(target = "priority", constant = "MEDIUM")
    Task toEntity(TaskCreateRequestDto dto);

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "project.name", target = "projectName")
    @Mapping(source = "assignee.id", target = "assigneeId")
    @Mapping(source = "assignee.email", target = "assigneeEmail")
    @Mapping(source = "createdBy.id", target = "createdById")
    @Mapping(source = "createdBy.email", target = "createdByEmail")
    TaskResponseDto toDto(Task entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(TaskCreateRequestDto dto, @MappingTarget Task entity);
}