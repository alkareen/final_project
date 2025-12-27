package com.example.taskmanager.service;

import com.example.taskmanager.dto.comment.CommentCreateRequestDto;
import com.example.taskmanager.dto.comment.CommentResponseDto;
import com.example.taskmanager.entity.*;
import com.example.taskmanager.mapper.CommentMapper;
import com.example.taskmanager.repository.CommentRepository;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.service.impl.CommentServiceImpl;
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
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    void create_success_whenUserIsAssignee() {
        // Подготовка пользователей
        User author = User.builder()
                .id(1L)
                .email("user@example.com")
                .roles("USER")
                .build();

        User projectOwner = User.builder().id(2L).roles("USER").build();

        Project project = Project.builder()
                .id(1L)
                .createdBy(projectOwner)
                .build();

        Task task = Task.builder()
                .id(1L)
                .assignee(author)
                .createdBy(projectOwner)
                .project(project)
                .build();

        Comment comment = Comment.builder()
                .id(1L)
                .text("Тестовый комментарий")
                .author(author)
                .task(task)
                .build();

        CommentResponseDto responseDto = new CommentResponseDto(
                1L, "Тестовый комментарий", 1L, 1L, "user@example.com", null
        );

        CommentCreateRequestDto dto = new CommentCreateRequestDto("Тестовый комментарий", 1L);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(author));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(commentMapper.toEntity(dto)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toDto(comment)).thenReturn(responseDto);

        CommentResponseDto result = commentService.create(dto, "user@example.com");

        assertNotNull(result);
        assertEquals("Тестовый комментарий", result.text());
        verify(commentRepository).save(comment);
    }

    @Test
    void create_forbidden_whenNoAccess() {
        User author = User.builder().id(1L).email("user@example.com").roles("USER").build();
        User otherUser = User.builder().id(2L).roles("USER").build();
        Project project = Project.builder().id(1L).createdBy(otherUser).build();
        Task task = Task.builder()
                .id(1L)
                .createdBy(otherUser)
                .project(project)
                .assignee(null)
                .build();

        CommentCreateRequestDto dto = new CommentCreateRequestDto("Текст", 1L);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(author));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                commentService.create(dto, "user@example.com"));

        assertEquals("You can only comment on your tasks or assigned tasks", exception.getMessage());
    }

    @Test
    void delete_success_whenAuthor() {
        User author = User.builder()
                .id(1L)
                .email("user@example.com")
                .roles("USER")
                .build();

        Comment comment = Comment.builder()
                .id(1L)
                .author(author)
                .build();

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(author));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        commentService.delete(1L, "user@example.com");

        verify(commentRepository).delete(comment);
    }

    @Test
    void delete_forbidden_whenNotAuthorAndNotAdmin() {
        User author = User.builder().id(1L).roles("USER").build();
        User otherUser = User.builder().id(2L).roles("USER").build();
        Comment comment = Comment.builder().id(1L).author(author).build();

        when(userRepository.findByEmail("other@example.com")).thenReturn(Optional.of(otherUser));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                commentService.delete(1L, "other@example.com"));

        assertEquals("You can only delete your own comments", exception.getMessage());
    }

    @Test
    void delete_success_whenManager() {
        User manager = User.builder().id(1L).roles("MANAGER").build();
        User commentAuthor = User.builder().id(2L).build();
        Comment comment = Comment.builder().id(1L).author(commentAuthor).build();

        when(userRepository.findByEmail("manager@example.com")).thenReturn(Optional.of(manager));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        commentService.delete(1L, "manager@example.com");

        verify(commentRepository).delete(comment);
    }
}