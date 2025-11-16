package com.abetappteam.abetapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "measure")
public class Measure extends BaseEntity {

    @JoinColumn(name = "courseindicator_id")
    private Long courseIndicatorId;

    //Every character length of 3000 should be reviewed prior to production
    @NotBlank(message = "Description of Measure is required")
    @Column(name = "measure_description", nullable = false, length = 3000)
    private String description;

    @Column(name = "observation", nullable = true, length = 3000)
    private String observation;

    @Column(name = "recommended_action", nullable = true, length = 3000)
    private String recAction;

    @Column(name = "fcar", nullable = true, length = 3000)
    private String fcar;

    @Column(name = "met", nullable = true)
    private Integer met;

    @Column(name = "exceeded", nullable = true)
    private Integer exceeded;

    @Column(name = "below", nullable = true)
    private Integer below;

    /*
     * Each measure has one of the following statuses:
     * "InProgress": The measure hasn't been started or submitted for review yet
     * "Submitted": The measure is awaiting approval from an administrator
     * "InReview": The measure has been rejected at least once, and has not yet been submitted again
     * "Complete": The measure has been approved 
     */
    @NotBlank(message = "Measure Status is required")
    @Size(min = 8, max = 10, message = "Measure must have a status of length 8, 9, or 10")
    @Column(name = "mStatus", nullable = false, length = 10)
    private String status;

    @Column(name="is_active", nullable = false)
    private Boolean active;

    //Constructors
    public Measure(){
        super();
    }

    public Measure(Long courseIndicatorId, String description, String observation, String recAction, String fcar, 
    Integer met, Integer exceeded, Integer below, String status, Boolean active){
        this.courseIndicatorId = courseIndicatorId;
        this.description = description;
        this.observation = observation;
        this.recAction = recAction;
        this.fcar = fcar;
        this.met = met;
        this.exceeded = exceeded;
        this.below = below;
        this.status = status;
        this.active = active;
    }


    //Getters and Setters
    public Long getCourseIndicatorId() {
        return courseIndicatorId;
    }

    public void setCourseIndicatorId(Long courseIndicatorId) {
        this.courseIndicatorId = courseIndicatorId;
    }
 
    public void setDescription(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }

    public String getObservation(){
        return observation;
    }

    public void setObservation(String observation){
        this.observation = observation;
    }

    public String getRecommendedAction(){
        return recAction;
    }

    public void setRecommendedAction(String recAction){
        this.recAction = recAction;
    }

    public String getFCar(){
        return fcar;
    }

    public void setFCar(String fcar){
        this.fcar = fcar;
    }

    public Integer getStudentsMet(){
        return met;
    }

    public Integer getStudentsExceeded(){
        return exceeded;
    }

    public Integer getStudentsBelow(){
        return below;
    }

    public void setStudentsMet(Integer met){
        this.met = met;
    }

    public void setStudentsExceeded(Integer exceeded){
        this.exceeded = exceeded;
    }

    public void setStudentsBelow(Integer below){
        this.below = below;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
 
    public Boolean getActive(){
        return active;
    }

    public void setActive(Boolean active){
        this.active = active;
    }
    
    @Override
    public String toString(){
        return "Measure{" +
                "id=" + getId() +
                ", courseIndicatorId=" + courseIndicatorId +
                ", description='" + description + '\'' +
                ", observation='" + observation + '\'' +
                ", recommended action='" + recAction + '\'' +
                ", fcar='" + fcar + '\'' +
                ", met='" + met + '\'' +
                ", exceeded='" + exceeded + '\'' +
                ", below='" + below + '\'' +
                ", status='" + status + '\'' +
                ", active=" + active +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }
}