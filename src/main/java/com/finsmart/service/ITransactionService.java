package com.finsmart.service;

import com.finsmart.dto.MonthlyComparison;
import com.finsmart.dto.TransactionCreateRequest;
import com.finsmart.dto.TransactionFilter;
import com.finsmart.dto.TransactionUpdateRequest;
import com.finsmart.model.Transaction;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Service interface for Transaction operations
 */
public interface ITransactionService {
        
        // ========== CRUD Operations ==========
        
        Transaction getById(Integer id) throws SQLException, ClassNotFoundException;
        
        List<Transaction> getAll() throws SQLException, ClassNotFoundException;
        
        boolean delete(Integer id) throws SQLException, ClassNotFoundException, Exception;
        
        // ========== Query Operations ==========
        
        List<Transaction> getTransactionsByUser(int userId) throws SQLException, ClassNotFoundException;
        
        List<Transaction> getTransactionsByType(int userId, String type) 
                throws SQLException, ClassNotFoundException;
        
        List<Transaction> getTransactionsByFilters(int userId, TransactionFilter filters) 
                throws SQLException, ClassNotFoundException;        
        
        List<Transaction> getRecentTransactions(int userId, int limit) 
                throws SQLException, ClassNotFoundException;
        
        // ========== Create/Update ==========
        
        Transaction createTransaction(int userId, TransactionCreateRequest request) 
                throws SQLException, ClassNotFoundException, Exception;
        
        Transaction updateTransaction(Integer transactionId, TransactionUpdateRequest request) 
                throws SQLException, ClassNotFoundException, Exception;
        
        // ========== Financial Calculations ==========
        
        BigDecimal calculateTotalIncome(int userId, LocalDate startDate, LocalDate endDate) 
                throws SQLException, ClassNotFoundException;
        
        BigDecimal calculateTotalExpense(int userId, LocalDate startDate, LocalDate endDate) 
                throws SQLException, ClassNotFoundException;
        
        BigDecimal calculateBalance(int userId) throws SQLException, ClassNotFoundException;
        
        // ========== Analytics ==========
        
        Map<String, BigDecimal> getCategorySpendingBreakdown(int userId, int month, int year) 
                throws SQLException, ClassNotFoundException;
        
        List<MonthlyComparison> getMonthlyTrend(int userId, int monthsBack) 
                throws SQLException, ClassNotFoundException;
        
        // ========== Permissions ==========
        boolean canEditTransaction(int transactionId, int userId) 
                throws SQLException, ClassNotFoundException;
        int deleteByCategoryId(int categoryId) throws SQLException, ClassNotFoundException;
        int updateCategoryId(int oldCategoryId, int newCategoryId) throws SQLException, ClassNotFoundException;

}