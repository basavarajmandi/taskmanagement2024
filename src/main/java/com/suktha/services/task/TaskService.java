package com.suktha.services.task;

import com.suktha.dtos.TaskDTO;
import com.suktha.entity.Task;
import com.suktha.enums.TaskStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface TaskService {

    List<Map<String, Object>> getAllTasksForExport();

    long getOverdueTaskCount();

    List<TaskDTO> getAllOverdueTasks();

    Map<String, Long> getTaskStatusCounts();

    long getTaskCountByStatus(TaskStatus status);

    public List<Map<String, Object>> getTaskCountsByPriority();

    Map<String, Object> getPaginatedTasks(int page, int size, String sortField, String sortDirection);


    // Employee-specific methods
    Map<String, Object> getEmployeeDashboard(Long employeeId);

    Map<String, Integer> getTaskCountsByPriority(Long employeeId);
}
