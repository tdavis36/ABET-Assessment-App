package com.abetappteam.abetapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Entity representing a Course in the ABET app
 * Courses are assigned to instructors through the CourseInstructor table
 */
@Entity
@Table(name = "course")
public class Course extends BaseEntity {

    @NotBlank(message = "Course code is required")
    @Column(name = "course_code", nullable = false, length = 20)
    private String courseCode;

    @NotBlank(message = "Course name is required")
    @Column(name = "course_name", nullable = false)
    private String courseName;

    @NotBlank(message = "Course description is required")
    @Column(name = "course_description", nullable = false, columnDefinition = "TEXT")
    private String courseDescription;

    @NotNull(message = "Semester ID is required")
    @Column(name = "semester_id", nullable = false)
    private Long semesterId;

    @Column(name = "student_count")
    private Integer studentCount;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Constructors
    public Course() {
    }

    public Course(String courseCode, String courseName, String courseDescription, Long semesterId) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.courseDescription = courseDescription;
        this.semesterId = semesterId;
        this.studentCount = null;
        this.isActive = true;
    }

    // Getters and Setters
    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    public Long getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(Long semesterId) {
        this.semesterId = semesterId;
    }

    public Integer getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(Integer studentCount) {
        this.studentCount = studentCount;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + getId() +
                ", courseCode='" + courseCode + '\'' +
                ", courseName='" + courseName + '\'' +
                ", courseDescription='" + courseDescription + '\'' +
                ", semesterId=" + semesterId +
                ", studentCount=" + studentCount +
                ", isActive=" + isActive +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }
}