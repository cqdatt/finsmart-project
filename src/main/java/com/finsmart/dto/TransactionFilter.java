package com.finsmart.dto;

import java.time.LocalDate;

public class TransactionFilter {
    private String type;
    private Integer categoryId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String keyword;
    private int page = 1;
    private int pageSize = 20;
    
    public TransactionFilter() {}
    
    // Getters & Setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
}