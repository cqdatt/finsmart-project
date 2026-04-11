package com.finsmart.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Transaction model for financial transactions
 * Represents both income and expense records
 */
public class Transaction {
    
    // ========== FIELDS ==========
    
    private int id;
    private int userId;
    private int categoryId;
    private String type;              // "INCOME" or "EXPENSE"
    private BigDecimal amount;
    private LocalDate transactionDate;
    private String description;
    private LocalDateTime createdAt;
    
    // ✅ Extended fields from JOIN with categories table
    private String categoryName;      // From categories.name
    private String categoryIcon; 
    private String categoryType;
    // ========== CONSTRUCTORS ==========
    
    /**
     * Default constructor
     */
    public Transaction() {
    }
    
    /**
     * Constructor with essential fields
     */
    public Transaction(int userId, int categoryId, BigDecimal amount, 
                       LocalDate transactionDate, String description, String type) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.description = description;
        this.type = type;
    }
    
    /**
     * Full constructor with all fields
     */
    public Transaction(int id, int userId, int categoryId, String type, 
                       BigDecimal amount, LocalDate transactionDate, 
                       String description, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.categoryId = categoryId;
        this.type = type;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.description = description;
        this.createdAt = createdAt;
    }
    
    // ========== GETTERS & SETTERS ==========
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public int getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public LocalDate getTransactionDate() {
        return transactionDate;
    }
    
    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // ✅ Extended fields getters/setters
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    public String getCategoryType() { 
        return categoryType; 
    }
    
    public void setCategoryType(String categoryType) { this.categoryType = categoryType; }
    
    public String getCategoryIcon() {
        return categoryIcon;
    }
    
    public void setCategoryIcon(String categoryIcon) {
        this.categoryIcon = categoryIcon;
    }
    
    // ========== HELPER METHODS ==========
    
    /**
     * Check if this is an income transaction
     * @return true if type is INCOME
     */
    public boolean isIncome() {
        return "INCOME".equals(type);
    }
    
    /**
     * Check if this is an expense transaction
     * @return true if type is EXPENSE
     */
    public boolean isExpense() {
        return "EXPENSE".equals(type);
    }
    
    /**
     * Get formatted amount for display
     * @return formatted string like "1.000.000đ" or "-1.000.000đ" for expense
     */
    public String getFormattedAmount() {
        if (amount == null) return "0đ";
        
        long value = amount.longValue();
        String formatted = String.format("%,d", Math.abs(value));
        
        if (isExpense()) {
            return "-" + formatted + "đ";
        }
        return "+" + formatted + "đ";
    }
    
    /**
     * Get formatted date for display
     * @return date in dd/MM/yyyy format
     */
    public String getFormattedDate() {
        if (transactionDate == null) return "";
        return String.format("%02d/%02d/%d", 
            transactionDate.getDayOfMonth(),
            transactionDate.getMonthValue(),
            transactionDate.getYear());
    }
    
    /**
     * Get full icon class with Font Awesome prefix
     * @return icon class like "fas fa-utensils"
     */
    public String getFullCategoryIcon() {
        if (categoryIcon == null || categoryIcon.trim().isEmpty()) {
            return isIncome() ? "fas fa-arrow-up" : "fas fa-arrow-down";
        }
        return categoryIcon;
    }
    
    // ========== OVERRIDES ==========
    
    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", userId=" + userId +
                ", categoryId=" + categoryId +
                ", type='" + type + '\'' +
                ", amount=" + amount +
                ", transactionDate=" + transactionDate +
                ", description='" + description + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", categoryIcon='" + categoryIcon + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Transaction that = (Transaction) o;
        return id == that.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}