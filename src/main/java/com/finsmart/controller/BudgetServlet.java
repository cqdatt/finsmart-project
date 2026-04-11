package com.finsmart.controller;

import com.finsmart.dto.BudgetCreateRequest;
import com.finsmart.dto.BudgetUpdateRequest;
import com.finsmart.model.Budget;
import com.finsmart.model.Category;
import com.finsmart.service.IBudgetService;
import com.finsmart.service.ICategoryService;
import com.finsmart.service.impl.BudgetServiceImpl;
import com.finsmart.service.impl.CategoryServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.List;

@WebServlet("/budgets/*")
public class BudgetServlet extends HttpServlet {
    
    private final IBudgetService budgetService;
    private final ICategoryService categoryService;
    
    public BudgetServlet() {
        this.budgetService = new BudgetServiceImpl();
        this.categoryService = new CategoryServiceImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        
        int userId = 1;
        String action = request.getPathInfo();
        
        try {
            if (action == null || action.equals("/") || action.equals("/list")) {
                listBudgets(request, response, userId);
            } else if (action.equals("/create")) {
                showCreateForm(request, response, userId);
            } else if (action.equals("/edit")) {
                showEditForm(request, response, userId);
            } else if (action.equals("/delete")) {
                deleteBudget(request, response, userId);
            } else {
                response.sendRedirect(request.getContextPath() + "/budgets");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi: " + e.getMessage());
            listBudgets(request, response, userId);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        
        int userId = 1;
        String action = request.getPathInfo();
        
        try {
            if (action.equals("/create")) {
                createBudget(request, response, userId);
            } else if (action.equals("/edit")) {
                updateBudget(request, response, userId);
            } else if (action.equals("/delete")) {
                deleteBudget(request, response, userId);
            } else {
                response.sendRedirect(request.getContextPath() + "/budgets");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi: " + e.getMessage());
            listBudgets(request, response, userId);
        }
    }
    
    private int getCurrentMonth() {
        return YearMonth.now().getMonthValue();
    }
    
    private int getCurrentYear() {
        return YearMonth.now().getYear();
    }
    
    private void listBudgets(HttpServletRequest request, HttpServletResponse response, int userId) 
            throws ServletException, IOException {
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setHeader("Expires", "0");
        
        try {
         
            String monthParam = request.getParameter("month");
            String yearParam = request.getParameter("year");
            
            int month = (monthParam != null) ? Integer.parseInt(monthParam) : getCurrentMonth();
            int year = (yearParam != null) ? Integer.parseInt(yearParam) : getCurrentYear();
            
            System.out.println("=== listBudgets === userId=" + userId + ", period=" + month + "/" + year);
            
            // Force reload from database
            List<Budget> budgets = budgetService.getBudgetsForPeriod(userId, month, year);
            System.out.println("✅ Found " + budgets.size() + " budgets");
                
            for (Budget b : budgets) {
                System.out.println("  - ID=" + b.getId() + ", Category=" + b.getCategoryName() + 
                                 ", Amount=" + b.getAmountLimit() + ", Spent=" + b.getSpentAmount());
            }
            
            // Sort
            budgets.sort((b1, b2) -> {
                if (b1.getCategoryId() == null && b2.getCategoryId() != null) return -1;
                if (b1.getCategoryId() != null && b2.getCategoryId() == null) return 1;
                String n1 = b1.getCategoryName() != null ? b1.getCategoryName() : "";
                String n2 = b2.getCategoryName() != null ? b2.getCategoryName() : "";
                int cmp = n1.compareToIgnoreCase(n2);
                return cmp != 0 ? cmp : Integer.compare(b1.getId(), b2.getId());
            });
            
            List<Category> expenseCategories = categoryService.getCategoriesForUser(userId, "EXPENSE");
            
            // ✅ SET ATTRIBUTES
            request.setAttribute("budgets", budgets);
            request.setAttribute("categories", expenseCategories);
            request.setAttribute("currentMonth", month);
            request.setAttribute("currentYear", year);
            
            // ✅ FORWARD ĐỂ GIỮ DATA
            request.getRequestDispatcher("/WEB-INF/views/budgets/list.jsp").forward(request, response);
            
        } catch (SQLException | ClassNotFoundException e) {
            throw new ServletException("Database error: " + e.getMessage(), e);
        }
    }
    
    private void showCreateForm(HttpServletRequest request, HttpServletResponse response, int userId) 
            throws ServletException, IOException {
        
        try {
            List<Category> categories = categoryService.getCategoriesForUser(userId, "EXPENSE");
            request.setAttribute("categories", categories);
            request.setAttribute("month", getCurrentMonth());
            request.setAttribute("year", getCurrentYear());
            request.setAttribute("isEdit", false);
            request.getRequestDispatcher("/WEB-INF/views/budgets/form.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException("Error: " + e.getMessage(), e);
        }
    }
    
    private void showEditForm(HttpServletRequest request, HttpServletResponse response, int userId) 
            throws ServletException, IOException {
        
        try {
            String idParam = request.getParameter("id");
            if (idParam == null) {
                response.sendRedirect(request.getContextPath() + "/budgets");
                return;
            }
            
            int id = Integer.parseInt(idParam);
            Budget budget = budgetService.getById(id);
            
            if (budget == null || budget.getUserId() != userId) {
                request.setAttribute("error", "Budget not found");
                response.sendRedirect(request.getContextPath() + "/budgets");
                return;
            }
            
            List<Category> categories = categoryService.getCategoriesForUser(userId, "EXPENSE");
            request.setAttribute("budget", budget);
            request.setAttribute("categories", categories);
            request.setAttribute("isEdit", true);
            request.getRequestDispatcher("/WEB-INF/views/budgets/form.jsp").forward(request, response);
            
        } catch (Exception e) {
            throw new ServletException("Error: " + e.getMessage(), e);
        }
    }
    
    private void createBudget(HttpServletRequest request, HttpServletResponse response, int userId) 
            throws ServletException, IOException {
        
        try {
            Integer categoryId = null;
            String catParam = request.getParameter("category_id");
            if (catParam != null && !catParam.isEmpty()) {
                categoryId = Integer.parseInt(catParam);
            }
            
            int month = Integer.parseInt(request.getParameter("month"));
            int year = Integer.parseInt(request.getParameter("year"));
            
            // ✅ PARSE AMOUNT ĐÚNG
            String amountStr = request.getParameter("amount_limit").trim();
            String cleanAmount = amountStr.replaceAll("[.,\\s]", "");
            BigDecimal amountLimit = new BigDecimal(cleanAmount);
            
            System.out.println("=== Create Budget ===");
            System.out.println("userId=" + userId + ", catId=" + categoryId + ", period=" + month + "/" + year);
            System.out.println("Amount: " + amountLimit);
            
            // ✅ CHECK DUPLICATE
            Budget existing = budgetService.getBudgetByCategoryAndPeriod(userId, categoryId, month, year);
            if (existing != null) {
                String catName = categoryId == null ? "Tổng thể" : 
                    (categoryService.getById(categoryId) != null ? categoryService.getById(categoryId).getName() : "Unknown");
                throw new Exception("Ngân sách cho <strong>" + catName + "</strong> tháng " + month + "/" + year + " đã tồn tại!");
            }
            
            // ✅ CREATE
            BudgetCreateRequest dto = new BudgetCreateRequest();
            dto.setCategoryId(categoryId);
            dto.setMonth(month);
            dto.setYear(year);
            dto.setAmountLimit(amountLimit);
            
            Budget created = budgetService.createBudget(userId, dto);
            System.out.println("✅ Created budget ID: " + created.getId());
            
            // ✅ VERIFY
            Thread.sleep(500); // Đợi DB commit
            Budget verify = budgetService.getBudgetByCategoryAndPeriod(userId, categoryId, month, year);
            System.out.println("Verify: " + (verify != null ? "SUCCESS - Found ID=" + verify.getId() : "FAILED"));
            
            // ✅ REDIRECT VỚI MONTH/YEAR
            response.sendRedirect(request.getContextPath() + "/budgets?month=" + month + "&year=" + year);
            
        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = e.getMessage() != null ? e.getMessage() : "Lỗi không xác định";
            request.setAttribute("error", "⚠️ " + errorMsg);
            listBudgets(request, response, userId);
        }
    }
    
    private void updateBudget(HttpServletRequest request, HttpServletResponse response, int userId) 
            throws ServletException, IOException {
        
        try {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.isEmpty()) {
                throw new Exception("Missing budget ID");
            }
            
            int id = Integer.parseInt(idParam);
            
            // ✅ PARSE CATEGORY
            Integer categoryId = null;
            String catParam = request.getParameter("category_id");
            if (catParam != null && !catParam.isEmpty()) {
                categoryId = Integer.parseInt(catParam);
            }
            
            // Parse amount
            String amountStr = request.getParameter("amount_limit");
            if (amountStr == null || amountStr.trim().isEmpty()) {
                throw new Exception("Amount is required");
            }
            
            String cleanAmount = amountStr.trim().replaceAll("[.,\\s]", "");
            BigDecimal amountLimit = new BigDecimal(cleanAmount);
            
            System.out.println("=== Update Budget ===");
            System.out.println("ID: " + id);
            System.out.println("Category: " + categoryId);
            System.out.println("Amount: " + amountLimit);
            
            // Check budget exists
            Budget existing = budgetService.getById(id);
            if (existing == null) {
                throw new Exception("Budget not found");
            }
            
            if (existing.getUserId() != userId) {
                throw new Exception("Unauthorized");
            }
            
            // ✅ CHECK IF CATEGORY CHANGED
            if (categoryId != null && !java.util.Objects.equals(existing.getCategoryId(), categoryId)) {
                System.out.println("Category changed from " + existing.getCategoryId() + " to " + categoryId);
                
                // Check if budget already exists for new category in same period
                Budget duplicate = budgetService.getBudgetByCategoryAndPeriod(
                    userId, categoryId, existing.getMonth(), existing.getYear());
                
                if (duplicate != null) {
                    throw new Exception("Ngân sách cho danh mục này đã tồn tại trong kỳ!");
                }
            }
            
            // ✅ UPDATE WITH NEW CATEGORY AND AMOUNT
            BudgetUpdateRequest dto = new BudgetUpdateRequest();
            dto.setCategoryId(categoryId);
            dto.setAmountLimit(amountLimit);
            
            budgetService.updateBudget(id, dto);
            System.out.println("✅ Budget updated successfully");
            
            // Redirect
            Budget updated = budgetService.getById(id);
            response.sendRedirect(request.getContextPath() + "/budgets?month=" + updated.getMonth() + "&year=" + updated.getYear());
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi: " + (e.getMessage() != null ? e.getMessage() : "Không xác định"));
            showEditForm(request, response, userId);
        }
    }
    
    private void deleteBudget(HttpServletRequest request, HttpServletResponse response, int userId) 
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
        String monthParam = request.getParameter("month");
        String yearParam = request.getParameter("year");
        
        System.out.println("=== Delete Budget === id=" + idParam);
        
        try {
            if (idParam != null && !idParam.isEmpty()) {
                int id = Integer.parseInt(idParam);
                Budget budget = budgetService.getById(id);
                
                if (budget != null && budget.getUserId() == userId) {
                    boolean deleted = budgetService.delete(id);
                    System.out.println("Delete result: " + deleted);
                    
                    if (deleted) {
                        Thread.sleep(500); // Đợi DB commit
                        Budget verify = budgetService.getById(id);
                        System.out.println("Verify: " + (verify == null ? "SUCCESS" : "FAILED"));
                        
                        int month = (monthParam != null) ? Integer.parseInt(monthParam) : getCurrentMonth();
                        int year = (yearParam != null) ? Integer.parseInt(yearParam) : getCurrentYear();
                        response.sendRedirect(request.getContextPath() + "/budgets?month=" + month + "&year=" + year);
                        return;
                    }
                }
                request.setAttribute("error", "Không thể xóa ngân sách!");
            }
            listBudgets(request, response, userId);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error: " + e.getMessage());
            listBudgets(request, response, userId);
        }
    }
}