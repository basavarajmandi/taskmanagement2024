package com.suktha.services.task;
import com.suktha.dtos.TaskDTO;
import com.suktha.entity.Task;
import com.suktha.enums.TaskStatus;
import com.suktha.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TaskServiceImplement implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public long getOverdueTaskCount() {
        return taskRepository.countOverdueTasks();
    }


    public List<TaskDTO> getAllOverdueTasks() {
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



    @Override
    public Map<String, Object> getEmployeeDashboard(Long employeeId) {
        Map<String, Object> dashboard = new HashMap<>();

        // Get counts of tasks by status for the employee
        List<Object[]> results = taskRepository.countTasksByEmployee(employeeId);

        for (Object[] entry : results) {
            TaskStatus status = (TaskStatus) entry[0];
            Long count = (Long) entry[1];

            switch (status) {
                case PENDING:
                    dashboard.put("pendingTasks", count);
                    break;
                case INPROGRESS:
                    dashboard.put("inProgressTasks", count);
                    break;
                case COMPLETED:
                    dashboard.put("completedTasks", count);
                    break;
                case CANCELLED:
                    dashboard.put("cancelledTasks", count);
                    break;
                default:
                    break;
            }
        }
        // Get total task count for the employee
        long totalTasks = results.stream().mapToLong(entry -> (Long) entry[1]).sum();
        dashboard.put("totalTasks", totalTasks);

        return dashboard;
    }
    // Service method to get task counts by priority
    public List<Map<String, Object>> getTaskCountsByPriority() {
        List<Object[]> results = taskRepository.countTasksByPriority();
        List<Map<String, Object>> response = new ArrayList<>();

        for (Object[] result : results) {
            Map<String, Object> data = new HashMap<>();
            data.put("priority", result[0]); // Priority as String
            data.put("count", result[1]);   // Count as Long
            response.add(data);
        }

        return response;
    }
    public Map<String, Integer> getTaskCountsByPriority(Long employeeId) {
        Map<String, Integer> taskCountsByPriority = new HashMap<>();
        taskCountsByPriority.put("High", taskRepository.countTasksByPriorityAndUserId("High", employeeId));
        taskCountsByPriority.put("Medium", taskRepository.countTasksByPriorityAndUserId("Medium", employeeId));
        taskCountsByPriority.put("Low", taskRepository.countTasksByPriorityAndUserId("Low", employeeId));
        taskCountsByPriority.put("VERY-HIGH", taskRepository.countTasksByPriorityAndUserId("VERY-HIGH", employeeId));
        taskCountsByPriority.put("VERY-LOW", taskRepository.countTasksByPriorityAndUserId("VERY-LOW", employeeId));

        return taskCountsByPriority;
    }

    @Override
    public List<TaskDTO> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream().map(Task:: getTaskDTO).collect(Collectors.toList());
    }


    public Map<String, Object> getPaginatedTasks(int page, int size, String sortField, String sortDirection) {

        // Create sorting and paging object
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        Pageable pageable = PageRequest.of(page, size, sort);
        // Fetch paginated and sorted data
        Page<Task> taskPage = taskRepository.findAll(pageable);
        // Convert Task entities to DTOs
        List<TaskDTO> tasks = taskPage.getContent()
                .stream()
                .map(Task::getTaskDTO)
                .collect(Collectors.toList());
        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("content", tasks); // Current page's data
        response.put("totalElements", taskPage.getTotalElements()); // Total number of items
        response.put("totalPages", taskPage.getTotalPages()); // Total pages
        response.put("size", taskPage.getSize()); // Page size
        response.put("page", taskPage.getNumber()); // Current page index
        response.put("sortField", sortField); // Current sort field
        response.put("sortDirection", sortDirection); // Current sort direction

        return response;
    }



    @Override
    public List<Map<String, Object>> getAllTasksForExport() {
        List<Task> tasksFromEntity = taskRepository.findAll();

        return tasksFromEntity.stream().map(task -> {
            TaskDTO taskDTO = task.getTaskDTO(); // Convert entity to DTO using the method in Task entity
            Map<String, Object> taskMap = new HashMap<>();
            taskMap.put("id", taskDTO.getId());
            taskMap.put("title", taskDTO.getTitle());
            taskMap.put("priority", taskDTO.getPriority());
            taskMap.put("dueDate", taskDTO.getDueDate());
            taskMap.put("taskStatus", taskDTO.getTaskStatus());
            taskMap.put("employeeId", taskDTO.getEmployeeId());
            taskMap.put("employeeName", taskDTO.getEmployeeName());
            taskMap.put("description", taskDTO.getDescription());
            return taskMap;
        }).collect(Collectors.toList());
    }





}