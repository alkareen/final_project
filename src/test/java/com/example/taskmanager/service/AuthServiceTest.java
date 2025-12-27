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
import org.springframework.security.crypto.password.PasswordEncoder;

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
    private UserDetails userDetails;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void register_success() {
        RegisterRequestDto dto = new RegisterRequestDto("test@example.com", "password123", "Test", "User");

        when(userRepository.existsByEmail(dto.email())).thenReturn(false);
        when(passwordEncoder.encode(dto.password())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn("fake-jwt-token");

        AuthResponseDto response = authService.register(dto);

        assertNotNull(response);
        assertEquals("fake-jwt-token", response.token());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_emailExists_throwsException() {
        RegisterRequestDto dto = new RegisterRequestDto("existing@example.com", "pass", "Name", "Last");

        when(userRepository.existsByEmail(dto.email())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.register(dto));
        assertEquals("Email already exists", exception.getMessage());
    }

    @Test
    void login_success() {
        LoginRequestDto dto = new LoginRequestDto("test@example.com", "password123");

        when(authenticationManager.authenticate(any())).thenReturn(null); // не бросает исключение
        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(jwtUtil.generateToken(userDetails)).thenReturn("fake-jwt-token");

        AuthResponseDto response = authService.login(dto);

        assertNotNull(response);
        assertEquals("fake-jwt-token", response.token());
    }

    @Test
    void login_wrongCredentials_throwsException() {
        LoginRequestDto dto = new LoginRequestDto("wrong@example.com", "wrong");

        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager).authenticate(any());

        assertThrows(RuntimeException.class, () -> authService.login(dto));
    }
}