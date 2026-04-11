package com.finsmart.filter;

import com.finsmart.model.User;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Authentication Filter - Kiểm tra user đã đăng nhập chưa
 * Chỉ áp dụng cho các path cần bảo mật (dashboard, transactions, etc.)
 */
public class AuthFilter implements Filter {
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("=== AuthFilter initialized ===");
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                         FilterChain chain) throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        
        String requestURI = req.getRequestURI();
        String contextPath = req.getContextPath();
        
        System.out.println("=== AuthFilter ===");
        System.out.println("Request URI: " + requestURI);
        
        // ✅ CHO PHÉP CÁC PATH KHÔNG CẦN AUTH
        // Login, register, logout, assets, error pages
        if (requestURI.contains("/auth/") || 
            requestURI.contains("/logout") ||
            requestURI.contains("/assets/") ||
            requestURI.contains("/error/") ||
            requestURI.endsWith(".css") ||
            requestURI.endsWith(".js") ||
            requestURI.endsWith(".png") ||
            requestURI.endsWith(".jpg") ||
            requestURI.endsWith(".ico")) {
            
            System.out.println("Skipping auth for: " + requestURI);
            chain.doFilter(request, response);
            return;
        }
        
        // ✅ CHECK SESSION
        HttpSession session = req.getSession(false);
        User user = null;
        
        if (session != null) {
            user = (User) session.getAttribute("currentUser");
        }
        
        System.out.println("User in session: " + (user != null ? user.getUsername() : "null"));
        
        // Nếu chưa login, redirect về login page
        if (user == null) {
            System.out.println("User not authenticated, redirecting to login...");
            
            // Save original URL to redirect back after login
            session = req.getSession(true);
            session.setAttribute("redirectAfterLogin", requestURI);
            
            res.sendRedirect(contextPath + "/auth/login");
            return;
        }
        
        // ✅ User đã login, cho phép tiếp tục
        System.out.println("User authenticated: " + user.getUsername());
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        System.out.println("=== AuthFilter destroyed ===");
    }
}