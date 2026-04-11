package com.finsmart.dao;

import com.finsmart.model.Budget;

import java.sql.SQLException;
import java.util.List;

/**
 * DAO interface for Budget operations
 */
public interface IBudgetDAO {
        
        // ========== CRUD ==========
        
        Budget findById(Integer id) throws SQLException, ClassNotFoundException;
        
        List<Budget> findAll() throws SQLException, ClassNotFoundException;
        
        boolean create(Budget budget) throws SQLException, ClassNotFoundException;
        
        boolean update(Budget budget) throws SQLException, ClassNotFoundException;
        
        boolean delete(Integer id) throws SQLException, ClassNotFoundException;
        
        // ========== Query Methods ==========
        
        List<Budget> findByUserIdAndPeriod(int userId, int month, int year) 
                throws SQLException, ClassNotFoundException;
        
        Budget findByUserAndCategoryAndPeriod(int userId, Integer categoryId, int month, int year) 
                throws SQLException, ClassNotFoundException;
        
        List<Budget> findByUserId(int userId) throws SQLException, ClassNotFoundException;
        // Thêm vào interface
        int deleteByCategoryId(int categoryId) throws SQLException, ClassNotFoundException;
}