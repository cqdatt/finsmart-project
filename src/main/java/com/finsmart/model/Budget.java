package com.finsmart.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.YearMonth;

/**
 * Entity class representing a Monthly Budget
 * Tracks spending limits for categories or overall budget
 */
public class Budget implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Private fields
    private int id;
    private int userId;
    private Integer categoryId; // NULL for overall budget
    private int month;
    private int year;
    private BigDecimal amountLimit;
    private Timestamp createdAt;
    
    // Extended fields (not stored in DB - for calculation and display)
    private String categoryName;
    private BigDecimal spentAmount; // Actual spent amount from transactions
    private BigDecimal remainingAmount; // amountLimit - spentAmount
    
    // Constructors
    public Budget() {}
    
    public Budget(int userId, Integer categoryId, int month, int year, BigDecimal amountLimit) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.month = month;
        this.year = year;
        this.amountLimit = amountLimit;
    }
    
    public Budget(int id, int userId, Integer categoryId, int month, int year, BigDecimal amountLimit) {
        this.id = id;
        this.userId = userId;
        this.categoryId = categoryId;
        this.month = month;
        this.year = year;
        this.amountLimit = amountLimit;
    }
    
    // Getters and Setters
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
    
    public Integer getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }
    
    public int getMonth() {
        return month;
    }
    
    public void setMonth(int month) {
        this.month = month;
    }
    
    public int getYear() {
        return year;
    }
    
    public void setYear(int year) {
        this.year = year;
    }
    
    public BigDecimal getAmountLimit() {
        return amountLimit;
    }
    
    public void setAmountLimit(BigDecimal amountLimit) {
        this.amountLimit = amountLimit;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    public BigDecimal getSpentAmount() {
        return spentAmount;
    }
    
    public void setSpentAmount(BigDecimal spentAmount) {
        this.spentAmount = spentAmount;
        calculateRemaining();
    }
    
    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }
    
    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }
    
    // Business Logic Methods
    /**
     * Check if this is an overall budget (not category-specific)
     */
    public boolean isOverallBudget() {
        return this.categoryId == null;
    }
    
    /**
     * Check if this is a category-specific budget
     */
    public boolean isCategoryBudget() {
        return this.categoryId != null;
    }
    
    /**
     * Calculate remaining amount
     */
    private void calculateRemaining() {
        if (amountLimit == null) {
            this.remainingAmount = null;
            return;
        }
        
        if (spentAmount == null) {
            this.remainingAmount = amountLimit;
            return;
        }
        
        this.remainingAmount = amountLimit.subtract(spentAmount);
    }
    
    /**
     * Check if budget is exceeded
     */
    public boolean isOverBudget() {
        if (spentAmount == null || amountLimit == null) {
            return false;
        }
        return spentAmount.compareTo(amountLimit) > 0;
    }
    
    /**
     * Check if budget is close to being exceeded (>= 80%)
     */
    public boolean isNearBudget() {
        int percentage = getPercentageUsed();
        return percentage >= 80 && percentage < 100;
    }
    
    /**
     * Get percentage of budget used
     * @return percentage from 0 to >100
     */
    public int getPercentageUsed() {
        if (spentAmount == null || amountLimit == null || 
            amountLimit.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }
        
        return spentAmount.multiply(new BigDecimal("100"))
                .divide(amountLimit, 0, RoundingMode.HALF_UP)
                .intValue();
    }
    
    /**
     * Get formatted budget limit
     */
    public String getFormattedLimit() {
        if (amountLimit == null) {
            return "0 ₫";
        }
        return String.format("%,d ₫", amountLimit.longValue());
    }
    
    /**
     * Get formatted spent amount
     */
    public String getFormattedSpent() {
        if (spentAmount == null) {
            return "0 ₫";
        }
        return String.format("%,d ₫", spentAmount.longValue());
    }
    
    /**
     * Get formatted remaining amount
     */
    public String getFormattedRemaining() {
        if (remainingAmount == null) {
            calculateRemaining();
        }
        if (remainingAmount == null) {
            return "0 ₫";
        }
        return String.format("%,d ₫", remainingAmount.longValue());
    }
    
    /**
     * Get month and year as string
     */
    public String getMonthYearString() {
        try {
            YearMonth yearMonth = YearMonth.of(year, month);
            return yearMonth.format(java.time.format.DateTimeFormatter.ofPattern("MM/yyyy"));
        } catch (Exception e) {
            return String.format("%02d/%d", month, year);
        }
    }
    
    /**
     * Get CSS class for budget status
     */
    public String getStatusCssClass() {
        if (isOverBudget()) {
            return "text-danger";
        } else if (isNearBudget()) {
            return "text-warning";
        }
        return "text-success";
    }
    
    /**
     * Get progress bar CSS class
     */
    public String getProgressBarCssClass() {
        if (isOverBudget()) {
            return "bg-danger";
        } else if (isNearBudget()) {
            return "bg-warning";
        }
        return "bg-success";
    }
    
    // Override toString
    @Override
    public String toString() {
        return "Budget{" +
                "id=" + id +
                ", month=" + month +
                ", year=" + year +
                ", amountLimit=" + amountLimit +
                ", categoryId=" + categoryId +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Budget budget = (Budget) o;
        return id == budget.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}