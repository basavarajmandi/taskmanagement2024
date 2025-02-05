package com.suktha.services.admin;

import com.suktha.dtos.CommentDTO;
import com.suktha.dtos.TaskDTO;
import com.suktha.dtos.UserDTO;
import com.suktha.entity.Comment;
import com.suktha.entity.Task;
import com.suktha.entity.User;
import com.suktha.enums.TaskStatus;
import com.suktha.enums.UserRole;
import com.suktha.repositories.CommentRepository;
import com.suktha.repositories.TaskRepository;
import com.suktha.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdminServiceImplement implements AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public List<UserDTO> getUsers() {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getUserRole() == UserRole.EMPLOYEE)
                .map(User::getUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TaskDTO postTask(TaskDTO taskDto) {
        Optional<User> optionalUser = userRepository.findById(taskDto.getEmployeeId());
        log.info("running postTask method in AdminServiceImplement class chack the method");
        if (optionalUser.isPresent()) {
            Task task = new Task();//task entity to dto sending
            task.setTitle(taskDto.getTitle());
            task.setUser(optionalUser.get());
            task.setPriority(taskDto.getPriority());
            task.setDescription(taskDto.getDescription());
            task.setTaskStatus(TaskStatus.CANCELLED);
            task.setDueDate(taskDto.getDueDate());
            return taskRepository.save(task).getTaskDTO();
        }
        return null;
    }

    @Override
    public List<TaskDTO> getTask() {
        return taskRepository
                .findAll()
                .stream()
                .map(Task::getTaskDTO)//this is canvert entity to dto map
                .collect(Collectors.toList());
    }

    @Override
    public TaskDTO getTaskByid(Long id) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        return optionalTask
                .map(Task::getTaskDTO) //entity to dto
                .orElse(null);
    }

    @Override
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    @Override
    public List<TaskDTO> searchTaskByTitle(String title) {
        return taskRepository.findAllBytitleContaining(title).stream().map(Task::getTaskDTO).collect(Collectors.toList());
    }


    @Override
    public TaskDTO updateTask(TaskDTO taskDto, Long id) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        log.info("running updatetask method in AdminServiceImplement clas :" + optionalTask);
        Optional<User> optionalUser = userRepository.findById(taskDto.getEmployeeId());
        if (optionalTask.isPresent() && optionalUser.isPresent()) {
            Task existingTask = optionalTask.get();
            existingTask.setTitle(taskDto.getTitle());
            existingTask.setDescription(taskDto.getDescription());
            existingTask.setPriority(taskDto.getPriority());
            existingTask.setUser(optionalUser.get());
            existingTask.setDueDate(taskDto.getDueDate());
            TaskStatus taskStatus = mapStringToTaskStatus(String.valueOf(taskDto.getTaskStatus()));
            existingTask.setTaskStatus(taskStatus);
            return taskRepository.save(existingTask).getTaskDTO();
        }
        return null;
    }

    @Override
    public CommentDTO createComment(Long taskId, Long postedBy, String content) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        Optional<User> optionalUser = userRepository.findById(postedBy);
        if (optionalTask.isPresent() && optionalUser.isPresent()) {
            Comment comment = new Comment();
            comment.setContent(content);
            comment.setCreatedAt(new Date());
            comment.setTask(optionalTask.get());
            comment.setUser(optionalUser.get());
            return commentRepository.save(comment).getCommentDto();
        }

        throw new EntityNotFoundException("task or user not found");
    }

    @Override
    public List<CommentDTO> getCommentsByTask(Long taskId) {
        return commentRepository
                .findAllByTaskId(taskId)
                .stream()
                .map(Comment::getCommentDto)//canavert entity to dto because i transfer data entity to controler geting
                .collect(Collectors.toList());

    }

    public List<TaskDTO> filterTasks(String priority, String title, LocalDate dueDate, TaskStatus taskStatus, String employeeName) {
        List<Task> filteredTasks = this.taskRepository.findByFilters(priority, title, dueDate, taskStatus, employeeName);
        log.info("running filterTasks method in AdminServiceImplement class:" + filteredTasks);
        return filteredTasks.stream()
                .map(Task::getTaskDTO) // Convert each Task entity to TaskDTO
                .collect(Collectors.toList());
    }

    private TaskStatus mapStringToTaskStatus(String taskStatus) {
        return switch (taskStatus) {
            case "PENDING" -> TaskStatus.PENDING;
            case "INPROGRESS" -> TaskStatus.INPROGRESS;
            case "COMPLETED" -> TaskStatus.COMPLETED;
            case "DEFERRED" -> TaskStatus.DEFERRED;
            default -> TaskStatus.CANCELLED;
        };
    }

}
