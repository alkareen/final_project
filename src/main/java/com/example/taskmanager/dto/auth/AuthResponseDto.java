package com.example.taskmanager.dto.auth;

public record AuthResponseDto(String token, String type) {

    public AuthResponseDto(String token) {
        this(token, "Bearer");
    }
}