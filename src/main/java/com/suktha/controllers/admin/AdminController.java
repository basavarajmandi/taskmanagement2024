package com.suktha.controllers.admin;
import com.suktha.dtos.CommentDTO;
import com.suktha.dtos.TaskDTO;
import com.suktha.enums.TaskStatus;
import com.suktha.services.admin.AdminService;
import com.suktha.services.task.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

    @Autowired
    private  AdminService adminService;

    @Autowired
    private TaskService taskService;

    @GetMapping("/users")
    public ResponseEntity<?> getUsers() {
        return ResponseEntity.ok(adminService.getUsers());
    }



    @GetMapping("/tasks/filter")
    public List<TaskDTO> filterTasks(
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) TaskStatus taskStatus,
            @RequestParam(required = false) String employeeName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE )LocalDate dueDate) {
        return adminService.filterTasks(priority, title,dueDate,taskStatus,employeeName);
    }

//    @GetMapping("/filter")
//    public ResponseEntity<List<TaskDTO>> filterTasks(
//            @RequestParam(required = false) String taskStatus,
//            @RequestParam(required = false) String priority,
//            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dueDate)
//      {
//
//        return ResponseEntity.ok(adminService.getFilteredTasks(taskStatus,priority,dueDate));

    @PostMapping("/savetask")
    public ResponseEntity<?> postTask(@RequestBody TaskDTO taskDto) {
        TaskDTO createdtask = adminService.postTask(taskDto);
        if (createdtask == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        return ResponseEntity.status(HttpStatus.CREATED).body(createdtask);

    }

    @GetMapping("/tasks")
    public ResponseEntity<?> getTask() {
        return ResponseEntity.ok(adminService.getTask());
    }
    @GetMapping("/task/{id}")
    public ResponseEntity<?> getTaskId(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getTaskByid(id));
    }

    @DeleteMapping("/task/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        adminService.deleteTask(id);
        return ResponseEntity.ok(null);
    }

    @PutMapping("/task/{id}")
    public ResponseEntity<?> UpdateTask(@RequestBody TaskDTO taskDto,@PathVariable Long id){
        TaskDTO updatedTaskDto = adminService.updateTask(taskDto, id);
        System.out.println("running updateTask method in AdminControlleruPdatetask:"+updatedTaskDto);
        if(updatedTaskDto==null) return  ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        return ResponseEntity.status(HttpStatus.OK).body(updatedTaskDto);
    }

    @PostMapping("/task/comment")
    public ResponseEntity<?> creatComment(@RequestParam Long taskId,@RequestParam Long postedBy, @RequestBody String content){
        CommentDTO creatCommentdtO = adminService.createComment(taskId, postedBy, content);
        if(creatCommentdtO ==null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        return ResponseEntity.status(HttpStatus.OK).body(creatCommentdtO);
    }
    @GetMapping("/task/{taskId}/comments")
    public ResponseEntity<?> getCommentsByTask(@PathVariable Long taskId){
        return ResponseEntity.ok(adminService.getCommentsByTask(taskId));
    }
    @GetMapping("task/search/{title}")
    public ResponseEntity<?> searchTaskbyTitle(@PathVariable String title){
        return ResponseEntity.ok(adminService.searchTaskByTitle(title));
    }

    @GetMapping("/tasks/overdue")
    public ResponseEntity<Long> getOverdueTaskCount() {
        long overdueTaskCount = taskService.getOverdueTaskCount();
        return ResponseEntity.ok(overdueTaskCount);
    }
    @GetMapping("/tasks/status-counts")
    public ResponseEntity<Map<String, Long>> getTaskStatusCounts() {
        Map<String, Long> statusCounts = taskService.getTaskStatusCounts();
        return ResponseEntity.ok(statusCounts);
    }



}
