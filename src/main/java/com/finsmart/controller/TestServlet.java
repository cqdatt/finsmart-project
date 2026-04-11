package com.finsmart.controller;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/test-session")
public class TestServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        HttpSession session = request.getSession(false);
        
        out.println("<html><body>");
        out.println("<h1>Session Test</h1>");
        out.println("<p>Session exists: " + (session != null) + "</p>");
        
        if (session != null) {
            out.println("<p>Session ID: " + session.getId() + "</p>");
            out.println("<p>Session created: " + session.getCreationTime() + "</p>");
            out.println("<p>User in session: " + session.getAttribute("currentUser") + "</p>");
            out.println("<p>All attributes: " + session.getAttributeNames() + "</p>");
        } else {
            out.println("<p>No session found!</p>");
        }
        
        out.println("</body></html>");
    }
}