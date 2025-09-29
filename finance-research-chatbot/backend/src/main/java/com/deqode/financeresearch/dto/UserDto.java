package com.deqode.financeresearch.dto;

import com.deqode.financeresearch.entity.User;

import java.time.LocalDateTime;

public class UserDto {
    
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private LocalDateTime createdAt;
    private Boolean isActive;
    
    // Constructors
    public UserDto() {}
    
    public UserDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.fullName = user.getFullName();
        this.createdAt = user.getCreatedAt();
        this.isActive = user.getIsActive();
    }
    
    public UserDto(Long id, String email, String firstName, String lastName, LocalDateTime createdAt, Boolean isActive) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = (firstName != null && lastName != null) ? firstName + " " + lastName : email;
        this.createdAt = createdAt;
        this.isActive = isActive;
    }
    
    // Static factory method
    public static UserDto fromEntity(User user) {
        return new UserDto(user);
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}