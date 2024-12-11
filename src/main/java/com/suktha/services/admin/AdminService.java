package com.suktha.services.admin;

import com.suktha.dtos.CommentDTO;
import com.suktha.dtos.TaskDTO;
import com.suktha.dtos.UserDTO;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public interface AdminService {

    List<UserDTO> getUsers();

    TaskDTO postTask(TaskDTO taskDto);

    List<TaskDTO> getTask();

    TaskDTO getTaskByid(Long id);

    void deleteTask(Long id);

    List<TaskDTO> searchTaskByTitle(String title);

  //  List<TaskDTO> searchTaskByEmployeName(String priority);

    TaskDTO updateTask(TaskDTO taskDto, Long id);

    CommentDTO createComment(Long taskId, Long postedBy,String content);

     List<CommentDTO> getCommentsByTask(Long taskId);

}
