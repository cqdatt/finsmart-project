package com.finsmart.service.impl;

import com.finsmart.dao.ICategoryDAO;
import com.finsmart.dao.ITransactionDAO;
import com.finsmart.dao.impl.CategoryDAOImpl;
import com.finsmart.dao.impl.TransactionDAOImpl;
import com.finsmart.dto.MonthlyComparison;
import com.finsmart.dto.TransactionCreateRequest;
import com.finsmart.dto.TransactionFilter;
import com.finsmart.dto.TransactionUpdateRequest;
import com.finsmart.model.Category;
import com.finsmart.model.Transaction;
import com.finsmart.service.ITransactionService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementation of ITransactionService
 * Handles business logic for transaction operations
 */
public class TransactionServiceImpl implements ITransactionService {
    
    private final ITransactionDAO transactionDAO;
    private final ICategoryDAO categoryDAO;
    
    public TransactionServiceImpl() {
        this.transactionDAO = new TransactionDAOImpl();
        this.categoryDAO = new CategoryDAOImpl();
    }
    
    // ========== CRUD METHODS ==========
    
    @Override
    public Transaction getById(Integer id) throws SQLException, ClassNotFoundException {
        return transactionDAO.findById(id);
    }
    
    @Override
    public List<Transaction> getAll() throws SQLException, ClassNotFoundException {
        return transactionDAO.findAll();
    }
    
    @Override
    public boolean delete(Integer id) throws SQLException, ClassNotFoundException, Exception {
        return transactionDAO.delete(id);
    }
    
    // ========== QUERY METHODS ==========
    
    @Override
    public List<Transaction> getTransactionsByUser(int userId) throws SQLException, ClassNotFoundException {
        return transactionDAO.findByUserId(userId);
    }
    @Override
    public List<Transaction> getTransactionsByType(int userId, String type) 
            throws SQLException, ClassNotFoundException {
        return transactionDAO.findByUserIdAndType(userId, type);
    }

    @Override
    public List<Transaction> getTransactionsByFilters(int userId, TransactionFilter filters) 
            throws SQLException, ClassNotFoundException {
        return transactionDAO.findByFilters(
            userId, filters.getType(), filters.getCategoryId(),
            filters.getStartDate(), filters.getEndDate(), filters.getKeyword()
        );
    }

    @Override
    public List<Transaction> getRecentTransactions(int userId, int limit) 
            throws SQLException, ClassNotFoundException {
        return transactionDAO.findRecentWithCategory(userId, limit);
    }
    
    // ========== CREATE/UPDATE ==========
    
    @Override
    public Transaction createTransaction(int userId, TransactionCreateRequest request) 
            throws SQLException, ClassNotFoundException, Exception {
        
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception("Amount must be positive");
        }
        
        // Validate category exists and type matches
        Integer catId = request.getCategoryId();
        if (catId != null) {
            Category category = categoryDAO.findById(catId);
            if (category == null) {
                throw new Exception("Category not found");
            }
            if (!category.getType().equals(request.getType())) {
                throw new Exception("Category type mismatch");
            }
        }
        
        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setCategoryId(request.getCategoryId());
        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setDescription(request.getDescription());
        transaction.setTransactionDate(request.getTransactionDate());
        
