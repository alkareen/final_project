package com.example.taskmanager.service;

import com.example.taskmanager.dto.auth.LoginRequestDto;
import com.example.taskmanager.dto.auth.RegisterRequestDto;
import com.example.taskmanager.dto.auth.AuthResponseDto;
import com.example.taskmanager.dto.profile.AdminCreateUserRequestDto;
import com.example.taskmanager.dto.profile.ChangePasswordRequestDto;
import com.example.taskmanager.dto.profile.UpdateProfileRequestDto;
import com.example.taskmanager.dto.user.UserResponseDto;

public interface AuthService {

    AuthResponseDto register(RegisterRequestDto request);

    AuthResponseDto login(LoginRequestDto request);
    AuthResponseDto changePassword(ChangePasswordRequestDto dto, String userEmail);

    UserResponseDto updateProfile(UpdateProfileRequestDto dto, String userEmail);

    AuthResponseDto adminCreateUser(AdminCreateUserRequestDto dto);
}