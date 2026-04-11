<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Giao dịch - FinSmart</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        /* ✅ STAT CARD - GIỐNG HỆT DASHBOARD */
        .stat-card { 
            border-radius: 12px; 
            box-shadow: 0 2px 10px rgba(0,0,0,0.08); 
            padding: 20px;
        }
        .stat-card.income { 
            background: linear-gradient(135deg, #198754, #20c997); 
            color: white; 
        }
        .stat-card.expense { 
            background: linear-gradient(135deg, #dc3545, #fd7e14); 
            color: white; 
        }
        .stat-card.balance { 
            background: linear-gradient(135deg, #0d6efd, #0dcaf0); 
            color: white; 
        }
        
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
        }
        
        /* Card styles */
        .card {
            border: none;
            box-shadow: 0 2px 8px rgba(0,0,0,0.05);
        }
        .card-header {
            background-color: white;
            border-bottom: 1px solid #e9ecef;
            font-weight: 600;
        }
    </style>
</head>
<body>
    <%@ include file="/WEB-INF/views/layouts/header.jsp" %>
    
    <!-- Page Header -->
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h1 class="h2 mb-1 fw-bold">
                <i class="fas fa-exchange-alt me-2"></i>Giao dịch
            </h1>
            <p class="text-muted mb-0" style="font-size: 1.1rem;">
                <i class="fas fa-wallet me-2"></i>Quản lý thu chi cá nhân
            </p>
        </div>
        <a href="${pageContext.request.contextPath}/transactions/create" class="btn btn-add-uniform">
            <i class="fas fa-plus-circle"></i>
            <span>Thêm giao dịch</span>
        </a>
    </div>
    
    <!-- Messages -->
    <c:if test="${not empty error}">
        <div class="alert alert-danger alert-dismissible fade show mb-3">
            <i class="fas fa-exclamation-circle me-2"></i>${error}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    
    <c:if test="${not empty success}">
        <div class="alert alert-success alert-dismissible fade show mb-3">
            <i class="fas fa-check-circle me-2"></i>${success}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    
    <!-- Summary Cards - ✅ GIỐNG HỆT DASHBOARD -->
    <div class="row g-3 mb-4">
        <!-- Tổng Thu -->
        <div class="col-md-4">
            <div class="stat-card income">
                <div class="d-flex justify-content-between">
                    <div>
                        <h6 class="mb-1" style="opacity: 0.9;">Tổng Thu</h6>
                        <h3 class="mb-0">
                            <c:choose>
                                <c:when test="${totalIncome != null}">
                                    <fmt:formatNumber value="${totalIncome}" type="number" groupingUsed="true"/>đ
                                </c:when>
                                <c:otherwise>0đ</c:otherwise>
                            </c:choose>
                        </h3>
                    </div>
                    <i class="fas fa-arrow-up fa-2x" style="opacity: 0.5;"></i>
                </div>
            </div>
        </div>
        
        <!-- Tổng Chi -->
        <div class="col-md-4">
            <div class="stat-card expense">
                <div class="d-flex justify-content-between">
                    <div>
                        <h6 class="mb-1" style="opacity: 0.9;">Tổng Chi</h6>
                        <h3 class="mb-0">
                            <c:choose>
                                <c:when test="${totalExpense != null}">
                                    <fmt:formatNumber value="${totalExpense}" type="number" groupingUsed="true"/>đ
                                </c:when>
                                <c:otherwise>0đ</c:otherwise>
                            </c:choose>
                        </h3>
                    </div>
                    <i class="fas fa-arrow-down fa-2x" style="opacity: 0.5;"></i>
                </div>
            </div>
        </div>
        
        <!-- Số Dư -->
        <div class="col-md-4">
            <div class="stat-card balance">
                <div class="d-flex justify-content-between">
                    <div>
                        <h6 class="mb-1" style="opacity: 0.9;">Số Dư</h6>
                        <h3 class="mb-0">
                            <c:choose>
                                <c:when test="${balance != null}">
                                    <fmt:formatNumber value="${balance}" type="number" groupingUsed="true"/>đ
                                </c:when>
                                <c:otherwise>0đ</c:otherwise>
                            </c:choose>
                        </h3>
                    </div>
                    <i class="fas fa-wallet fa-2x" style="opacity: 0.5;"></i>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Filter Section -->
    <div class="card mb-4">
        <div class="card-body">
            <form method="get" action="${pageContext.request.contextPath}/transactions" class="row g-3">
                <div class="col-md-3">
                    <label class="form-label"><i class="fas fa-calendar-alt me-1"></i>Từ ngày</label>
                    <input type="date" name="startDate" class="form-control" value="${param.startDate}">
                </div>
                <div class="col-md-3">
                    <label class="form-label"><i class="fas fa-calendar-alt me-1"></i>Đến ngày</label>
                    <input type="date" name="endDate" class="form-control" value="${param.endDate}">
                </div>
                <div class="col-md-3">
                    <label class="form-label"><i class="fas fa-filter me-1"></i>Loại</label>
                    <select name="type" class="form-select">
                        <option value="all" ${param.type == 'all' || empty param.type ? 'selected' : ''}>Tất cả</option>
                        <option value="INCOME" ${param.type == 'INCOME' ? 'selected' : ''}>Thu nhập</option>
                        <option value="EXPENSE" ${param.type == 'EXPENSE' ? 'selected' : ''}>Chi tiêu</option>
                    </select>
                </div>
                <div class="col-md-3 d-flex align-items-end">
                    <button type="submit" class="btn btn-primary w-100">
                        <i class="fas fa-search me-1"></i>Lọc
                    </button>
                </div>
            </form>
        </div>
    </div>
    
    <!-- Transactions Table -->
    <div class="card">
        <div class="card-header d-flex justify-content-between align-items-center">
            <span><i class="fas fa-list me-2"></i>Danh sách giao dịch</span>
            <span class="badge bg-primary">${transactions != null ? transactions.size() : 0} giao dịch</span>
        </div>
        <div class="table-responsive">
            <table class="table table-hover mb-0">
                <thead class="table-light">
                    <tr>
                        <th>Ngày</th>
                        <th>Danh mục</th>
                        <th>Mô tả</th>
                        <th class="text-end">Số tiền</th>
                        <th class="text-center">Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty transactions}">
                            <tr>
                                <td colspan="5" class="text-center py-5 text-muted">
                                    <i class="fas fa-receipt fa-3x mb-3"></i>
                                    <p>Chưa có giao dịch nào</p>
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="t" items="${transactions}">
                                <tr>
                                    <td><small class="text-muted"><i class="far fa-calendar me-1"></i>${t.transactionDate}</small></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${t.type == 'INCOME'}">
                                                <span class="badge bg-success">
                                                    <i class="${t.categoryIcon != null ? t.categoryIcon : 'fas fa-arrow-up'} me-1"></i>${t.categoryName}
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-danger">
                                                    <i class="${t.categoryIcon != null ? t.categoryIcon : 'fas fa-arrow-down'} me-1"></i>${t.categoryName}
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>${t.description}</td>
                                    <td class="text-end fw-bold ${t.type == 'INCOME' ? 'text-success' : 'text-danger'}">
                                        <fmt:formatNumber value="${t.amount}" type="number" groupingUsed="true"/>đ
                                    </td>
                                    <td class="text-center">
                                        <div class="btn-group btn-group-sm">
                                            <a href="${pageContext.request.contextPath}/transactions/edit?id=${t.id}" class="btn btn-outline-primary">
                                                <i class="fas fa-edit"></i>
                                            </a>
                                            <button type="button" class="btn btn-outline-danger" onclick="if(confirm('Xóa giao dịch này?')) window.location='${pageContext.request.contextPath}/transactions/delete?id=${t.id}'">
                                                <i class="fas fa-trash"></i>
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </div>
    
    <%@ include file="/WEB-INF/views/layouts/footer.jsp" %>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>