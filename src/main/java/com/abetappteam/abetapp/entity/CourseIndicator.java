package com.abetappteam.abetapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Entity representing the relationship between a Course and a Performance Indicator
 * Junction table: course_indicator
 */
@Entity
@Table(name = "course_indicator")
public class CourseIndicator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Course ID is required")
    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @NotNull(message = "Indicator ID is required")
    @Column(name = "indicator_id", nullable = false)
    private Long indicatorId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Optional: You can add ManyToOne relationships if needed
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "course_id", insertable = false, updatable = false)
    // private Course course;
    //
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "indicator_id", insertable = false, updatable = false)
    // private PerformanceIndicator indicator;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isActive == null) {
            isActive = true;
        }
    }

    // Constructors
    public CourseIndicator() {
    }

    public CourseIndicator(Long courseId, Long indicatorId) {
        this.courseId = courseId;
        this.indicatorId = indicatorId;
        this.isActive = true;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getIndicatorId() {
        return indicatorId;
    }

    public void setIndicatorId(Long indicatorId) {
        this.indicatorId = indicatorId;
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
        return "CourseIndicator{" +
                "id=" + id +
                ", courseId=" + courseId +
                ", indicatorId=" + indicatorId +
                ", createdAt=" + createdAt +
                ", isActive=" + isActive +
                '}';
    }
}