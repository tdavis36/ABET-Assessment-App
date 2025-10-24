package com.abetappteam.abetapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;

@Entity
@Table(name = "users")
public class Users extends BaseEntity {

    @NotBlank(message = "Email address is required")
    @Email()
    @Size(min = 3, max = 255, message = "Email address must be 3 to 255 characters long")
    @Column(name = "email", nullable = false)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 1, max = 255, message = "Password must be 1 to 255 characters long")
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @NotBlank(message = "First name is required")
    @Size(min = 1, max = 100, message = "First name must be 1 to 100 characters long")
    @Column(name = "name_first", nullable = false, length = 100)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 1, max = 100, message = "Last name must be 1 to 100 characters long")
    @Column(name = "name_last", nullable = false, length = 100)
    private String lastName;

    @Size(min = 1, max = 50, message = "Title must be 1 to 50 characters long")
    @Column(name = "name_title", nullable = true, length = 50)
    private String title;

    @Column(name = "is_active", nullable = false)
    private Boolean active = true;

    //Constructors
    public Users(){
        super();
    }

    public Users(String email, String passwordHash, String firstName, String lastName, String title, Boolean active) { 
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

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", email" + email + '\'' +
                ", passwordHash" + passwordHash + '\'' +
                ", firstName" + firstName + '\'' +
                ", lastName" + lastName + '\'' +
                ", title" + title + '\'' +
                ", active=" + active +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }
}
