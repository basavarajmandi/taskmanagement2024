package com.suktha.services.employee;

import com.suktha.dtos.CommentDTO;
import com.suktha.dtos.TaskDTO;
import com.suktha.entity.Comment;
import com.suktha.entity.Task;
import com.suktha.entity.User;
import com.suktha.enums.TaskStatus;
import com.suktha.repositories.CommentRepository;
import com.suktha.repositories.TaskRepository;
import com.suktha.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImple implements EmployeeService {

    @Autowired
    private TaskRepository taskRepository;

//    @Autowired
//    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public List<TaskDTO> getTasksByUserId(Long id) {
        return taskRepository.
                findAllByUserId(id).
                stream().
                map(Task::getTaskDTO).
                collect(Collectors.toList());
    }

//    public List<TaskDTO> getTasksByUserId(){
//        User user = jwtUtil.getLoggedInUser();
//        if(user!=null){
//            return  taskRepository.findAllByUserId(user.getId())
//          .stream()
//          .sorted(Comparator.comparing(Task::getDueDate).reversed())
//          .map(Task::getTaskDTO)
//          .collect(Collectors.toList());
//
//        }
//        throw  new EntityNotFoundException("UserNot Found");
//
//    }

    @Override
    public TaskDTO updateTask(Long id, String status) {
        Optional<Task> optionalTask = taskRepository.findById(id);

        if (optionalTask.isPresent()) {
            Task existingTask = optionalTask.get();

            TaskStatus taskStatus = mapStringToTaskStatus(String.valueOf(status));
            existingTask.setTaskStatus(taskStatus);
            return taskRepository.save(existingTask).getTaskDTO();
        }
      throw new EntityNotFoundException("Task not found");
    }

    @Override
    public TaskDTO getTaskById(Long id) {
        return taskRepository.findById(id).map(Task::getTaskDTO).orElse(null);
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
        return commentRepository.findAllByTaskId(taskId)
                .stream()
                .map(Comment::getCommentDto)// entity to dto Canvarting
                .collect(Collectors.toList());
    }

//    // Method to fetch filtered tasks based on title, taskStatus, priority, and dueDate
//    public List<Task> getFilteredTasks(String title, TaskStatus taskStatus, String priority, LocalDate dueDate) {
//        // Implement filtering logic based on the provided parameters
//        if (title != null && !title.isEmpty()) {
//            return taskRepository.findByTitleContaining(title);
//        } else if (taskStatus != null) {
//            return taskRepository.findByTaskStatus(taskStatus);
//        } else if (priority != null && !priority.isEmpty()) {
//            return taskRepository.findByPriority(priority);
//        } else if (dueDate != null) {
//            return taskRepository.findByDueDate(dueDate);
//        }
//        return taskRepository.findAll(); // Return all tasks if no filter is applied
//    }


    public List<TaskDTO> getFilteredTasksByUserId(Long userid, String title, String priority, TaskStatus taskStatus, LocalDate dueDate) {
        // Get filtered tasks from the repository based on parameters
        List<Task> tasks = taskRepository.findFilteredTasks(userid, title, priority, taskStatus, dueDate);

        // Convert Task entities to TaskDTOs
        List<TaskDTO> taskDTOs = tasks.stream()
                .map(Task::getTaskDTO)  // Assuming Task has a method to convert to TaskDTO
                .collect(Collectors.toList());

        return taskDTOs;
    }
    private TaskStatus mapStringToTaskStatus(String taskStatus) {
        System.out.println("running mapStringTo taskStatus method in EmployeeServiceImple");
        return switch (taskStatus) {
            case "PENDING" -> TaskStatus.PENDING;
            case "INPROGRESS" -> TaskStatus.INPROGRESS;
            case "COMPLETED" -> TaskStatus.COMPLETED;
            case "DEFERRED" -> TaskStatus.DEFERRED;
            default -> TaskStatus.CANCELLED;
        };
    }
}
