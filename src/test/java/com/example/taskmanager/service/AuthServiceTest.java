package com.example.taskmanager.service;

import com.example.taskmanager.dto.auth.LoginRequestDto;
import com.example.taskmanager.dto.auth.RegisterRequestDto;
import com.example.taskmanager.dto.auth.AuthResponseDto;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.security.JwtUtil;
import com.example.taskmanager.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void register_success() {
        RegisterRequestDto dto = new RegisterRequestDto(
                "test@example.com",
                "password123",
                "Test",
                "User"
        );

        when(userRepository.existsByEmail(dto.email())).thenReturn(false);

        when(passwordEncoder.encode(dto.password())).thenReturn("encoded-password");

        User savedUser = User.builder()
                .id(1L)
                .email(dto.email())
                .password("encoded-password")
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .roles("USER")
                .enabled(true)
                .build();

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        when(userDetailsService.loadUserByUsername(dto.email())).thenReturn(userDetails);

        when(jwtUtil.generateToken(userDetails)).thenReturn("fake-jwt-token");

        AuthResponseDto response = authService.register(dto);

        assertNotNull(response);
        assertEquals("fake-jwt-token", response.token());

        verify(userRepository).save(any(User.class));
        verify(jwtUtil).generateToken(userDetails);
    }

    @Test
    void register_emailExists_throwsException() {
        RegisterRequestDto dto = new RegisterRequestDto(
                "existing@example.com",
                "pass",
                "Name",
                "Last"
        );

        when(userRepository.existsByEmail(dto.email())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                authService.register(dto));

        assertEquals("Email already exists", exception.getMessage());
    }

    @Test
    void login_success() {
        LoginRequestDto dto = new LoginRequestDto("test@example.com", "password123");

        when(authenticationManager.authenticate(any())).thenReturn(null);

        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);

        when(jwtUtil.generateToken(userDetails)).thenReturn("fake-jwt-token");

        AuthResponseDto response = authService.login(dto);

        assertNotNull(response);
        assertEquals("fake-jwt-token", response.token());

        verify(authenticationManager).authenticate(any());
        verify(jwtUtil).generateToken(userDetails);
    }

    @Test
    void login_wrongCredentials_throwsException() {
        LoginRequestDto dto = new LoginRequestDto("wrong@example.com", "wrong");

        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager)
                .authenticate(any());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                authService.login(dto));

        assertTrue(exception.getMessage().contains("Invalid email or password") ||
                exception.getMessage().contains("Bad credentials"));
    }
}