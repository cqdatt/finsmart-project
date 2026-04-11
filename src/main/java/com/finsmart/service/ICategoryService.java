package com.finsmart.service;

import com.finsmart.dto.CategoryCreateRequest;
import com.finsmart.dto.CategoryUpdateRequest;
import com.finsmart.model.Category;
import java.sql.SQLException;
import java.util.List;

/**
 * Service interface for Category business logic
 * Handles category management with system/user category distinction
 */
public interface ICategoryService extends IService<Category, Integer> {
    
    // ========== CATEGORY RETRIEVAL ==========
    
    /**
     * Get categories by type for a specific user
     * Includes system categories + user's own categories
     */
    List<Category> getCategoriesForUser(Integer userId, String type) 
            throws SQLException, ClassNotFoundException;
    
    /**
     * Get only system categories (available for all users)
     */
    List<Category> getSystemCategories(String type) 
            throws SQLException, ClassNotFoundException;
    
    /**
     * Get only user's custom categories
     */
    List<Category> getUserCategories(Integer userId, String type) 
            throws SQLException, ClassNotFoundException;
    
    // ========== CRUD WITH DTOs ==========
    boolean hasTransactions(int categoryId) throws SQLException, ClassNotFoundException;
    /**
     * Create new category with validation
     */
    Category createCategory(Integer userId, CategoryCreateRequest request) 
            throws SQLException, ClassNotFoundException, Exception;
    
    /**
     * Update existing category
     */
    Category updateCategory(Integer categoryId, CategoryUpdateRequest request) 
            throws SQLException, ClassNotFoundException, Exception;
    
    // ========== VALIDATION & BUSINESS RULES ==========
    
    /**
     * Check if category name is available for user
     */
    boolean isCategoryNameAvailable(Integer userId, String name, String type) 
            throws SQLException, ClassNotFoundException;
    
    /**
     * Check if category can be deleted (not used in transactions)
     */
    boolean canDeleteCategory(int categoryId) 
            throws SQLException, ClassNotFoundException;
    
    // ========== STATISTICS ==========
    
    /**
     * Get category count for user
     */
    int getCategoryCount(Integer userId, String type) 
            throws SQLException, ClassNotFoundException;
}