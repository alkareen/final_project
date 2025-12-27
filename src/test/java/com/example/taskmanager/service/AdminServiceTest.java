package com.example.taskmanager.service;

import com.example.taskmanager.dto.user.UserAdminUpdateDto;
import com.example.taskmanager.dto.user.UserResponseDto;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.mapper.UserMapper;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.service.impl.AdminServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AdminServiceImpl adminService;

    @Test
    void getAllUsers_success() {
        User user = User.builder().id(1L).email("user@example.com").roles("USER").build();
        UserResponseDto dto = new UserResponseDto(1L, "user@example.com", null, null, "USER", true);

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toDto(user)).thenReturn(dto);

        List<UserResponseDto> result = adminService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("user@example.com", result.get(0).email());
    }

    @Test
    void updateUserRoleAndStatus_success() {
        User user = User.builder().id(1L).roles("USER").enabled(true).build();
        UserAdminUpdateDto dto = new UserAdminUpdateDto("MANAGER", false);
        UserResponseDto responseDto = new UserResponseDto(1L, null, null, null, "MANAGER", false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(responseDto);

        UserResponseDto result = adminService.updateUserRoleAndStatus(1L, dto);

        assertEquals("MANAGER", result.roles());
        assertFalse(result.enabled());
        verify(userRepository).save(user);
    }

    @Test
    void updateUserRoleAndStatus_notFound_throwsException() {
        UserAdminUpdateDto dto = new UserAdminUpdateDto("MANAGER", true);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                adminService.updateUserRoleAndStatus(99L, dto));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void deleteUser_softDelete_success() {
        User user = User.builder().id(1L).enabled(true).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        adminService.deleteUser(1L);

        assertFalse(user.isEnabled());
        verify(userRepository).save(user);
    }
}