package com.finsmart.controller;

import com.finsmart.dto.TransactionCreateRequest;
import com.finsmart.dto.TransactionFilter;
import com.finsmart.dto.TransactionUpdateRequest;
import com.finsmart.model.Category;
import com.finsmart.model.Transaction;
import com.finsmart.service.ICategoryService;
import com.finsmart.service.ITransactionService;
import com.finsmart.service.impl.CategoryServiceImpl;
import com.finsmart.service.impl.TransactionServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/transactions/*")
public class TransactionServlet extends HttpServlet {
    
    private final ITransactionService transactionService;
    private final ICategoryService categoryService;
    
    public TransactionServlet() {
        this.transactionService = new TransactionServiceImpl();
        this.categoryService = new CategoryServiceImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        
        // HARDCODE USER ID = 1 ĐỂ TEST
        int userId = 1;
        String action = request.getPathInfo();
        
        try {
            if (action == null || action.equals("/") || action.equals("/list")) {
                listTransactions(request, response, userId);
            } else if (action.equals("/create")) {
                showCreateForm(request, response, userId);
            } else if (action.equals("/edit")) {
                showEditForm(request, response, userId);
            } else if (action.equals("/delete")) {
                deleteTransaction(request, response, userId);
            } else {
                response.sendRedirect(request.getContextPath() + "/transactions");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi: " + e.getMessage());
            listTransactions(request, response, userId);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        
        // HARDCODE USER ID = 1 ĐỂ TEST
        int userId = 1;
        String action = request.getPathInfo();
        
        try {
            if (action.equals("/create")) {
                createTransaction(request, response, userId);
            } else if (action.equals("/edit")) {
                updateTransaction(request, response, userId);
            } else if (action.equals("/delete")) {
                deleteTransaction(request, response, userId);
            } else {
                response.sendRedirect(request.getContextPath() + "/transactions");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi: " + e.getMessage());
            listTransactions(request, response, userId);
        }
    }
    
    private void listTransactions(HttpServletRequest request, HttpServletResponse response, int userId) 
            throws ServletException, IOException {
        
        System.out.println("=== listTransactions ===");
        
        try {
            // ✅ GET FILTER PARAMETERS (đúng tên)
            String typeFilter = request.getParameter("type");
            String startDateStr = request.getParameter("startDate");
            String endDateStr = request.getParameter("endDate");
            
            System.out.println("Filters: type=" + typeFilter + ", startDate=" + startDateStr + ", endDate=" + endDateStr);
            
            LocalDate startDate = null;
            LocalDate endDate = null;
            
            // Parse dates
            if (startDateStr != null && !startDateStr.isEmpty()) {
                try {
                    startDate = LocalDate.parse(startDateStr);
                    System.out.println("Parsed startDate: " + startDate);
                } catch (Exception e) {
                    System.out.println("Invalid startDate format");
                }
            }
            
            if (endDateStr != null && !endDateStr.isEmpty()) {
                try {
                    endDate = LocalDate.parse(endDateStr);
                    System.out.println("Parsed endDate: " + endDate);
                } catch (Exception e) {
                    System.out.println("Invalid endDate format");
                }
            }
            
            // Get transactions based on filters
            List<Transaction> transactions;
            
            if ((typeFilter == null || typeFilter.isEmpty() || typeFilter.equals("all")) && 
                startDate == null && endDate == null) {
                // No filters - get all
                System.out.println("No filters - getting all transactions");
                transactions = transactionService.getTransactionsByUser(userId);
            } else {
                // Has filters - use TransactionFilter
                System.out.println("Applying filters...");
                TransactionFilter filter = new TransactionFilter();
                filter.setType(typeFilter != null && !typeFilter.isEmpty() && !typeFilter.equals("all") ? typeFilter : null);
                filter.setStartDate(startDate);
                filter.setEndDate(endDate);
                filter.setCategoryId(null);
                filter.setKeyword(null);
                
                transactions = transactionService.getTransactionsByFilters(userId, filter);
            }
            
            System.out.println("Found " + transactions.size() + " transactions");
            
            // Calculate totals from FILTERED transactions
            BigDecimal totalIncome = BigDecimal.ZERO;
            BigDecimal totalExpense = BigDecimal.ZERO;
            
            for (Transaction t : transactions) {
                System.out.println("  - " + t.getTransactionDate() + " | " + t.getType() + " | " + t.getAmount());
                if ("INCOME".equals(t.getType())) {
                    totalIncome = totalIncome.add(t.getAmount());
                } else if ("EXPENSE".equals(t.getType())) {
                    totalExpense = totalExpense.add(t.getAmount());
                }
            }
            
            BigDecimal balance = totalIncome.subtract(totalExpense);
            
            System.out.println("Total Income: " + totalIncome);
            System.out.println("Total Expense: " + totalExpense);
            System.out.println("Balance: " + balance);
            
            // Get categories for dropdown
            List<Category> categories = categoryService.getAll();
            
            // Set attributes
            request.setAttribute("transactions", transactions);
            request.setAttribute("totalIncome", totalIncome);
            request.setAttribute("totalExpense", totalExpense);
            request.setAttribute("balance", balance);
            request.setAttribute("currentType", typeFilter);
            request.setAttribute("categories", categories);
            
            request.getRequestDispatcher("/WEB-INF/views/transactions/list.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading transactions: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/transactions/list.jsp").forward(request, response);
        }
    }
        
    private void showCreateForm(HttpServletRequest request, HttpServletResponse response, int userId) 
            throws ServletException, IOException {
        
        try {
            List<Category> allCategories = categoryService.getAll();
            request.setAttribute("categories", allCategories);
            request.setAttribute("isEdit", false);
            request.getRequestDispatcher("/WEB-INF/views/transactions/form.jsp").forward(request, response);
            
        } catch (SQLException | ClassNotFoundException e) {
            throw new ServletException("Error loading form: " + e.getMessage(), e);
        }
    }
    
    private void showEditForm(HttpServletRequest request, HttpServletResponse response, int userId) 
            throws ServletException, IOException {
        
        try {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/transactions");
                return;
            }
            
            int id = Integer.parseInt(idParam);
            Transaction transaction = transactionService.getById(id);
            
            if (transaction == null || transaction.getUserId() != userId) {
                request.setAttribute("error", "Giao dịch không tồn tại");
                request.getRequestDispatcher("/WEB-INF/views/transactions/list.jsp").forward(request, response);
                return;
            }
            
            // ✅ LOAD CATEGORIES
            List<Category> allCategories = categoryService.getAll();
            
            // ✅ CHECK XEM CATEGORY CỦA GIAO DỊCH NÀY CÒN TỒN TẠI KHÔNG
            // ⚠️ Lưu ý: transaction.getCategoryId() trả về int (primitive), không phải Integer
            boolean categoryExists = false;
            int transactionCategoryId = transaction.getCategoryId();  // ✅ Lấy giá trị int
            
            // ✅ So sánh đúng cách với primitive int
            if (transactionCategoryId > 0) {  // ✅ Kiểm tra valid ID (> 0)
                for (Category cat : allCategories) {
                    if (cat.getId() == transactionCategoryId) {  // ✅ So sánh int với int
                        categoryExists = true;
                        break;
                    }
                }
            }
            
            // ✅ NẾU CATEGORY ĐÃ XÓA, SET WARNING
            if (!categoryExists && transactionCategoryId > 0) {
                request.setAttribute("categoryDeleted", true);
                request.setAttribute("warningMessage", 
                    "⚠️ Danh mục cũ của giao dịch này đã bị xóa. Vui lòng chọn danh mục mới!");
            }
            
            request.setAttribute("transaction", transaction);
            request.setAttribute("categories", allCategories);
            request.setAttribute("isEdit", true);
            request.getRequestDispatcher("/WEB-INF/views/transactions/form.jsp").forward(request, response);
            
        } catch (SQLException | ClassNotFoundException | NumberFormatException e) {
            throw new ServletException("Error: " + e.getMessage(), e);
        }
    }
    
    private void createTransaction(HttpServletRequest request, HttpServletResponse response, int userId) 
            throws ServletException, IOException {
        
        try {
            // Parse amount: remove dots/commas if any
            String amountStr = request.getParameter("amount");
            if (amountStr != null) {
                amountStr = amountStr.replace(".", "").replace(",", "");
            }
            BigDecimal amount = new BigDecimal(amountStr);
            
            Integer categoryId = null;
            String catParam = request.getParameter("category_id");
            if (catParam != null && !catParam.isEmpty()) {
                categoryId = Integer.parseInt(catParam);
            }
            
            String type = request.getParameter("type");
            String description = request.getParameter("description");
            String dateStr = request.getParameter("transaction_date");
            LocalDate transactionDate = LocalDate.parse(dateStr);
            
            // Create DTO
            TransactionCreateRequest dto = new TransactionCreateRequest();
            dto.setCategoryId(categoryId);
            dto.setAmount(amount);
            dto.setType(type);
            dto.setDescription(description);
            dto.setTransactionDate(transactionDate);
            
            transactionService.createTransaction(userId, dto);
            
            response.sendRedirect(request.getContextPath() + "/transactions");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi khi tạo giao dịch: " + e.getMessage());
            listTransactions(request, response, userId);
        }
    }
    
    private void updateTransaction(HttpServletRequest request, HttpServletResponse response, int userId) 
            throws ServletException, IOException {
        
        try {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/transactions");
                return;
            }
            
            int id = Integer.parseInt(idParam);
            
            // ✅ GET TYPE FROM REQUEST
            String type = request.getParameter("type");
            System.out.println("New type: " + type);
            
            // Parse amount
            String amountStr = request.getParameter("amount");
            if (amountStr != null) {
                amountStr = amountStr.replace(".", "").replace(",", "");
            }
            BigDecimal amount = new BigDecimal(amountStr);
            
            // Get category
            String catParam = request.getParameter("category_id");
            Integer categoryId = null;
            if (catParam != null && !catParam.isEmpty()) {
                categoryId = Integer.parseInt(catParam);
                
                // Validate category type matches new type
                Category category = categoryService.getById(categoryId);
                if (category != null && !category.getType().equals(type)) {
                    request.setAttribute("error", "Loại danh mục không khớp với loại giao dịch!");
                    listTransactions(request, response, userId);
                    return;
                }
            }
            
            // Create DTO
            TransactionUpdateRequest dto = new TransactionUpdateRequest();
            dto.setCategoryId(categoryId);
            dto.setAmount(amount);
            dto.setType(type);  // ✅ SET TYPE INTO DTO
            
            String description = request.getParameter("description");
            if (description != null) {
                dto.setDescription(description);
            }
            
            String dateStr = request.getParameter("transaction_date");
            if (dateStr != null && !dateStr.isEmpty()) {
                dto.setTransactionDate(LocalDate.parse(dateStr));
            }
            
            // Update
            transactionService.updateTransaction(id, dto);
            
            response.sendRedirect(request.getContextPath() + "/transactions");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi: " + e.getMessage());
            listTransactions(request, response, userId);
        }
    }
            
    private void deleteTransaction(HttpServletRequest request, HttpServletResponse response, int userId) 
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
        
        try {
            if (idParam != null && !idParam.isEmpty()) {
                int id = Integer.parseInt(idParam);
                
                // Check if transaction exists and belongs to user
                Transaction transaction = transactionService.getById(id);
                
                if (transaction != null) {
                    //  DELETE RELATED BUDGETS FIRST (if any)
                    // This prevents foreign key constraint errors
                    
                    // Delete the transaction
                    boolean deleted = transactionService.delete(id);
                    
                    if (deleted) {
                        System.out.println("✅ Deleted transaction ID: " + id);
                    } else {
                        System.out.println("❌ Failed to delete transaction ID: " + id);
                    }
                } else {
                    System.out.println("Transaction not found or unauthorized");
                }
            }
            
            // Redirect back to transactions list
            response.sendRedirect(request.getContextPath() + "/transactions");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi khi xóa giao dịch: " + e.getMessage());
            listTransactions(request, response, userId);
        }
    }
    }