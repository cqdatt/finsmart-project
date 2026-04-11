package com.finsmart.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionCreateRequest {
    private int categoryId;
    private BigDecimal amount;
    private LocalDate transactionDate;
    private String description;
    private String type; // "INCOME" or "EXPENSE"
    
    public TransactionCreateRequest() {}
    
    // Getters & Setters
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public LocalDate getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDate transactionDate) { this.transactionDate = transactionDate; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}