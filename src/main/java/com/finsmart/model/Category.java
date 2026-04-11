package com.finsmart.model;

import java.math.BigDecimal;

/**
 * Category model for transaction categorization
 * Supports both system categories (userId = null) and user-defined categories
 */
public class Category {
    
    // ========== FIELDS ==========
    
    private int id;
    private Integer userId;           // null = system category, not null = user category
    private String name;
    private String type;              // "INCOME" or "EXPENSE"
    private String icon;              // Font Awesome icon class
    private BigDecimal budgetLimit;   // Optional monthly budget limit for this category
    
    // ========== CONSTRUCTORS ==========
    
    /**
     * Default constructor
     */
    public Category() {
    }
    
    /**
     * Constructor with essential fields
     */
    public Category(Integer userId, String name, String type, String icon) {
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.icon = icon;
    }
    
    /**
     * Full constructor with all fields
     */
    public Category(int id, Integer userId, String name, String type, 
                    String icon, BigDecimal budgetLimit) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.icon = icon;
        this.budgetLimit = budgetLimit;
    }
    
    // ========== GETTERS & SETTERS ==========
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public void setIcon(String icon) {
        this.icon = icon;
    }
    
    public BigDecimal getBudgetLimit() {
        return budgetLimit;
    }
    
    public void setBudgetLimit(BigDecimal budgetLimit) {
        this.budgetLimit = budgetLimit;
    }
    
    // ========== HELPER METHODS ==========
    
    /**
     * Check if this is a system category (available to all users)
     * @return true if system category, false if user-defined
     */
    public boolean isSystemCategory() {
        return userId == null;
    }
    
    /**
     * Check if this is an income category
     * @return true if type is INCOME
     */
    public boolean isIncome() {
        return "INCOME".equals(type);
    }
    
    /**
     * Check if this is an expense category
     * @return true if type is EXPENSE
     */
    public boolean isExpense() {
        return "EXPENSE".equals(type);
    }
    
    /**
     * Get formatted budget limit for display
     * @return formatted string like "2.000.000đ" or "Không giới hạn"
     */
    public String getFormattedBudgetLimit() {
        if (budgetLimit == null) {
            return "Không giới hạn";
        }
        return String.format("%,dđ", budgetLimit.longValue());
    }
    
    /**
     * Get icon with Font Awesome prefix if not present
     * @return icon class with proper prefix
     */
    public String getFullIcon() {
        if (icon == null || icon.trim().isEmpty()) {
            return "fas fa-tag";
        }
        return icon;
    }
    
    // ========== OVERRIDES ==========
    
    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", icon='" + icon + '\'' +
                ", budgetLimit=" + budgetLimit +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Category category = (Category) o;
        return id == category.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}