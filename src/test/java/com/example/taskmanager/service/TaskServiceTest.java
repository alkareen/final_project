package com.example.taskmanager.service;

import com.example.taskmanager.dto.task.TaskCreateRequestDto;
import com.example.taskmanager.dto.task.TaskResponseDto;
import com.example.taskmanager.entity.*;
import com.example.taskmanager.mapper.TaskMapper;
import com.example.taskmanager.repository.*;
import com.example.taskmanager.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskServiceImpl taskService;

    private User createUser(Long id, String email, String roles) {
        return User.builder()
                .id(id)
                .email(email)
                .roles(roles)
                .build();
    }

    private Project createProject(Long id, User owner) {
        return Project.builder()
                .id(id)
                .name("Test Project")
                .createdBy(owner)
                .build();
    }

    private Task createTask(Long id, User creator, Project project, User assignee) {
        return Task.builder()
                .id(id)
                .title("Test Task")
                .project(project)
                .createdBy(creator)
                .assignee(assignee)
                .build();
    }

    @Test
    void create_success_userInOwnProject() {
        User user = createUser(1L, "user@example.com", "USER");
        Project project = createProject(1L, user);

        TaskCreateRequestDto dto = new TaskCreateRequestDto(
                "New Task", "Desc", null, null, null, 1L, null, null
        );

        Task task = createTask(null, user, project, null);
        Task savedTask = createTask(1L, user, project, null);
        TaskResponseDto responseDto = new TaskResponseDto(1L, "New Task", "Desc", TaskStatus.NEW, TaskPriority.MEDIUM,
                null, 1L, "Test Project", null, null, 1L, "user@example.com", null, List.of());

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(taskMapper.toEntity(dto)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(savedTask);
        when(taskMapper.toDto(savedTask)).thenReturn(responseDto);

        TaskResponseDto result = taskService.create(dto, "user@example.com");

        assertNotNull(result);
        assertEquals("New Task", result.title());
        verify(taskRepository).save(task);
    }

    @Test
    void create_success_managerInAnyProject() {
        User manager = createUser(1L, "manager@example.com", "MANAGER");
        User projectOwner = createUser(2L, "owner@example.com", "USER");
        Project project = createProject(1L, projectOwner);

        TaskCreateRequestDto dto = new TaskCreateRequestDto(
                "Task", null, null, null, null, 1L, 3L, List.of(1L)
        );

        User assignee = createUser(3L, "assignee@example.com", "USER");
        Category category = Category.builder().id(1L).name("Work").build();

        Task task = new Task();
        Task savedTask = createTask(1L, manager, project, assignee);
        savedTask.setCategories(Set.of(category));

        TaskResponseDto responseDto = new TaskResponseDto(1L, "Task", null, TaskStatus.NEW, TaskPriority.MEDIUM,
                null, 1L, "Test Project", 3L, "assignee@example.com", 1L, "manager@example.com", null, List.of());

        when(userRepository.findByEmail("manager@example.com")).thenReturn(Optional.of(manager));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findById(3L)).thenReturn(Optional.of(assignee));
        when(categoryRepository.findAllById(List.of(1L))).thenReturn(List.of(category));
        when(taskMapper.toEntity(dto)).thenReturn(task);
        when(taskRepository.save(any())).thenReturn(savedTask);
        when(taskMapper.toDto(savedTask)).thenReturn(responseDto);

        TaskResponseDto result = taskService.create(dto, "manager@example.com");

        assertNotNull(result);
        assertEquals(3L, result.assigneeId());
        verify(taskRepository).save(any());
    }

    @Test
    void create_forbidden_userInForeignProject() {
        User user = createUser(1L, "user@example.com", "USER");
        User owner = createUser(2L, "owner@example.com", "USER");
        Project project = createProject(1L, owner);

        TaskCreateRequestDto dto = new TaskCreateRequestDto("Task", null, null, null, null, 1L, null, null);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                taskService.create(dto, "user@example.com"));

        assertEquals("You can only create tasks in your own projects", exception.getMessage());
    }

    @Test
    void update_success_managerChangesAssigneeAndCategories() {
        User manager = createUser(1L, "manager@example.com", "MANAGER");
        User creator = createUser(2L, "creator@example.com", "USER");
        Project project = createProject(1L, creator);
        Task task = createTask(1L, creator, project, null);

        TaskCreateRequestDto dto = new TaskCreateRequestDto(
                "Updated Task", "New desc", TaskStatus.IN_PROGRESS, TaskPriority.HIGH,
                LocalDateTime.now(), null, 4L, List.of(2L)
        );

        User newAssignee = createUser(4L, "new@example.com", "USER");
        Category newCategory = Category.builder().id(2L).name("Urgent").build();

        Task updatedTask = task;
        updatedTask.setTitle("Updated Task");
        updatedTask.setStatus(TaskStatus.IN_PROGRESS);
        updatedTask.setPriority(TaskPriority.HIGH);
        updatedTask.setAssignee(newAssignee);
        updatedTask.setCategories(Set.of(newCategory));

        TaskResponseDto responseDto = new TaskResponseDto(1L, "Updated Task", "New desc", TaskStatus.IN_PROGRESS,
                TaskPriority.HIGH, null, 1L, "Test Project", 4L, "new@example.com", 2L, "creator@example.com", null, List.of());

        when(userRepository.findByEmail("manager@example.com")).thenReturn(Optional.of(manager));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepository.findById(4L)).thenReturn(Optional.of(newAssignee));
        when(categoryRepository.findAllById(List.of(2L))).thenReturn(List.of(newCategory));
        when(taskRepository.save(task)).thenReturn(updatedTask);
        when(taskMapper.toDto(updatedTask)).thenReturn(responseDto);

        TaskResponseDto result = taskService.update(1L, dto, "manager@example.com");

        assertNotNull(result);
        assertEquals(TaskStatus.IN_PROGRESS, result.status());
        assertEquals(4L, result.assigneeId());
    }

    @Test
    void update_forbidden_userChangesAssignee() {
        User user = createUser(1L, "user@example.com", "USER");
        Task task = createTask(1L, user, createProject(1L, user), null);

        TaskCreateRequestDto dto = new TaskCreateRequestDto(
                "Task", null, null, null, null, null, 999L, null
        );

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                taskService.update(1L, dto, "user@example.com"));

        assertEquals("Only MANAGER or ADMIN can change assignee", exception.getMessage());
    }

    @Test
    void delete_success_projectOwner() {
        User owner = createUser(1L, "owner@example.com", "USER");
        Project project = createProject(1L, owner);
        Task task = createTask(1L, createUser(2L, "creator@example.com", "USER"), project, null);

        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        taskService.delete(1L, "owner@example.com");

        verify(taskRepository).delete(task);
    }

    @Test
    void getById_success_assigneeCanView() {
        User assignee = createUser(1L, "assignee@example.com", "USER");
        Task task = createTask(1L, createUser(2L, "creator", "USER"), createProject(1L, createUser(3L, "owner", "USER")), assignee);

        TaskResponseDto responseDto = new TaskResponseDto(1L, "Task", null, TaskStatus.NEW, TaskPriority.MEDIUM,
                null, 1L, "Project", 1L, "assignee@example.com", 2L, "creator", null, List.of());

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepository.findByEmail("assignee@example.com")).thenReturn(Optional.of(assignee));
        when(taskMapper.toDto(task)).thenReturn(responseDto);

        TaskResponseDto result = taskService.getById(1L, "assignee@example.com");

        assertNotNull(result);
    }
}