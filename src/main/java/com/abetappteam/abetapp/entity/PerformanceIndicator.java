package com.abetappteam.abetapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Entity representing a Performance Indicator in the ABET app
 * Performance indicators are associated with student outcomes
 */
@Entity
@Table(name = "performance_indicator")
public class PerformanceIndicator extends BaseEntity {

    @NotBlank(message = "Performance indicator description is required")
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Indicator number is required")
    @Column(name = "indicator_number", nullable = false)
    private Integer indicatorNumber;

    @NotNull(message = "Student outcome ID is required")
    @Column(name = "student_outcome_id", nullable = false)
    private Long studentOutcomeId;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Constructors
    public PerformanceIndicator() {
    }

    public PerformanceIndicator(String description, Integer indicatorNumber, Long studentOutcomeId) {
        this.description = description;
        this.indicatorNumber = indicatorNumber;
        this.studentOutcomeId = studentOutcomeId;
        this.isActive = true;
    }

    // Getters and Setters
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getIndicatorNumber() {
        return indicatorNumber;
    }

    public void setIndicatorNumber(Integer indicatorNumber) {
        this.indicatorNumber = indicatorNumber;
    }

    public Long getStudentOutcomeId() {
        return studentOutcomeId;
    }

    public void setStudentOutcomeId(Long studentOutcomeId) {
        this.studentOutcomeId = studentOutcomeId;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "PerformanceIndicator{" +
                "id=" + getId() +
                ", description='" + description + '\'' +
                ", indicatorNumber=" + indicatorNumber +
                ", studentOutcomeId=" + studentOutcomeId +
                ", isActive=" + isActive +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }
}