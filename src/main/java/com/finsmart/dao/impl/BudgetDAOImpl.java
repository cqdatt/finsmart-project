package com.finsmart.dao.impl;

import com.finsmart.dao.IBudgetDAO;
import com.finsmart.model.Budget;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BudgetDAOImpl extends BaseDAO implements IBudgetDAO {
    
    private Budget mapResultSetToBudget(ResultSet rs) throws SQLException {
        Budget budget = new Budget();
        budget.setId(rs.getInt("id"));
        budget.setUserId(rs.getInt("user_id"));
        
        // ✅ Handle nullable category_id
        int catId = rs.getInt("category_id");
        budget.setCategoryId(rs.wasNull() ? null : catId);
        
        budget.setAmountLimit(rs.getBigDecimal("amount_limit"));
        budget.setMonth(rs.getInt("month"));
        budget.setYear(rs.getInt("year"));
        
        try {
            budget.setCategoryName(rs.getString("category_name"));
        } catch (SQLException e) {
            budget.setCategoryName(null);
        }
        
        try {
            budget.setCreatedAt(rs.getTimestamp("created_at"));
        } catch (SQLException e) {
            budget.setCreatedAt(null);
        }
        
        return budget;
    }
    
    @Override
    public Budget findById(Integer id) throws SQLException, ClassNotFoundException {
        String sql = "SELECT b.*, c.name AS category_name FROM budgets b " +
                     "LEFT JOIN categories c ON b.category_id = c.id WHERE b.id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapResultSetToBudget(rs) : null;
            }
        }
    }
    
    @Override
    public List<Budget> findAll() throws SQLException, ClassNotFoundException {
        List<Budget> budgets = new ArrayList<>();
        String sql = "SELECT b.*, c.name AS category_name FROM budgets b " +
                     "LEFT JOIN categories c ON b.category_id = c.id ORDER BY b.year DESC, b.month DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) budgets.add(mapResultSetToBudget(rs));
        }
        return budgets;
    }
    
    @Override
public boolean create(Budget budget) throws SQLException, ClassNotFoundException {
    String sql = "INSERT INTO budgets (user_id, category_id, month, year, amount_limit) VALUES (?, ?, ?, ?, ?)";
    
    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        
        ps.setInt(1, budget.getUserId());
        if (budget.getCategoryId() != null) {
            ps.setInt(2, budget.getCategoryId());
        } else {
            ps.setNull(2, Types.INTEGER);
        }
        ps.setInt(3, budget.getMonth());
        ps.setInt(4, budget.getYear());
        ps.setBigDecimal(5, budget.getAmountLimit());
        
        int rows = ps.executeUpdate();
        System.out.println("BudgetDAOImpl.create: rows affected=" + rows);
        
        if (rows > 0) {
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    budget.setId(keys.getInt(1));
                    System.out.println("BudgetDAOImpl.create: generated ID=" + budget.getId());
                }
            }
        }
        
        // ✅ XÓA DÒNG NÀY - KHÔNG CẦN COMMIT KHI AUTO-COMMIT=TRUE
        // conn.commit();
        
        return rows > 0;
    }
}
    
    @Override
    public boolean update(Budget budget) throws SQLException, ClassNotFoundException {
        System.out.println("BudgetDAOImpl.update: id=" + budget.getId());
        
        // ✅ UPDATE BOTH CATEGORY AND AMOUNT
        String sql = "UPDATE budgets SET category_id = ?, amount_limit = ? WHERE id = ?";
        
        try (Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            
            if (budget.getCategoryId() != null) {
                ps.setInt(1, budget.getCategoryId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            
            ps.setBigDecimal(2, budget.getAmountLimit());
            ps.setInt(3, budget.getId());
            
            int rows = ps.executeUpdate();
            System.out.println("BudgetDAOImpl.update: rows affected=" + rows);
            
            return rows > 0;
        }
    }
    
    @Override
    public boolean delete(Integer id) throws SQLException, ClassNotFoundException {
        System.out.println("BudgetDAOImpl.delete: id=" + id);
        String sql = "DELETE FROM budgets WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            System.out.println("BudgetDAOImpl.delete rows affected: " + rows);
            return rows > 0;
        }
    }
    
    @Override
    public List<Budget> findByUserIdAndPeriod(int userId, int month, int year) 
            throws SQLException, ClassNotFoundException {
        
        List<Budget> budgets = new ArrayList<>();
        String sql = "SELECT b.*, c.name AS category_name, " +
                    "COALESCE(SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END), 0) AS spent_amount " +
                    "FROM budgets b " +
                    "LEFT JOIN categories c ON b.category_id = c.id " +
                    "LEFT JOIN transactions t ON (b.category_id = t.category_id OR (b.category_id IS NULL)) " +
                    "    AND t.type = 'EXPENSE' AND t.user_id = b.user_id " +
                    "    AND MONTH(t.transaction_date) = b.month AND YEAR(t.transaction_date) = b.year " +
                    "WHERE b.user_id = ? AND b.month = ? AND b.year = ? " +
                    "GROUP BY b.id, c.name ORDER BY b.category_id IS NULL DESC, c.name ASC";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, month);
            ps.setInt(3, year);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Budget b = mapResultSetToBudget(rs);
                    b.setSpentAmount(rs.getBigDecimal("spent_amount"));
                    budgets.add(b);
                }
            }
        }
        return budgets;
    }
    
    @Override
    public Budget findByUserAndCategoryAndPeriod(int userId, Integer categoryId, int month, int year) 
            throws SQLException, ClassNotFoundException {
        
        String sql;
        if (categoryId == null) {
            sql = "SELECT b.*, c.name AS category_name FROM budgets b " +
                  "LEFT JOIN categories c ON b.category_id = c.id " +
                  "WHERE b.user_id = ? AND b.category_id IS NULL AND b.month = ? AND b.year = ?";
        } else {
            sql = "SELECT b.*, c.name AS category_name FROM budgets b " +
                  "LEFT JOIN categories c ON b.category_id = c.id " +
                  "WHERE b.user_id = ? AND b.category_id = ? AND b.month = ? AND b.year = ?";
        }
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            if (categoryId == null) {
                ps.setInt(2, month);
                ps.setInt(3, year);
            } else {
                ps.setInt(2, categoryId);
                ps.setInt(3, month);
                ps.setInt(4, year);
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapResultSetToBudget(rs) : null;
            }
        }
    }
    
    @Override
    public List<Budget> findByUserId(int userId) throws SQLException, ClassNotFoundException {
        List<Budget> budgets = new ArrayList<>();
        String sql = "SELECT b.*, c.name AS category_name FROM budgets b " +
                     "LEFT JOIN categories c ON b.category_id = c.id WHERE b.user_id = ? ORDER BY b.year DESC, b.month DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) budgets.add(mapResultSetToBudget(rs));
            }
        }
        return budgets;
    }
    
    @Override
    public int deleteByCategoryId(int categoryId) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM budgets WHERE category_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            return ps.executeUpdate();
        }
    }
}