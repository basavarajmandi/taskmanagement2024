package com.suktha.controllers.employee;

import com.suktha.dtos.CommentDTO;
import com.suktha.dtos.TaskDTO;
import com.suktha.entity.Task;
import com.suktha.enums.TaskStatus;
import com.suktha.services.employee.EmployeeService;
import com.suktha.services.task.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/employee")
@CrossOrigin(origins = "http://localhost:4200")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private TaskService taskService;


    @GetMapping("/task/user/{userid}")
    public ResponseEntity<?> getTaskByUserId(@PathVariable() Long userid) {
        log.info("Fetching tasks for user Id:" + userid);
        List<TaskDTO> task = employeeService.getTasksByUserId(userid);
        log.info("Task Fetched:" + task);

        if (task != null && !task.isEmpty()) {
            task.forEach(tasks -> {
                if (tasks.getImageName() != null) {
                    tasks.setImageName("http://localhost:8080/api/files/images/" + tasks.getImageName());
                }
            });
        }

        log.info("Tasks Fetched: " + task);
        return ResponseEntity.ok(task);
    }


    @GetMapping("/task/{taskid}")
    public ResponseEntity<?> getTaskByid(@PathVariable Long taskid) {
        return ResponseEntity.ok(employeeService.getTaskById(taskid));
    }

    @PutMapping("/task/{id}/{status}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @PathVariable String status) {
        log.info("Updating Task ID: " + id + " with Status: " + status);
        TaskDTO updateTaskDto = employeeService.updateTask(id, status);
        log.info("chack the updated task and status:" + updateTaskDto);

        if (updateTaskDto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("task updated failed!..");
        }
        return ResponseEntity.status(HttpStatus.OK).body(updateTaskDto);
    }



    @PostMapping("/task/comment")
    public ResponseEntity<?> creatComment(@RequestParam Long taskId, @RequestParam Long postedBy, @RequestBody String content) {
        log.info("running createCommit method in EmployeeController ");
        CommentDTO creatCommentdtO = employeeService.createComment(taskId, postedBy, content);
        log.info("chack createCommentdto:" + creatCommentdtO);
        if (creatCommentdtO == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        return ResponseEntity.status(HttpStatus.OK).body(creatCommentdtO);

    }

    @GetMapping("/task/{taskId}/comments")
    public ResponseEntity<?> getCommentsByTask(@PathVariable Long taskId) {
        return ResponseEntity.ok(employeeService.getCommentsByTask(taskId));
    }

    @GetMapping("/tasks/user/{userid}")
    public ResponseEntity<?> getFilteredTasksByUserIds(
            @PathVariable Long userid,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) TaskStatus taskStatus,
            @RequestParam(required = false) LocalDate dueDate) {

        log.info("Fetching tasks for user Id: " + userid);
        List<TaskDTO> task = employeeService.getFilteredTasksByUserId(userid, title, priority, taskStatus, dueDate);

        log.info("Tasks Fetched: " + task);
        return ResponseEntity.ok(task);
    }

    // Get employee dashboard
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getEmployeeTaskStatus(@RequestParam Long employeeId) {
        Map<String, Object> dashboard = taskService.getEmployeeDashboard(employeeId);

        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/{employeeId}/task-counts-by-priority")
    public ResponseEntity<Map<String, Integer>> getTaskCountsByPriority(@PathVariable Long employeeId) {
        // Get task counts by priority for the given employeeId
        Map<String, Integer> taskCountsByPriority = taskService.getTaskCountsByPriority(employeeId);
       log.info("chack priority data:"+taskCountsByPriority);
        // Return the result inside a ResponseEntity with a 200 OK status
        return new ResponseEntity<>(taskCountsByPriority, HttpStatus.OK);
    }


}
