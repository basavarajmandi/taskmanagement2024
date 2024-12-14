package com.suktha.repositories;
import com.suktha.entity.Task;
import com.suktha.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task,Long>{

   List<Task> findAllBytitleContaining(String title);
    List<Task> findAllByUserId(Long id);
    //jpql query insed of spcification query

        @Query("SELECT t FROM Task t WHERE " +
                "(:priority IS NULL OR t.priority = :priority) AND " +
                "(:title IS NULL OR t.title LIKE %:title%) AND" +
                  "(:dueDate IS NULL OR t.dueDate = :dueDate)")
        List<Task> findByFilters(
               // Corrected to TaskStatus
                @Param("priority") String priority,
                @Param("title") String title,
               @Param("dueDate") LocalDate dueDate
        );
    }

