package com.abetappteam.abetapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PerformanceIndicatorDTO {

    private Long id;

    @NotBlank(message = "Performance indicator description is required")
    private String description;

    @NotNull(message = "Indicator number is required")
    private Integer indicatorNumber;

    private Integer indicatorValue;

    private String evaluation;

    @NotNull(message = "Student outcome ID is required")
    private Long studentOutcomeId;

    private Double thresholdPercentage;

    private Boolean isActive;

    // Constructors
    public PerformanceIndicatorDTO() {
    }

    public PerformanceIndicatorDTO(String description, Integer indicatorNumber, Long studentOutcomeId) {
        this.description = description;
        this.indicatorNumber = indicatorNumber;
        this.studentOutcomeId = studentOutcomeId;
        this.isActive = true;
        this.thresholdPercentage = 70.00;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Integer getIndicatorValue() {
        return indicatorValue;
    }

    public void setIndicatorValue(Integer indicatorValue) {
        this.indicatorValue = indicatorValue;
    }

    public String getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(String evaluation) {
        this.evaluation = evaluation;
    }

    public Long getStudentOutcomeId() {
        return studentOutcomeId;
    }

    public void setStudentOutcomeId(Long studentOutcomeId) {
        this.studentOutcomeId = studentOutcomeId;
    }

    public Double getThresholdPercentage() {
        return thresholdPercentage;
    }

    public void setThresholdPercentage(Double thresholdPercentage) {
        this.thresholdPercentage = thresholdPercentage;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "PerformanceIndicatorDTO{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", indicatorNumber=" + indicatorNumber +
                ", indicatorValue=" + indicatorValue +
                ", evaluation='" + evaluation + '\'' +
                ", studentOutcomeId=" + studentOutcomeId +
                ", thresholdPercentage=" + thresholdPercentage +
                ", isActive=" + isActive +
                '}';
    }
}