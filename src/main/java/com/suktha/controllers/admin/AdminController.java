package com.suktha.controllers.admin;
import com.suktha.dtos.CommentDTO;
import com.suktha.dtos.TaskDTO;
import com.suktha.services.admin.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

    @Autowired
    private  AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<?> getUsers() {
        return ResponseEntity.ok(adminService.getUsers());
    }

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

    @GetMapping("task/search/{title}")
    public ResponseEntity<?> searchTaskbyTitle(@PathVariable String title){
        return ResponseEntity.ok(adminService.searchTaskByTitle(title));
    }

    @PutMapping("/task/{id}")
    public ResponseEntity<?> UpdateTask(@RequestBody TaskDTO taskDto,@PathVariable Long id){
        TaskDTO updatedTaskDto = adminService.updateTask(taskDto, id);
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

}