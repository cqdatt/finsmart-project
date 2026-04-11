package com.finsmart.dao;

import com.finsmart.model.Transaction;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * DAO interface for Transaction operations
 * ONLY METHOD SIGNATURES - NO IMPLEMENTATION
 */
public interface ITransactionDAO {
    
        // ========== CRUD ==========
        
        Transaction findById(Integer id) throws SQLException, ClassNotFoundException;
        
        List<Transaction> findAll() throws SQLException, ClassNotFoundException;
        
        boolean create(Transaction transaction) throws SQLException, ClassNotFoundException;
        
        boolean update(Transaction transaction) throws SQLException, ClassNotFoundException;
        
        boolean delete(Integer id) throws SQLException, ClassNotFoundException;
        
        // ========== Query Methods ==========
        
        List<Transaction> findByUserId(int userId) throws SQLException, ClassNotFoundException;
        
        List<Transaction> findByUserIdAndType(int userId, String type) 
                throws SQLException, ClassNotFoundException;
        
        List<Transaction> findByUserIdAndCategory(int userId, int categoryId) 
                throws SQLException, ClassNotFoundException;
        
        List<Transaction> findByUserIdAndDateRange(int userId, LocalDate startDate, LocalDate endDate) 
                throws SQLException, ClassNotFoundException;
        
        List<Transaction> findByFilters(int userId, String type, Integer categoryId, 
                                        LocalDate startDate, LocalDate endDate, String keyword) 
                throws SQLException, ClassNotFoundException;
        
        List<Transaction> findRecentWithCategory(int userId, int limit) 
                throws SQLException, ClassNotFoundException;
        
        // ========== Financial Calculations ==========
        
        BigDecimal getTotalAmountByTypeAndDateRange(int userId, String type, 
                                                        LocalDate startDate, LocalDate endDate) 
                throws SQLException, ClassNotFoundException;
        
        BigDecimal getTotalAmountByCategoryAndDateRange(int userId, int categoryId,
                                                        LocalDate startDate, LocalDate endDate) 
                throws SQLException, ClassNotFoundException;
        
        // ========== Analytics ==========
        
        Map<String, BigDecimal> getCategorySpendingSummary(int userId, int month, int year) 
                throws SQLException, ClassNotFoundException;
        
        List<Map<String, Object>> getMonthlyComparison(int userId, int months) 
                throws SQLException, ClassNotFoundException;
        
        // ========== CASCADE DELETE ==========
    
        int deleteByCategoryId(int categoryId) throws SQLException, ClassNotFoundException;
        int updateCategoryId(int oldCategoryId, int newCategoryId) throws SQLException, ClassNotFoundException;
        int deleteByUserId(int userId) throws SQLException, ClassNotFoundException;
}