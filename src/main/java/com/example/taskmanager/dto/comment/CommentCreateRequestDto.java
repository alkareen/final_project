package com.example.taskmanager.dto.comment;

import jakarta.validation.constraints.NotBlank;

public record CommentCreateRequestDto(

        @NotBlank(message = "Текст комментария обязателен")
        String text,

        Long taskId
) {}