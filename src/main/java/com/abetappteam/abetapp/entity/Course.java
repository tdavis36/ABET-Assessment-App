package com.abetappteam.abetapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Entity representing a Course in the ABET app
 * Courses are assigned to instructors and can have multiple sections
 */
@Entity
@Table(name = "courses")
public class Course extends BaseEntity {

    @NotBlank(message = "Course name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Course ID is required")
    @Column(name = "course_id", nullable = false, unique = true)
    private String courseId;

    @NotNull(message = "Semester ID is required")
    @Column(name = "semester_id", nullable = false)
    private Long semesterId;

    @NotNull(message = "Program ID is required")
    @Column(name = "program_id", nullable = false)
    private Long programId;

    @Column(name = "instructor_id")
    private Long instructorId;

    @Column(name = "section")
    private String section;

    // Optional: Course description
    @Column(columnDefinition = "TEXT")
    private String description;

    // Constructors
    public Course() {
    }

    public Course(String name, String courseId, Long semesterId, Long programId) {
        this.name = name;
        this.courseId = courseId;
        this.semesterId = semesterId;
        this.programId = programId;
    }

    public Course(String name, String courseId, Long semesterId, Long programId, String section) {
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

    // Getters and Setters
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
        return "CourseEntity{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", courseId='" + courseId + '\'' +
                ", section='" + section + '\'' +
                ", semesterId=" + semesterId +
                ", programId=" + programId +
                ", instructorId=" + instructorId +
                ", description='" + description + '\'' +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }
}