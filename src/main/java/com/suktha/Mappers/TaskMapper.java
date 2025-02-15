package com.suktha.Mappers;

import com.suktha.dtos.TaskDTO;
import com.suktha.entity.Task;

import java.util.List;
import java.util.stream.Collectors;

public class TaskMapper {

    // Convert Task entity to TaskDTO
    public static TaskDTO toDTO(Task task) {
        if (task == null) {
            return null;
        }

        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(task.getId());
        taskDTO.setTitle(task.getTitle());
        taskDTO.setTaskStatus(task.getTaskStatus());
        taskDTO.setEmployeeName(task.getUser().getName());
        taskDTO.setEmployeeId(task.getUser().getId());
        taskDTO.setPriority(task.getPriority());
        taskDTO.setDueDate(task.getDueDate());
        taskDTO.setDescription(task.getDescription());
        taskDTO.setImageName(task.getImageName());
        taskDTO.setVoiceName(task.getVoiceName());
        taskDTO.setCategoryId(task.getCategory().getId());
        taskDTO.setCategoryName(task.getCategory().getName());
        taskDTO.setAssignedDate(task.getAssignedDate());
        return taskDTO;
    }

    // Convert a list of Task entities to a list of TaskDTOs
    public static List<TaskDTO> entitytoDTOList(List<Task> tasks) {
        return tasks.stream().map(TaskMapper::toDTO).collect(Collectors.toList());
    }


//    // Convert TaskDTO back to Task entity
//    public static Task toEntity(TaskDTO taskDTO) {
//        if (taskDTO == null) {
//            return null;
//        }
//
//        Task task = new Task();
//        task.setId(taskDTO.getId());
//        task.setTitle(taskDTO.getTitle());
//        task.setTaskStatus(taskDTO.getTaskStatus());
//        task.setPriority(taskDTO.getPriority());
//        task.setDueDate(taskDTO.getDueDate());
//        task.setDescription(taskDTO.getDescription());
//        task.setImageName(taskDTO.getImageName());
//        task.setVoiceName(taskDTO.getVoiceName());
//        return task;
//    }







}
