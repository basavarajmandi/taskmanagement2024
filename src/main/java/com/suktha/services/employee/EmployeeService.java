package com.suktha.services.employee;

import com.suktha.dtos.CommentDTO;
import com.suktha.dtos.TaskDTO;

import java.util.List;

public interface EmployeeService {

  List<TaskDTO> getTasksByUserId(Long id);
  //List<TaskDTO> getTasksByUserId();

  TaskDTO updateTask( Long id, String status);

   TaskDTO getTaskById(Long id);

    CommentDTO createComment(Long taskId, Long postedBy, String content);

    List<CommentDTO> getCommentsByTask(Long taskId);

}
