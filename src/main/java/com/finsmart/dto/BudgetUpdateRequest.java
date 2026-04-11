package com.finsmart.dto;

import java.math.BigDecimal;

public class BudgetUpdateRequest {
    private Integer categoryId;
    private BigDecimal amountLimit;
    
    public Integer getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }
    
    public BigDecimal getAmountLimit() {
        return amountLimit;
    }
    
    public void setAmountLimit(BigDecimal amountLimit) {
        this.amountLimit = amountLimit;
    }
}