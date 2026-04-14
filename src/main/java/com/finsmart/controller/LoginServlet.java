package com.finsmart.controller;

import com.finsmart.model.User;
import com.finsmart.service.IUserService;
import com.finsmart.service.impl.UserServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

// ✅ ANNOTATION PHẢI ĐÚNG
@WebServlet("/auth/login")
public class LoginServlet extends HttpServlet {
    
    private final IUserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Show login page
        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            User user = userService.authenticate(username, password);

            if (user != null) {
                HttpSession session = request.getSession();
                session.setAttribute("currentUser", user);
                
                String displayName = user.getFullName() != null && !user.getFullName().isEmpty() 
                                    ? user.getFullName() 
                                    : user.getUsername();
                session.setAttribute("userName", displayName);
                session.setAttribute("displayName", displayName);

                // Get redirect URL if exists
                String redirectUrl = (String) session.getAttribute("redirectAfterLogin");
                if (redirectUrl != null) {
                    session.removeAttribute("redirectAfterLogin");
                    response.sendRedirect(redirectUrl);
                } else {
                    response.sendRedirect(request.getContextPath() + "/dashboard");
                }
            } else {
                request.setAttribute("error", "Sai tài khoản hoặc mật khẩu");
                request.setAttribute("username", username);
                request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp")
                       .forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi đăng nhập: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp")
                   .forward(request, response);
        }
    }
}