package com.suktha.services.admin;
import com.suktha.Mappers.TaskMapper;
import com.suktha.dtos.CommentDTO;
import com.suktha.dtos.TaskDTO;
import com.suktha.dtos.UserDTO;
import com.suktha.entity.Category;
import com.suktha.entity.Comment;
import com.suktha.entity.Task;
import com.suktha.entity.User;
import com.suktha.enums.TaskStatus;
import com.suktha.enums.UserRole;
import com.suktha.repositories.CategoryRepository;
import com.suktha.repositories.CommentRepository;
import com.suktha.repositories.TaskRepository;
import com.suktha.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdminServiceImplement implements AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<UserDTO> getUsers() {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getUserRole() == UserRole.EMPLOYEE)
                .map(User::getUserDTO)
                .collect(Collectors.toList());
    }

    public TaskDTO postTask(TaskDTO taskDto) {
        Optional<User> optionalUser = userRepository.findById(taskDto.getEmployeeId());
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + taskDto.getEmployeeId());
        }
        Task task = new Task();
        task.setTitle(taskDto.getTitle());
        task.setUser(optionalUser.get());
        task.setPriority(taskDto.getPriority());
        task.setDescription(taskDto.getDescription());
        task.setTaskStatus(TaskStatus.PENDING);
        task.setDueDate(taskDto.getDueDate());
        // Handle category selection or creation
        Category category = null;
        if (taskDto.getCategoryId() != null) {
            category = categoryRepository.findById(taskDto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found!"));
        } else if (taskDto.getCategoryName() != null && !taskDto.getCategoryName().isBlank()) {
            category = new Category();
            category.setName(taskDto.getCategoryName());
            category = categoryRepository.save(category);
        } else {
            throw new RuntimeException("Category is required!");
        }

        task.setCategory(category);

        // Store the image name in the Task entity image column
        if (taskDto.getImageName() != null) {
            task.setImageName(taskDto.getImageName());
        }
        if (taskDto.getVoiceName() != null) {
            task.setVoiceName((taskDto.getVoiceName()));
        }



        return taskRepository.save(task).getTaskDTO();
    }

    //        // Set image and voice file names
//        task.setImageName(taskDto.getImageName());
//        task.setVoiceName(taskDto.getVoiceName());
    @Override
    public List<TaskDTO> getTask() {
        return taskRepository
                .findAll()
                .stream()
                .map(Task::getTaskDTO)//this is canvert entity to dto map
                .collect(Collectors.toList());
    }
    @Override
    public TaskDTO getTaskByid(Long id) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        return optionalTask
                .map(Task::getTaskDTO) //entity to dto
                .orElse(null);
    }

    @Override
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    @Override
    public List<TaskDTO> searchTaskByTitle(String title) {
        return taskRepository.findAllBytitleContaining(title).stream().map(Task::getTaskDTO).collect(Collectors.toList());
    }

    @Override
    public TaskDTO updateTask(TaskDTO taskDto, Long id) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        log.info("running updatetask method in AdminServiceImplement clas :" + optionalTask);
        Optional<User> optionalUser = userRepository.findById(taskDto.getEmployeeId());
        // Optional<Category> optionalCategory = categoryRepository.findById(taskDto.getCategoryId());
        // Fetch category
        if (optionalTask.isPresent() && optionalUser.isPresent()) {
            Task existingTask = optionalTask.get();
            existingTask.setTitle(taskDto.getTitle());
            existingTask.setDescription(taskDto.getDescription());
            existingTask.setPriority(taskDto.getPriority());
            existingTask.setUser(optionalUser.get());
            existingTask.setDueDate(taskDto.getDueDate());
            TaskStatus taskStatus = mapStringToTaskStatus(String.valueOf(taskDto.getTaskStatus()));
            existingTask.setTaskStatus(taskStatus);

            // Handle category selection or creation
            Category category = null;
            if (taskDto.getCategoryId() != null) {
                // Use existing category
                Optional<Category> optionalCategory = categoryRepository.findById(taskDto.getCategoryId());
                category = optionalCategory.orElse(null);
            } else if (taskDto.getCategoryName() != null && !taskDto.getCategoryName().isEmpty()) {
                // Create new category
                category = new Category();
                category.setName(taskDto.getCategoryName());
                category = categoryRepository.save(category); // Save new category
            }
            existingTask.setCategory(category); // Set category before saving
            // existingTask.setCategory((optionalCategory.get())); //update category
            return taskRepository.save(existingTask).getTaskDTO();
        }
        return null;
    }

    @Override
    public CommentDTO createComment(Long taskId, Long postedBy, String content) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        Optional<User> optionalUser = userRepository.findById(postedBy);
        if (optionalTask.isPresent() && optionalUser.isPresent()) {
            Comment comment = new Comment();
            comment.setContent(content);
            comment.setCreatedAt(new Date());
            comment.setTask(optionalTask.get());
            comment.setUser(optionalUser.get());
            return commentRepository.save(comment).getCommentDto();
        }

        throw new EntityNotFoundException("task or user not found");
    }

    @Override
    public List<CommentDTO> getCommentsByTask(Long taskId) {
        return commentRepository
                .findAllByTaskId(taskId)
                .stream()
                .map(Comment::getCommentDto)//canavert entity to dto because i transfer data entity to controler geting
                .collect(Collectors.toList());
    }
    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

