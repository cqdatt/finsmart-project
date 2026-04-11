package com.finsmart.dao.impl;

import com.finsmart.dao.ITransactionDAO;
import com.finsmart.model.Transaction;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of ITransactionDAO using JDBC
 * Handles all transaction-related database operations
 */
public class TransactionDAOImpl extends BaseDAO implements ITransactionDAO {
    
    /**
     * Helper: Map ResultSet to Transaction with category info (from JOIN)
     */
    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setId(rs.getInt("id"));
        transaction.setUserId(rs.getInt("user_id"));
        transaction.setCategoryId(rs.getObject("category_id", Integer.class));
        transaction.setAmount(rs.getBigDecimal("amount"));
        transaction.setType(rs.getString("type"));
        transaction.setDescription(rs.getString("description"));
        transaction.setTransactionDate(rs.getObject("transaction_date", LocalDate.class));
        
        // ✅ FIX: Convert Timestamp to LocalDateTime properly
        Timestamp createdAtTs = rs.getTimestamp("created_at");
        if (createdAtTs != null) {
            transaction.setCreatedAt(createdAtTs.toLocalDateTime());
        }
        
        // Set category info if JOIN returned data
        String categoryName = rs.getString("category_name");
        if (categoryName != null) {
            transaction.setCategoryName(categoryName);
            transaction.setCategoryIcon(rs.getString("category_icon"));
            transaction.setCategoryType(rs.getString("category_type"));
        }
        
