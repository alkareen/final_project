package com.example.taskmanager.dto.project;

import java.time.LocalDateTime;

public record ProjectResponseDto(

        Long id,
        String name,
        String description,
        Long createdById,
        String createdByEmail,
        LocalDateTime createdAt
) {}