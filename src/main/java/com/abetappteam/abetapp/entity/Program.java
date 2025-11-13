package com.abetappteam.abetapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "program")
public class Program extends BaseEntity {
    
    @NotBlank(message = "Name of the Program is required")
    @Size(min = 3, max = 255, message = "The Name of a Program must be between 3 and 255 characters long")
    @Column(name="program_name", nullable = false, length = 255)
    private String name;

    @NotBlank(message = "Name of the Institution is required")
    @Size(min = 2, max = 255, message = "The Name of the Institution must be between 2 and 255 characters long")
    @Column(name="institution", nullable = false, length = 255)
    private String institution;

    @Column(name="is_active", nullable = false)
    private Boolean active = true;

    //Constructors
    public Program() {
        super();
    }

    public Program(String name, String institution, Boolean active) {
        this.name = name;
        this.institution = institution;
        this.active = active;
    }

    //Getters and Setters 
    public String getName() {
        return name;
    }

    public String getInstitution() {
        return institution;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public Boolean getActive(){
        return active;
    }

    public void setActive(Boolean active){
        this.active = active;
    }

    @Override
    public String toString(){
        return "Program{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", institution='" + institution + '\'' +
                ", active=" + active +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }
}