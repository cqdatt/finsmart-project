package com.finsmart.dto;

public class UserUpdateRequest {
    private String email;
    private String fullName;
    private String avatar;
    
    public UserUpdateRequest() {}
    
    public UserUpdateRequest(String email, String fullName, String avatar) {
        this.email = email;
        this.fullName = fullName;
        this.avatar = avatar;
    }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
}