package com.ABETAppTeam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TaskController manages Task creation and retrieval.
 * 
 * This class follows the singleton pattern for consistency with other
 * controllers
 * in the application.
 */
public class TaskController {
    // Singleton instance
    private static TaskController instance;

    // Storage for tasks
    private final Map<String, Task> tasks;

    /**
     * Private constructor for singleton pattern
     */
    private TaskController() {
        this.tasks = new HashMap<>();
    }

    /**
     * Get the singleton instance of the TaskController
     * 
     * @return The TaskController instance
     */
    public static synchronized TaskController getInstance() {
        if (instance == null) {
            instance = new TaskController();
        }
        return instance;
    }

    /**
     * Create a new task and store it using parameters
     * 
     * @param taskName    Name of the task
     * @param description Description of the task
     * @param professorId ID of the professor assigned to the task
     * @return The created Task object
     */
    public Task createTask(String taskName, String description, String professorId) {
        Task newTask = new Task(taskName, description, professorId);
        this.tasks.put(newTask.getTaskId(), newTask);
        return newTask;
    }

    /**
     * Print all tasks to the console (for debugging)
     */
    public void printAllTasks() {
        System.out.println("📌 DEBUG: All Tasks in TaskController:");
        if (this.tasks.isEmpty()) {
            System.out.println("❌ No tasks found.");
        } else {
            for (Task task : this.tasks.values()) {
                System.out.println("🔹 Task: " + task.getTaskName() + " | Professor: "
                        + task.getAssignedProfessorId() + " | Status: " + task.getStatus());
            }
        }
    }

    /**
     * Store an existing task
     * 
     * @param task The task to store
     */
    public void createTask(Task task) {
        if (task != null && task.getTaskId() != null) {
            this.tasks.put(task.getTaskId(), task);
        }
    }

    /**
     * Retrieve a task by its ID
     * 
     * @param taskId ID of the task to retrieve
     * @return The Task object, or null if not found
     */
    public Task getTask(String taskId) {
        return this.tasks.get(taskId);
    }

    /**
     * Update a task in the map
     * 
     * @param updatedTask The task to update
     * @return true if the update was successful, false otherwise
     */
    public boolean updateTask(Task updatedTask) {
        if (updatedTask == null || updatedTask.getTaskId() == null) {
            return false;
        }

        String taskId = updatedTask.getTaskId();
        if (!this.tasks.containsKey(taskId)) {
            return false;
        }

        this.tasks.put(taskId, updatedTask);
        return true;
    }

    /**
     * Get all tasks assigned to a specific professor
     * 
     * @param professorId ID of the professor
     * @return List of tasks assigned to the professor
     */
    public List<Task> getTasksForProfessor(String professorId) {
        List<Task> result = new ArrayList<>();
        for (Task t : this.tasks.values()) {
            if (t.getAssignedProfessorId() != null && t.getAssignedProfessorId().equals(professorId)) {
                result.add(t);
            }
        }
        return result;
    }

    /**
     * Retrieve all tasks (for admin overview)
     * 
     * @return List of all tasks
     */
    public List<Task> getAllTasks() {
        return new ArrayList<>(this.tasks.values());
    }
}
