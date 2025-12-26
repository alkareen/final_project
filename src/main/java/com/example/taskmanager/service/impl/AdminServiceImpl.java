package com.example.taskmanager.service.impl;

import com.example.taskmanager.dto.user.UserAdminUpdateDto;
import com.example.taskmanager.dto.user.UserResponseDto;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.mapper.UserMapper;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@PreAuthorize("hasRole('ADMIN')")  // все методы только для ADMIN
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public UserResponseDto updateUserRoleAndStatus(Long userId, UserAdminUpdateDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRoles(dto.roles());
        user.setEnabled(dto.enabled());

        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(false);  // soft delete
        userRepository.save(user);
    }
}