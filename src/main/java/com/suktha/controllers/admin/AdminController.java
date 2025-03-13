package com.suktha.controllers.admin;
import com.suktha.dtos.CommentDTO;
import com.suktha.dtos.TaskDTO;
import com.suktha.dtos.TaskLinkDTO;
import com.suktha.entity.Category;
import com.suktha.entity.Task;
import com.suktha.enums.TaskStatus;
import com.suktha.repositories.CategoryRepository;
import com.suktha.services.admin.AdminService;
import com.suktha.services.category.CategoryService;
import com.suktha.services.exportToExcel.ExportToExcelService;
import com.suktha.services.task.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/users")
    public ResponseEntity<?> getUsers() {
        log.info("running getUserMethod in AdminController");
        return ResponseEntity.ok(adminService.getUsers());
    }

    @GetMapping("/tasks")
    public ResponseEntity<?> getTask() {
        log.info("running getTask method");
        return ResponseEntity.ok(adminService.getTask());
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getCategories() {
        return ResponseEntity.ok(adminService.getCategories());
    }

    @GetMapping("/task/{id}")
    public ResponseEntity<?> getTaskId(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getTaskByid(id));
    }


    @PostMapping("/savetask")
    public ResponseEntity<?> postTask(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dueDate,
            @RequestParam("priority") String priority,
            @RequestParam("employeeId") Long employeeId,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "categoryName", required = false) String categoryName,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "voice", required = false) MultipartFile voiceFile,
            @RequestParam(value = "links", required = false) List<String> links) throws IOException {

        log.info("🔗 Links received: " + links);
        // Create TaskDTO and populate it with form data
        TaskDTO taskDto = new TaskDTO();
        taskDto.setTitle(title);
        taskDto.setDescription(description);
        taskDto.setDueDate(dueDate); // No need to parse manuallyhe dueDate is a LocalDate
        taskDto.setPriority(priority);
        taskDto.setEmployeeId(employeeId);
        taskDto.setCategoryId((categoryId));
        taskDto.setLocation((location));
        taskDto.setCategoryName((categoryName));
        log.info("📍 Location received: " + location); // Logging location

        // If an image is provided, store the image file name
        if (image != null && !image.isEmpty()) {
            String imageName = saveFileToFileSystem(image, "C:/uploaded_images/");
            taskDto.setImageName(imageName);  // Set the image name in the DTO
        }

        // Save voice file
        if (voiceFile != null && !voiceFile.isEmpty()) {
            String voiceName = saveFileToFileSystem(voiceFile, "C:/uploaded_voices/");
            taskDto.setVoiceName(voiceName);
        }
        // Handle links
        if (links != null && !links.isEmpty()) { //  Ensure the links list is not null
            List<TaskLinkDTO> linkDTOs = links.stream()
                    .map(url -> {
                        TaskLinkDTO linkDTO = new TaskLinkDTO();
                        linkDTO.setUrl(url);
                        return linkDTO;
                    }).toList();
            taskDto.setLinks(linkDTOs);
            log.info("links set in DTO :" + linkDTOs);
        }


        TaskDTO createdTask = adminService.postTask(taskDto);

        if (createdTask == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    private String saveFileToFileSystem(MultipartFile file, String directoryPath) throws IOException {
        Path directory = Paths.get(directoryPath);
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }
        String fileName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
        Path filePath = directory.resolve(fileName);
        Files.write(filePath, file.getBytes());
        return fileName;
    }

    @PutMapping(value = "/task/{id}")
    public ResponseEntity<?> UpdateTask(
            @RequestPart("taskDto") TaskDTO taskDto,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @PathVariable Long id) {
        log.info("Received update request for task {}: {}", id, taskDto);

        if (taskDto.getDueDate() == null) {
            log.warn("Due date is null in the request!");
        }
        TaskDTO updatedTaskDto = adminService.updateTask(taskDto, image, id);
        log.info("running updateTask method in AdminControlleruPdatetask:" + updatedTaskDto);
        if (updatedTaskDto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(updatedTaskDto);
    }

    // Endpoint to get all categories with names only
    @GetMapping("/filter/categories")
    public List<String> getAllCategories() {
        log.info("geting all category names only thats why i used <string> insted of <category> because it geting all with id and names it not necessary");
        return categoryService.getAllCategories();
    }

    @DeleteMapping("/task/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        adminService.deleteTask(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/tasks/filter")
    public List<TaskDTO> filterTasks(
            @RequestParam(required = false) List<String> priority,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) List<String> taskStatus,
            @RequestParam(required = false) String employeeName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate,
            @RequestParam(required = false) List<String> categoryNames) {

        List<TaskStatus> taskStatusEnums = (taskStatus != null) ?
                taskStatus.stream().map(TaskStatus::valueOf).collect(Collectors.toList()) : null;

        return adminService.filterTasks(priority, title, dueDate, taskStatusEnums, employeeName, categoryNames);
    }

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

    @GetMapping("tasks/today")
    public ResponseEntity<List<TaskDTO>> getTaskDueToday() {
        return ResponseEntity.ok(adminService.getTasksDueToday());
    }

    @GetMapping("tasks/yesterday")
    public ResponseEntity<List<TaskDTO>> getTasksDueYesterday() {
        return ResponseEntity.ok(adminService.getTasksDueYesterday());
    }

    @GetMapping("tasks/this-week")
    public ResponseEntity<List<TaskDTO>> getTasksDueThisWeek() {
        return ResponseEntity.ok(adminService.getTasksDueThisWeek());
    }

    @GetMapping("tasks/last-week")
    public ResponseEntity<List<TaskDTO>> getTasksLastWeek() {
        return ResponseEntity.ok(adminService.getTasksDueLastWeek());
    }

    @GetMapping("/tasks/this-month")
    public ResponseEntity<List<TaskDTO>> getTasksThisMonth() {
        return ResponseEntity.ok(adminService.getTasksDueThisMonth());
    }

    @GetMapping("/tasks/last-month")
    public ResponseEntity<List<TaskDTO>> getTasksLastMonth() {
        return ResponseEntity.ok(adminService.getTasksDueLastMonth());
    }

    @GetMapping("/tasks/this-year")
    public ResponseEntity<List<TaskDTO>> getTasksThisYear() {
        return ResponseEntity.ok(adminService.getTasksDueThisYear());
    }

    @GetMapping("/tasks/custom")
    public ResponseEntity<List<TaskDTO>> getTasksByCustomDateRange(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(adminService.getTasksByCustomDateRange(startDate, endDate));
    }

    @GetMapping("/tasks/export")
    public ResponseEntity<byte[]> exportToExcel() {
        try {
            //  Map<String, Object> paginatedData = taskService.getPaginatedTasks(page, size, sortField, sortDirection);

            List<TaskDTO> tasksForExport = taskService.getAllTasks();

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
            headers.add("Content-Disposition", "attachment; filename=all_tasks.xlsx");
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
//    private String saveImageToFileSystem(MultipartFile image) throws IOException {
//        Path imagesDirectory = Paths.get("C:/uploaded_images");
//
//        // Check if the directory exists, and if not, create it
//        if (!Files.exists(imagesDirectory)) {
//            Files.createDirectories(imagesDirectory);
//        }
//
//        // Create a unique file name for the image
//        String imageName = System.currentTimeMillis() + "-" + image.getOriginalFilename();
//
//        // Define the file path to store the image
//        Path imagePath = imagesDirectory.resolve(imageName);
//
//        // Save the image to the file system
//        Files.write(imagePath, image.getBytes());
//
//        return imageName;  // Return the image name (or path) to save in the database

//    @GetMapping("/tasks/filter")
//    public List<TaskDTO> filterTasks(
//            @RequestParam(required = false) List<String> priority,
//            @RequestParam(required = false) String title,
//            @RequestParam(required = false) List<TaskStatus> taskStatus,
//            @RequestParam(required = false) String employeeName,
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate,
//            @RequestParam(required = false) List<String> categoryNames) {
//        return adminService.filterTasks(priority, title, dueDate, taskStatus, employeeName,categoryNames);
//    }
