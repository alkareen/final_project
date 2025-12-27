package com.example.taskmanager.service;

import com.example.taskmanager.dto.category.CategoryRequestDto;
import com.example.taskmanager.dto.category.CategoryResponseDto;
import com.example.taskmanager.entity.Category;
import com.example.taskmanager.mapper.CategoryMapper;
import com.example.taskmanager.repository.CategoryRepository;
import com.example.taskmanager.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void create_success() {
        CategoryRequestDto requestDto = new CategoryRequestDto("Работа", "Задачи по работе");
        Category category = Category.builder().id(1L).name("Работа").description("Задачи по работе").build();
        CategoryResponseDto responseDto = new CategoryResponseDto(1L, "Работа", "Задачи по работе");

        when(categoryMapper.toEntity(requestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(responseDto);

        CategoryResponseDto result = categoryService.create(requestDto);

        assertNotNull(result);
        assertEquals("Работа", result.name());
        verify(categoryRepository).save(category);
    }

    @Test
    void getAll_success() {
        Category category = Category.builder().id(1L).name("Личное").build();
        CategoryResponseDto responseDto = new CategoryResponseDto(1L, "Личное", null);

        when(categoryRepository.findAll()).thenReturn(List.of(category));
        when(categoryMapper.toDto(category)).thenReturn(responseDto);

        List<CategoryResponseDto> result = categoryService.getAll();

        assertEquals(1, result.size());
        assertEquals("Личное", result.get(0).name());
    }

    @Test
    void getById_success() {
        Category category = Category.builder().id(1L).name("Учёба").build();
        CategoryResponseDto responseDto = new CategoryResponseDto(1L, "Учёба", null);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(responseDto);

        CategoryResponseDto result = categoryService.getById(1L);

        assertEquals("Учёба", result.name());
    }

    @Test
    void getById_notFound_throwsException() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                categoryService.getById(99L));

        assertEquals("Category not found", exception.getMessage());
    }

    @Test
    void update_success() {
        Category existing = Category.builder().id(1L).name("Старое имя").build();
        CategoryRequestDto dto = new CategoryRequestDto("Новое имя", "Новое описание");
        CategoryResponseDto responseDto = new CategoryResponseDto(1L, "Новое имя", "Новое описание");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.save(existing)).thenReturn(existing);
        when(categoryMapper.toDto(existing)).thenReturn(responseDto);

        CategoryResponseDto result = categoryService.update(1L, dto);

        assertEquals("Новое имя", result.name());
        assertEquals("Новое описание", result.description());
    }

    @Test
    void delete_success() {
        doNothing().when(categoryRepository).deleteById(1L);

        categoryService.delete(1L);

        verify(categoryRepository).deleteById(1L);
    }
}