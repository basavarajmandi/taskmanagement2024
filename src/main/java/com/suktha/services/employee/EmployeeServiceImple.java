package com.suktha.services.employee;
import com.suktha.dtos.CommentDTO;
import com.suktha.dtos.TaskDTO;
import com.suktha.entity.Comment;
import com.suktha.entity.Task;
import com.suktha.entity.User;
import com.suktha.enums.TaskStatus;
import com.suktha.repositories.CommentRepository;
import com.suktha.repositories.TaskRepository;
import com.suktha.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EmployeeServiceImple implements EmployeeService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public Map<String, Object> getPaginatedTasksByUserId(Long userId, int page, int size, String sortField, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Task> taskPage = taskRepository.findAllByUserId(userId, pageable);
        List<TaskDTO> tasks = taskPage.getContent()
                .stream()
                .map(Task::getTaskDTO)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("content", tasks);
        response.put("totalElements", taskPage.getTotalElements());
        response.put("totalPages", taskPage.getTotalPages());
        response.put("size", taskPage.getSize());
        response.put("page", taskPage.getNumber());
        response.put("sortField", sortField);
        response.put("sortDirection", sortDirection);

        return response;
    }

    @Override
    public List<TaskDTO> getTasksByUserId(Long id) {
        return taskRepository.
                findAllByUserId(id).
                stream().
                map(Task::getTaskDTO).
                collect(Collectors.toList());
    }


    @Override
    public TaskDTO updateTask(Long id, String status) {
        Optional<Task> optionalTask = taskRepository.findById(id);

        if (optionalTask.isPresent()) {
            Task existingTask = optionalTask.get();

            TaskStatus taskStatus = mapStringToTaskStatus(String.valueOf(status));
            existingTask.setTaskStatus(taskStatus);
            return taskRepository.save(existingTask).getTaskDTO();
        }
        throw new EntityNotFoundException("Task not found");
    }

    @Override
    public TaskDTO getTaskById(Long id) {
        return taskRepository.findById(id).map(Task::getTaskDTO).orElse(null);
    }

    @Override
    public CommentDTO createComment(Long taskId, Long postedBy, String content,MultipartFile imageFile, MultipartFile voiceFile) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);

        Optional<User> optionalUser = userRepository.findById(postedBy);
        if (optionalTask.isPresent() && optionalUser.isPresent()) {
            Comment comment = new Comment();
            comment.setContent(content);
            comment.setCreatedAt(new Date());
            comment.setTask(optionalTask.get());
            comment.setUser(optionalUser.get());

            // Save Image
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageName = storeFile(imageFile, "C:/uploaded_images/employee/");
                comment.setImageName(imageName);
            }

            // Save Voice Message
            if (voiceFile != null && !voiceFile.isEmpty()) {
                String voiceName = storeFile(voiceFile, "C:/uploaded_voices/employee/");
                comment.setVoiceName(voiceName);
            }

            return commentRepository.save(comment).getCommentDto();
        }

        throw new EntityNotFoundException("task or user not found");
    }

    private String storeFile(MultipartFile file, String directory) {
        try {
            // Ensure the directory exists
            File dir = new File(directory);
            if (!dir.exists()) {
                dir.mkdirs(); // Create directories if they do not exist
            }

            // Generate a unique file name
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            // Define the full path
            Path filePath = Paths.get(directory, fileName);

            // Save the file
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Error saving file: " + e.getMessage(), e);
        }
    }

    @Override
    public List<CommentDTO> getCommentsByTask(Long taskId) {
        return commentRepository.findAllByTaskId(taskId)
                .stream()
                .map(Comment::getCommentDto)// entity to dto Canvarting
                .collect(Collectors.toList());
    }

    public List<TaskDTO> getFilteredTasksByUserId(Long userid, String title, List<String> priorities, List<TaskStatus> taskStatuses, LocalDate dueDate, List<String> categoryNames) {
        // Get filtered tasks from the repository based on parameters
        List<Task> tasks = taskRepository.findFilteredTasks(userid, title, priorities, taskStatuses, dueDate,categoryNames);

        // Convert Task entities to TaskDTOs
        List<TaskDTO> taskDTOs = tasks.stream()
                .map(Task::getTaskDTO)  // Assuming Task has a method to convert to TaskDTO
                .collect(Collectors.toList());

        return taskDTOs;
    }

    private TaskStatus mapStringToTaskStatus(String taskStatus) {
        log.info("running mapStringTo taskStatus method in EmployeeServiceImple");
        return switch (taskStatus) {
            case "PENDING" -> TaskStatus.PENDING;
            case "INPROGRESS" -> TaskStatus.INPROGRESS;
            case "COMPLETED" -> TaskStatus.COMPLETED;
            case "DEFERRED" -> TaskStatus.DEFERRED;
            default -> TaskStatus.CANCELLED;
        };
    }
}
//    public List<TaskDTO> getTasksByUserId(){
//        User user = jwtUtil.getLoggedInUser();
//        if(user!=null){
//            return  taskRepository.findAllByUserId(user.getId())
//          .stream()
//          .sorted(Comparator.comparing(Task::getDueDate).reversed())
//          .map(Task::getTaskDTO)
//          .collect(Collectors.toList());
//
//        }
//        throw  new EntityNotFoundException("UserNot Found");
//
//    }

//    // Method to fetch filtered tasks based on title, taskStatus, priority, and dueDate
//    public List<Task> getFilteredTasks(String title, TaskStatus taskStatus, String priority, LocalDate dueDate) {
//        // Implement filtering logic based on the provided parameters
//        if (title != null && !title.isEmpty()) {
//            return taskRepository.findByTitleContaining(title);
//        } else if (taskStatus != null) {
//            return taskRepository.findByTaskStatus(taskStatus);
//        } else if (priority != null && !priority.isEmpty()) {
//            return taskRepository.findByPriority(priority);
//        } else if (dueDate != null) {
//            return taskRepository.findByDueDate(dueDate);
//        }
//        return taskRepository.findAll(); // Return all tasks if no filter is applied
//    }