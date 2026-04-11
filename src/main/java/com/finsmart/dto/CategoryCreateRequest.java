package com.finsmart.dto;

import java.math.BigDecimal;  // ✅ THÊM IMPORT NÀY

public class CategoryCreateRequest {
    private String name;
    private String type;
    private String icon;
    private BigDecimal budgetLimit;  // ✅ THÊM FIELD NÀY
    
    public CategoryCreateRequest() {}
    
    // Getters & Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    
    public BigDecimal getBudgetLimit() { return budgetLimit; }  // ✅ THÊM GETTER
    public void setBudgetLimit(BigDecimal budgetLimit) { this.budgetLimit = budgetLimit; }  // ✅ THÊM SETTER
}