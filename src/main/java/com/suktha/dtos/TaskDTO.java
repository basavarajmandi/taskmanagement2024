package com.suktha.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.suktha.enums.TaskState;
import com.suktha.enums.TaskStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@ToString
@Data
@Setter
@Getter
public class TaskDTO {

    private Long id;
    private String title;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dueDate;
    private String description;
    private String priority;
    private TaskStatus taskStatus;
    private Long employeeId;
    private String employeeName;
    // Change the field to store image name (not image data)
    private String imageName;  // Store image file name
    private String voiceName;
    private String categoryName;
    private Long categoryId;
    private LocalDateTime assignedDate;
    private TaskState taskLifecycle;
    private List<TaskLinkDTO> links;
}
