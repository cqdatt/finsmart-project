package com.finsmart.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Entity class representing a User in the system
 * Implements Serializable for session storage
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Private fields (Encapsulation)
    private int id;
    private String username;
    private String password; // BCrypt hashed
    private String email;
    private String fullName;
    private String avatar;
    private boolean isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Constructors
    public User() {
        this.isActive = true;
    }
    
    public User(String username, String email, String fullName) {
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.isActive = true;
    }
    
    public User(int id, String username, String email, String fullName, String avatar) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.avatar = avatar;
        this.isActive = true;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getAvatar() {
        return avatar;
    }
    
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Business Logic Methods
    /**
     * Get display name - returns fullName if available, otherwise username
     */
    public String getDisplayName() {
        if (fullName != null && !fullName.trim().isEmpty()) {
            return fullName;
        }
        return username;
    }
    
    /**
     * Check if user has avatar
     */
    public boolean hasAvatar() {
        return avatar != null && !avatar.trim().isEmpty() && !"default-avatar.png".equals(avatar);
    }
    
    // Override toString for debugging
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", isActive=" + isActive +
                '}';
    }
    
    // Override equals and hashCode for comparison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        User user = (User) o;
        return id == user.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}