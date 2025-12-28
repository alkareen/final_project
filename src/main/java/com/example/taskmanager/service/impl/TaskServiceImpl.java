package com.example.taskmanager.service.impl;

import com.example.taskmanager.dto.task.TaskCreateRequestDto;
import com.example.taskmanager.dto.task.TaskResponseDto;
import com.example.taskmanager.entity.*;
import com.example.taskmanager.mapper.TaskMapper;
import com.example.taskmanager.repository.*;
import com.example.taskmanager.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TaskMapper taskMapper;

    @Override
    public TaskResponseDto create(TaskCreateRequestDto dto, String userEmail) {
        User currentUser = getUserByEmail(userEmail);

        Project project = projectRepository.findById(dto.projectId())
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + dto.projectId()));

        boolean isManagerOrAdmin = isManagerOrAdmin(currentUser);
        boolean isProjectOwner = project.getCreatedBy().getId().equals(currentUser.getId());

        if (!isManagerOrAdmin && !isProjectOwner) {
            throw new RuntimeException("You can only create tasks in your own projects");
        }

        Task task = taskMapper.toEntity(dto);
        task.setProject(project);
        task.setCreatedBy(currentUser);

        if (dto.assigneeId() != null) {
            if (!isManagerOrAdmin && !dto.assigneeId().equals(currentUser.getId())) {
                throw new RuntimeException("You can only assign the task to yourself");
            }
            User assignee = getUserById(dto.assigneeId());
            task.setAssignee(assignee);
        }

        if (dto.status() != null) {
            task.setStatus(dto.status());
        }
        if (dto.priority() != null) {
            task.setPriority(dto.priority());
        }

        if (dto.categoryIds() != null && !dto.categoryIds().isEmpty()) {
            Set<Category> categories = new HashSet<>(
                    categoryRepository.findAllById(dto.categoryIds())
            );
            if (categories.size() != dto.categoryIds().size()) {
                throw new RuntimeException("One or more category IDs not found");
            }
            task.setCategories(categories);
        }

        Task saved = taskRepository.save(task);
        return taskMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDto> getMyTasks(String userEmail) {
        User user = getUserByEmail(userEmail);
        return taskRepository.findAllByCreatedById(user.getId())
                .stream()
                .map(taskMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDto> getTasksInProject(Long projectId, String userEmail) {
        User user = getUserByEmail(userEmail);
        Project project = getProjectById(projectId);

        boolean isManagerOrAdmin = isManagerOrAdmin(user);
        boolean isProjectOwner = project.getCreatedBy().getId().equals(user.getId());

        if (!isManagerOrAdmin && !isProjectOwner) {
            throw new RuntimeException("Access denied to tasks in this project");
        }

        return taskRepository.findAllByProjectId(projectId)
                .stream()
                .map(taskMapper::toDto)
                .toList();
    }

    @Override
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<TaskResponseDto> getAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(taskMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponseDto getById(Long id, String userEmail) {
        Task task = getTaskById(id);
        User user = getUserByEmail(userEmail);

        boolean isManagerOrAdmin = isManagerOrAdmin(user);
        boolean isOwner = task.getCreatedBy().getId().equals(user.getId());
        boolean isAssignee = task.getAssignee() != null && task.getAssignee().getId().equals(user.getId());
        boolean isProjectOwner = task.getProject().getCreatedBy().getId().equals(user.getId());

        if (!isManagerOrAdmin && !isOwner && !isAssignee && !isProjectOwner) {
            throw new RuntimeException("Access denied to this task");
        }

        return taskMapper.toDto(task);
    }

    @Override
    public TaskResponseDto update(Long id, TaskCreateRequestDto dto, String userEmail) {
        User user = getUserByEmail(userEmail);
        Task task = getTaskById(id);

        boolean isManagerOrAdmin = isManagerOrAdmin(user);
        boolean isOwner = task.getCreatedBy().getId().equals(user.getId());
        boolean isAssignee = task.getAssignee() != null && task.getAssignee().getId().equals(user.getId());
        boolean isProjectOwner = task.getProject().getCreatedBy().getId().equals(user.getId());

        if (!isManagerOrAdmin && !isOwner && !isAssignee && !isProjectOwner) {
            throw new RuntimeException("You don't have permission to update this task");
        }

        task.setTitle(dto.title());
        task.setDescription(dto.description());

        if (dto.status() != null) {
            task.setStatus(dto.status());
        }
        if (dto.priority() != null) {
            task.setPriority(dto.priority());
        }
        if (dto.deadline() != null) {
            task.setDeadline(dto.deadline());
        }

        if (dto.projectId() != null && isManagerOrAdmin) {
            Project newProject = getProjectById(dto.projectId());
            task.setProject(newProject);
        }

        if (dto.assigneeId() != null) {
            if (!isManagerOrAdmin) {
                throw new RuntimeException("Only MANAGER or ADMIN can change assignee");
            }
            User assignee = getUserById(dto.assigneeId());
            task.setAssignee(assignee);
        }

        if (dto.categoryIds() != null) {
            Set<Category> categories = new HashSet<>(
                    categoryRepository.findAllById(dto.categoryIds())
            );
            if (categories.size() != dto.categoryIds().size()) {
                throw new RuntimeException("One or more category IDs not found");
            }
            task.setCategories(categories);
        }

        Task updated = taskRepository.save(task);
        return taskMapper.toDto(updated);
    }

    @Override
    public void delete(Long id, String userEmail) {
        User user = getUserByEmail(userEmail);
        Task task = getTaskById(id);

        boolean isManagerOrAdmin = isManagerOrAdmin(user);
        boolean isOwner = task.getCreatedBy().getId().equals(user.getId());
        boolean isProjectOwner = task.getProject().getCreatedBy().getId().equals(user.getId());

        if (!isManagerOrAdmin && !isOwner && !isProjectOwner) {
            throw new RuntimeException("You can only delete your own tasks or tasks in your projects");
        }

        taskRepository.delete(task);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }

    private Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    private boolean isManagerOrAdmin(User user) {
        String roles = user.getRoles();
        return roles.contains("MANAGER") || roles.contains("ADMIN");
    }
}