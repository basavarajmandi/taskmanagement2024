package com.suktha.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.suktha.dtos.TaskDTO;
import com.suktha.dtos.TaskLinkDTO;
import com.suktha.enums.TaskState;
import com.suktha.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
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
    @Enumerated(EnumType.ORDINAL) // Maps to 'task_status' as an integer
    @Column(name = "task_status", columnDefinition = "TINYINT CHECK (task_status BETWEEN 0 AND 4)")
    private TaskStatus taskStatus;
    // New field to store image as byte array
    // Store the image file name instead of the byte array
    @Column(name = "image_name")
    private String imageName;  // Store image name or path
    @Column(name = "voice_name")
    private String voiceName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @CreationTimestamp  // Auto-assigns the current date
    @Column(updatable = false)
    private LocalDateTime assignedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "taskLifecycle", nullable = false)
    private TaskState taskLifecycle;


    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL,  orphanRemoval = true,fetch = FetchType.EAGER )
    @JsonManagedReference  // This is the forward side of the reference
    private List<TaskLink> links = new ArrayList<>();

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
        taskDTO.setImageName(imageName);
        taskDTO.setVoiceName(voiceName);// Set image name
        taskDTO.setCategoryId(category.getId());
        taskDTO.setCategoryName(category.getName());
        taskDTO.setAssignedDate(assignedDate);
        taskDTO.setTaskLifecycle(taskLifecycle);

        // Convert List<TaskLink> to List<TaskLinkDTO>
        if (this.links != null && !this.links.isEmpty()) {
            List<TaskLinkDTO> linkDTOs = this.links.stream()
                    .map(link -> new TaskLinkDTO(link.getId(), link.getUrl()))
                    .collect(Collectors.toList());
            taskDTO.setLinks(linkDTOs);
        } else {
            taskDTO.setLinks(new ArrayList<>()); // Ensure empty list instead of null
        }

        return taskDTO;
    }


    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", priority='" + priority + '\'' +
                ", description='" + description + '\'' +
                ", dueDate=" + dueDate +
                ", taskStatus=" + taskStatus +
                ", category=" + (category != null ? category.getName() : "null") +
                ", user=" + (user != null ? user.getId() : "null") +  // Avoid calling user.toString()
                ", linksCount=" + (links != null ? links.size() : 0) +  // Avoid listing all links
                '}';
    }




}
