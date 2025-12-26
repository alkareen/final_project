package com.example.taskmanager.mapper;

import com.example.taskmanager.dto.user.UserResponseDto;
import com.example.taskmanager.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDto toDto(User user);
}