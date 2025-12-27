package com.example.taskmanager.service;

import com.example.taskmanager.dto.task.TaskCreateRequestDto;
import com.example.taskmanager.dto.task.TaskResponseDto;

import java.util.List;

public interface TaskService {

    TaskResponseDto create(TaskCreateRequestDto dto, String userEmail);

    List<TaskResponseDto> getMyTasks(String userEmail);

    List<TaskResponseDto> getTasksInProject(Long projectId, String userEmail);

    List<TaskResponseDto> getAllTasks();

    TaskResponseDto getById(Long id, String userEmail);

    TaskResponseDto update(Long id, TaskCreateRequestDto dto, String userEmail);

    void delete(Long id, String userEmail);
}