package com.abetappteam.abetapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Entity representing a Semester in the ABET app
 * Semesters organize courses and assessments by academic period
 */
@Entity
@Table(name = "semester")
public class Semester extends BaseEntity {

    @NotBlank(message = "Semester name is required")
    @Size(max = 50, message = "Semester name must not exceed 50 characters")
    @Column(nullable = false, length = 50)
    private String name;

    @NotBlank(message = "Semester code is required")
    @Size(max = 20, message = "Semester code must not exceed 20 characters")
    @Column(nullable = false, unique = true, length = 20)
    private String code; // e.g., "FALL-2025"

    @NotNull(message = "Start date is required")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @NotNull(message = "Academic year is required")
    @Column(name = "academic_year", nullable = false)
    private Integer academicYear;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private SemesterType type; // FALL, SPRING, SUMMER, WINTER

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private SemesterStatus status = SemesterStatus.UPCOMING;

    @NotNull(message = "Program ID is required")
    @Column(name = "program_id", nullable = false)
    private Long programId;

    @Column(length = 500)
    private String description;

    @Column(name = "is_current")
    private Boolean isCurrent = false;

    // Constructors
    public Semester() {
    }

    public Semester(String name, String code, LocalDate startDate, LocalDate endDate,
                    Integer academicYear, SemesterType type, Long programId) {
        this.name = name;
        this.code = code;
        this.startDate = startDate;
        this.endDate = endDate;
        this.academicYear = academicYear;
        this.type = type;
        this.programId = programId;
    }

    // Enum definitions
    public enum SemesterType {
        FALL, SPRING, SUMMER, WINTER
    }

    public enum SemesterStatus {
        UPCOMING, // Semester hasn't started yet
        ACTIVE, // Semester is currently ongoing
        COMPLETED, // Semester has ended but assessments may be in progress
        ARCHIVED // Semester and all assessments are finalized
    }

    // Business logic methods
    public boolean isActive() {
        LocalDate today = LocalDate.now();
        return !today.isBefore(startDate) && !today.isAfter(endDate);
    }

    public boolean canAddCourses() {
        return status == SemesterStatus.UPCOMING || status == SemesterStatus.ACTIVE;
    }

    public boolean canGenerateAssessment() {
        return status == SemesterStatus.COMPLETED || status == SemesterStatus.ACTIVE;
    }

    public boolean isEditable() {
        return status == SemesterStatus.UPCOMING || status == SemesterStatus.ACTIVE;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Integer getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(Integer academicYear) {
        this.academicYear = academicYear;
    }

    public SemesterType getType() {
        return type;
    }

    public void setType(SemesterType type) {
        this.type = type;
    }

    public SemesterStatus getStatus() {
        return status;
    }

    public void setStatus(SemesterStatus status) {
        this.status = status;
    }

    public Long getProgramId() {
        return programId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsCurrent() {
        return isCurrent;
    }

    public void setIsCurrent(Boolean isCurrent) {
        this.isCurrent = isCurrent;
    }

    @Override
    public String toString() {
        return "Semester{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", academicYear=" + academicYear +
                ", type=" + type +
                ", status=" + status +
                ", programId=" + programId +
                ", description='" + description + '\'' +
                ", isCurrent=" + isCurrent +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }
}