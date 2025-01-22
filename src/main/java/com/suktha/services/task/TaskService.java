package com.suktha.services.task;

import com.suktha.dtos.TaskDTO;
import com.suktha.entity.Task;
import com.suktha.enums.TaskStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface TaskService {

     long getOverdueTaskCount();

     List<TaskDTO> getAllOverdueTasks();

     Map<String, Long> getTaskStatusCounts();

     long getTaskCountByStatus(TaskStatus status);
}
