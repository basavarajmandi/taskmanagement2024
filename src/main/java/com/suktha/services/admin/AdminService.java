package com.suktha.services.admin;

import com.suktha.dtos.CommentDTO;
import com.suktha.dtos.TaskDTO;
import com.suktha.dtos.UserDTO;
import com.suktha.entity.Task;
import com.suktha.enums.TaskStatus;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
public interface AdminService {

    List<UserDTO> getUsers();

    TaskDTO postTask(TaskDTO taskDto);

    List<TaskDTO> getTask();

    TaskDTO getTaskByid(Long id);

    void deleteTask(Long id);

   List<TaskDTO> searchTaskByTitle(String title);


    TaskDTO updateTask(TaskDTO taskDto, Long id);

    CommentDTO createComment(Long taskId, Long postedBy,String content);

     List<CommentDTO> getCommentsByTask(Long taskId);

    // New method for filtering tasks
    List<TaskDTO> filterTasks(String priority, String title,LocalDate dueDate);
}
