package com.finsmart.service;

// IMPORT DTO TỪ PACKAGE RIÊNG
import com.finsmart.dto.UserRegisterRequest;
import com.finsmart.dto.UserUpdateRequest;
import com.finsmart.model.User;
import java.sql.SQLException;
import java.util.List;

public interface IUserService extends IService<User, Integer> {
    
        // Authentication
        User authenticate(String username, String password) 
                throws SQLException, ClassNotFoundException, Exception;
        
        boolean isUsernameAvailable(String username) throws SQLException, ClassNotFoundException;
        boolean isEmailAvailable(String email) throws SQLException, ClassNotFoundException;
        
        // CRUD with DTOs (nhận DTO từ package dto, không phải nested class)
        User register(UserRegisterRequest request) 
                throws SQLException, ClassNotFoundException, Exception;
        
        User updateProfile(Integer userId, UserUpdateRequest request) 
                throws SQLException, ClassNotFoundException, Exception;
        
        boolean changePassword(Integer userId, String currentPassword, String newPassword) 
                throws SQLException, ClassNotFoundException, Exception;
        
        boolean deactivateAccount(Integer userId) 
                throws SQLException, ClassNotFoundException, Exception;
        
        // Search
        List<User> searchUsers(String keyword) throws SQLException, ClassNotFoundException;
        
        // ========== AUTH METHODS ==========

        /**
         * Authenticate user with username and password
         * @param username the username
         * @param password the password (plain text or hashed)
         * @return User object if authenticated, null otherwise
         */
        User login(String username, String password) throws SQLException, ClassNotFoundException;

        /**
         * Register new user
         */
        boolean register(User user) throws SQLException, ClassNotFoundException;

        /**
         * Get user by ID
         */
        User getById(Integer id) throws SQLException, ClassNotFoundException;
}