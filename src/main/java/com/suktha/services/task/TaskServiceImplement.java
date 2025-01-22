package com.suktha.services.task;
import com.suktha.dtos.TaskDTO;
import com.suktha.entity.Task;
import com.suktha.enums.TaskStatus;
import com.suktha.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TaskServiceImplement implements TaskService{

    @Autowired
    private TaskRepository taskRepository;

    public long getOverdueTaskCount() {
        return taskRepository.countOverdueTasks();
    }


    public List<TaskDTO> getAllOverdueTasks(){
        return taskRepository.findAllOverdueTasks()
                .stream().
                map(Task::getTaskDTO).
                collect(Collectors.toList());
    }

    public Map<String, Long> getTaskStatusCounts() {
        List<Object[]> result = taskRepository.countTasksByStatus();
        Map<String, Long> statusCounts = new HashMap<>();
        for (Object[] entry : result) {
            TaskStatus status = (TaskStatus) entry[0];
            Long count = (Long) entry[1];
            statusCounts.put(status.name(), count);
        }
        return statusCounts;
    }


    @Override
    public long getTaskCountByStatus(TaskStatus status) {
        // Call the repository method to count tasks based on the status
        return taskRepository.countTasksByStatus(status);
    }
}