package com.suktha.repositories;

import com.suktha.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task,Long> {

    List<Task> findAllBytitleContaining(String title);

   // List<Task> findAllByTitleContaining(String priority);

    List<Task> findAllByUserId(Long id);

}
