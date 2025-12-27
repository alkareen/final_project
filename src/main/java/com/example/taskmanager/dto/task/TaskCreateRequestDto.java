package com.example.taskmanager.dto.task;

import com.example.taskmanager.entity.TaskPriority;
import com.example.taskmanager.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.List;

public record TaskCreateRequestDto(

        @NotBlank(message = "Заголовок обязателен")
        String title,

        String description,

        TaskStatus status,

        TaskPriority priority,

        LocalDateTime deadline,

        Long projectId,

        Long assigneeId,

        List<Long> categoryIds  // ← НОВОЕ ПОЛЕ
) {}