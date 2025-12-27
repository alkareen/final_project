package com.example.taskmanager.dto.task;

import com.example.taskmanager.dto.category.CategoryResponseDto;
import com.example.taskmanager.entity.TaskPriority;
import com.example.taskmanager.entity.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;

public record TaskResponseDto(

        Long id,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        LocalDateTime deadline,
        Long projectId,
        String projectName,
        Long assigneeId,
        String assigneeEmail,
        Long createdById,
        String createdByEmail,
        LocalDateTime createdAt,

        List<CategoryResponseDto> categories
) {}