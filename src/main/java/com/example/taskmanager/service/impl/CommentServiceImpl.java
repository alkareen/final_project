package com.example.taskmanager.service.impl;

import com.example.taskmanager.dto.comment.CommentCreateRequestDto;
import com.example.taskmanager.dto.comment.CommentResponseDto;
import com.example.taskmanager.entity.Comment;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.mapper.CommentMapper;
import com.example.taskmanager.repository.CommentRepository;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    @Override
    public CommentResponseDto create(CommentCreateRequestDto dto, String userEmail) {
        User author = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = taskRepository.findById(dto.taskId())
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Проверка: может ли пользователь комментировать эту задачу
        boolean isManagerOrAdmin = author.getRoles().contains("MANAGER") || author.getRoles().contains("ADMIN");
        boolean isOwner = task.getCreatedBy().getId().equals(author.getId());
        boolean isAssignee = task.getAssignee() != null && task.getAssignee().getId().equals(author.getId());
        boolean isProjectOwner = task.getProject().getCreatedBy().getId().equals(author.getId());

        if (!isManagerOrAdmin && !isOwner && !isAssignee && !isProjectOwner) {
            throw new RuntimeException("You can only comment on your tasks or assigned tasks");
        }

        Comment comment = commentMapper.toEntity(dto);
        comment.setTask(task);
        comment.setAuthor(author);

        Comment saved = commentRepository.save(comment);
        return commentMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponseDto> getByTaskId(Long taskId, String userEmail) {
        // Можно добавить проверку доступа к задаче, как в TaskService
        return commentRepository.findAllByTaskId(taskId)
                .stream()
                .map(commentMapper::toDto)
                .toList();
    }

    @Override
    public void delete(Long id, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        boolean isAuthor = comment.getAuthor().getId().equals(user.getId());
        boolean isManagerOrAdmin = user.getRoles().contains("MANAGER") || user.getRoles().contains("ADMIN");

        if (!isAuthor && !isManagerOrAdmin) {
            throw new RuntimeException("You can only delete your own comments");
        }

        commentRepository.delete(comment);
    }
}