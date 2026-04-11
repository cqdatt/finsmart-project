package com.finsmart.dto;

import com.finsmart.model.Budget;
import java.math.BigDecimal;

public class BudgetStatus {
    private Budget budget;
    private BigDecimal spentAmount;
    private BigDecimal remainingAmount;
    private int percentageUsed;
    private String alertLevel;
    
    public BudgetStatus() {}
    
    public BudgetStatus(Budget budget, BigDecimal spentAmount, 
                       BigDecimal remainingAmount, int percentageUsed, String alertLevel) {
        this.budget = budget;
        this.spentAmount = spentAmount;
        this.remainingAmount = remainingAmount;
        this.percentageUsed = percentageUsed;
        this.alertLevel = alertLevel;
    }
    
    // Getters & Setters
    public Budget getBudget() { return budget; }
    public void setBudget(Budget budget) { this.budget = budget; }
    public BigDecimal getSpentAmount() { return spentAmount; }
    public void setSpentAmount(BigDecimal spentAmount) { this.spentAmount = spentAmount; }
    public BigDecimal getRemainingAmount() { return remainingAmount; }
    public void setRemainingAmount(BigDecimal remainingAmount) { this.remainingAmount = remainingAmount; }
    public int getPercentageUsed() { return percentageUsed; }
    public void setPercentageUsed(int percentageUsed) { this.percentageUsed = percentageUsed; }
    public String getAlertLevel() { return alertLevel; }
    public void setAlertLevel(String alertLevel) { this.alertLevel = alertLevel; }
}