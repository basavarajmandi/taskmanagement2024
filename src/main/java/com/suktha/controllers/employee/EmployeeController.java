package com.suktha.controllers.employee;
import com.suktha.dtos.CommentDTO;
import com.suktha.dtos.TaskDTO;
import com.suktha.enums.TaskStatus;
import com.suktha.services.category.CategoryService;
import com.suktha.services.employee.EmployeeService;
import com.suktha.services.exportToExcel.ExportToExcelService;
import com.suktha.services.task.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/employee")
//@CrossOrigin(origins = "http://localhost:4200")
@CrossOrigin(origins = "*")

public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ExportToExcelService exportToExcelService;

    @GetMapping("/tasks/paginated/user/{userId}")
    public ResponseEntity<Map<String, Object>> getPaginatedTasksByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Map<String, Object> response = employeeService.getPaginatedTasksByUserId(userId, page, size, sortField, sortDirection);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tasks/export/{userId}")
    public ResponseEntity<byte[]> exportTasksToExcel(@PathVariable Long userId) {
        List<TaskDTO> tasks = employeeService.getTasksByUserId(userId); // Fetch all tasks
        List<Map<String, Object>> taskList = tasks.stream().map(task -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", task.getId());
            map.put("employeeid",task.getEmployeeId());
            map.put("title", task.getTitle());
            map.put("priority", task.getPriority());
            map.put("dueDate", task.getDueDate());
            map.put("taskStatus", task.getTaskStatus());
            map.put("employeeName", task.getEmployeeName());
            map.put("description", task.getDescription());
            return map;
        }).collect(Collectors.toList());

        try {
            byte[] excelData = exportToExcelService.generateExcel(taskList);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=tasks.xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(excelData);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }



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

    @PostMapping(value = "/task/comment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> creatComment(@RequestParam Long taskId, @RequestParam Long postedBy,  @RequestParam(required = false) String content, @RequestParam(required = false) MultipartFile imageFile,
                                          @RequestParam(required = false) MultipartFile voiceFile) {
        log.info("running createCommit method in EmployeeController ");
        CommentDTO creatCommentdtO = employeeService.createComment(taskId, postedBy, content, imageFile, voiceFile);
        log.info("chack createCommentdto:" + creatCommentdtO);
        if (creatCommentdtO == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        return ResponseEntity.status(HttpStatus.OK).body(creatCommentdtO);

    }

    @GetMapping("/task/{taskId}/comments")
    public ResponseEntity<?> getCommentsByTask(@PathVariable Long taskId) {
        return ResponseEntity.ok(employeeService.getCommentsByTask(taskId));
    }

    @GetMapping("/filter/categories")
    public ResponseEntity<List<String>> getAllCategories(){
        List<String> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/tasks/user/{userid}")
    public ResponseEntity<?> getFilteredTasksByUserIds(
            @PathVariable Long userid,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) List<String> priorities,
            @RequestParam(required = false) List<TaskStatus>  taskStatuses,
            @RequestParam(required = false) LocalDate dueDate,
            @RequestParam(required = false) List<String> categoryNames) {

        log.info("Fetching tasks for user Id: " + userid);
        List<TaskDTO> task = employeeService.getFilteredTasksByUserId(userid, title, priorities, taskStatuses, dueDate,categoryNames);

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

//    @GetMapping("/tasks/export/user/{userId}")
//    public ResponseEntity<byte[]> exportPaginatedTasksToExcel(
//            @PathVariable Long userId,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "5") int size,
//            @RequestParam(defaultValue = "id") String sortField,
//            @RequestParam(defaultValue = "asc") String sortDirection) {
//        try {
//            // Fetch paginated tasks for the given employee ID
//            Map<String, Object> paginatedData = employeeService.getPaginatedTasksByUserId(userId, page, size, sortField, sortDirection);
//
//            // Extract the task list from the response
//            List<TaskDTO> tasksForExport = (List<TaskDTO>) paginatedData.get("tasks");
//
//            // Convert TaskDTO list to a list of maps for Excel
//            List<Map<String, Object>> tasksForExcel = tasksForExport.stream().map(taskDTO -> {
//                Map<String, Object> taskMap = new HashMap<>();
//                taskMap.put("id", taskDTO.getId());
//                taskMap.put("title", taskDTO.getTitle());
//                taskMap.put("priority", taskDTO.getPriority());
//                taskMap.put("dueDate", taskDTO.getDueDate());
//                taskMap.put("taskStatus", taskDTO.getTaskStatus());
//                taskMap.put("employeeName", taskDTO.getEmployeeName());
//                taskMap.put("description", taskDTO.getDescription());
//                return taskMap;
//            }).collect(Collectors.toList());
//
//            // Generate Excel file as byte array
//            byte[] excelFile = exportToExcelService.generateExcel(tasksForExcel);
//
//            // Set headers to force download
//            HttpHeaders headers = new HttpHeaders();
//            headers.add("Content-Disposition", "attachment; filename=paginated_tasks_user_" + userId + ".xlsx");
//            headers.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//
//            return new ResponseEntity<>(excelFile, headers, HttpStatus.OK);
//        } catch (IOException  e) {
//            // Handle error generating Excel file
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }