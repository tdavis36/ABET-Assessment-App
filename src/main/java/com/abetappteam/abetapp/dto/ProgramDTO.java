package com.abetappteam.abetapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProgramDTO {

    @NotBlank(message = "Name of the Program is required")
    @Size(min = 3, max = 255, message = "The Name of a Program must be between 3 and 255 characters long")
    private String name;

    @NotBlank(message = "Name of the Institution is required")
    @Size(min = 2, max = 255, message = "The Name of the Institution must be between 2 and 255 characters long")
    private String institution;

    private Boolean active = true;

    //Constructors
    public ProgramDTO() {}

    public ProgramDTO(String name, String institution, Boolean active) {
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

}
