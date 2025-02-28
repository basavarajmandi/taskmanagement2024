package com.suktha.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "task_link")
public class TaskLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url",nullable = false,length=1000)
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id",nullable = false)
    @JsonIgnore  // Prevents infinite recursion
    private Task task;


    @Override
    public String toString() {
        return "TaskLink{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", taskId=" + (task != null ? task.getId() : "null") +  // Avoid calling task.toString()
                '}';
    }

}
