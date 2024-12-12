package com.suktha.controllers.employee;

import com.suktha.dtos.CommentDTO;
import com.suktha.dtos.TaskDTO;
import com.suktha.services.employee.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
@CrossOrigin(origins = "http://localhost:4200")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

//         @GetMapping("/tasks")
//        public ResponseEntity<List<TaskDTO>> getTaskByUserId() {
//        return ResponseEntity.ok(employeeService.getTasksByUserId());
//    }


    @GetMapping("/task/user/{userid}")
    public ResponseEntity<?> getTaskByUserId(@PathVariable() Long userid) {
        System.out.println("Fetching tasks for user Id:"+ userid);
        List<TaskDTO> task = employeeService.getTasksByUserId(userid);
        System.out.println("Task Fetched:"+task);
        return ResponseEntity.ok(task);
    }


    @PutMapping("/task/{id}/{status}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @PathVariable String status) {
        System.out.println("Updating Task ID: " + id + " with Status: " + status);
        TaskDTO updateTaskDto = employeeService.updateTask(id, status);
        System.out.println("chack the updated task and status:"+ updateTaskDto);

        if (updateTaskDto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("task updated failed!..");
        }
        return ResponseEntity.status(HttpStatus.OK).body(updateTaskDto);
    }

    @GetMapping("/task/{taskid}")
    public ResponseEntity<?> getTaskByid (@PathVariable Long taskid) {
        return ResponseEntity.ok(employeeService.getTaskById(taskid));
    }

    @PostMapping("/task/comment")
    public ResponseEntity<?> creatComment(@RequestParam Long taskId,@RequestParam Long postedBy, @RequestBody String content) {
        System.out.println("running createCommit method in EmployeeController ");
        CommentDTO creatCommentdtO = employeeService.createComment(taskId, postedBy, content);
        System.out.println("chack createCommentdto:"+creatCommentdtO);
        if (creatCommentdtO == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        return ResponseEntity.status(HttpStatus.OK).body(creatCommentdtO);

    }

    @GetMapping("/task/{taskId}/comments")
    public ResponseEntity<?> getCommentsByTask(@PathVariable Long taskId){
    return ResponseEntity.ok(employeeService.getCommentsByTask(taskId));
    }


}
