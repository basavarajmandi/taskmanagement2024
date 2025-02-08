package com.suktha.controllers.admin;
import com.suktha.dtos.CommentDTO;
import com.suktha.dtos.TaskDTO;
import com.suktha.enums.TaskStatus;
import com.suktha.services.admin.AdminService;
import com.suktha.services.exportToExcel.ExportToExcelService;
import com.suktha.services.task.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ExportToExcelService exportToExcelService;

    @GetMapping("/users")
    public ResponseEntity<?> getUsers() {
        log.info("running getUserMethod in AdminController");
        return ResponseEntity.ok(adminService.getUsers());
    }

    @GetMapping("/tasks/filter")
    public List<TaskDTO> filterTasks(
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) TaskStatus taskStatus,
            @RequestParam(required = false) String employeeName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate) {
        return adminService.filterTasks(priority, title, dueDate, taskStatus, employeeName);
    }

    @PostMapping("/savetask")
    public ResponseEntity<?> postTask(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate,
            @RequestParam("priority") String priority,
            @RequestParam("employeeId") Long employeeId,
            @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {
        // Create TaskDTO and populate it with form data
        TaskDTO taskDto = new TaskDTO();
        taskDto.setTitle(title);
        taskDto.setDescription(description);
        taskDto.setDueDate(dueDate); // No need to parse manuallyhe dueDate is a LocalDate
        taskDto.setPriority(priority);
        taskDto.setEmployeeId(employeeId);

        // If an image is provided, store the image file name
        if (image != null && !image.isEmpty()) {
            String imageName = saveImageToFileSystem(image);  // Save the image and get the file name
            taskDto.setImageName(imageName);  // Set the image name in the DTO
        }

        TaskDTO createdTask = adminService.postTask(taskDto);

        if (createdTask == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    private String saveImageToFileSystem(MultipartFile image) throws IOException {
        Path imagesDirectory = Paths.get("C:/uploaded_images");

        // Check if the directory exists, and if not, create it
        if (!Files.exists(imagesDirectory)) {
            Files.createDirectories(imagesDirectory);
        }

        // Create a unique file name for the image
        String imageName = System.currentTimeMillis() + "-" + image.getOriginalFilename();

        // Define the file path to store the image
        Path imagePath = imagesDirectory.resolve(imageName);

        // Save the image to the file system
        Files.write(imagePath, image.getBytes());

        return imageName;  // Return the image name (or path) to save in the database
    }


    @GetMapping("/tasks")
    public ResponseEntity<?> getTask() {
        log.info("running getTask method");
        return ResponseEntity.ok(adminService.getTask());
    }




//    @GetMapping("/images/{imageName}")
//    public ResponseEntity<Resource> getImage(@PathVariable String imageName) throws IOException {
//        Path imagePath = Paths.get("images").resolve(imageName);
//        Resource resource = new UrlResource(imagePath.toUri());
//
//        if (!resource.exists() || !resource.isReadable()) {
//            throw new RuntimeException("Image not found: " + imageName);
//        }
//
//        return ResponseEntity.ok()
//                .contentType(MediaType.IMAGE_JPEG)
//                .body(resource);
//    }




    @GetMapping("/tasks/paginated")
    public ResponseEntity<Map<String, Object>> getTasksWithPagination(
            @RequestParam(defaultValue = "0") int page, // Default page index
            @RequestParam(defaultValue = "5") int size, // Default page size
            @RequestParam(defaultValue = "id") String sortField, // Default sort field
            @RequestParam(defaultValue = "asc") String sortDirection // Default sort direction
    ) {
        Map<String, Object> response = taskService.getPaginatedTasks(page, size, sortField, sortDirection);
        log.info("running taskService in getPaginatedTask at AdminController");
        return ResponseEntity.ok(response);
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
    public ResponseEntity<?> UpdateTask(@RequestBody TaskDTO taskDto, @PathVariable Long id) {
        TaskDTO updatedTaskDto = adminService.updateTask(taskDto, id);
        log.info("running updateTask method in AdminControlleruPdatetask:" + updatedTaskDto);
        if (updatedTaskDto == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        return ResponseEntity.status(HttpStatus.OK).body(updatedTaskDto);
    }

    @PostMapping("/task/comment")
    public ResponseEntity<?> creatComment(@RequestParam Long taskId, @RequestParam Long postedBy, @RequestBody String content) {
        CommentDTO creatCommentdtO = adminService.createComment(taskId, postedBy, content);
        log.info("running creatCommnent methiod in  AdminController:" + creatCommentdtO);
        if (creatCommentdtO == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        return ResponseEntity.status(HttpStatus.OK).body(creatCommentdtO);
    }

    @GetMapping("/task/{taskId}/comments")
    public ResponseEntity<?> getCommentsByTask(@PathVariable Long taskId) {
        return ResponseEntity.ok(adminService.getCommentsByTask(taskId));
    }

    @GetMapping("task/search/{title}")
    public ResponseEntity<?> searchTaskbyTitle(@PathVariable String title) {
        return ResponseEntity.ok(adminService.searchTaskByTitle(title));
    }

    @GetMapping("/tasks/overdue")
    public ResponseEntity<Long> getOverdueTaskCount() {
        long overdueTaskCount = taskService.getOverdueTaskCount();
        log.info("running overduTaskCount method in AdminController:" + overdueTaskCount);
        return ResponseEntity.ok(overdueTaskCount);
    }

    @GetMapping("/task/alloverdue")
    public ResponseEntity<?> getOverDueAllTasks() {
        List<TaskDTO> overdutaks = taskService.getAllOverdueTasks();
        if (overdutaks.isEmpty()) {
            return ResponseEntity.
                    status(HttpStatus.NO_CONTENT)
                    .body("No OverDue Tasks Found");
        }
        return ResponseEntity.ok(overdutaks);
    }

    @GetMapping("/tasks/status-counts")
    public ResponseEntity<Map<String, Long>> getTaskStatusCounts() {
        Map<String, Long> statusCounts = taskService.getTaskStatusCounts();
        log.info("running getTaskStatusCount method in adminController:" + statusCounts);
        return ResponseEntity.ok(statusCounts);
    }

    @GetMapping("/tasks/{status}")
    public long getTaskCountByStatus(@PathVariable("status") String status) {
        try {
            // this is a Convert the string status to TaskStatus enum
            TaskStatus taskStatus = TaskStatus.valueOf(status); //  and This will convert the status to TaskStatus enum
            return taskService.getTaskCountByStatus(taskStatus); //  than Call the service method
        } catch (IllegalArgumentException e) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid task status: " + status, e);
        }
    }

    // Endpoint to get task counts by priority
    @GetMapping("/tasks/priority-counts")
    public List<Map<String, Object>> getTaskCountsByPriority() {
        return taskService.getTaskCountsByPriority();
    }

    @GetMapping("/tasks/export")
    public ResponseEntity<byte[]> exportToExcel(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        try {
            // Fetch paginated data using the existing method
            Map<String, Object> paginatedData = taskService.getPaginatedTasks(page, size, sortField, sortDirection);

            // Extract the list of tasks from the response map (paginated tasks for export)
            List<TaskDTO> tasksForExport = (List<TaskDTO>) paginatedData.get("content");

            // Convert TaskDTO list to a list of maps for Excel
            List<Map<String, Object>> tasksForExcel = tasksForExport.stream().map(taskDTO -> {
                Map<String, Object> taskMap = new HashMap<>();
                taskMap.put("id", taskDTO.getId());
                taskMap.put("employeeId", taskDTO.getEmployeeId());
                taskMap.put("title", taskDTO.getTitle());
                taskMap.put("priority", taskDTO.getPriority());
                taskMap.put("dueDate", taskDTO.getDueDate());
                taskMap.put("taskStatus", taskDTO.getTaskStatus());
                taskMap.put("employeeName", taskDTO.getEmployeeName());
                taskMap.put("description", taskDTO.getDescription());
                return taskMap;
            }).collect(Collectors.toList());

            // Generate Excel file as byte array
            byte[] excelFile = exportToExcelService.generateExcel(tasksForExcel);

            // Set headers to force download and return the byte array
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=tasks_page_" + page + "_size_" + size + "_sorted_by_" + sortField + "_" + sortDirection + ".xlsx");
            headers.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            return new ResponseEntity<>(excelFile, headers, HttpStatus.OK);
        } catch (IOException e) {
            // Handle error generating Excel file
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


}
//    @PostMapping("/savetask")
//    public ResponseEntity<?> postTask(@RequestBody TaskDTO taskDto,  @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {
//        // Check if an image is provided and set it in TaskDTO
//        if (image != null && !image.isEmpty()) {
//            taskDto.setImageData(image.getBytes());
//        }
//        TaskDTO createdtask = adminService.postTask(taskDto);
//        log.info("running  adminService method in Admincontroller by creaedTask:" + createdtask);
//        if (createdtask == null)
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//        return ResponseEntity.status(HttpStatus.CREATED).body(createdtask);
//
//    }