package com.abetappteam.abetapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "student_outcome")
public class Outcome extends BaseEntity{
    
    @NotBlank(message = "Number of Student Outcome is required")
    @Column(name = "out_number", nullable = false)
    private Integer number;

    @Column(name = "out_value", nullable = true)
    private Integer value;

    @NotBlank(message = "Description of Student Outcome is required")
    @Column(name = "out_description", nullable = false, length = 3000)
    private String description;

    @Column(name = "evaluation", nullable = true, length = 3000)
    private String evaluation;

    @JoinColumn(name = "semester_id")
    private Long semesterId;

    @Column(name = "is_active", nullable = false)
    private Boolean active;

    //Constructors
    public Outcome(){
        super();
    }

    public Outcome(Integer number, Integer value, String description, String evaluation, Long semesterId, Boolean active){
        this.number = number;
        this.value = value;
        this.description = description;
        this.evaluation = evaluation;
        this.semesterId = semesterId;
        this.active = active;
    }

    //Setters and Getters
    public Integer getNumber(){
        return number;
    }

    public void setNumber(Integer number){
        this.number = number;
    }

    public Integer getValue(){
        return value;
    }

    public void setValue(Integer value){
        this.value = value;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getEvaluation(){
        return evaluation;
    }

    public void setEvaluation(String evaluation){
        this.evaluation = evaluation;
    }

    public Long getSemesterId(){
        return semesterId;
    }

    public void setSemesterId(Long semesterId){
        this.semesterId = semesterId;
    }

    public Boolean getActive(){
        return active;
    }

    public void setActive(Boolean active){
        this.active = active;
    }

    @Override
    public String toString(){
        return "StudentOutcome{" +
                "id=" + getId() +
                ", number=" + number +
                ", value='" + value + '\'' +
                ", semesterId='" + semesterId + '\'' +
                ", description='" + description + '\'' +
                ", evaluation='" + evaluation + '\'' +
                ", active=" + active +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }
}
