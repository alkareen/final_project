package com.example.taskmanager.dto.category;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequestDto(

        @NotBlank
        String name,

        String description
) {}