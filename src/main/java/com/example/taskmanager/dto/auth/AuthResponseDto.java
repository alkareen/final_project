package com.example.taskmanager.dto.auth;

public record AuthResponseDto(String token, String type) {

    // Конструктор сзначением по умолчанию для type
    public AuthResponseDto(String token) {
        this(token, "Bearer");
    }
}