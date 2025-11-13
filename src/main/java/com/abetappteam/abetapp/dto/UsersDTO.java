package com.abetappteam.abetapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;

//Data Transfer Object for Users
public class UsersDTO {

    @NotBlank(message = "Email address is required")
    @Email()
    @Size(min = 3, max = 255, message = "Email address must be 3 to 255 characters long")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 1, max = 255, message = "Password must be 1 to 255 characters long")
    private String passwordHash;

    @NotBlank(message = "First name is required")
    @Size(min = 1, max = 100, message = "First name must be 1 to 100 characters long")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 1, max = 100, message = "Last name must be 1 to 100 characters long")
    private String lastName;

    @Size(min = 1, max = 50, message = "Title must be 1 to 50 characters long")
    private String title;

    private Boolean active;

    //Constructors
    public UsersDTO(){}

    public UsersDTO(String email, String passwordHash, String firstName, String lastName, String title, Boolean active) { 
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.title = title;
        this.active = active;
    }


    //Getters and Setters
    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getPasswordHash(){
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash){
        this.passwordHash = passwordHash;
    }

    public String getFirstName(){
        return firstName;
    }

    public void setFirstName(String firstName){
        this.firstName = firstName;
    }

    public String getLastName(){
        return lastName;
    }

    public void setLastName(String lastName){
        this.lastName = lastName;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public Boolean getActive(){
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getFullName(){
        if(title == null){
            return firstName + " " + lastName;
        }
        else{
            return title + " " + firstName + " " + lastName;
        }
    }
}