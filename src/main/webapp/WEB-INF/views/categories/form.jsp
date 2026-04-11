<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${isEdit ? 'Sửa' : 'Thêm'} Danh mục - FinSmart</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .form-wrapper {
            display: flex;
            justify-content: center;
            align-items: flex-start;
            min-height: calc(100vh - 100px);
            padding: 20px 20px;
        }
        .form-container {
            width: 100%;
            max-width: 600px;
        }
        .form-card {
            border: none;
            border-radius: 12px;
            box-shadow: 0 4px 20px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        .form-card .card-header {
            background: linear-gradient(135deg, #0d6efd 0%, #0dcaf0 100%) !important;
            padding: 20px 24px;
            border: none !important;
        }
        .form-card .card-header h5 {
            margin: 0;
            color: white !important;
            font-weight: 600;
            font-size: 1.25rem;
        }
        .form-card .card-body {
            padding: 24px;
            background: white;
        }
        .btn-submit-uniform {
            background: linear-gradient(135deg, #0d6efd 0%, #0dcaf0 100%) !important;
            border: none !important;
            color: #ffffff !important;
            padding: 12px 28px !important;
            font-size: 15px !important;
            font-weight: 600 !important;
            border-radius: 10px !important;
            box-shadow: 0 4px 15px rgba(13, 110, 253, 0.35) !important;
            transition: all 0.3s ease !important;
            width: 100%;
        }
        .btn-submit-uniform:hover {
            transform: translateY(-3px) !important;
            box-shadow: 0 8px 25px rgba(13, 110, 253, 0.5) !important;
            color: #ffffff !important;
        }
        .btn-cancel-uniform {
            border: 2px solid #dee2e6 !important;
            color: #6c757d !important;
            padding: 12px 28px !important;
            font-size: 15px !important;
            font-weight: 600 !important;
            border-radius: 10px !important;
            transition: all 0.3s ease !important;
            width: 100%;
            background: white !important;
            margin-top: 10px;
        }
        .btn-cancel-uniform:hover {
            background: #f8f9fa !important;
            border-color: #adb5bd !important;
        }
    </style>
</head>
<body>
    <%@ include file="/WEB-INF/views/layouts/header.jsp" %>
    
    <!-- BREADCRUMB & TITLE -->
    <div class="container-fluid px-4 py-3">
        <nav aria-label="breadcrumb">
            <ol class="breadcrumb mb-2">
                <li class="breadcrumb-item">
                    <a href="${pageContext.request.contextPath}/categories" class="text-decoration-none">
                        <i class="fas fa-folder me-1"></i>Danh mục
                    </a>
                </li>
                <li class="breadcrumb-item active">${isEdit ? 'Sửa' : 'Thêm'} danh mục</li>
            </ol>
        </nav>
        <h4 class="mb-0">
            <i class="fas ${isEdit ? 'fa-edit' : 'fa-plus-circle'} me-2 text-primary"></i>
            ${isEdit ? 'Sửa Danh mục' : 'Thêm Danh mục mới'}
        </h4>
    </div>
    
    <!-- FORM -->
    <div class="form-wrapper">
        <div class="form-container">
            <div class="card form-card">
                <div class="card-header">
                    <h5>
                        <i class="fas ${isEdit ? 'fa-folder-open' : 'fa-folder-plus'} me-2"></i>
                        ${isEdit ? 'Thông tin danh mục' : 'Thêm danh mục mới'}
                    </h5>
                </div>
                <div class="card-body">
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger mb-3">
                            <i class="fas fa-exclamation-circle me-2"></i>${error}
                        </div>
                    </c:if>
                    
                    <form method="post" action="${pageContext.request.contextPath}/categories/${isEdit ? 'edit' : 'create'}">
                        <c:if test="${isEdit}">
                            <input type="hidden" name="id" value="${category.id}">
                        </c:if>
                        
                        <div class="mb-3">
                            <label class="form-label">Tên danh mục <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" name="name"
                                   value="${category != null ? category.name : ''}"
                                   placeholder="Ví dụ: Ăn uống, Lương, Trộm..."
                                   required maxlength="100">
                            <div class="form-text">Tối đa 100 ký tự</div>
                        </div>
                        
                        <div class="mb-3">
                            <label class="form-label">Loại danh mục <span class="text-danger">*</span></label>
                            <c:choose>
                                <c:when test="${isEdit}">
                                    <input type="text" class="form-control"
                                           value="${category.type == 'INCOME' ? 'Thu nhập' : 'Chi tiêu'}"
                                           disabled>
                                    <input type="hidden" name="type" value="${category.type}">
                                    <div class="form-text">Không thể thay đổi loại danh mục</div>
                                </c:when>
                                <c:otherwise>
                                    <select class="form-select" name="type" required>
                                        <option value="">-- Chọn loại --</option>
                                        <option value="INCOME" ${type == 'INCOME' ? 'selected' : ''}>Thu nhập</option>
                                        <option value="EXPENSE" ${type == 'EXPENSE' ? 'selected' : ''}>Chi tiêu</option>
                                    </select>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        
                        <div class="mb-3">
                            <label class="form-label">Icon (Font Awesome)</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="fas fa-icons"></i></span>
                                <input type="text" class="form-control" name="icon" id="iconInput"
                                       value="${category != null ? category.icon : 'fas fa-tag'}"
                                       placeholder="fas fa-utensils">
                            </div>
                            <div class="form-text">
                                Ví dụ: <code>fas fa-utensils</code>, <code>fas fa-car</code>, <code>fas fa-money-bill</code>
                            </div>
                            <div class="mt-2 text-center p-3 bg-light rounded">
                                <i id="iconPreview" class="${category != null ? category.icon : 'fas fa-tag'} fa-2x text-primary"></i>
                            </div>
                        </div>
                        
                        <div class="d-grid gap-2 mt-4">
                            <button type="submit" class="btn btn-submit-uniform">
                                <i class="fas fa-save me-2"></i> ${isEdit ? 'Cập nhật' : 'Tạo danh mục'}
                            </button>
                            <a href="${pageContext.request.contextPath}/categories" class="btn btn-cancel-uniform">
                                <i class="fas fa-times me-2"></i> Hủy
                            </a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
    
    <%@ include file="/WEB-INF/views/layouts/footer.jsp" %>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
    document.addEventListener('DOMContentLoaded', function() {
        const iconInput = document.getElementById('iconInput');
        const iconPreview = document.getElementById('iconPreview');
        
        if (iconInput && iconPreview) {
            iconInput.addEventListener('input', function() {
                const iconClass = this.value.trim() || 'fas fa-tag';
                iconPreview.className = iconClass + ' fa-2x text-primary';
            });
            if (iconInput.value) {
                iconPreview.className = iconInput.value + ' fa-2x text-primary';
            }
        }
    });
    </script>
</body>
</html>