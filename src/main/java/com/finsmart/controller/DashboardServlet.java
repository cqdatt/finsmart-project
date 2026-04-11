package com.finsmart.controller;

import com.finsmart.dto.BudgetAlert;
import com.finsmart.dto.MonthlyComparison;
import com.finsmart.model.Transaction;
import com.finsmart.service.IBudgetService;
import com.finsmart.service.ITransactionService;
import com.finsmart.service.impl.BudgetServiceImpl;
import com.finsmart.service.impl.TransactionServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    
    private final ITransactionService transactionService;
    private final IBudgetService budgetService;
    
    public DashboardServlet() {
        this.transactionService = new TransactionServiceImpl();
        this.budgetService = new BudgetServiceImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // HARDCODE USER ID = 1
        int userId = 1;
        
        try {
            // ✅ FIX: Gọi đúng method signature (chỉ 2 params)
            loadDashboardData(request, userId);
            
            request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Failed to load dashboard: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(request, response);
        }
    }
    
    private void loadDashboardData(HttpServletRequest request, int userId) 
            throws SQLException, ClassNotFoundException {
        
        // Get current period
        YearMonth currentMonth = YearMonth.now();
        int month = currentMonth.getMonthValue();
        int year = currentMonth.getYear();
        
        // Or from request params
        String monthParam = request.getParameter("month");
        String yearParam = request.getParameter("year");
        if (monthParam != null && yearParam != null) {
            month = Integer.parseInt(monthParam);
            year = Integer.parseInt(yearParam);
        }
        
        YearMonth ym = YearMonth.of(year, month);
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate = ym.atEndOfMonth();
        
        System.out.println("=== Dashboard Data ===");
        System.out.println("Period: " + month + "/" + year);
        System.out.println("Date range: " + startDate + " to " + endDate);
        
        // ✅ CALCULATE TOTALS FOR CURRENT MONTH ONLY
        BigDecimal totalIncome = transactionService.calculateTotalIncome(userId, startDate, endDate);
        BigDecimal totalExpense = transactionService.calculateTotalExpense(userId, startDate, endDate);
        
        // Handle null
        if (totalIncome == null) totalIncome = BigDecimal.ZERO;
        if (totalExpense == null) totalExpense = BigDecimal.ZERO;
        
        BigDecimal balance = totalIncome.subtract(totalExpense);
        
        System.out.println("Total Income: " + totalIncome);
        System.out.println("Total Expense: " + totalExpense);
        System.out.println("Balance: " + balance);
        
        // Get recent transactions (keep as is - all time)
        List<Transaction> recentTransactions = transactionService.getRecentTransactions(userId, 5);
        if (recentTransactions == null) recentTransactions = new ArrayList<>();
        
        // ✅ CATEGORY BREAKDOWN FOR CURRENT MONTH ONLY
        Map<String, BigDecimal> categorySpending = transactionService.getCategorySpendingBreakdown(userId, month, year);
        if (categorySpending == null) categorySpending = new HashMap<>();
        
        // ✅ MONTHLY TREND (6 months - keep as is)
        List<MonthlyComparison> monthlyTrend = transactionService.getMonthlyTrend(userId, 6);
        if (monthlyTrend == null) monthlyTrend = new ArrayList<>();
        
        // Get budget alerts
        List<BudgetAlert> budgetAlerts = new ArrayList<>();
        try {
            budgetAlerts = budgetService.getBudgetAlerts(userId, month, year);
        } catch (Exception e) {
            System.out.println("Budget alerts error: " + e.getMessage());
        }
        if (budgetAlerts == null) budgetAlerts = new ArrayList<>();
        
        // Set attributes
        request.setAttribute("totalIncome", totalIncome);
        request.setAttribute("totalExpense", totalExpense);
        request.setAttribute("balance", balance);
        request.setAttribute("recentTransactions", recentTransactions);
        request.setAttribute("categorySpending", categorySpending);
        request.setAttribute("monthlyTrend", monthlyTrend);
        request.setAttribute("budgetAlerts", budgetAlerts);
        request.setAttribute("currentMonth", month);
        request.setAttribute("currentYear", year);
        request.setAttribute("currentPeriod", month + "/" + year);
    }
}