        if (transactionDAO.create(transaction)) {
            return transactionDAO.findById(transaction.getId());
        }
        throw new Exception("Failed to create transaction");
    }
    
    @Override
    public Transaction updateTransaction(Integer transactionId, TransactionUpdateRequest request) 
            throws SQLException, ClassNotFoundException, Exception {
        
        System.out.println("=== updateTransaction ===");
        System.out.println("Transaction ID: " + transactionId);
        
        // Validate amount
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception("Amount must be positive");
        }
        
        // Check category exists
        if (request.getCategoryId() != null) {
            Category category = categoryDAO.findById(request.getCategoryId());
            if (category == null) {
                throw new Exception("Category not found");
            }
        }
        
        // Get existing transaction
        Transaction existing = transactionDAO.findById(transactionId);
        if (existing == null) {
            throw new Exception("Transaction not found");
        }
        
        // ✅ UPDATE TYPE FROM REQUEST (if provided)
        if (request.getType() != null) {
            existing.setType(request.getType());
            System.out.println("Updating type to: " + request.getType());
        }
        
        // Update other fields
        if (request.getCategoryId() != null) {
            existing.setCategoryId(request.getCategoryId());
        }
        existing.setAmount(request.getAmount());
        
        if (request.getTransactionDate() != null) {
            existing.setTransactionDate(request.getTransactionDate());
        }
        
        if (request.getDescription() != null) {
            existing.setDescription(request.getDescription());
        }
        
        // Save to database
        boolean updated = transactionDAO.update(existing);
        System.out.println("Update result: " + updated);
        
        // Reload and return
        if (updated) {
            return transactionDAO.findById(transactionId);
        }
        return null;
    }
        // ========== FINANCIAL CALCULATIONS ==========
    
    @Override
    public BigDecimal calculateTotalIncome(int userId, LocalDate startDate, LocalDate endDate) 
            throws SQLException, ClassNotFoundException {
        return transactionDAO.getTotalAmountByTypeAndDateRange(userId, "INCOME", startDate, endDate);
    }
    
    @Override
    public BigDecimal calculateTotalExpense(int userId, LocalDate startDate, LocalDate endDate) 
            throws SQLException, ClassNotFoundException {
        return transactionDAO.getTotalAmountByTypeAndDateRange(userId, "EXPENSE", startDate, endDate);
    }
    
    @Override
    public BigDecimal calculateBalance(int userId) throws SQLException, ClassNotFoundException {
        LocalDate farPast = LocalDate.of(2000, 1, 1);
        LocalDate farFuture = LocalDate.now().plusYears(50);
        
        BigDecimal income = calculateTotalIncome(userId, farPast, farFuture);
        BigDecimal expense = calculateTotalExpense(userId, farPast, farFuture);
        return income.subtract(expense);
    }
    
    // ========== ANALYTICS ==========
    
    @Override
    public Map<String, BigDecimal> getCategorySpendingBreakdown(int userId, int month, int year) 
            throws SQLException, ClassNotFoundException {
        return transactionDAO.getCategorySpendingSummary(userId, month, year);
    }
    
    @Override
    public List<MonthlyComparison> getMonthlyTrend(int userId, int monthsBack) 
            throws SQLException, ClassNotFoundException {
        
        List<Map<String, Object>> rawData = transactionDAO.getMonthlyComparison(userId, monthsBack);
        List<MonthlyComparison> result = new ArrayList<>();
        
        for (Map<String, Object> row : rawData) {
            MonthlyComparison mc = new MonthlyComparison();
            Integer yearVal = (Integer) row.get("year");
            Integer monthVal = (Integer) row.get("month");
            BigDecimal income = (BigDecimal) row.get("income");
            BigDecimal expense = (BigDecimal) row.get("expense");
            
            mc.setYear(yearVal != null ? yearVal : 0);
            mc.setMonth(monthVal != null ? monthVal : 0);
            mc.setIncome(income != null ? income : BigDecimal.ZERO);
            mc.setExpense(expense != null ? expense : BigDecimal.ZERO);
            
            BigDecimal inc = mc.getIncome() != null ? mc.getIncome() : BigDecimal.ZERO;
            BigDecimal exp = mc.getExpense() != null ? mc.getExpense() : BigDecimal.ZERO;
            mc.setBalance(inc.subtract(exp));
            
            result.add(mc);
        }
        return result;
    }
    
    // ========== PERMISSIONS ==========
    
    @Override
    public boolean canEditTransaction(int transactionId, int userId) 
            throws SQLException, ClassNotFoundException {
        
        Transaction transaction = transactionDAO.findById(transactionId);
        if (transaction == null) {
            return false;
        }
        
        // ✅ SAFE: Compare primitives directly (no null check needed)
        if (transaction.getUserId() != userId) {
            return false;
        }
        
        // Optional: Only allow editing recent transactions
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        return !transaction.getTransactionDate().isBefore(thirtyDaysAgo);
    }

    @Override
    public int deleteByCategoryId(int categoryId) throws SQLException, ClassNotFoundException {
        return transactionDAO.deleteByCategoryId(categoryId);
    }
    
    @Override
    public int updateCategoryId(int oldCategoryId, int newCategoryId) throws SQLException, ClassNotFoundException {
        return transactionDAO.updateCategoryId(oldCategoryId, newCategoryId);
    }

}