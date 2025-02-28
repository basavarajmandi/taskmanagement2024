package com.suktha.repositories;

import com.suktha.entity.TaskLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskLinkRepository extends JpaRepository<TaskLink,Long> {
}
