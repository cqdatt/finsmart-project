<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${isEdit ? 'Sửa' : 'Thiết lập'} Ngân sách - FinSmart</title>
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
                    <a href="${pageContext.request.contextPath}/budgets" class="text-decoration-none">
                        <i class="fas fa-piggy-bank me-1"></i>Ngân sách
                    </a>
                </li>
                <li class="breadcrumb-item active" aria-current="page">
                    ${isEdit ? 'Sửa' : 'Thiết lập'} ngân sách
                </li>
            </ol>
        </nav>
        
        <h4 class="mb-0">
            <i class="fas ${isEdit ? 'fa-edit' : 'fa-piggy-bank'} me-2 text-primary"></i>
            ${isEdit ? 'Sửa Ngân sách' : 'Thiết lập Ngân sách'}
        </h4>
    </div>
    
    <!-- FORM -->
    <div class="form-wrapper">
        <div class="form-container">
            <div class="card form-card">
                <div class="card-header">
                    <h5>
                        <i class="fas ${isEdit ? 'fa-edit' : 'fa-piggy-bank'} me-2"></i>
                        ${isEdit ? 'Thông tin ngân sách' : 'Thiết lập ngân sách'}
                    </h5>
                </div>
                <div class="card-body">
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger mb-3">
                            <i class="fas fa-exclamation-circle me-2"></i>${error}
                        </div>
                    </c:if>
                    
                    <form method="post" action="${pageContext.request.contextPath}/budgets/${isEdit ? 'edit' : 'create'}">
                        <c:if test="${isEdit}">
                            <input type="hidden" name="id" value="${budget.id}">
                        </c:if>
                        
                        <!-- Danh mục -->
                        <div class="mb-3">
                            <label class="form-label">Danh mục</label>
                            <select name="category_id" class="form-select" required>
                                <option value="">-- Tổng thể --</option>
                                <c:forEach var="cat" items="${categories}">
                                    <c:if test="${cat.type == 'EXPENSE'}">
                                        <option value="${cat.id}" 
                                                ${budget != null && budget.categoryId == cat.id ? 'selected' : ''}>
                                            ${cat.name}
                                        </option>
                                    </c:if>
                                </c:forEach>
                            </select>
                            <small class="text-muted">Chọn danh mục cho ngân sách</small>
                        </div>
                                                
                        <!-- Tháng -->
                        <div class="mb-3">
                            <label class="form-label">Tháng</label>
                            <select name="month" class="form-select" required>
                                <c:forEach var="m" begin="1" end="12">
                                    <option value="${m}" ${budget != null ? (budget.month == m ? 'selected' : '') : (m == month ? 'selected' : '')}>
                                        Tháng ${m}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        
                        <!-- Năm -->
                        <div class="mb-3">
                            <label class="form-label">Năm</label>
                            <select name="year" class="form-select" required>
                                <c:forEach var="y" begin="2024" end="2030">
                                    <option value="${y}" ${budget != null ? (budget.year == y ? 'selected' : '') : (y == year ? 'selected' : '')}>
                                        ${y}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        
                        <!-- Giới hạn chi tiêu - ✅ FIX: HIỂN THỊ RAW NUMBER -->
                        <!-- Giới hạn chi tiêu -->
                        <div class="mb-3">
                            <label class="form-label">Giới hạn chi tiêu (VNĐ)</label>
                            <c:set var="amountValue" value="${budget != null && budget.amountLimit != null ? budget.amountLimit.longValue() : ''}"/>
                            <input type="text" class="form-control" name="amount_limit" id="amountInput"
                                value="${amountValue}"
                                placeholder="2000000" required>
                            <small class="text-muted">Nhập số nguyên (ví dụ: 2000000 cho 2 triệu)</small>
                        </div>
                        <script>
                        document.addEventListener('DOMContentLoaded', function() {
                            const amountInput = document.getElementById('amountInput');
                            
                            if (amountInput) {
                                // Lưu raw value ban đầu
                                let rawValue = amountInput.value.replace(/[^0-9]/g, '');
                                amountInput.setAttribute('data-raw', rawValue);
                                
                                // Format hiển thị (chỉ để đẹp)
                                if (rawValue && rawValue.length > 0) {
                                    amountInput.value = parseInt(rawValue).toLocaleString('vi-VN');
                                }
                                
                                // Khi blur: format hiển thị
                                amountInput.addEventListener('blur', function() {
                                    let val = this.value.replace(/[^0-9]/g, '');
                                    if (val && val.length > 0) {
                                        this.setAttribute('data-raw', val);
                                        this.value = parseInt(val).toLocaleString('vi-VN');
                                    }
                                });
                                
                                // Khi focus: hiện raw number để edit
                                amountInput.addEventListener('focus', function() {
                                    let raw = this.getAttribute('data-raw') || this.value.replace(/[^0-9]/g, '');
                                    this.value = raw;
                                });
                                
                                // Trước khi submit: set raw number
                                const form = amountInput.closest('form');
                                if (form) {
                                    form.addEventListener('submit', function(e) {
                                        let raw = amountInput.getAttribute('data-raw') || amountInput.value.replace(/[^0-9]/g, '');
                                        amountInput.value = raw;
                                        console.log("✅ Submitting amount: " + raw);
                                    });
                                }
                            }
                        });
                        </script>
                        <!-- Buttons -->
                        <div class="d-grid gap-2 mt-4">
                            <button type="submit" class="btn btn-submit-uniform">
                                <i class="fas fa-save me-2"></i> ${isEdit ? 'Cập nhật' : 'Lưu ngân sách'}
                            </button>
                            <a href="${pageContext.request.contextPath}/budgets" class="btn btn-cancel-uniform">
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
    
    <!-- ✅ MONEY FORMAT SCRIPT - CHỈ FORMAT HIỂN THỊ, KHÔNG ẢNH HƯỞNG SUBMIT -->
    <script>
    document.addEventListener('DOMContentLoaded', function() {
        const amountInput = document.getElementById('amountInput');
        
        if (amountInput) {
            // ✅ FORMAT HIỂN THỊ KHI LOAD (chỉ để đẹp, không ảnh hưởng value thực)
            const rawValue = amountInput.value.replace(/[^0-9]/g, '');
            if (rawValue && !isNaN(rawValue) && rawValue.length > 3) {
                // Chỉ format nếu số >= 1000
                amountInput.setAttribute('data-raw', rawValue); // Lưu raw value
                amountInput.value = parseInt(rawValue).toLocaleString('vi-VN');
            } else {
                amountInput.setAttribute('data-raw', rawValue);
            }
            
            // ✅ FORMAT KHI BLUR (mất focus)
            amountInput.addEventListener('blur', function() {
                let val = this.value.replace(/[^0-9]/g, '');
                if (val && !isNaN(val)) {
                    this.setAttribute('data-raw', val);
                    this.value = parseInt(val).toLocaleString('vi-VN');
                }
            });
            
            // ✅ CLEAN KHI FOCUS (để user edit dễ)
            amountInput.addEventListener('focus', function() {
                let raw = this.getAttribute('data-raw') || this.value.replace(/[^0-9]/g, '');
                this.value = raw;
            });
            
            // ✅ CLEAN TRƯỚC KHI SUBMIT (quan trọng nhất!)
            const form = amountInput.closest('form');
            if (form) {
                form.addEventListener('submit', function() {
                    // Lấy raw value và set lại vào input trước khi submit
                    let raw = amountInput.getAttribute('data-raw') || amountInput.value.replace(/[^0-9]/g, '');
                    amountInput.value = raw; // Set raw number để server nhận đúng
                    console.log("Submitting amount: " + raw);
                });
            }
        }
    });
    </script>
</body>
</html>