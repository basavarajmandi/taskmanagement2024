package com.suktha.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.suktha.dtos.TaskDTO;
import com.suktha.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


import java.time.LocalDate;
import java.util.Date;

@NoArgsConstructor
@ToString
@Entity
@Data
@Table(name = "task")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "priority", length = 255)
    private String priority;

    @Column(name = "description", length = 255)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private User user;

    @Enumerated(EnumType.ORDINAL) // Maps to 'task_status' as an integer
    @Column(name = "task_status", columnDefinition = "TINYINT CHECK (task_status BETWEEN 0 AND 4)")
    private TaskStatus taskStatus;

    public TaskDTO getTaskDTO() {
        // canvart taskentity to taskDto method this one encapsulation
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(id);
        taskDTO.setTitle(title);
        taskDTO.setTaskStatus(taskStatus);
        taskDTO.setEmployeeName(user.getName());
        taskDTO.setEmployeeId(user.getId());
        taskDTO.setPriority(priority);
        taskDTO.setDueDate(dueDate);
        taskDTO.setDescription(description);
        return taskDTO;

    }

}
