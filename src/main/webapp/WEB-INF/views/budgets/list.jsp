
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ngân sách - FinSmart</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
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
    </style>
</head>
<body>
    <%@ include file="/WEB-INF/views/layouts/header.jsp" %>
    <!-- Page Header -->
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h1 class="h2 mb-1 fw-bold">
                <i class="fas fa-piggy-bank me-2"></i>Ngân sách
            </h1>
            <p class="text-muted mb-0" style="font-size: 1.1rem;">
                <i class="fas fa-calendar me-2"></i>Tháng ${currentMonth}/${currentYear}
            </p>
        </div>
        <a href="${pageContext.request.contextPath}/budgets/create" class="btn btn-add-uniform">
            <i class="fas fa-plus-circle"></i>
            <span>Thêm ngân sách</span>
        </a>
    </div>
    
    <!-- Month Selector -->
    <div class="card mb-4">
        <div class="card-body">
            <div class="row align-items-center">
                <div class="col-md-8">
                    <h5 class="mb-0"><i class="fas fa-calendar-alt me-2"></i>Chọn tháng xem ngân sách</h5>
                </div>
                <div class="col-md-4">
                    <form method="get" action="${pageContext.request.contextPath}/budgets" class="row g-2">
                        <div class="col-6">
                            <select name="month" class="form-select form-select-sm">
                                <c:forEach var="m" begin="1" end="12">
                                    <option value="${m}" ${m == currentMonth ? 'selected' : ''}>Tháng ${m}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-6">
                            <select name="year" class="form-select form-select-sm">
                                <c:forEach var="y" begin="2024" end="2030">
                                    <option value="${y}" ${y == currentYear ? 'selected' : ''}>${y}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-12 mt-2">
                            <button type="submit" class="btn btn-primary btn-sm w-100">
                                <i class="fas fa-filter"></i> Xem
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Messages -->
    <c:if test="${not empty error}">
        <div class="alert alert-danger alert-dismissible fade show mb-3">
            <i class="fas fa-exclamation-circle me-2"></i>${error}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    
    <!-- Budgets Table -->
    <c:choose>
        <c:when test="${empty budgets}">
            <div class="text-center py-5">
                <i class="fas fa-piggy-bank fa-3x text-muted mb-3"></i>
                <p class="text-muted">Chưa thiết lập ngân sách nào cho kỳ này.</p>
                <a href="${pageContext.request.contextPath}/budgets/create" class="btn btn-outline-primary">
                    <i class="fas fa-plus"></i> Thiết lập ngân sách đầu tiên
                </a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="table-responsive">
                <table class="table table-striped table-hover">
                    <thead class="table-light">
                        <tr>
                            <th>Danh mục</th>
                            <th class="text-end">Giới hạn</th>
                            <th class="text-end">Đã chi</th>
                            <th class="text-end">Còn lại</th>
                            <th>Tiến độ</th>
                            <th class="text-center">Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="b" items="${budgets}">
                            <tr>
                                <!-- 1. Danh mục -->
                                <td>
                                    <c:choose>
                                        <c:when test="${b.categoryId == null}">
                                            <span class="badge bg-primary"><i class="fas fa-wallet"></i> Tổng thể</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-info"><i class="fas fa-folder"></i> ${b.categoryName}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                
                                <!-- 2. Giới hạn (amountLimit) -->
                                <td class="text-end fw-bold">
                                    <fmt:formatNumber value="${b.amountLimit}" type="number" groupingUsed="true"/>đ
                                </td>
                                
                                <!-- 3. Đã chi (spentAmount) -->
                                <td class="text-end">
                                    <fmt:formatNumber value="${b.spentAmount != null ? b.spentAmount : 0}" type="number" groupingUsed="true"/>đ
                                </td>
                                
                                <!-- 4. Còn lại (remaining = limit - spent) -->
                                <td class="text-end fw-bold">
                                    <c:set var="remaining" value="${b.amountLimit - (b.spentAmount != null ? b.spentAmount : 0)}"/>
                                    <fmt:formatNumber value="${remaining}" type="number" groupingUsed="true"/>đ
                                </td>
                                
                                <!-- 5. Tiến độ (progress bar) -->
                                <td>
                                    <c:set var="percent" value="${b.amountLimit > 0 ? (b.spentAmount != null ? b.spentAmount : 0) * 100 / b.amountLimit : 0}"/>
                                    <div class="progress" style="height: 20px;">
                                        <div class="progress-bar ${percent >= 100 ? 'bg-danger' : percent >= 80 ? 'bg-warning' : 'bg-success'}" 
                                             style="width: ${percent > 100 ? 100 : percent}%">
                                            ${percent > 100 ? 100 : percent}%
                                        </div>
                                    </div>
                                </td>
                                
                                <!-- 6. Thao tác (edit/delete) -->
                                <td class="text-center">
                                    <div class="btn-group btn-group-sm">
                                        <a href="${pageContext.request.contextPath}/budgets/edit?id=${b.id}" class="btn btn-outline-primary">
                                            <i class="fas fa-edit"></i>
                                        </a>
                                        <button type="button" class="btn btn-outline-danger" 
                                                onclick="if(confirm('Xóa ngân sách này?')) window.location='${pageContext.request.contextPath}/budgets/delete?id=${b.id}&month=${currentMonth}&year=${currentYear}'">
                                            <i class="fas fa-trash"></i>
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
    
    <%@ include file="/WEB-INF/views/layouts/footer.jsp" %>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>