<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page pageEncoding="UTF-8" %>

<div class="col-md-3 col-lg-2 d-md-block sidebar collapse">
    <div class="position-sticky pt-3">
        <ul class="nav flex-column">
            <li class="nav-item">
                <a class="nav-link ${pageContext.request.requestURI.contains('/dashboard') ? 'active' : ''}" 
                   href="${pageContext.request.contextPath}/dashboard">
                    <i class="fas fa-home me-2"></i>Dashboard
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="${pageContext.request.contextPath}/transactions">
                    <i class="fas fa-exchange-alt me-2"></i>Giao dịch
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${pageContext.request.requestURI.contains('/categories') ? 'active' : ''}" 
                   href="${pageContext.request.contextPath}/categories">
                    <i class="fas fa-folder me-2"></i>Danh mục
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="${pageContext.request.contextPath}/budgets">
                    <i class="fas fa-bullseye me-2"></i>Ngân sách
                </a>
            </li>
        </ul>
    </div>
</div>