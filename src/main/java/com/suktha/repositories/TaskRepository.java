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
public interface TaskRepository extends JpaRepository<Task,Long>{

    @Query("SELECT COUNT(t) FROM Task t WHERE t.dueDate < CURRENT_DATE AND t.taskStatus != TaskStatus.COMPLETED")
    long countOverdueTasks();

    @Query("SELECT t FROM Task t WHERE t.dueDate < CURRENT_DATE AND t.taskStatus != TaskStatus.COMPLETED")
    List<Task> findAllOverdueTasks();

    @Query("SELECT t.taskStatus, COUNT(t) FROM Task t GROUP BY t.taskStatus")
    List<Object[]> countTasksByStatus();

    @Query("SELECT COUNT(t) FROM Task t WHERE t.taskStatus=:status")
   long countTasksByStatus( @Param("status") TaskStatus status);

    List<Task> findAllBytitleContaining(String title);

    List<Task> findAllByUserId(Long id);

    //jpql query insed of spcification query
    @Query("SELECT t FROM Task t JOIN t.user u where"+
            "(:priority IS NULL OR t.priority = :priority) AND"+
            "(:title IS NULL OR t.title LIKE %:title%) AND"+
            "(:dueDate IS NULL OR t.dueDate = :dueDate) AND"+
            "(:taskStatus IS NULL OR t.taskStatus= :taskStatus) AND"+
            "(:employeeName IS NULL OR u.name LIKE %:employeeName%)")
        List<Task> findByFilters(@Param("priority") String priority,
                                 @Param("title") String title,
                                 @Param("dueDate") LocalDate dueDate,
                                 @Param("taskStatus") TaskStatus taskStatus,
                                 @Param("employeeName") String employeeName);


//    List<Task> findByTitleContaining(String title);
//    List<Task> findByTaskStatus(TaskStatus taskStatus);
//    List<Task> findByPriority(String priority);
//    List<Task> findByDueDate(LocalDate dueDate);
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

}





