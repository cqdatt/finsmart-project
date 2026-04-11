package com.finsmart.dto;

import java.math.BigDecimal;

public class BudgetCreateRequest {
    private Integer categoryId; // null for overall budget
    private int month;
    private int year;
    private BigDecimal amountLimit;
    
    public BudgetCreateRequest() {}
    
    // Getters & Setters
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    
    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }
    
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    
    public BigDecimal getAmountLimit() { return amountLimit; }
    public void setAmountLimit(BigDecimal amountLimit) { this.amountLimit = amountLimit; }
}