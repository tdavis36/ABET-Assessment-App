package com.abetappteam.abetapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "measure")
public class Measure extends BaseEntity {

    @Column(name = "course_indicator_id", nullable = false)
    private Long courseIndicatorId;

    // Description (required)
    @NotBlank(message = "Description of Measure is required")
    @Column(name = "measure_description", nullable = false, length = 3000)
    private String description;

    // Optional fields
    @Column(name = "observation", length = 3000)
    private String observation;

    @Column(name = "recommended_action", length = 3000)
    private String recommendedAction;

    @Column(name = "fcar", length = 3000)
    private String fcar;

    // Student performance counts
    @Column(name = "met")
    private Integer studentsMet;

    @Column(name = "exceeded")
    private Integer studentsExceeded;

    @Column(name = "below")
    private Integer studentsBelow;

    /*
     * Each measure has a status:
     * "InProgress", "Submitted", "InReview", "Complete"
     */
    @NotBlank(message = "Measure Status is required")
    @Size(min = 8, max = 10, message = "Measure must have a status of length 8, 9, or 10")
    @Column(name = "m_status", nullable = false, length = 10)
    private String status;

    @Column(name = "is_active", nullable = false)
    private Boolean active;

    // ------------------------------------------
    // Constructors
    // ------------------------------------------

    public Measure() {
        super();
    }

    public Measure(
            Long courseIndicatorId,
            String description,
            String observation,
            String recommendedAction,
            String fcar,
            Integer studentsMet,
            Integer studentsExceeded,
            Integer studentsBelow,
            String status,
            Boolean active
    ) {
        this.courseIndicatorId = courseIndicatorId;
        this.description = description;
        this.observation = observation;
        this.recommendedAction = recommendedAction;
        this.fcar = fcar;
        this.studentsMet = studentsMet;
        this.studentsExceeded = studentsExceeded;
        this.studentsBelow = studentsBelow;
        this.status = status;
        this.active = active;
    }

    // ------------------------------------------
    // Getters & Setters
    // ------------------------------------------

    public Long getCourseIndicatorId() {
        return courseIndicatorId;
    }

    public void setCourseIndicatorId(Long courseIndicatorId) {
        this.courseIndicatorId = courseIndicatorId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public String getRecommendedAction() {
        return recommendedAction;
    }

    public void setRecommendedAction(String recommendedAction) {
        this.recommendedAction = recommendedAction;
    }

    public String getFcar() {
        return fcar;
    }

    public void setFcar(String fcar) {
        this.fcar = fcar;
    }

    public Integer getStudentsMet() {
        return studentsMet;
    }

    public void setStudentsMet(Integer studentsMet) {
        this.studentsMet = studentsMet;
    }

    public Integer getStudentsExceeded() {
        return studentsExceeded;
    }

    public void setStudentsExceeded(Integer studentsExceeded) {
        this.studentsExceeded = studentsExceeded;
    }

    public Integer getStudentsBelow() {
        return studentsBelow;
    }

    public void setStudentsBelow(Integer studentsBelow) {
        this.studentsBelow = studentsBelow;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    // ------------------------------------------
    // toString()
    // ------------------------------------------

    @Override
    public String toString() {
        return "Measure{" +
                "id=" + getId() +
                ", courseIndicatorId=" + courseIndicatorId +
                ", description='" + description + '\'' +
                ", observation='" + observation + '\'' +
                ", recommendedAction='" + recommendedAction + '\'' +
                ", fcar='" + fcar + '\'' +
                ", studentsMet=" + studentsMet +
                ", studentsExceeded=" + studentsExceeded +
                ", studentsBelow=" + studentsBelow +
                ", status='" + status + '\'' +
                ", active=" + active +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }
}
