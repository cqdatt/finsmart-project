package com.finsmart.dto;

import java.math.BigDecimal;

public class CategoryUpdateRequest {
    private String name;
    private String icon;
    private BigDecimal budgetLimit; 
    
    public CategoryUpdateRequest() {}
    
    // Getters & Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    
    public BigDecimal getBudgetLimit() { return budgetLimit; }  
    public void setBudgetLimit(BigDecimal budgetLimit) { this.budgetLimit = budgetLimit; } 
}