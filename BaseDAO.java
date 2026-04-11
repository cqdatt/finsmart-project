

import com.finsmart.config.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Lớp cơ sở cho tất cả DAO
 * Cung cấp các method tiện ích cho JDBC
 */
public abstract class BaseDAO {
    
    /**
     * Lấy kết nối database
     */
    protected Connection getConnection() throws SQLException, ClassNotFoundException {
        return DatabaseConnection.getInstance().getConnection();
    }
    
    /**
     * Đóng resources an toàn
     */
    protected void closeResources(PreparedStatement ps, ResultSet rs, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.err.println("Lỗi khi đóng resources: " + e.getMessage());
        }
    }
    
    /**
     * Đóng resources (không có ResultSet)
     */
    protected void closeResources(PreparedStatement ps, Connection conn) {
        try {
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.err.println("Lỗi khi đóng resources: " + e.getMessage());
        }
    }
}