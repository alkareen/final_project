package com.example.taskmanager.service;

import com.example.taskmanager.dto.user.UserAdminUpdateDto;
import com.example.taskmanager.dto.user.UserResponseDto;

import java.util.List;

public interface AdminService {

    List<UserResponseDto> getAllUsers();

    UserResponseDto updateUserRoleAndStatus(Long userId, UserAdminUpdateDto dto);

    void deleteUser(Long userId);
}