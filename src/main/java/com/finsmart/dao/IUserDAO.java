package com.finsmart.dao;

import com.finsmart.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;


/**
 * Data Access Object interface for User entity
 * Extends base IDao with user-specific operations
 */
public interface IUserDAO extends IDao<User, Integer> {
    
    /**
     * Find user by username (case-sensitive)
     * Only returns active users
     * @param username the username to search
     * @return User object if found, null otherwise
     */
// Thêm vào interface
    User findByUsername(String username) throws SQLException, ClassNotFoundException;    
    /**
     * Find user by email (case-insensitive)
     * Only returns active users
     * @param email the email to search
     * @return User object if found, null otherwise
     */
    User findByEmail(String email) throws SQLException, ClassNotFoundException;
    
    /**
     * Check if username already exists in database
     * @param username the username to check
     * @return true if exists, false otherwise
     */
    boolean existsByUsername(String username) throws SQLException, ClassNotFoundException;
    
    /**
     * Check if email already exists in database
     * @param email the email to check
     * @return true if exists, false otherwise
     */
    boolean existsByEmail(String email) throws SQLException, ClassNotFoundException;
    
    /**
     * Find active users by keyword (search in username, email, fullName)
     * @param keyword the search keyword
     * @return list of matching users
     */
    List<User> searchByKeyword(String keyword) throws SQLException, ClassNotFoundException;
    
    /**
     * Update user's last login timestamp
     * @param userId the user ID
     * @return true if update successful
     */
    boolean updateLastLogin(int userId) throws SQLException, ClassNotFoundException;
    
    /**
     * Activate or deactivate user account
     * @param userId the user ID
     * @param isActive the new active status
     * @return true if update successful
     */
    boolean updateActiveStatus(int userId, boolean isActive) throws SQLException, ClassNotFoundException;
    
    /**
     * Change user password (with BCrypt hash)
     * @param userId the user ID
     * @param newPasswordHash the new hashed password
     * @return true if update successful
     */
    boolean changePassword(int userId, String newPasswordHash) throws SQLException, ClassNotFoundException;
}