//    public List<TaskDTO> filterTasks(List<String> priorities, String title, LocalDate dueDate, List<TaskStatus> taskStatuses, String employeeName, List<String> categoryNames) {
//        List<Task> filteredTasks = this.taskRepository.findByFilters(priorities, title, dueDate, taskStatuses, employeeName,categoryNames);
//        log.info("running filterTasks method in AdminServiceImplement class:" + filteredTasks);
//        return filteredTasks.stream()
//                .map(Task::getTaskDTO) // Convert each Task entity to TaskDTO
//                .collect(Collectors.toList());
//    }

    public List<TaskDTO> filterTasks(List<String> priorities, String title, LocalDate dueDate, List<TaskStatus> taskStatuses, String employeeName, List<String> categoryNames) {
        if (priorities != null && priorities.isEmpty()) priorities = null;
        if (taskStatuses != null && taskStatuses.isEmpty()) taskStatuses = null;
        if (categoryNames != null && categoryNames.isEmpty()) categoryNames = null;

        List<Task> filteredTasks = this.taskRepository.findByFilters(priorities, title, dueDate, taskStatuses, employeeName, categoryNames);
        return filteredTasks.stream()
                .map(Task::getTaskDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(Category::getName)
                .collect(Collectors.toList());
    }

    private TaskStatus mapStringToTaskStatus(String taskStatus) {
        return switch (taskStatus) {
            case "PENDING" -> TaskStatus.PENDING;
            case "INPROGRESS" -> TaskStatus.INPROGRESS;
            case "COMPLETED" -> TaskStatus.COMPLETED;
            case "DEFERRED" -> TaskStatus.DEFERRED;
            default -> TaskStatus.CANCELLED;
        };
    }

    public List<TaskDTO> getTasksDueToday() {
        List<Task> tasks = taskRepository.findByDueDate((LocalDate.now()));
        return TaskMapper.entitytoDTOList(tasks);
    }

    public List<TaskDTO> getTasksDueYesterday() {
        List<Task> tasks = taskRepository.findByDueDate(LocalDate.now().minusDays(1));
        return TaskMapper.entitytoDTOList(tasks);
    }

    public List<TaskDTO> getTasksDueThisWeek() {
        LocalDate startOfWeek = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
        LocalDate endOfWeek = LocalDate.now().with(java.time.DayOfWeek.SUNDAY);
        List<Task> tasks = taskRepository.findByDueDateBetween(startOfWeek, endOfWeek);
        return TaskMapper.entitytoDTOList(tasks);
    }

    public List<TaskDTO> getTasksDueLastWeek() {
        LocalDate startOfLastWeek = LocalDate.now().minusWeeks(1).with(java.time.DayOfWeek.MONDAY);
        LocalDate endOfLastWeek = LocalDate.now().minusWeeks(1).with(java.time.DayOfWeek.SUNDAY);
        List<Task> tasks = taskRepository.findByDueDateBetween(startOfLastWeek, endOfLastWeek);
        return TaskMapper.entitytoDTOList(tasks);
    }

    public List<TaskDTO> getTasksDueThisMonth() {
        YearMonth currentMonth = YearMonth.now();
        LocalDate startOfMonth = currentMonth.atDay(1);
        LocalDate endOfMonth = currentMonth.atEndOfMonth();
        List<Task> tasks = taskRepository.findByDueDateBetween(startOfMonth, endOfMonth);
        return TaskMapper.entitytoDTOList(tasks);
    }

    public List<TaskDTO> getTasksDueLastMonth() {
        YearMonth lastMonth = YearMonth.now().minusMonths(1);
        LocalDate startOfLastMonth = lastMonth.atDay(1);
        LocalDate endOfLastMonth = lastMonth.atEndOfMonth();
        List<Task> tasks = taskRepository.findByDueDateBetween(startOfLastMonth, endOfLastMonth);
        return TaskMapper.entitytoDTOList(tasks);
    }

    public List<TaskDTO> getTasksDueThisYear() {
        LocalDate startOfYear = LocalDate.now().withDayOfYear(1);
        LocalDate endOfYear = LocalDate.now().withDayOfYear(LocalDate.now().lengthOfYear());
        List<Task> tasks = taskRepository.findByDueDateBetween(startOfYear, endOfYear);
        return TaskMapper.entitytoDTOList(tasks);
    }

    public List<TaskDTO> getTasksByCustomDateRange(LocalDate startDate, LocalDate endDate) {
        List<Task> tasks = taskRepository.findByDueDateBetween(startDate, endDate);
        return TaskMapper.entitytoDTOList(tasks);
    }

}
