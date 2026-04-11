<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Danh mục - FinSmart</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
</head>
<body>
    <%@ include file="/WEB-INF/views/layouts/header.jsp" %>
    
    <div class="container-fluid">
        <div class="row">
            <%@ include file="/WEB-INF/views/layouts/sidebar.jsp" %>
            
            <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4 py-4">
                <!-- ✅ UNIFORM HEADER WITH BUTTON -->
                <div class="page-header-with-action">
                    <div>
                        <h1 class="h3 mb-0">
                            <i class="fas fa-folder me-2"></i>Quản lý Danh mục
                        </h1>
                        <p class="text-muted mb-0 mt-1">
                            <i class="fas fa-tags"></i> Phân loại thu chi
                        </p>
                    </div>
                    
                    <!-- ✅ UNIFORM BUTTON -->
                    <a href="${pageContext.request.contextPath}/categories/create" 
                       class="btn btn-add-uniform">
                        <i class="fas fa-plus-circle"></i>
                        <span>Thêm danh mục</span>
                    </a>
                </div>
                
                <!-- Tabs -->
                <ul class="nav nav-tabs mb-4">
                    <li class="nav-item">
                        <a class="nav-link ${currentType == 'INCOME' ? 'active' : ''}" 
                           href="${pageContext.request.contextPath}/categories?type=INCOME">
                            <i class="fas fa-arrow-up"></i> Thu nhập (${incomeCount})
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link ${currentType == 'EXPENSE' ? 'active' : ''}" 
                           href="${pageContext.request.contextPath}/categories?type=EXPENSE">
                            <i class="fas fa-arrow-down"></i> Chi tiêu (${expenseCount})
                        </a>
                    </li>
                </ul>
                
                <!-- Error Message -->
                <c:if test="${not empty error}">
                    <div class="alert alert-danger alert-dismissible fade show">
                        <i class="fas fa-exclamation-circle me-2"></i>${error}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>
                
                <!-- Success Message -->
                <c:if test="${not empty success}">
                    <div class="alert alert-success alert-dismissible fade show">
                        <i class="fas fa-check-circle me-2"></i>${success}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>
                
                <!-- Categories Grid -->
                <c:choose>
                    <c:when test="${empty categories}">
                        <div class="text-center py-5">
                            <i class="fas fa-folder-open fa-3x text-muted mb-3"></i>
                            <p class="text-muted">Chưa có danh mục nào.</p>
                            <a href="${pageContext.request.contextPath}/categories/create" class="btn btn-primary">
                                <i class="fas fa-plus"></i> Tạo danh mục đầu tiên
                            </a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="row">
                            <c:forEach var="cat" items="${categories}">
                                <div class="col-md-3 mb-3">
                                    <div class="card h-100 text-center">
                                        <div class="card-body">
                                            <i class="${cat.icon} fa-3x mb-3 ${cat.type == 'INCOME' ? 'text-success' : 'text-danger'}"></i>
                                            <h5 class="card-title">${cat.name}</h5>
                                            <p class="text-muted small">
                                                ${cat.userId != null ? '• Cá nhân' : '• Hệ thống'}
                                            </p>
                                            
                                            <c:choose>
                                                <c:when test="${cat.userId != null}">
                                                    <div class="btn-group w-100">
                                                        <a href="${pageContext.request.contextPath}/categories/edit?id=${cat.id}" 
                                                           class="btn btn-outline-primary btn-sm">
                                                            <i class="fas fa-edit"></i> Sửa
                                                        </a>
                                                        <button type="button" 
                                                                class="btn btn-outline-danger btn-sm"
                                                                onclick="if(confirm('Xóa danh mục ${cat.name}?')) { window.location='${pageContext.request.contextPath}/categories/delete?id=${cat.id}&type=${currentType}'; }">
                                                            <i class="fas fa-trash"></i> Xóa
                                                        </button>
                                                    </div>
                                                </c:when>
                                                <c:otherwise>
                                                    <button class="btn btn-outline-success btn-sm w-100" 
                                                            onclick="window.location='${pageContext.request.contextPath}/categories/create?name=${cat.name}&icon=${cat.icon}&type=${cat.type}'">
                                                        <i class="fas fa-copy"></i> Tạo bản sao
                                                    </button>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </main>
        </div>
    </div>
    
    <%@ include file="/WEB-INF/views/layouts/footer.jsp" %>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>