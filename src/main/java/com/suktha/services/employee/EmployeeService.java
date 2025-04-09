package com.suktha.services.employee;

import com.suktha.dtos.CommentDTO;
import com.suktha.dtos.TaskDTO;
import com.suktha.entity.Task;
import com.suktha.enums.TaskStatus;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface EmployeeService {



    Map<String, Object> getPaginatedTasksByUserId(Long userId, int page, int size, String sortField, String sortDirection);


    List<TaskDTO> getTasksByUserId(Long id);
    //List<TaskDTO> getTasksByUserId();

    TaskDTO updateTask(Long id, String status);

    TaskDTO getTaskById(Long id);

    CommentDTO createComment(Long taskId, Long postedBy, String content, MultipartFile imageFile, MultipartFile voiceFile);

    List<CommentDTO> getCommentsByTask(Long taskId);

    // List<Task> getFilteredTasks(String title, TaskStatus taskStatus, String priority, LocalDate dueDate);
   List<TaskDTO> getFilteredTasksByUserId(Long userid, String title, List<String> priorities, List<TaskStatus> taskStatuses, LocalDate dueDate,List<String> categoryNames);
}
