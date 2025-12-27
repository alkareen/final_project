package com.example.taskmanager.service;

import com.example.taskmanager.dto.category.CategoryRequestDto;
import com.example.taskmanager.dto.category.CategoryResponseDto;

import java.util.List;

public interface CategoryService {

    CategoryResponseDto create(CategoryRequestDto dto);

    List<CategoryResponseDto> getAll();

    CategoryResponseDto getById(Long id);

    CategoryResponseDto update(Long id, CategoryRequestDto dto);

    void delete(Long id);
}