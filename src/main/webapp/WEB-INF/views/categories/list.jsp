<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Danh mục - FinSmart</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .category-card {
            border: 2px solid #e0e0e0;
            border-radius: 12px;
            padding: 20px;
            text-align: center;
            transition: all 0.3s;
            height: 100%;
        }
        .category-card:hover {
            border-color: #0d6efd;
            transform: translateY(-5px);
            box-shadow: 0 8px 16px rgba(0,0,0,0.1);
        }
        .category-icon { font-size: 48px; margin-bottom: 15px; }
        .category-name { font-weight: 600; font-size: 16px; margin-bottom: 5px; }
        .category-type { font-size: 12px; color: #6c757d; margin-bottom: 15px; }
        .income-icon { color: #198754; }
        .expense-icon { color: #dc3545; }
        
        /* ✅ UNIFORM BUTTON STYLE */
        .btn-add-uniform {
            background: linear-gradient(135deg, #0d6efd 0%, #0dcaf0 100%) !important;
            border: none !important;
            color: #ffffff !important;
            padding: 12px 28px !important;
            font-size: 15px !important;
            font-weight: 600 !important;
            border-radius: 10px !important;
            box-shadow: 0 4px 15px rgba(13, 110, 253, 0.35) !important;
            transition: all 0.3s ease !important;
            display: inline-flex !important;
            align-items: center !important;
            gap: 10px !important;
        }
        .btn-add-uniform:hover {
            transform: translateY(-3px) !important;
            box-shadow: 0 8px 25px rgba(13, 110, 253, 0.5) !important;
            color: #ffffff !important;
        }
    </style>
</head>
<body>
    <%@ include file="/WEB-INF/views/layouts/header.jsp" %>
    
    <!-- Page Header -->
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h1 class="h2 mb-1 fw-bold">
                <i class="fas fa-folder me-2"></i>Quản lý Danh mục
            </h1>
            <p class="text-muted mb-0" style="font-size: 1.1rem;">
                <i class="fas fa-tags me-2"></i>Phân loại thu chi
            </p>
        </div>
        <!-- ✅ UNIFORM BUTTON -->
        <a href="${pageContext.request.contextPath}/categories/create" class="btn btn-add-uniform">
            <i class="fas fa-plus me-1"></i>Thêm danh mục
        </a>
    </div>
    
    <!-- Messages -->
    <c:if test="${not empty success}">
        <div class="alert alert-success alert-dismissible fade show mb-3">
            <i class="fas fa-check-circle me-2"></i>${success}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    
    <c:if test="${not empty error}">
        <div class="alert alert-danger alert-dismissible fade show mb-3">
            <i class="fas fa-exclamation-circle me-2"></i>${error}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    
    <!-- Filter Tabs -->
    <div class="mb-4">
        <div class="btn-group">
            <a href="${pageContext.request.contextPath}/categories?type=INCOME" 
               class="btn btn-outline-primary ${param.type == 'INCOME' || empty param.type ? 'active' : ''}">
                <i class="fas fa-arrow-up me-1"></i>Thu nhập (${incomeCount})
            </a>
            <a href="${pageContext.request.contextPath}/categories?type=EXPENSE" 
               class="btn btn-outline-danger ${param.type == 'EXPENSE' ? 'active' : ''}">
                <i class="fas fa-arrow-down me-1"></i>Chi tiêu (${expenseCount})
            </a>
        </div>
    </div>
    
    <!-- Categories Grid -->
    <div class="row g-3">
        <c:choose>
            <c:when test="${empty categories}">
                <div class="col-12">
                    <div class="text-center text-muted py-5">
                        <i class="fas fa-folder-open fa-3x mb-3"></i>
                        <p>Chưa có danh mục nào</p>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <c:forEach var="cat" items="${categories}">
                    <c:if test="${cat.type == currentType}">
                        <div class="col-md-6 col-lg-3">
                            <div class="category-card">
                                <div class="category-icon ${cat.type == 'INCOME' ? 'income-icon' : 'expense-icon'}">
                                    <i class="${cat.icon != null ? cat.icon : 'fas fa-tag'}"></i>
                                </div>
                                <div class="category-name">${cat.name}</div>
                                <div class="category-type">
                                    <c:choose>
                                        <c:when test="${cat.userId == null}">
                                            <i class="fas fa-globe"></i> Hệ thống
                                        </c:when>
                                        <c:otherwise>
                                            <i class="fas fa-user"></i> Cá nhân
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <div class="btn-group w-100">
                                    <c:choose>
                                        <c:when test="${cat.userId != null}">
                                            <a href="${pageContext.request.contextPath}/categories/edit?id=${cat.id}" class="btn btn-outline-primary btn-sm">
                                                <i class="fas fa-edit"></i> Sửa
                                            </a>
                                            <button type="button" class="btn btn-outline-danger btn-sm" onclick="if(confirm('Xóa danh mục này?')) window.location='${pageContext.request.contextPath}/categories/delete?id=${cat.id}&type=${cat.type}'">
                                                <i class="fas fa-trash"></i> Xóa
                                            </button>
                                        </c:when>
                                        <c:otherwise>
                                            <button type="button" class="btn btn-outline-success btn-sm w-100" onclick="window.location='${pageContext.request.contextPath}/categories/clone?id=${cat.id}'">
                                                <i class="fas fa-copy"></i> Tạo bản sao
                                            </button>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </div>
                    </c:if>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
    
    <%@ include file="/WEB-INF/views/layouts/footer.jsp" %>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>