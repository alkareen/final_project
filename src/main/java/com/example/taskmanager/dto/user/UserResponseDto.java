package com.example.taskmanager.dto.user;

import com.example.taskmanager.entity.Role;

public record UserResponseDto(

        Long id,
        String email,
        String firstName,
        String lastName,
        String roles,
        boolean enabled
) {}