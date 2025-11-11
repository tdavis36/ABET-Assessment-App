package com.abetappteam.abetapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "program_user")
public class ProgramUser {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Admin Status is required")
    @Column(name = "isAdmin", nullable = false)
    private Boolean isAdmin;

    @NotNull(message = "Program ID is required")
    @Column(name = "program_id", nullable = false)
    private Long programId;

    @NotNull(message = "User ID is required")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isActive == null) {
            isActive = true;
        }
    }

    //Constructors
    public ProgramUser() {
    }

    public ProgramUser(Boolean isAdmin, Long programId, Long userId) {
        this.isAdmin = isAdmin;
        this.programId = programId;
        this.userId = userId;
    }

    //Getters and Setters
    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public Boolean getAdminStatus(){
        return isAdmin;
    }

    public void setAdminStatus(boolean isAdmin){
        this.isAdmin = isAdmin;
    }

    public Long getProgramId(){
        return programId;
    }

    public void setProgramId(Long programId){
        this.programId = programId;
    }

    public Long getUserId(){
        return userId;
    }

    public void setUserId(Long userId){
        this.userId = userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "ProgramUser{" +
                "id=" + id +
                ", isAdmin=" + isAdmin +
                ", programId=" + programId +
                ", userId=" + userId +
                ", createdAt=" + createdAt +
                ", isActive=" + isActive +
                '}';
    }
}
