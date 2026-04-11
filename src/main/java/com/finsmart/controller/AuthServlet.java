package com.finsmart.controller;  //  Package declaration phải khớp đường dẫn

//  IMPORTS - QUAN TRỌNG: DTO từ package dto, không phải nested class
import com.finsmart.dto.UserRegisterRequest;  //  Từ com.finsmart.dto
import com.finsmart.model.User;
import com.finsmart.service.IUserService;      //  Interface type
import com.finsmart.service.impl.UserServiceImpl; //  Implementation class
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/auth/*")
public class AuthServlet extends HttpServlet {
    
    //  Khai báo variable là interface type, khởi tạo bằng implementation
    private final IUserService userService;  //  Interface type
    
    public AuthServlet() {
        //  Khởi tạo implementation, gán vào interface variable (polymorphism)
        this.userService = new UserServiceImpl();  //  UserServiceImpl implements IUserService
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getPathInfo();
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }
        
        switch (action) {
            case "/login": showLoginPage(request, response); break;
            case "/register": showRegisterPage(request, response); break;
            case "/logout": logout(request, response); break;
            default: response.sendRedirect(request.getContextPath() + "/auth/login");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getPathInfo();
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }
        
        switch (action) {
            case "/login": login(request, response); break;
            case "/register": register(request, response); break;
            default: response.sendRedirect(request.getContextPath() + "/auth/login");
        }
    }
    
    private void showLoginPage(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
    }
    
    private void showRegisterPage(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
    }
    private void login(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        try {
            // Authenticate
            User user = userService.authenticate(username, password);
            
            //  TẠO SESSION MỚI (quan trọng!)
            HttpSession session = request.getSession(true);  // true = tạo mới nếu chưa có
            session.setAttribute("currentUser", user);       // ✅ Set user vào session
            session.setMaxInactiveInterval(30 * 60);         // ✅ 30 phút timeout

            // ✅ Force save session
            session.setAttribute("lastAccess", System.currentTimeMillis());

            // ✅ Debug log
            System.out.println("=== Login Success ===");
            System.out.println("Session ID: " + session.getId());
            System.out.println("Session created: " + session.getCreationTime());
            System.out.println("User: " + user.getUsername());
            System.out.println("User object class: " + user.getClass().getName());
            System.out.println("All attributes: " + session.getAttributeNames());
            
            // Redirect to dashboard
            response.sendRedirect(request.getContextPath() + "/dashboard");
            
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("username", username);
            showLoginPage(request, response);
        }
    } 
    
    private void register(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirm_password");
        String email = request.getParameter("email");
        String fullName = request.getParameter("full_name");
        
        try {
            if (!password.equals(confirmPassword)) {
                throw new Exception("Passwords do not match");
            }
            
            // TẠO DTO TỪ PACKAGE dto, KHÔNG PHẢI NESTED CLASS
            UserRegisterRequest registerRequest = new UserRegisterRequest();  // ✅ Import từ com.finsmart.dto
            registerRequest.setUsername(username);
            registerRequest.setPassword(password);
            registerRequest.setEmail(email);
            registerRequest.setFullName(fullName);
            registerRequest.setAvatar("default-avatar.png");
            
            // Gọi method register() nhận DTO
            User user = userService.register(registerRequest);
            
            HttpSession session = request.getSession();
            session.setAttribute("currentUser", user);
            
            response.sendRedirect(request.getContextPath() + "/dashboard");
            
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("username", username);
            request.setAttribute("email", email);
            request.setAttribute("fullName", fullName);
            showRegisterPage(request, response);
        }
    }
    
    private void logout(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("username")) {
                    cookie.setMaxAge(0);
                    cookie.setPath(request.getContextPath());
                    response.addCookie(cookie);
                }
            }
        }
        
        response.sendRedirect(request.getContextPath() + "/auth/login");
    }
}