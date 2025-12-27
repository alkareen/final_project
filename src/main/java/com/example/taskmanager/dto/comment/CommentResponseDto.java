package com.example.taskmanager.dto.comment;

import java.time.LocalDateTime;

public record CommentResponseDto(

        Long id,
        String text,
        Long taskId,
        Long authorId,
        String authorEmail,
        LocalDateTime createdAt
) {}