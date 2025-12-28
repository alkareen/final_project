package com.example.taskmanager.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserAdminUpdateDto(

        @NotBlank
        String roles,

        @NotNull
        Boolean enabled
) {}