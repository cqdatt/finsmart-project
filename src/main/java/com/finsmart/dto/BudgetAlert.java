package com.finsmart.dto;

import com.finsmart.model.Budget;
import java.math.BigDecimal;

public class BudgetAlert {
    private Budget budget;
    private String alertType;
    private String message;
    private BigDecimal spentAmount;
    private int percentageUsed;
    
    public BudgetAlert() {}
    
    public BudgetAlert(Budget budget, String alertType, String message, 
                      BigDecimal spentAmount, int percentageUsed) {
        this.budget = budget;
        this.alertType = alertType;
        this.message = message;
        this.spentAmount = spentAmount;
        this.percentageUsed = percentageUsed;
    }
    
    // Getters & Setters
    public Budget getBudget() { return budget; }
    public void setBudget(Budget budget) { this.budget = budget; }
    public String getAlertType() { return alertType; }
    public void setAlertType(String alertType) { this.alertType = alertType; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public BigDecimal getSpentAmount() { return spentAmount; }
    public void setSpentAmount(BigDecimal spentAmount) { this.spentAmount = spentAmount; }
    public int getPercentageUsed() { return percentageUsed; }
    public void setPercentageUsed(int percentageUsed) { this.percentageUsed = percentageUsed; }
}