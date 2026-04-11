package com.finsmart.service;

//  IMPORT DTOs TỪ PACKAGE RIÊNG
import com.finsmart.dto.BudgetAlert;
import com.finsmart.dto.BudgetStatus;
import com.finsmart.dto.BudgetCreateRequest;
import com.finsmart.dto.BudgetUpdateRequest;
import com.finsmart.model.Budget;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import com.finsmart.dao.IBudgetDAO; 

public interface IBudgetService extends IService<Budget, Integer> {
    
        // ========== BUDGET RETRIEVAL ==========
        
        Optional<Budget> getBudgetForPeriod(int userId, Integer categoryId, int month, int year) 
                throws SQLException, ClassNotFoundException;
        
        List<Budget> getBudgetsForPeriod(int userId, int month, int year) 
                throws SQLException, ClassNotFoundException;
        
        Optional<Budget> getOverallBudget(int userId, int month, int year) 
                throws SQLException, ClassNotFoundException;
        
        // ========== CRUD WITH DTOs ==========
        
        Budget createBudget(int userId, BudgetCreateRequest request) 
                throws SQLException, ClassNotFoundException, Exception;
        
        Budget updateBudget(Integer budgetId, BudgetUpdateRequest request) 
                throws SQLException, ClassNotFoundException, Exception;
        Budget getBudgetByCategoryAndPeriod(int userId, Integer categoryId, int month, int year) 
                throws SQLException, ClassNotFoundException;
        // ========== SPENDING TRACKING ==========
        
        BigDecimal calculateSpentAmount(int userId, int categoryId, int month, int year) 
                throws SQLException, ClassNotFoundException;
        
        BigDecimal calculateRemaining(Budget budget) 
                throws SQLException, ClassNotFoundException;
        
        //  RETURN TYPE LÀ DTO TỪ PACKAGE, KHÔNG PHẢI NESTED CLASS
        BudgetStatus getBudgetStatus(Budget budget) 
                throws SQLException, ClassNotFoundException;
        
        // ========== ALERTS ==========
        
        // RETURN TYPE LÀ LIST OF DTO TỪ PACKAGE
        List<BudgetAlert> getBudgetAlerts(int userId, int month, int year) 
                throws SQLException, ClassNotFoundException;
        
        // ========== BUSINESS RULES ==========
        
        boolean canCreateBudget(int userId, Integer categoryId, int month, int year) 
                throws SQLException, ClassNotFoundException;
        
        void validateBudgetAmount(BigDecimal amount) throws Exception;
        // Thêm vào interface
        int deleteByCategoryId(int categoryId) throws SQLException, ClassNotFoundException;
}