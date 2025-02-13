package com.suktha.repositories;

import com.suktha.entity.Task;
import com.suktha.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByUserId(Long id);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.dueDate < CURRENT_DATE AND t.taskStatus != TaskStatus.COMPLETED")
    long countOverdueTasks();

    @Query("SELECT t FROM Task t WHERE t.dueDate < CURRENT_DATE AND t.taskStatus != TaskStatus.COMPLETED")
    List<Task> findAllOverdueTasks();

    @Query("SELECT t.taskStatus, COUNT(t) FROM Task t GROUP BY t.taskStatus")
    List<Object[]> countTasksByStatus();


    @Query("SELECT COUNT(t) FROM Task t WHERE t.taskStatus=:status")
    long countTasksByStatus(@Param("status") TaskStatus status);

    List<Task> findAllBytitleContaining(String title);



    //jpql query insed of spcification query
    @Query("SELECT t FROM Task t JOIN t.user u where" +
            "(:priorities IS NULL OR t.priority IN :priorities) AND " +
            "(:title IS NULL OR t.title LIKE %:title%) AND" +
            "(:dueDate IS NULL OR t.dueDate = :dueDate) AND" +
            "(:taskStatuses IS NULL OR t.taskStatus IN :taskStatuses) AND " +
            "(:employeeName IS NULL OR u.name LIKE %:employeeName%)")
    List<Task> findByFilters(@Param("priorities") List<String> priorities,
                             @Param("title") String title,
                             @Param("dueDate") LocalDate dueDate,
                             @Param("taskStatuses") List<TaskStatus> taskStatuses,
                             @Param("employeeName") String employeeName);


    //it used for filter for employees card
    @Query("SELECT t FROM Task t JOIN t.user u WHERE t.user.id = :userid " +
            "AND (:title IS NULL OR t.title LIKE %:title%) " +
            "AND (:priority IS NULL OR t.priority = :priority) " +
            "AND (:taskStatus IS NULL OR t.taskStatus = :taskStatus) " +
            "AND (:dueDate IS NULL OR t.dueDate = :dueDate)")
    List<Task> findFilteredTasks(
            @Param("userid") Long userid,
            @Param("title") String title,
            @Param("priority") String priority,
            @Param("taskStatus") TaskStatus taskStatus,
            @Param("dueDate") LocalDate dueDate);

    //used for chats
    @Query("SELECT t.priority, COUNT(t) FROM Task t GROUP BY t.priority")
    List<Object[]> countTasksByPriority();


    @Query("SELECT t.taskStatus, COUNT(t) FROM Task t WHERE t.user.id = :employeeId GROUP BY t.taskStatus")
    List<Object[]> countTasksByEmployee(@Param("employeeId") Long employeeId);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.taskStatus = :status AND t.user.id = :employeeId")
    long countTasksByEmployeeAndStatus(@Param("status") TaskStatus status, @Param("employeeId") Long employeeId);
    // JPQL query to count tasks by priority and userId

    @Query("SELECT COUNT(t) FROM Task t WHERE t.priority = :priority AND t.user.id = :userId")
    int countTasksByPriorityAndUserId(String priority, Long userId);

}

//    List<Task> findByTitleContaining(String title);
//    List<Task> findByTaskStatus(TaskStatus taskStatus);
//    List<Task> findByPriority(String priority);
//    List<Task> findByDueDate(LocalDate dueDate);



