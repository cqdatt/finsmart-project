<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${isEdit ? 'Sửa' : 'Thêm'} Giao dịch - FinSmart</title>
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
                    <a href="${pageContext.request.contextPath}/transactions" class="text-decoration-none">
                        <i class="fas fa-exchange-alt me-1"></i>Giao dịch
                    </a>
                </li>
                <li class="breadcrumb-item active">${isEdit ? 'Sửa' : 'Thêm'} giao dịch</li>
            </ol>
        </nav>
        <h4 class="mb-0">
            <i class="fas ${isEdit ? 'fa-edit' : 'fa-plus-circle'} me-2 text-primary"></i>
            ${isEdit ? 'Sửa Giao dịch' : 'Thêm Giao dịch mới'}
        </h4>
    </div>
    
    <!-- FORM -->
    <div class="form-wrapper">
        <div class="form-container">
            <div class="card form-card">
                <div class="card-header">
                    <h5>
                        <i class="fas ${isEdit ? 'fa-edit' : 'fa-plus-circle'} me-2"></i>
                        ${isEdit ? 'Thông tin giao dịch' : 'Thêm giao dịch mới'}
                    </h5>
                </div>
                <div class="card-body">
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger mb-3">
                            <i class="fas fa-exclamation-circle me-2"></i>${error}
                        </div>
                    </c:if>
                    
                    <form method="post" action="${pageContext.request.contextPath}/transactions/${isEdit ? 'edit' : 'create'}">
                        <c:if test="${isEdit}">
                            <input type="hidden" name="id" value="${transaction.id}">
                        </c:if>
                        
                        <div class="mb-3">
                            <label class="form-label">Loại giao dịch <span class="text-danger">*</span></label>
                            <select name="type" class="form-select" required id="typeSelect">
                                <option value="INCOME" ${transaction.type == 'INCOME' ? 'selected' : ''}>💰 Thu nhập</option>
                                <option value="EXPENSE" ${transaction.type == 'EXPENSE' ? 'selected' : ''}>💸 Chi tiêu</option>
                            </select>
                        </div>
                        
                        <div class="mb-3">
                            <label class="form-label">Danh mục <span class="text-danger">*</span></label>
                            <select name="category_id" class="form-select" required id="categorySelect">
                                <option value="">-- Chọn danh mục --</option>
                                <c:forEach var="cat" items="${categories}">
                                    <option value="${cat.id}" data-type="${cat.type}"
                                            ${transaction.categoryId == cat.id ? 'selected' : ''}>
                                        ${cat.name}
                                    </option>
                                </c:forEach>
                            </select>
                            <c:if test="${categoryDeleted}">
                                <div class="alert alert-warning mt-2">
                                    <i class="fas fa-exclamation-triangle me-1"></i>${warningMessage}
                                </div>
                            </c:if>
                            <small class="text-muted">Danh mục sẽ lọc theo loại giao dịch</small>
                        </div>
                        
                        <div class="mb-3">
                            <label class="form-label">Số tiền (VNĐ) <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" name="amount" id="amountInput"
                                   value="${transaction.amount != null ? transaction.amount.longValue() : ''}"
                                   placeholder="Ví dụ: 5000000" required>
                            <small class="text-muted">Nhập số nguyên, không dấu chấm/phẩy</small>
                        </div>
                        
                        <div class="mb-3">
                            <label class="form-label">Ngày giao dịch <span class="text-danger">*</span></label>
                            <input type="date" name="transaction_date" class="form-control"
                                   value="${transaction.transactionDate != null ? transaction.transactionDate : ''}"
                                   required>
                        </div>
                        
                        <div class="mb-3">
                            <label class="form-label">Mô tả</label>
                            <input type="text" name="description" class="form-control"
                                   value="${transaction.description != null ? transaction.description : ''}"
                                   placeholder="Ví dụ: Lương tháng, Ăn trưa...">
                        </div>
                        
                        <div class="d-grid gap-2 mt-4">
                            <button type="submit" class="btn btn-submit-uniform">
                                <i class="fas fa-save me-2"></i> ${isEdit ? 'Cập nhật' : 'Thêm mới'}
                            </button>
                            <a href="${pageContext.request.contextPath}/transactions" class="btn btn-cancel-uniform">
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
        // FILTER CATEGORIES BY TYPE
        const typeSelect = document.getElementById('typeSelect');
        const categorySelect = document.getElementById('categorySelect');
        
        if (typeSelect && categorySelect) {
            function filterCategories() {
                const selectedType = typeSelect.value;
                const options = categorySelect.querySelectorAll('option');
                
                options.forEach(function(option) {
                    if (option.value === '') {
                        option.style.display = '';
                        return;
                    }
                    const categoryType = option.getAttribute('data-type');
                    if (categoryType === selectedType) {
                        option.style.display = '';
                    } else {
                        option.style.display = 'none';
                        if (option.selected) option.selected = false;
                    }
                });
                
                if (!categorySelect.value) {
                    const firstVisible = categorySelect.querySelector('option:not([style*="display: none"]):not([value=""])');
                    if (firstVisible) firstVisible.selected = true;
                }
            }
            
            typeSelect.addEventListener('change', function() {
                filterCategories();
                const currentCategory = categorySelect.value;
                if (currentCategory) {
                    const selectedOption = categorySelect.options[categorySelect.selectedIndex];
                    const catType = selectedOption.getAttribute('data-type');
                    if (catType !== typeSelect.value) {
                        alert('⚠️ Bạn đang đổi loại giao dịch. Vui lòng chọn danh mục phù hợp!');
                        categorySelect.value = '';
                    }
                }
            });
            
            filterCategories();
        }
        
        // MONEY FORMAT
        const amountInput = document.getElementById('amountInput');
        if (amountInput) {
            if (amountInput.value) {
                var raw = amountInput.value.replace(/[^0-9]/g, '');
                if (raw && !isNaN(raw)) {
                    amountInput.value = parseInt(raw).toLocaleString('vi-VN');
                }
            }
            
            amountInput.addEventListener('blur', function() {
                var val = this.value.replace(/[^0-9]/g, '');
                if (val && !isNaN(val)) {
                    this.value = parseInt(val).toLocaleString('vi-VN');
                }
            });
            
            amountInput.addEventListener('focus', function() {
                this.value = this.value.replace(/[^0-9]/g, '');
            });
            
            var form = amountInput.form;
            if (form) {
                form.addEventListener('submit', function() {
                    amountInput.value = amountInput.value.replace(/[^0-9]/g, '');
                });
            }
        }
    });
    </script>
</body>
</html>