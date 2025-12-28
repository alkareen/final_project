package com.example.taskmanager.dto.profile;

import jakarta.validation.constraints.Size;

public record UpdateProfileRequestDto(
        @Size(min = 1, max = 100) String firstName,
        @Size(min = 1, max = 100) String lastName
) {}