package com.finsmart.service.impl;

//  IMPORTS ĐẦY ĐỦ
import com.finsmart.dao.IUserDAO;
import com.finsmart.dao.impl.UserDAOImpl;
import com.finsmart.dto.UserRegisterRequest;  //  Từ package dto
import com.finsmart.dto.UserUpdateRequest;    // Từ package dto
import com.finsmart.model.User;
import com.finsmart.service.IUserService;      // Interface
import org.mindrot.jbcrypt.BCrypt;
import java.sql.SQLException;
import java.util.List;

/**
 * Implementation of IUserService
 */
public class UserServiceImpl implements IUserService {  //  Implements đúng interface
    
    private final IUserDAO userDAO;
    
    public UserServiceImpl() {
        this.userDAO = new UserDAOImpl();
    }
    
    @Override
    public User login(String username, String password) throws SQLException, ClassNotFoundException {
        User user = userDAO.findByUsername(username);

        if (user == null || !user.isActive()) {
            return null;
        }

        String storedPassword = user.getPassword();
        if (storedPassword == null || password == null) {
            return null;
        }

        boolean authenticated = false;

        // Hỗ trợ đúng chuẩn BCrypt cho tài khoản mới
        if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$") || storedPassword.startsWith("$2y$")) {
            authenticated = BCrypt.checkpw(password, storedPassword);
        } else {
            // Tương thích ngược cho dữ liệu cũ đang lưu plain text
            authenticated = password.equals(storedPassword);
            if (authenticated) {
                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
                userDAO.changePassword(user.getId(), hashedPassword);
                user.setPassword(hashedPassword);
            }
        }

        if (!authenticated) {
            return null;
        }

        userDAO.updateLastLogin(user.getId());
        user.setPassword(null);
        return user;
    }
    
    @Override
    public boolean register(User user) throws SQLException, ClassNotFoundException {
        if (userDAO.findByUsername(user.getUsername()) != null) {
            return false;
        }

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return false;
        }

        if (!(user.getPassword().startsWith("$2a$") || user.getPassword().startsWith("$2b$") || user.getPassword().startsWith("$2y$"))) {
            user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(12)));
        }

        return userDAO.create(user);
    }
    // ========== IService GENERIC METHODS ==========
    
    @Override
    public User getById(Integer id) throws SQLException, ClassNotFoundException {
        User user = userDAO.findById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }
    
    @Override
    public List<User> getAll() throws SQLException, ClassNotFoundException {
        return userDAO.findAll();
    }
    
    @Override
    public boolean delete(Integer id) throws SQLException, ClassNotFoundException, Exception {
        return userDAO.updateActiveStatus(id, false);
    }
    
    // ========== AUTHENTICATION ==========
    
    @Override
    public User authenticate(String username, String password) 
            throws SQLException, ClassNotFoundException, Exception {
        
        User user = userDAO.findByUsername(username);
        if (user == null) throw new Exception("Username does not exist");
        if (!user.isActive()) throw new Exception("Account is deactivated");
        if (!BCrypt.checkpw(password, user.getPassword())) throw new Exception("Invalid password");
        
        userDAO.updateLastLogin(user.getId());
        user.setPassword(null);
        return user;
    }
    
    @Override
    public boolean isUsernameAvailable(String username) 
            throws SQLException, ClassNotFoundException {
        return !userDAO.existsByUsername(username);
    }
    
    @Override
    public boolean isEmailAvailable(String email) 
            throws SQLException, ClassNotFoundException {
        return !userDAO.existsByEmail(email);
    }
    
    // ========== REGISTRATION - NHẬN DTO TỪ PACKAGE dto ==========
    
    @Override
    public User register(UserRegisterRequest request)  //  Parameter là DTO từ package dto
            throws SQLException, ClassNotFoundException, Exception {
        
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) 
            throw new Exception("Username cannot be empty");
        if (request.getPassword() == null || request.getPassword().length() < 6) 
            throw new Exception("Password must be at least 6 characters");
        if (request.getEmail() == null || !request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) 
            throw new Exception("Invalid email format");
        
        if (userDAO.existsByUsername(request.getUsername())) 
            throw new Exception("Username already exists");
        if (userDAO.existsByEmail(request.getEmail())) 
            throw new Exception("Email already exists");
        
        User user = new User(request.getUsername(), request.getEmail(), request.getFullName());
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt(12)));
        user.setAvatar(request.getAvatar() != null ? request.getAvatar() : "default-avatar.png");
        
        if (userDAO.create(user)) {
            user.setPassword(null);
            return user;
        } else {
            throw new Exception("Registration failed");
        }
    }
    
    // ========== PROFILE MANAGEMENT ==========
    
    @Override
    public User updateProfile(Integer userId, UserUpdateRequest request) 
            throws SQLException, ClassNotFoundException, Exception {
        
        User existingUser = userDAO.findById(userId);
        if (existingUser == null) throw new Exception("User not found");
        
        if (request.getEmail() != null && !request.getEmail().equals(existingUser.getEmail())) {
            if (userDAO.existsByEmail(request.getEmail())) 
                throw new Exception("Email already exists");
            existingUser.setEmail(request.getEmail());
        }
        if (request.getFullName() != null) existingUser.setFullName(request.getFullName());
        if (request.getAvatar() != null) existingUser.setAvatar(request.getAvatar());
        
        if (userDAO.update(existingUser)) {
            existingUser.setPassword(null);
            return existingUser;
        } else {
            throw new Exception("Profile update failed");
        }
    }
    
    @Override
    public boolean changePassword(Integer userId, String currentPassword, String newPassword) 
            throws SQLException, ClassNotFoundException, Exception {
        
        User user = userDAO.findById(userId);
        if (user == null) throw new Exception("User not found");
        if (!BCrypt.checkpw(currentPassword, user.getPassword())) 
            throw new Exception("Current password is incorrect");
        if (newPassword == null || newPassword.length() < 6) 
            throw new Exception("New password must be at least 6 characters");
        
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt(12));
        return userDAO.changePassword(userId, hashedPassword);
    }
    
    @Override
    public boolean deactivateAccount(Integer userId) 
            throws SQLException, ClassNotFoundException, Exception {
        return userDAO.updateActiveStatus(userId, false);
    }
    
    @Override
    public List<User> searchUsers(String keyword) 
            throws SQLException, ClassNotFoundException {
        return userDAO.searchByKeyword(keyword);
    }
}