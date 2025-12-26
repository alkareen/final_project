package com.example.taskmanager.service;

import com.example.taskmanager.dto.auth.LoginRequestDto;
import com.example.taskmanager.dto.auth.RegisterRequestDto;
import com.example.taskmanager.dto.auth.AuthResponseDto;

public interface AuthService {

    AuthResponseDto register(RegisterRequestDto request);

    AuthResponseDto login(LoginRequestDto request);
}