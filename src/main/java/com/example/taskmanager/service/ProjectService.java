package com.example.taskmanager.service;

import com.example.taskmanager.dto.project.ProjectCreateRequestDto;
import com.example.taskmanager.dto.project.ProjectResponseDto;

import java.util.List;

public interface ProjectService {

    ProjectResponseDto create(ProjectCreateRequestDto dto, String userEmail);

    List<ProjectResponseDto> getMyProjects(String userEmail);

    List<ProjectResponseDto> getAllProjects();

    ProjectResponseDto getById(Long id);

    ProjectResponseDto update(Long id, ProjectCreateRequestDto dto, String userEmail);

    void delete(Long id, String userEmail);
}