        return transaction;
    }
    
    /**
     * Helper: Map ResultSet to Transaction (simple, no category info)
     */
    private Transaction mapResultSetToTransactionSimple(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setId(rs.getInt("id"));
        transaction.setUserId(rs.getInt("user_id"));
        transaction.setCategoryId(rs.getObject("category_id", Integer.class));
        transaction.setAmount(rs.getBigDecimal("amount"));
        transaction.setType(rs.getString("type"));
        transaction.setDescription(rs.getString("description"));
        transaction.setTransactionDate(rs.getObject("transaction_date", LocalDate.class));
        
        // ✅ FIX: Convert Timestamp to LocalDateTime properly
        Timestamp createdAtTs = rs.getTimestamp("created_at");
        if (createdAtTs != null) {
            transaction.setCreatedAt(createdAtTs.toLocalDateTime());
        }
        
        return transaction;
    }
    
    // ========== CRUD METHODS ==========
    
    @Override
    public Transaction findById(Integer id) throws SQLException, ClassNotFoundException {
        String sql = "SELECT t.*, c.name as category_name, c.icon as category_icon, c.type as category_type " +
                    "FROM transactions t " +
                    "LEFT JOIN categories c ON t.category_id = c.id " +
                    "WHERE t.id = ?";
        
        System.out.println("=== TransactionDAO.findById ===");
        System.out.println("Looking for transaction ID: " + id);
        
        try (Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Transaction transaction = mapResultSetToTransaction(rs);
                    
                    System.out.println("Found transaction:");
                    System.out.println("  ID: " + transaction.getId());
                    System.out.println("  Category ID: " + transaction.getCategoryId());
                    System.out.println("  Category Name: " + transaction.getCategoryName());
                    System.out.println("  Category Icon: " + transaction.getCategoryIcon());
                    System.out.println("  Type: " + transaction.getType());
                    
                    return transaction;
                } else {
                    System.out.println("Transaction not found!");
                }
            }
        }
        return null;
    }
        
    @Override
    public List<Transaction> findAll() throws SQLException, ClassNotFoundException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY transaction_date DESC, created_at DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                transactions.add(mapResultSetToTransactionSimple(rs));
            }
        }
        return transactions;
    }
    
    @Override
    public boolean create(Transaction transaction) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO transactions (user_id, category_id, type, amount, transaction_date, description) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, transaction.getUserId());
            ps.setObject(2, transaction.getCategoryId());
            ps.setString(3, transaction.getType());
            ps.setBigDecimal(4, transaction.getAmount());
            ps.setObject(5, transaction.getTransactionDate());
            ps.setString(6, transaction.getDescription());
            
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        transaction.setId(keys.getInt(1));
                    }
                }
            }
            return rows > 0;
        }
    }
    
    @Override
    public boolean update(Transaction transaction) throws SQLException, ClassNotFoundException {
        // ✅ Update type field as well
        String sql = "UPDATE transactions SET category_id = ?, amount = ?, type = ?, " +
                    "transaction_date = ?, description = ? WHERE id = ?";
        
        try (Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setObject(1, transaction.getCategoryId());
            ps.setBigDecimal(2, transaction.getAmount());
            ps.setString(3, transaction.getType());  // ✅ Update type too
            ps.setObject(4, transaction.getTransactionDate());
            ps.setString(5, transaction.getDescription());
            ps.setInt(6, transaction.getId());
            
            return ps.executeUpdate() > 0;
        }
    }
    
    @Override
    public boolean delete(Integer id) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM transactions WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
    
    // ========== QUERY METHODS ==========
    
    @Override
    public List<Transaction> findByUserId(int userId) throws SQLException, ClassNotFoundException {
        List<Transaction> transactions = new ArrayList<>();
        
        // ✅ Include type in both transactions AND categories
        String sql = "SELECT t.*, c.name as category_name, c.icon as category_icon, c.type as category_type " +
                    "FROM transactions t " +
                    "LEFT JOIN categories c ON t.category_id = c.id " +
                    "WHERE t.user_id = ? " +
                    "ORDER BY t.transaction_date DESC, t.created_at DESC";
        
        System.out.println("=== findByUserId ===");
        System.out.println("Loading transactions for user: " + userId);
        
        try (Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Transaction t = mapResultSetToTransaction(rs);
                    System.out.println("Transaction ID: " + t.getId() + 
                                    ", Type: " + t.getType() + 
                                    ", Category: " + t.getCategoryName());
                    transactions.add(t);
                }
            }
        }
        
        System.out.println("Found " + transactions.size() + " transactions");
        return transactions;
    }
        
    @Override
    public List<Transaction> findByUserIdAndType(int userId, String type) 
            throws SQLException, ClassNotFoundException {
        
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.*, c.name as category_name, c.icon as category_icon, c.type as category_type " +
                     "FROM transactions t " +
                     "LEFT JOIN categories c ON t.category_id = c.id " +
                     "WHERE t.user_id = ? AND t.type = ? " +
                     "ORDER BY t.transaction_date DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setString(2, type);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        }
        return transactions;
    }
    
    @Override
    public List<Transaction> findByUserIdAndCategory(int userId, int categoryId) 
            throws SQLException, ClassNotFoundException {
        
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.*, c.name as category_name, c.icon as category_icon, c.type as category_type " +
                     "FROM transactions t " +
                     "LEFT JOIN categories c ON t.category_id = c.id " +
                     "WHERE t.user_id = ? AND t.category_id = ? " +
                     "ORDER BY t.transaction_date DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setInt(2, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        }
        return transactions;
    }
    
    @Override
    public List<Transaction> findByUserIdAndDateRange(int userId, LocalDate startDate, LocalDate endDate) 
            throws SQLException, ClassNotFoundException {
        
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.*, c.name as category_name, c.icon as category_icon, c.type as category_type " +
                     "FROM transactions t " +
                     "LEFT JOIN categories c ON t.category_id = c.id " +
                     "WHERE t.user_id = ? AND t.transaction_date BETWEEN ? AND ? " +
                     "ORDER BY t.transaction_date DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setObject(2, startDate);
            ps.setObject(3, endDate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        }
        return transactions;
    }
    
    @Override
    public List<Transaction> findByFilters(int userId, String type, Integer categoryId, 
                                            LocalDate startDate, LocalDate endDate, String keyword) 
            throws SQLException, ClassNotFoundException {
        
        System.out.println("=== findByFilters ===");
        System.out.println("userId: " + userId);
        System.out.println("type: " + type);
        System.out.println("startDate: " + startDate);
        System.out.println("endDate: " + endDate);
        
        List<Transaction> transactions = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT t.*, c.name as category_name, c.icon as category_icon, c.type as category_type " +
            "FROM transactions t " +
            "LEFT JOIN categories c ON t.category_id = c.id " +
            "WHERE t.user_id = ?"
        );
        List<Object> params = new ArrayList<>();
        params.add(userId);
        
        if (type != null && !type.isEmpty()) {
            sql.append(" AND t.type = ?");
            params.add(type);
        }
        if (categoryId != null) {
            sql.append(" AND t.category_id = ?");
            params.add(categoryId);
        }
        
        // ✅ DATE FILTERS
        if (startDate != null) {
            sql.append(" AND t.transaction_date >= ?");
            params.add(startDate);
            System.out.println("Added startDate filter: " + startDate);
        }
        if (endDate != null) {
            sql.append(" AND t.transaction_date <= ?");
            params.add(endDate);
            System.out.println("Added endDate filter: " + endDate);
        }
        
        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND t.description LIKE ?");
            params.add("%" + keyword + "%");
        }
        
        sql.append(" ORDER BY t.transaction_date DESC");
        
        System.out.println("SQL: " + sql.toString());
        System.out.println("Params: " + params);
        
        try (Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        }
        
        System.out.println("Found " + transactions.size() + " transactions");
        return transactions;
    }
    
    @Override
    public BigDecimal getTotalAmountByTypeAndDateRange(int userId, String type, 
                                                        LocalDate startDate, LocalDate endDate) 
            throws SQLException, ClassNotFoundException {
        
        String sql = "SELECT COALESCE(SUM(amount), 0) as total FROM transactions " +
                     "WHERE user_id = ? AND type = ? AND transaction_date BETWEEN ? AND ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setString(2, type);
            ps.setObject(3, startDate);
            ps.setObject(4, endDate);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal total = rs.getBigDecimal("total");
                    return total != null ? total : BigDecimal.ZERO;
                }
            }
        }
        return BigDecimal.ZERO;
    }
    
    @Override
    public BigDecimal getTotalAmountByCategoryAndDateRange(int userId, int categoryId,
                                                            LocalDate startDate, LocalDate endDate) 
            throws SQLException, ClassNotFoundException {
        
        String sql = "SELECT COALESCE(SUM(amount), 0) as total FROM transactions " +
                     "WHERE user_id = ? AND category_id = ? AND transaction_date BETWEEN ? AND ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setInt(2, categoryId);
            ps.setObject(3, startDate);
            ps.setObject(4, endDate);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal total = rs.getBigDecimal("total");
                    return total != null ? total : BigDecimal.ZERO;
                }
            }
        }
        return BigDecimal.ZERO;
    }
    
    @Override
    public Map<String, BigDecimal> getCategorySpendingSummary(int userId, int month, int year) 
            throws SQLException, ClassNotFoundException {
        
        Map<String, BigDecimal> summary = new HashMap<>();
        String sql = "SELECT c.name as category_name, COALESCE(SUM(t.amount), 0) as total " +
                     "FROM transactions t " +
                     "JOIN categories c ON t.category_id = c.id " +
                     "WHERE t.user_id = ? AND t.type = 'EXPENSE' " +
                     "AND MONTH(t.transaction_date) = ? AND YEAR(t.transaction_date) = ? " +
                     "GROUP BY c.id, c.name ORDER BY total DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setInt(2, month);
            ps.setInt(3, year);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    summary.put(rs.getString("category_name"), rs.getBigDecimal("total"));
                }
            }
        }
        return summary;
    }
    
    @Override
    public List<Map<String, Object>> getMonthlyComparison(int userId, int months) 
            throws SQLException, ClassNotFoundException {
        
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT YEAR(transaction_date) as year, MONTH(transaction_date) as month, " +
                     "SUM(CASE WHEN type = 'INCOME' THEN amount ELSE 0 END) as income, " +
                     "SUM(CASE WHEN type = 'EXPENSE' THEN amount ELSE 0 END) as expense " +
                     "FROM transactions WHERE user_id = ? " +
                     "AND transaction_date >= DATE_SUB(CURDATE(), INTERVAL ? MONTH) " +
                     "GROUP BY YEAR(transaction_date), MONTH(transaction_date) " +
                     "ORDER BY year DESC, month DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setInt(2, months);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("year", rs.getInt("year"));
                    row.put("month", rs.getInt("month"));
                    row.put("income", rs.getBigDecimal("income"));
                    row.put("expense", rs.getBigDecimal("expense"));
                    result.add(row);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<Transaction> findRecentWithCategory(int userId, int limit) 
            throws SQLException, ClassNotFoundException {
        
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.*, c.name as category_name, c.icon as category_icon, c.type as category_type " +
                     "FROM transactions t " +
                     "LEFT JOIN categories c ON t.category_id = c.id " +
                     "WHERE t.user_id = ? " +
                     "ORDER BY t.transaction_date DESC, t.created_at DESC " +
                     "LIMIT ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setInt(2, limit);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        }
        return transactions;
    }
    
    @Override
    public int deleteByUserId(int userId) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM transactions WHERE user_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            return ps.executeUpdate();
        }
    }
    @Override
    public int deleteByCategoryId(int categoryId) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM transactions WHERE category_id = ?";
        
        try (Connection conn = getConnection();  // ✅ Có vì extends BaseDAO
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, categoryId);
            return ps.executeUpdate();
        }
    }
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
}