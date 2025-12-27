package com.example.taskmanager.service;

import com.example.taskmanager.dto.project.ProjectCreateRequestDto;
import com.example.taskmanager.dto.project.ProjectResponseDto;
import com.example.taskmanager.entity.Project;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.mapper.ProjectMapper;
import com.example.taskmanager.repository.ProjectRepository;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.service.impl.ProjectServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectServiceImpl projectService;

    @Test
    void create_success() {
        User user = User.builder().id(1L).email("user@example.com").roles("USER").build();
        Project project = Project.builder().id(1L).name("Test Project").createdBy(user).build();
        ProjectResponseDto responseDto = new ProjectResponseDto(1L, "Test Project", null, 1L, "user@example.com", null);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(projectMapper.toEntity(any())).thenReturn(project);
        when(projectRepository.save(any())).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(responseDto);

        ProjectResponseDto result = projectService.create(new ProjectCreateRequestDto("Test Project", null), "user@example.com");

        assertNotNull(result);
        assertEquals("Test Project", result.name());
        verify(projectRepository).save(any());
    }

    @Test
    void create_notOwner_throwsException() {
        User user = User.builder().id(1L).email("user@example.com").roles("USER").build();
        User otherUser = User.builder().id(2L).build();
        Project otherProject = Project.builder().id(1L).createdBy(otherUser).build();

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                projectService.create(new ProjectCreateRequestDto("Test", null), "user@example.com"));

        assertTrue(exception.getMessage().contains("your own projects"));
    }
}