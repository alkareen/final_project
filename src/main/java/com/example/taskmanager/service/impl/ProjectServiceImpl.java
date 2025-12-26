package com.example.taskmanager.service.impl;

import com.example.taskmanager.dto.project.ProjectCreateRequestDto;
import com.example.taskmanager.dto.project.ProjectResponseDto;
import com.example.taskmanager.entity.Project;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.mapper.ProjectMapper;
import com.example.taskmanager.repository.ProjectRepository;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;

    @Override
    public ProjectResponseDto create(ProjectCreateRequestDto dto, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Project project = projectMapper.toEntity(dto);
        project.setCreatedBy(user);

        Project saved = projectRepository.save(project);
        return projectMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponseDto> getMyProjects(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return projectRepository.findAllByCreatedById(user.getId())
                .stream()
                .map(projectMapper::toDto)
                .toList();
    }

    @Override
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<ProjectResponseDto> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(projectMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponseDto getById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        return projectMapper.toDto(project);
    }

    // update и delete реализуем позже (с проверкой владельца)
    @Override
    public ProjectResponseDto update(Long id, ProjectCreateRequestDto dto, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Проверка, что пользователь — владелец проекта (или MANAGER/ADMIN — добавим позже)
        if (!project.getCreatedBy().getId().equals(user.getId())) {
            throw new RuntimeException("You can only update your own projects");
        }

        project.setName(dto.name());
        project.setDescription(dto.description());

        Project updated = projectRepository.save(project);
        return projectMapper.toDto(updated);

    }
    @Override
    public void delete(Long id, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getCreatedBy().getId().equals(user.getId())) {
            throw new RuntimeException("You can only delete your own projects");
        }

        projectRepository.delete(project);
    }

}