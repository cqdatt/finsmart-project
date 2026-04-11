package com.finsmart.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

// ✅ ANNOTATION PHẢI ĐÚNG
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("=== LogoutServlet.doGet ===");
        
        HttpSession session = request.getSession(false);
        if (session != null) {
            System.out.println("Invalidating session...");
            session.invalidate();
        }
        
        System.out.println("Redirecting to /auth/login");
        response.sendRedirect(request.getContextPath() + "/auth/login");
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}