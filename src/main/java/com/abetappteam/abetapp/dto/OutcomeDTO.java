package com.abetappteam.abetapp.dto;

import jakarta.validation.constraints.NotBlank;

public class OutcomeDTO {
    private Integer number;

    private Integer value;

    @NotBlank(message = "Description of Student Outcome is required")
    private String description;

    private String evaluation;

    private Long semesterId;

    private Boolean active;

    //Constructors
    public OutcomeDTO(){}

    public OutcomeDTO(Integer number, Integer value, String description, String evaluation, Long semesterId, Boolean active){
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
}
