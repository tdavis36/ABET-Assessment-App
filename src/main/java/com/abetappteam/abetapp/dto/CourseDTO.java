package com.abetappteam.abetapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CourseDTO {

    private Long id;

    @NotBlank(message = "Course name is required")
    private String name;

    @NotBlank(message = "Course ID is required")
    private String courseId;

    @NotNull(message = "Semester ID is required")
    private Long semesterId;

    @NotNull(message = "Program ID is required")
    private Long programId;

    private Long instructorId;

    private String section;

    private String description;

    // Constructors
    public CourseDTO() {
    }

    public CourseDTO(String name, String courseId, Long semesterId, Long programId) {
        this.name = name;
        this.courseId = courseId;
        this.semesterId = semesterId;
        this.programId = programId;
    }

    public CourseDTO(String name, String courseId, Long semesterId, Long programId, String section) {
        this.name = name;
        this.courseId = courseId;
        this.semesterId = semesterId;
        this.programId = programId;
        this.section = section;
    }

    public String getFullCourseId() {
        if (section != null && !section.trim().isEmpty()) {
            return courseId + "-" + section;
        }
        return courseId;
    }

    public boolean hasSection() {
        return section != null && !section.trim().isEmpty();
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public Long getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(Long semesterId) {
        this.semesterId = semesterId;
    }

    public Long getProgramId() {
        return programId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    public Long getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(Long instructorId) {
        this.instructorId = instructorId;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "CourseDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", courseId='" + courseId + '\'' +
                ", section='" + section + '\'' +
                ", semesterId=" + semesterId +
                ", programId=" + programId +
                ", instructorId=" + instructorId +
                ", description='" + description + '\'' +
                '}';
    }
}