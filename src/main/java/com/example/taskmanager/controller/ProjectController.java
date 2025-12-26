package com.example.taskmanager.controller;

import com.example.taskmanager.dto.project.ProjectCreateRequestDto;
import com.example.taskmanager.dto.project.ProjectResponseDto;
import com.example.taskmanager.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectResponseDto> create(
            @Valid @RequestBody ProjectCreateRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(projectService.create(dto, userDetails.getUsername()));
    }

    @GetMapping("/my")
    public ResponseEntity<List<ProjectResponseDto>> getMy(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(projectService.getMyProjects(userDetails.getUsername()));
    }

    @GetMapping
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<List<ProjectResponseDto>> getAll() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getById(id));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody ProjectCreateRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(projectService.update(id, dto, userDetails.getUsername()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        projectService.delete(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}