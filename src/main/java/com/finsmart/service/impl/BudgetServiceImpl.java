package com.finsmart.service.impl;

import com.finsmart.dao.IBudgetDAO;
import com.finsmart.dao.impl.BudgetDAOImpl;
import com.finsmart.dto.BudgetAlert;
import com.finsmart.dto.BudgetCreateRequest;
import com.finsmart.dto.BudgetStatus;
import com.finsmart.dto.BudgetUpdateRequest;
import com.finsmart.model.Budget;
import com.finsmart.service.IBudgetService;
import com.finsmart.service.ITransactionService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BudgetServiceImpl implements IBudgetService {
    
    private final IBudgetDAO budgetDAO;
    private final ITransactionService transactionService;
    
    public BudgetServiceImpl() {
        this.budgetDAO = new BudgetDAOImpl();
        this.transactionService = new TransactionServiceImpl();
    }
    
    // ========== BASE CRUD ==========
    
    @Override
    public Budget getById(Integer id) throws SQLException, ClassNotFoundException {
        return budgetDAO.findById(id);
    }
    
    @Override
    public List<Budget> getAll() throws SQLException, ClassNotFoundException {
        return budgetDAO.findAll();
    }
    
    @Override
    public boolean delete(Integer id) throws SQLException, ClassNotFoundException {
        System.out.println("BudgetServiceImpl.delete: id=" + id);
        boolean result = budgetDAO.delete(id);
        System.out.println("BudgetServiceImpl.delete result: " + result);
        return result;
    }
    
    // ========== BUDGET RETRIEVAL ==========
    
    @Override
    public Optional<Budget> getBudgetForPeriod(int userId, Integer categoryId, int month, int year) 
            throws SQLException, ClassNotFoundException {
        System.out.println("getBudgetForPeriod: userId=" + userId + ", catId=" + categoryId + ", period=" + month + "/" + year);
        Budget budget = budgetDAO.findByUserAndCategoryAndPeriod(userId, categoryId, month, year);
        System.out.println("Result: " + (budget != null ? "Found ID=" + budget.getId() : "Not found"));
        return Optional.ofNullable(budget);
    }
    
    @Override
    public List<Budget> getBudgetsForPeriod(int userId, int month, int year) 
            throws SQLException, ClassNotFoundException {
        System.out.println("getBudgetsForPeriod: userId=" + userId + ", period=" + month + "/" + year);
        List<Budget> budgets = budgetDAO.findByUserIdAndPeriod(userId, month, year);
        System.out.println("Found " + budgets.size() + " budgets");
        return budgets;
    }
    
    @Override
    public Optional<Budget> getOverallBudget(int userId, int month, int year) 
            throws SQLException, ClassNotFoundException {
        System.out.println("getOverallBudget: userId=" + userId + ", period=" + month + "/" + year);
        Budget budget = budgetDAO.findByUserAndCategoryAndPeriod(userId, null, month, year);
        System.out.println("Result: " + (budget != null ? "Found ID=" + budget.getId() : "Not found"));
        return Optional.ofNullable(budget);
    }
    
    @Override
    public Budget getBudgetByCategoryAndPeriod(int userId, Integer categoryId, int month, int year) 
            throws SQLException, ClassNotFoundException {
        System.out.println("getBudgetByCategoryAndPeriod: userId=" + userId + ", catId=" + categoryId + ", period=" + month + "/" + year);
        return budgetDAO.findByUserAndCategoryAndPeriod(userId, categoryId, month, year);
    }
    
    // ========== CRUD WITH DTOs ==========
    
   @Override
    public Budget createBudget(int userId, BudgetCreateRequest request) 
            throws SQLException, ClassNotFoundException, Exception {
        
        System.out.println("BudgetServiceImpl.createBudget: userId=" + userId + 
                        ", catId=" + request.getCategoryId() + ", period=" + request.getMonth() + "/" + request.getYear());
        
        // ✅ CHECK DUPLICATE
        Budget existing = budgetDAO.findByUserAndCategoryAndPeriod(
            userId, request.getCategoryId(), request.getMonth(), request.getYear());
        
        if (existing != null) {
            System.out.println("⚠️ Budget already exists! ID=" + existing.getId());
            throw new Exception("Budget already exists for this category and period");
        }
        
        Budget budget = new Budget();
        budget.setUserId(userId);
        budget.setCategoryId(request.getCategoryId());
        budget.setAmountLimit(request.getAmountLimit());
        budget.setMonth(request.getMonth());
        budget.setYear(request.getYear());
        
        System.out.println("Creating budget...");
        boolean created = budgetDAO.create(budget);
        System.out.println("budgetDAO.create result: " + created);
        
        if (!created) {
            throw new Exception("Failed to create budget in database");
        }
        
        // ✅ VERIFY CREATION
        Budget createdBudget = budgetDAO.findById(budget.getId());
        System.out.println("Created budget ID: " + (createdBudget != null ? createdBudget.getId() : "null"));
        System.out.println("Amount: " + (createdBudget != null ? createdBudget.getAmountLimit() : "null"));
        
        return createdBudget;
    }
    
    @Override
    public Budget updateBudget(Integer budgetId, BudgetUpdateRequest request) 
            throws SQLException, ClassNotFoundException, Exception {
        
        System.out.println("BudgetServiceImpl.updateBudget: id=" + budgetId);
        
        Budget budget = budgetDAO.findById(budgetId);
        if (budget == null) {
            throw new Exception("Budget not found");
        }
        
        // ✅ UPDATE CATEGORY IF PROVIDED
        if (request.getCategoryId() != null) {
            System.out.println("Updating category from " + budget.getCategoryId() + " to " + request.getCategoryId());
            budget.setCategoryId(request.getCategoryId());
        }
        
        // ✅ UPDATE AMOUNT IF PROVIDED
        if (request.getAmountLimit() != null) {
            System.out.println("Updating amount from " + budget.getAmountLimit() + " to " + request.getAmountLimit());
            budget.setAmountLimit(request.getAmountLimit());
        }
        
        boolean updated = budgetDAO.update(budget);
        
        if (!updated) {
            throw new Exception("Failed to update budget");
        }
        
        return budgetDAO.findById(budgetId);
    }
        
    // ========== SPENDING TRACKING ==========
    
    @Override
    public BigDecimal calculateSpentAmount(int userId, int categoryId, int month, int year) 
            throws SQLException, ClassNotFoundException {
        
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();
        
        if (categoryId == 0) {
            return transactionService.calculateTotalExpense(userId, start, end);
        }
        return transactionService.calculateTotalExpense(userId, start, end);
    }
    
    @Override
    public BigDecimal calculateRemaining(Budget budget) throws SQLException, ClassNotFoundException {
        if (budget == null) return BigDecimal.ZERO;
        
        Integer catId = budget.getCategoryId();
        int categoryId = (catId != null) ? catId : 0;
        
        BigDecimal spent = calculateSpentAmount(
            budget.getUserId(),
            categoryId,
            budget.getMonth(),
            budget.getYear()
        );
        
        return budget.getAmountLimit().subtract(spent);
    }
    
    @Override
    public BudgetStatus getBudgetStatus(Budget budget) throws SQLException, ClassNotFoundException {
        if (budget == null) return new BudgetStatus();
        
        Integer catId = budget.getCategoryId();
        int categoryId = (catId != null) ? catId : 0;
        
        BigDecimal spent = calculateSpentAmount(
            budget.getUserId(),
            categoryId,
            budget.getMonth(),
            budget.getYear()
        );
        
        BigDecimal limit = budget.getAmountLimit();
        BigDecimal remaining = limit.subtract(spent);
        int pct = limit.compareTo(BigDecimal.ZERO) > 0 
            ? spent.multiply(BigDecimal.valueOf(100)).divide(limit, 0, BigDecimal.ROUND_HALF_UP).intValue()
            : 0;
        
        BudgetStatus status = new BudgetStatus();
        status.setBudget(budget);
        status.setSpentAmount(spent);
        status.setRemainingAmount(remaining);
        status.setPercentageUsed(pct);
        status.setAlertLevel(pct >= 100 ? "danger" : pct >= 80 ? "warning" : "success");
        return status;
    }
    
    // ========== ALERTS ==========
    
    @Override
    public List<BudgetAlert> getBudgetAlerts(int userId, int month, int year) 
            throws SQLException, ClassNotFoundException {
        
        List<BudgetAlert> alerts = new ArrayList<>();
        List<Budget> budgets = budgetDAO.findByUserIdAndPeriod(userId, month, year);
        
        for (Budget b : budgets) {
            BudgetStatus s = getBudgetStatus(b);
            if (s.getPercentageUsed() >= 80) {
                String type = s.getPercentageUsed() >= 100 ? "OVER_LIMIT" : "NEAR_LIMIT";
                String msg = s.getPercentageUsed() >= 100 
                    ? "Đã vượt ngân sách! (" + s.getPercentageUsed() + "%)"
                    : "Sắp vượt ngân sách! (" + s.getPercentageUsed() + "%)";
                alerts.add(new BudgetAlert(b, type, msg, s.getSpentAmount(), s.getPercentageUsed()));
            }
        }
        return alerts;
    }
    
    // ========== BUSINESS RULES ==========
    
    @Override
    public boolean canCreateBudget(int userId, Integer categoryId, int month, int year) 
            throws SQLException, ClassNotFoundException {
        
        Budget existing = budgetDAO.findByUserAndCategoryAndPeriod(userId, categoryId, month, year);
        return existing == null;
    }
    
    @Override
    public void validateBudgetAmount(BigDecimal amount) throws Exception {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception("Budget amount must be positive");
        }
    }
    
    // ========== CASCADE DELETE ==========
    
    @Override
    public int deleteByCategoryId(int categoryId) throws SQLException, ClassNotFoundException {
        System.out.println("deleteByCategoryId: " + categoryId);
        return budgetDAO.deleteByCategoryId(categoryId);
    }
}