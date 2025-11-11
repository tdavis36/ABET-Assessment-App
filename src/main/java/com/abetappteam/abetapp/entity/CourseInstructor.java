package com.abetappteam.abetapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Entity representing the relationship between a Course and an Instructor (via ProgramUser)
 * Junction table: course_instructor
 */
@Entity
@Table(name = "course_instructor")
public class CourseInstructor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Program User ID is required")
    @Column(name = "programuser_id", nullable = false)
    private Long programUserId;

    @NotNull(message = "Course ID is required")
    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // You can add ManyToOne relationships if needed
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "course_id", insertable = false, updatable = false)
    // private Course course;
    //
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "programUser_id", insertable = false, updatable = false)
    // private ProgramUser programUser;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isActive == null) {
            isActive = true;
        }
    }

    // Constructors
    public CourseInstructor() {
    }

    public CourseInstructor(Long programUserId, Long courseId) {
        this.programUserId = programUserId;
        this.courseId = courseId;
        this.isActive = true;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProgramUserId() {
        return programUserId;
    }

    public void setProgramUserId(Long programUserId) {
        this.programUserId = programUserId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "CourseInstructor{" +
                "id=" + id +
                ", programUserId=" + programUserId +
                ", courseId=" + courseId +
                ", createdAt=" + createdAt +
                ", isActive=" + isActive +
                '}';
    }
}