<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${isEdit ? 'Sửa' : 'Thiết lập'} Ngân sách - FinSmart</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
</head>
<body>
    <%@ include file="/WEB-INF/views/layouts/header.jsp" %>
    
    <div class="container-fluid">
        <div class="row">
            <%@ include file="/WEB-INF/views/layouts/sidebar.jsp" %>
            
            <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4 py-4">
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb">
                        <li class="breadcrumb-item">
                            <a href="${pageContext.request.contextPath}/budgets?month=${month}&year=${year}">Ngân sách</a>
                        </li>
                        <li class="breadcrumb-item active">${isEdit ? 'Sửa' : 'Thiết lập'} ngân sách</li>
                    </ol>
                </nav>
                
                <div class="row justify-content-center">
                    <div class="col-md-6 col-lg-5">
                        <div class="card shadow-sm">
                            <div class="card-header bg-primary text-white">
                                <h5 class="mb-0">
                                    <i class="fas ${isEdit ? 'fa-edit' : 'fa-bullseye'} me-2"></i>
                                    ${isEdit ? 'Sửa ngân sách' : 'Thiết lập ngân sách mới'}
                                </h5>
                            </div>
                            <div class="card-body">
                                <c:if test="${not empty error}">
                                    <div class="alert alert-danger">
                                        <i class="fas fa-exclamation-circle me-2"></i>${error}
                                    </div>
                                </c:if>
                                
                                <form method="post" 
                                      action="${pageContext.request.contextPath}/budgets/${isEdit ? 'edit' : 'create'}">
                                    
                                    <c:if test="${isEdit}">
                                        <input type="hidden" name="id" value="${budget.id}">
                                    </c:if>
                                    
                                    <!-- Period (Read-only) -->
                                    <div class="mb-3">
                                        <label class="form-label">Kỳ ngân sách</label>
                                        <input type="text" class="form-control" 
                                               value="Tháng ${month}/${year}" disabled>
                                        <input type="hidden" name="month" value="${month}">
                                        <input type="hidden" name="year" value="${year}">
                                    </div>
                                    
                                    <!-- Category -->
                                    <div class="mb-3">
                                        <label class="form-label">Danh mục <span class="text-danger">*</span></label>
                                        <c:choose>
                                            <c:when test="${isEdit}">
                                                <input type="text" class="form-control" 
                                                       value="${budget.categoryName != null ? budget.categoryName : 'Ngân sách tổng'}" 
                                                       disabled>
                                                <input type="hidden" name="category_id" value="${budget.categoryId != null ? budget.categoryId : 'overall'}">
                                            </c:when>
                                            <c:otherwise>
                                                <select name="category_id" class="form-select" required>
                                                    <option value="overall">📊 Ngân sách tổng (tất cả chi tiêu)</option>
                                                    <c:forEach var="cat" items="${categories}">
                                                        <option value="${cat.id}">
                                                            <i class="${cat.icon} me-1"></i>${cat.name}
                                                        </option>
                                                    </c:forEach>
                                                </select>
                                            </c:otherwise>
                                        </c:choose>
                                        <div class="form-text">Chọn "Ngân sách tổng" để giới hạn toàn bộ chi tiêu tháng</div>
                                    </div>
                                    
                                    <!-- Amount Limit -->
                                    <div class="mb-4">
                                        <label class="form-label">Giới hạn chi tiêu <span class="text-danger">*</span></label>
                                        <div class="input-group">
                                            <span class="input-group-text">₫</span>
                                            <input type="number" class="form-control" name="amount_limit" 
                                                   value="${isEdit ? budget.amountLimit : ''}" 
                                                   placeholder="0" min="0" step="1000" required>
                                        </div>
                                        <div class="form-text">Nhập số nguyên, ví dụ: 5000000 cho 5 triệu đồng</div>
                                    </div>
                                    
                                    <!-- Actions -->
                                    <div class="d-grid gap-2">
                                        <button type="submit" class="btn btn-primary">
                                            <i class="fas fa-save me-1"></i> ${isEdit ? 'Cập nhật' : 'Thiết lập ngân sách'}
                                        </button>
                                        <a href="${pageContext.request.contextPath}/budgets?month=${month}&year=${year}" 
                                           class="btn btn-outline-secondary">
                                            <i class="fas fa-times me-1"></i> Hủy
                                        </a>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </main>
        </div>
    </div>
    
    <%@ include file="/WEB-INF/views/layouts/footer.jsp" %>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>