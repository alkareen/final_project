package com.example.taskmanager.dto.project;

import jakarta.validation.constraints.NotBlank;

public record ProjectCreateRequestDto(

        @NotBlank(message = "Название проекта обязательно")
        String name,

        String description
) {}