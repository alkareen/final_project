package com.example.taskmanager.dto.profile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AdminCreateUserRequestDto(
        @NotBlank @Email String email,
        @NotBlank String password,
        String firstName,
        String lastName,
        String roles
) {}