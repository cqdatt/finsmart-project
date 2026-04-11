package com.finsmart.dto;

import java.math.BigDecimal;

public class MonthlyComparison {
    private int year;
    private int month;
    private BigDecimal income;
    private BigDecimal expense;
    private BigDecimal balance;
    
    public MonthlyComparison() {}
    
    public MonthlyComparison(int year, int month, BigDecimal income, BigDecimal expense) {
        this.year = year;
        this.month = month;
        this.income = income;
        this.expense = expense;
        this.balance = income != null && expense != null ? income.subtract(expense) : null;
    }
    
    // Getters & Setters
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }
    public BigDecimal getIncome() { return income; }
    public void setIncome(BigDecimal income) { this.income = income; }
    public BigDecimal getExpense() { return expense; }
    public void setExpense(BigDecimal expense) { this.expense = expense; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
}