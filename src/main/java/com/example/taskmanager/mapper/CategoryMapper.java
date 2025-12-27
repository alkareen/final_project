package com.example.taskmanager.mapper;

import com.example.taskmanager.dto.category.CategoryRequestDto;
import com.example.taskmanager.dto.category.CategoryResponseDto;
import com.example.taskmanager.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toEntity(CategoryRequestDto dto);

    CategoryResponseDto toDto(Category entity);
}