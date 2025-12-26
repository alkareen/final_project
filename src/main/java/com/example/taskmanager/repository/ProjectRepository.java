package com.example.taskmanager.repository;

import com.example.taskmanager.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findAllByCreatedById(Long userId);
}