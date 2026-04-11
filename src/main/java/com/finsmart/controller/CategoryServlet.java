package com.finsmart.controller;

import com.finsmart.dto.CategoryCreateRequest;
import com.finsmart.dto.CategoryUpdateRequest;
import com.finsmart.model.Category;
import com.finsmart.service.ICategoryService;
import com.finsmart.service.impl.CategoryServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/categories/*")
public class CategoryServlet extends HttpServlet {
    
    private final ICategoryService categoryService;
    
    public CategoryServlet() {
        this.categoryService = new CategoryServiceImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getPathInfo();
        
        try {
            if (action == null || action.equals("/") || action.equals("/list")) {
                listCategories(request, response);
            } else if (action.equals("/create")) {
                showCreateForm(request, response);
            } else if (action.equals("/edit")) {
                showEditForm(request, response);
            } else if (action.equals("/delete")) {
                deleteCategory(request, response);
            } else if (action.equals("/clone")) {
                cloneCategory(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/categories");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error: " + e.getMessage());
            listCategories(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // ✅ QUAN TRỌNG: Set UTF-8 encoding cho request
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        
        String action = request.getPathInfo();
        
        try {
            if (action.equals("/create")) {
                createCategory(request, response);
            } else if (action.equals("/edit")) {
                updateCategory(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/categories");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error: " + e.getMessage());
            listCategories(request, response);
        }
    }
    
    // ✅ PRIVATE METHODS - CATCH Exception CHUNG
    private void listCategories(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            int userId = 1;  // HARDCODE
            
            String type = request.getParameter("type");
            if (type == null || type.isEmpty()) {
                type = "INCOME";
            }
            
            System.out.println("=== listCategories ===");
            System.out.println("userId: " + userId + ", type: " + type);
            
            List<Category> categories = categoryService.getCategoriesForUser(userId, type);
            int incomeCount = categoryService.getCategoryCount(userId, "INCOME");
            int expenseCount = categoryService.getCategoryCount(userId, "EXPENSE");
            
            System.out.println("Income: " + incomeCount + ", Expense: " + expenseCount);
            System.out.println("Showing: " + categories.size() + " categories");
            
            request.setAttribute("categories", categories);
            request.setAttribute("currentType", type);
            request.setAttribute("incomeCount", incomeCount);
            request.setAttribute("expenseCount", expenseCount);
            
            request.getRequestDispatcher("/WEB-INF/views/categories/list.jsp").forward(request, response);
            
        } catch (Exception e) {
            throw new ServletException("Error loading categories", e);
        }
    }
    
    private void showCreateForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String type = request.getParameter("type");
        if (type == null || type.isEmpty()) {
            type = "INCOME";
        }
        
        request.setAttribute("type", type);
        request.getRequestDispatcher("/WEB-INF/views/categories/form.jsp").forward(request, response);
    }
    
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            String idParam = request.getParameter("id");
            System.out.println("=== showEditForm ===");
            System.out.println("ID param: " + idParam);
            
            if (idParam == null || idParam.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/categories");
                return;
            }
            
            int id = Integer.parseInt(idParam);
            Category category = categoryService.getById(id);
            
            System.out.println("Category loaded: " + (category != null ? category.getName() : "null"));
            
            if (category == null) {
                request.setAttribute("error", "Danh mục không tồn tại");
                response.sendRedirect(request.getContextPath() + "/categories");
                return;
            }
            
            // ✅ QUAN TRỌNG: Set isEdit = true để JSP biết đang edit
            request.setAttribute("isEdit", true);
            request.setAttribute("category", category);
            request.setAttribute("type", category.getType());  // ✅ Cho breadcrumb
            
            System.out.println("Forwarding to form.jsp with isEdit=true, category=" + category.getName());
            
            // Forward to form.jsp
            request.getRequestDispatcher("/WEB-INF/views/categories/form.jsp")
                .forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi: " + e.getMessage());
            try {
                listCategories(request, response);
            } catch (Exception ex) {
                throw new ServletException(ex);
            }
        }
    }
    
    private void createCategory(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        int userId = 1;
        
        try {
            CategoryCreateRequest dto = new CategoryCreateRequest();
            dto.setName(request.getParameter("name"));
            dto.setType(request.getParameter("type"));
            dto.setIcon(request.getParameter("icon"));
            
            Category created = categoryService.createCategory(userId, dto);
            String redirectType = created != null ? created.getType() : "INCOME";
            
            response.sendRedirect(request.getContextPath() + "/categories?type=" + redirectType);
            
        } catch (Exception e) {
            request.setAttribute("error", "Lỗi: " + e.getMessage());
            request.setAttribute("name", request.getParameter("name"));
            request.setAttribute("type", request.getParameter("type"));
            request.setAttribute("icon", request.getParameter("icon"));
            showCreateForm(request, response);
        }
    }
    
    // ✅ CHỈ MỘT METHOD updateCategory
    private void updateCategory(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            String idParam = request.getParameter("id");
            System.out.println("=== updateCategory ===");
            System.out.println("ID: " + idParam);
            
            if (idParam == null || idParam.isEmpty()) {
                request.setAttribute("error", "Thiếu ID danh mục");
                showEditForm(request, response);
                return;
            }
            
            int id = Integer.parseInt(idParam);
            
            // ✅ LẤY CATEGORY CŨ ĐỂ LẤY TYPE
            Category existing = categoryService.getById(id);
            if (existing == null) {
                request.setAttribute("error", "Danh mục không tồn tại");
                showEditForm(request, response);
                return;
            }
            
            String originalType = existing.getType();  // ✅ Lưu type gốc
            System.out.println("Original type: " + originalType);
            
            // Create DTO
            CategoryUpdateRequest dto = new CategoryUpdateRequest();
            dto.setName(request.getParameter("name"));
            dto.setIcon(request.getParameter("icon"));
            
            System.out.println("Updating category " + id + ":");
            System.out.println("  Name: " + dto.getName());
            System.out.println("  Icon: " + dto.getIcon());
            
            // Update
            Category updated = categoryService.updateCategory(id, dto);
            
            System.out.println("Update result: " + (updated != null ? "SUCCESS" : "FAILED"));
            
            // ✅ REDIRECT VỀ ĐÚNG TAB
            response.sendRedirect(request.getContextPath() + "/categories?type=" + originalType);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi cập nhật: " + e.getMessage());
            showEditForm(request, response);
        }
    }
    
    private void deleteCategory(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
        String type = request.getParameter("type");
        
        try {
            if (idParam != null && !idParam.isEmpty()) {
                int id = Integer.parseInt(idParam);
                categoryService.delete(id);
            }
            
            if (type == null || type.isEmpty()) {
                type = "INCOME";
            }
            response.sendRedirect(request.getContextPath() + "/categories?type=" + type);
            
        } catch (Exception e) {
            request.setAttribute("error", "Lỗi: " + e.getMessage());
            listCategories(request, response);
        }
    }
    
    private void cloneCategory(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        int userId = 1;
        String idParam = request.getParameter("id");
        
        try {
            if (idParam != null && !idParam.isEmpty()) {
                int id = Integer.parseInt(idParam);
                Category original = categoryService.getById(id);
                
                if (original != null) {
                    CategoryCreateRequest dto = new CategoryCreateRequest();
                    dto.setName(original.getName() + " (Copy)");
                    dto.setType(original.getType());
                    dto.setIcon(original.getIcon());
                    
                    categoryService.createCategory(userId, dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        response.sendRedirect(request.getContextPath() + "/categories");
    }
}