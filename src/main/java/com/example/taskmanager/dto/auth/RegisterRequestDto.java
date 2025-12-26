package com.example.taskmanager.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDto(

        @Email(message = "Неверный формат email")
        @NotBlank(message = "Email обязателен")
        String email,

        @NotBlank(message = "Пароль обязателен")
        @Size(min = 6, message = "Пароль должен быть не менее 6 символов")
        String password,

        String firstName,

        String lastName
) {}