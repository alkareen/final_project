package com.example.taskmanager.mapper;

import com.example.taskmanager.dto.comment.CommentCreateRequestDto;
import com.example.taskmanager.dto.comment.CommentResponseDto;
import com.example.taskmanager.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "task", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Comment toEntity(CommentCreateRequestDto dto);

    @Mapping(source = "task.id", target = "taskId")
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "author.email", target = "authorEmail")
    CommentResponseDto toDto(Comment entity);
}