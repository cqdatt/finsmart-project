
package com.finsmart.dao;

import com.finsmart.model.Category;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Data Access Object interface for Category entity
 * Handles both system categories and user-defined categories
 */
public interface ICategoryDAO extends IDao<Category, Integer> {
    
    /**
     * Find categories by type (INCOME or EXPENSE)
     * Includes both system and user categories
     * @param type "INCOME" or "EXPENSE"
     * @return list of matching categories
     */
    List<Category> findByType(String type) throws SQLException, ClassNotFoundException;
    
    /**
     * Find categories by type for specific user
     * Includes system categories + user's own categories
     * @param userId the user ID
     * @param type "INCOME" or "EXPENSE"
     * @return list of available categories for user
     */
    List<Category> findByUserIdAndType(Integer userId, String type) throws SQLException, ClassNotFoundException;
    
    /**
     * Find only system categories (available for all users)
     * @param type "INCOME" or "EXPENSE"
     * @return list of system categories
     */
    List<Category> findSystemCategoriesByType(String type) throws SQLException, ClassNotFoundException;
    
    /**
     * Find only user's own categories (not system)
     * @param userId the user ID
     * @param type "INCOME" or "EXPENSE"
     * @return list of user-defined categories
     */
    List<Category> findUserCategoriesByType(Integer userId, String type) throws SQLException, ClassNotFoundException;
    
    /**
     * Check if category name already exists for user
     * @param userId the user ID (null for system categories)
     * @param name the category name
     * @param type "INCOME" or "EXPENSE"
     * @return true if exists, false otherwise
     */
    boolean existsByName(Integer userId, String name, String type) throws SQLException, ClassNotFoundException;
    
    /**
     * Check if category is being used in any transaction
     * Prevents deletion of categories with existing transactions
     * @param categoryId the category ID
     * @return true if in use, false otherwise
     */
    boolean isUsedInTransactions(int categoryId) throws SQLException, ClassNotFoundException;
    
    /**
     * Get count of categories by type for user
     * @param userId the user ID
     * @param type "INCOME" or "EXPENSE"
     * @return count of categories
     */
    int countByUserIdAndType(Integer userId, String type) throws SQLException, ClassNotFoundException;
    boolean hasTransactions(int categoryId) throws SQLException, ClassNotFoundException;
    int deleteByCategoryId(int categoryId) throws SQLException, ClassNotFoundException;
    int updateCategoryId(int oldCategoryId, int newCategoryId) throws SQLException, ClassNotFoundException;
}