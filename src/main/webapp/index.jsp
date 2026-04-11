<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    session = request.getSession(false);
    if (session != null && session.getAttribute("currentUser") != null) {
        response.sendRedirect(request.getContextPath() + "/dashboard");
    } else {
        response.sendRedirect(request.getContextPath() + "/auth/login");
    }
%>