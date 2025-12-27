package com.example.taskmanager.service;

import com.example.taskmanager.dto.comment.CommentCreateRequestDto;
import com.example.taskmanager.dto.comment.CommentResponseDto;

import java.util.List;

public interface CommentService {

    CommentResponseDto create(CommentCreateRequestDto dto, String userEmail);

    List<CommentResponseDto> getByTaskId(Long taskId, String userEmail);

    void delete(Long id, String userEmail);
}