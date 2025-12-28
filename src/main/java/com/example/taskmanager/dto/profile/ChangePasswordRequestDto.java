package com.example.taskmanager.dto.profile;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequestDto(
        @NotBlank String oldPassword,
        @NotBlank String newPassword
) {}