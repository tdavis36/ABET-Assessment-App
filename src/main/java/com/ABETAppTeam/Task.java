package com.ABETAppTeam;

import java.util.UUID;

public class Task {
    private String taskId;
    private String taskName;
    private String description;
    private String assignedProfessorId;
    private String status; // e.g., "Not Started", "In Progress", "Submitted", "Completed"

    public Task(String taskName, String description, String assignedProfessorId) {
        this.taskId = UUID.randomUUID().toString();
        this.taskName = taskName;
        this.description = description;
        this.assignedProfessorId = assignedProfessorId;
        this.status = "Not Started";
    }

    // Getters/Setters
    public String getTaskId() {
        return taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAssignedProfessorId() {
        return assignedProfessorId;
    }

    public void setAssignedProfessorId(String assignedProfessorId) {
        this.assignedProfessorId = assignedProfessorId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setFcarId(String fcarId) {

    }
}
