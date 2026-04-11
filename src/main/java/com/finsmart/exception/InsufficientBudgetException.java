package com.finsmart.exception;

/**
 * Custom exception for budget-related errors
 * Thrown when a transaction exceeds the allocated budget
 */
public class InsufficientBudgetException extends Exception {
    
    private final Double budgetLimit;
    private final Double transactionAmount;
    private final String categoryName;
    
    /**
     * Constructor with message only
     */
    public InsufficientBudgetException(String message) {
        super(message);
        this.budgetLimit = null;
        this.transactionAmount = null;
        this.categoryName = null;
    }
    
    /**
     * Constructor with budget details
     * @param message error message
     * @param budgetLimit the allocated budget limit
     * @param transactionAmount the attempted transaction amount
     * @param categoryName the category name
     */
    public InsufficientBudgetException(String message, Double budgetLimit, 
                                        Double transactionAmount, String categoryName) {
        super(message);
        this.budgetLimit = budgetLimit;
        this.transactionAmount = transactionAmount;
        this.categoryName = categoryName;
    }
    
    /**
     * Get the budget limit that was exceeded
     * @return budget limit or null if not set
     */
    public Double getBudgetLimit() {
        return budgetLimit;
    }
    
    /**
     * Get the transaction amount that caused the exception
     * @return transaction amount or null if not set
     */
    public Double getTransactionAmount() {
        return transactionAmount;
    }
    
    /**
     * Get the category name related to this budget error
     * @return category name or null if not set
     */
    public String getCategoryName() {
        return categoryName;
    }
    
    /**
     * Get formatted error message with details
     * @return detailed error message
     */
    @Override
    public String getMessage() {
        if (categoryName != null && budgetLimit != null && transactionAmount != null) {
            return String.format("Vượt ngân sách '%s': Giới hạn %,dđ, Cố gắng chi %,dđ", 
                categoryName, budgetLimit.intValue(), transactionAmount.intValue());
        }
        return super.getMessage();
    }
}