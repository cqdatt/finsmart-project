package com.finsmart.service.impl;

import com.finsmart.dao.ICategoryDAO;
import com.finsmart.dao.impl.CategoryDAOImpl;
import com.finsmart.dto.CategoryCreateRequest;
import com.finsmart.dto.CategoryUpdateRequest;
import com.finsmart.model.Category;
import com.finsmart.service.IBudgetService;
import com.finsmart.service.ICategoryService;
import com.finsmart.service.ITransactionService;

import java.sql.SQLException;
import java.util.List;

public class CategoryServiceImpl implements ICategoryService {
    
    private final ICategoryDAO categoryDAO;
    // ✅ DÙNG SERVICE LAYER - KHÔNG DÙNG DAO TRỰC TIẾP
    private final ITransactionService transactionService;
    private final IBudgetService budgetService;
    
    public CategoryServiceImpl() {
        this.categoryDAO = new CategoryDAOImpl();
        this.transactionService = new TransactionServiceImpl();
        this.budgetService = new BudgetServiceImpl();
    }
    
    @Override
    public Category getById(Integer id) throws SQLException, ClassNotFoundException {
        return categoryDAO.findById(id);
    }
    
    @Override
    public List<Category> getAll() throws SQLException, ClassNotFoundException {
        return categoryDAO.findAll();
    }
    
    @Override
    public boolean delete(Integer id) throws SQLException, ClassNotFoundException, Exception {
        
        System.out.println("=== CategoryServiceImpl.delete ===");
        System.out.println("Deleting category ID: " + id);
        
        // ✅ STEP 1: TÌM CATEGORY DEFAULT ĐỂ GÁN VÀO GIAO DỊCH CŨ
        Category defaultCategory = null;
        List<Category> allCategories = getAll();
        for (Category cat : allCategories) {
            if (cat.getId() != id) {
                defaultCategory = cat;
                break;
            }
        }
        
        // ✅ STEP 2: UPDATE CÁC GIAO DỊCH DÙNG CATEGORY NÀY (DÙNG SERVICE)
        if (defaultCategory != null) {
            int transactionsUpdated = transactionService.updateCategoryId(id, defaultCategory.getId());
            System.out.println("Updated " + transactionsUpdated + " transactions to: " + defaultCategory.getName());
        } else {
            // Nếu không có category khác, xóa giao dịch
            int transactionsDeleted = transactionService.deleteByCategoryId(id);
            System.out.println("Deleted " + transactionsDeleted + " transactions");
        }
        
        // ✅ STEP 3: XÓA BUDGETS LIÊN QUAN (DÙNG SERVICE)
        int budgetsDeleted = budgetService.deleteByCategoryId(id);
        System.out.println("Deleted " + budgetsDeleted + " budgets");
        
        // ✅ STEP 4: XÓA CATEGORY
        boolean deleted = categoryDAO.delete(id);
        System.out.println("Category deleted: " + deleted);
        
        return deleted;
    }
    
    @Override
    public List<Category> getCategoriesForUser(Integer userId, String type) 
            throws SQLException, ClassNotFoundException {
        
        System.out.println("=== getCategoriesForUser ===");
        System.out.println("userId: " + userId + ", type: " + type);
        
        if (userId == null) {
            return categoryDAO.findSystemCategoriesByType(type);
        }
        
        List<Category> categories = categoryDAO.findByUserIdAndType(userId, type);
        
        System.out.println("Found " + categories.size() + " categories");
        for (Category cat : categories) {
            System.out.println("  - " + cat.getName() + " (type: " + cat.getType() + ")");
        }
        
        return categories;
    }
    
    @Override
    public List<Category> getSystemCategories(String type) 
            throws SQLException, ClassNotFoundException {
        return categoryDAO.findSystemCategoriesByType(type);
    }
    
    @Override
    public List<Category> getUserCategories(Integer userId, String type) 
            throws SQLException, ClassNotFoundException {
        
        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null for getUserCategories");
        }
        return categoryDAO.findUserCategoriesByType(userId, type);
    }
    
    @Override
    public Category createCategory(Integer userId, CategoryCreateRequest request) 
            throws SQLException, ClassNotFoundException, Exception {
        
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new Exception("Category name cannot be empty");
        }
        if (request.getType() == null || (!request.getType().equals("INCOME") && !request.getType().equals("EXPENSE"))) {
            throw new Exception("Category type must be INCOME or EXPENSE");
        }
        
        if (categoryDAO.existsByName(userId, request.getName(), request.getType())) {
            throw new Exception("Category name already exists for this type");
        }
        
        Category category = new Category();
        category.setUserId(userId);
        category.setName(request.getName());
        category.setType(request.getType());
        category.setIcon(request.getIcon());
        category.setBudgetLimit(request.getBudgetLimit());
        
        if (categoryDAO.create(category)) {
            return category;
        } else {
            throw new Exception("Failed to create category");
        }
    }
    
    @Override
    public Category updateCategory(Integer categoryId, CategoryUpdateRequest request) 
            throws SQLException, ClassNotFoundException, Exception {
        
        Category existing = categoryDAO.findById(categoryId);
        if (existing == null) {
            throw new Exception("Category not found");
        }
        
        if (request.getName() != null && !request.getName().trim().isEmpty() 
                && !request.getName().equals(existing.getName())) {
            
            if (categoryDAO.existsByName(existing.getUserId(), request.getName(), existing.getType())) {
                throw new Exception("Category name already exists for this type");
            }
            existing.setName(request.getName());
        }
        
        if (request.getIcon() != null) {
            existing.setIcon(request.getIcon());
        }
        
        if (request.getBudgetLimit() != null) {
            existing.setBudgetLimit(request.getBudgetLimit());
        }
        
        if (categoryDAO.update(existing)) {
            return existing;
        } else {
            throw new Exception("Failed to update category");
        }
    }
    
    @Override
    public boolean isCategoryNameAvailable(Integer userId, String name, String type) 
            throws SQLException, ClassNotFoundException {
        return !categoryDAO.existsByName(userId, name, type);
    }
    
    @Override
    public boolean canDeleteCategory(int categoryId) 
            throws SQLException, ClassNotFoundException {
        return !categoryDAO.isUsedInTransactions(categoryId);
    }
    
    @Override
    public int getCategoryCount(Integer userId, String type) 
            throws SQLException, ClassNotFoundException {
        return categoryDAO.countByUserIdAndType(userId, type);
    }
    
    @Override
    public boolean hasTransactions(int categoryId) throws SQLException, ClassNotFoundException {
        return categoryDAO.hasTransactions(categoryId);
    }
}