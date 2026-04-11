package com.finsmart.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionUpdateRequest {
    private Integer categoryId;
    private BigDecimal amount;
    private LocalDate transactionDate;
    private String description;
    private String type;  // ✅ THÊM FIELD NÀY
    
    public TransactionUpdateRequest() {}
    
    // Getters & Setters
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public LocalDate getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDate transactionDate) { this.transactionDate = transactionDate; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    //  Thêm getter/setter cho type
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}