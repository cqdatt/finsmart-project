package com.finsmart.dao.impl;

import com.finsmart.dao.ICategoryDAO;
import com.finsmart.model.Category;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAOImpl extends BaseDAO implements ICategoryDAO {
    
    private Category mapResultSetToCategory(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setId(rs.getInt("id"));
        
        int userId = rs.getInt("user_id");
        category.setUserId(rs.wasNull() ? null : userId);
        
        category.setName(rs.getString("name"));
        category.setType(rs.getString("type"));
        category.setIcon(rs.getString("icon"));
        
        BigDecimal budgetLimit = rs.getBigDecimal("budget_limit");
        category.setBudgetLimit(budgetLimit);
        
        return category;
    }
    
    @Override
    public Category findById(Integer id) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM categories WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCategory(rs);
                }
            }
        }
        return null;
    }
    
    @Override
    public List<Category> findAll() throws SQLException, ClassNotFoundException {
        List<Category> categories = new ArrayList<>();
        
        // ✅ CHỈ LẤY SYSTEM CATEGORIES (user_id IS NULL) HOẶC CỦA USER HIỆN TẠI
        String sql = "SELECT * FROM categories WHERE user_id IS NULL OR user_id = 1 ORDER BY type, name";
        
        try (Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                categories.add(mapResultSetToCategory(rs));
            }
        }
        
        return categories;
    }

    @Override
    public List<Category> findByUserIdAndType(Integer userId, String type) 
            throws SQLException, ClassNotFoundException {
        
        List<Category> categories = new ArrayList<>();
        
        // ✅ CHỈ LẤY CATEGORIES CỦA USER NÀY HOẶC SYSTEM CATEGORIES
        String sql = "SELECT * FROM categories WHERE (user_id IS NULL OR user_id = ?) ORDER BY type, name";
        
        try (Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setObject(1, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    categories.add(mapResultSetToCategory(rs));
                }
            }
        }
        return categories;
    }

    @Override
    public boolean create(Category category) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO categories (user_id, name, type, icon, budget_limit) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            if (category.getUserId() != null) {
                ps.setInt(1, category.getUserId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setString(2, category.getName());
            ps.setString(3, category.getType());
            ps.setString(4, category.getIcon());
            ps.setBigDecimal(5, category.getBudgetLimit());
            
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        category.setId(keys.getInt(1));
                    }
                }
            }
            return rows > 0;
        }
    }
    
    @Override
    public boolean update(Category category) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE categories SET name = ?, icon = ?, budget_limit = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, category.getName());
            ps.setString(2, category.getIcon());
            ps.setBigDecimal(3, category.getBudgetLimit());
            ps.setInt(4, category.getId());
            
            return ps.executeUpdate() > 0;
        }
    }
    
    @Override
    public boolean delete(Integer id) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM categories WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
    
    @Override
    public List<Category> findByType(String type) throws SQLException, ClassNotFoundException {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories WHERE type = ? ORDER BY name";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, type);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    categories.add(mapResultSetToCategory(rs));
                }
            }
        }
        return categories;
    }
    
    @Override
    public List<Category> findSystemCategoriesByType(String type) 
            throws SQLException, ClassNotFoundException {
        
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories WHERE type = ? AND user_id IS NULL ORDER BY name";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, type);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    categories.add(mapResultSetToCategory(rs));
                }
            }
        }
        return categories;
    }
    
    @Override
    public List<Category> findUserCategoriesByType(Integer userId, String type) 
            throws SQLException, ClassNotFoundException {
        
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories WHERE type = ? AND user_id = ? ORDER BY name";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, type);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    categories.add(mapResultSetToCategory(rs));
                }
            }
        }
        return categories;
    }
    
    @Override
    public boolean existsByName(Integer userId, String name, String type) 
            throws SQLException, ClassNotFoundException {
        
        String sql;
        if (userId == null) {
            sql = "SELECT COUNT(*) FROM categories WHERE user_id IS NULL AND name = ? AND type = ?";
        } else {
            sql = "SELECT COUNT(*) FROM categories WHERE (user_id IS NULL OR user_id = ?) AND name = ? AND type = ?";
        }
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            if (userId != null) {
                ps.setInt(1, userId);
                ps.setString(2, name);
                ps.setString(3, type);
            } else {
                ps.setString(1, name);
                ps.setString(2, type);
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    @Override
    public boolean isUsedInTransactions(int categoryId) 
            throws SQLException, ClassNotFoundException {
        
        String sql = "SELECT COUNT(*) FROM transactions WHERE category_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    @Override
    public int countByUserIdAndType(Integer userId, String type) 
            throws SQLException, ClassNotFoundException {
        
        String sql = "SELECT COUNT(*) FROM categories WHERE type = ? AND (user_id IS NULL OR user_id = ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, type);
            ps.setObject(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    @Override
    public boolean hasTransactions(int categoryId) throws SQLException, ClassNotFoundException {
        String sql = "SELECT COUNT(*) FROM transactions WHERE category_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, categoryId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    // ✅ THÊM 2 METHOD MỚI CHO CASCADE UPDATE
    @Override
    public int updateCategoryId(int oldCategoryId, int newCategoryId) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE transactions SET category_id = ? WHERE category_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, newCategoryId);
            ps.setInt(2, oldCategoryId);
            return ps.executeUpdate();
        }
    }
    
    @Override
    public int deleteByCategoryId(int categoryId) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM transactions WHERE category_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, categoryId);
            return ps.executeUpdate();
        }
    }
}