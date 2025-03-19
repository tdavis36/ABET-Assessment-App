package com.ABETAppTeam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TaskFactory manages Task creation and retrieval.
 */
public class TaskController {
    private static Map<String, Task> tasks = new HashMap<>();

    /**
     * Create a new task and store it in memory
     */
    public static Task createTask(String taskName, String description, String professorId) {
        Task newTask = new Task(taskName, description, professorId);
        tasks.put(newTask.getTaskId(), newTask);
        return newTask;
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
            if (t.getAssignedProfessorId().equals(professorId)) {
                result.add(t);
            }
        }
        return result;
    }

    /**
     * Optional: retrieve all tasks (for admin overview)
     */
    public static List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }
}
