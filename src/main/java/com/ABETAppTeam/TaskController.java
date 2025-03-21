package com.ABETAppTeam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TaskController manages Task creation and retrieval.
 */
public class TaskController {
    private static final Map<String, Task> tasks = new HashMap<>();  // Corrected storage

    /**
     * Create a new task and store it using parameters
     */
    public static void createTask(String taskName, String formTemplate, String professorId) {
        Task newTask = new Task(taskName, formTemplate, professorId);
        tasks.put(newTask.getTaskId(), newTask);
    }


    public static void printAllTasks() {
        System.out.println("📌 DEBUG: All Tasks in TaskController:");
        if (tasks.isEmpty()) {
            System.out.println("❌ No tasks found.");
        } else {
            for (Task task : tasks.values()) {
                System.out.println("🔹 Task: " + task.getTaskName() + " | Professor: "
                        + task.getAssignedProfessorId() + " | Status: " + task.getStatus());
            }
        }
    }

    public static void createTask(Task task) {
        if (task != null && task.getTaskId() != null) {
            tasks.put(task.getTaskId(), task);
        }
    }

    /**
     * Retrieve a task by its ID
     */
    public static Task getTask(String taskId) {
        return tasks.get(taskId);
    }

    /**
     * Update a task in the map
     */
    public static boolean updateTask(Task updatedTask) {
        String taskId = updatedTask.getTaskId();
        if (!tasks.containsKey(taskId)) {
            return false;
        }
        tasks.put(taskId, updatedTask);
        return true;
    }

    /**
     * Get all tasks assigned to a specific professor
     */
    public static List<Task> getTasksForProfessor(String professorId) {
        List<Task> result = new ArrayList<>();
        for (Task t : tasks.values()) {
            if (t.getAssignedProfessorId() != null && t.getAssignedProfessorId().equals(professorId)) {  // ✅ FIXED
                result.add(t);
            } else {
                System.out.println("Warning: Task " + t.getTaskId() + " has a null professorId.");
            }
        }
        return result;
    }

    /**
     * Retrieve all tasks (for admin overview)
     */
    public static List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());  // Ensures consistency
    }
}
