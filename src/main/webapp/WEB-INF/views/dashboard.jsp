<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - FinSmart</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/chart.js@3.9.1/dist/chart.min.js"></script>
    <style>
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
        .chart-container { 
            position: relative; 
            height: 300px; 
        }
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
    
    <!-- Dashboard Header -->
    <div class="mb-4">
        <div class="d-flex justify-content-between align-items-center">
            <div>
                <h1 class="h2 mb-1 fw-bold">Dashboard</h1>
                <p class="text-muted mb-0" style="font-size: 1.1rem;">
                    <i class="fas fa-calendar-alt me-2"></i>Kỳ: <strong>${currentPeriod}</strong>
                </p>
            </div>
        </div>
    </div>
    
    <!-- Budget Alert -->
    <c:if test="${not empty budgetAlerts}">
        <div class="alert alert-warning alert-dismissible fade show mb-4">
            <div class="d-flex align-items-center">
                <i class="fas fa-bell fa-lg me-3 text-warning"></i>
                <div>
                    <strong>Cảnh báo chi tiêu:</strong>
                    <span class="ms-2">Chi tiết hơn trong phần <a href="${pageContext.request.contextPath}/budgets" class="alert-link"><strong>NGÂN SÁCH</strong></a></span>
                </div>
            </div>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    
    <!-- Summary Cards -->
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
    
    <!-- Charts Row -->
    <div class="row mb-4">
        <!-- Category Pie Chart (CURRENT MONTH) -->
        <div class="col-md-6 mb-3">
            <div class="card h-100">
                <div class="card-header">
                    <i class="fas fa-chart-pie me-1"></i>Chi tiêu theo danh mục
                </div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${not empty categorySpending}">
                            <canvas id="categoryChart"></canvas>
                        </c:when>
                        <c:otherwise>
                            <div class="text-center text-muted py-5">
                                <i class="fas fa-chart-pie fa-3x mb-3"></i>
                                <p>Chưa có dữ liệu chi tiêu trong kỳ này</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
        
        <!-- Monthly Trend Bar Chart (6 MONTHS - KEEP AS IS) -->
        <div class="col-md-6 mb-3">
            <div class="card h-100">
                <div class="card-header">
                    <i class="fas fa-chart-bar me-1"></i>Xu hướng thu/chi 6 tháng
                </div>
                <div class="card-body">
                    <div class="chart-container">
                        <canvas id="trendChart"></canvas>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Recent Transactions -->
    <div class="card">
        <div class="card-header d-flex justify-content-between align-items-center">
            <span><i class="fas fa-list me-1"></i>Giao dịch gần đây</span>
            <a href="${pageContext.request.contextPath}/transactions" class="btn btn-sm btn-primary">Xem tất cả</a>
        </div>
        <div class="table-responsive">
            <table class="table table-hover mb-0">
                <thead class="table-light">
                    <tr>
                        <th>Ngày</th>
                        <th>Danh mục</th>
                        <th>Mô tả</th>
                        <th class="text-end">Số tiền</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty recentTransactions}">
                            <tr>
                                <td colspan="4" class="text-center text-muted py-4">Chưa có giao dịch nào</td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="t" items="${recentTransactions}">
                                <tr>
                                    <td><small class="text-muted">${t.transactionDate}</small></td>
                                    <td>
                                        <i class="${t.categoryIcon} me-1 ${t.type == 'INCOME' ? 'text-success' : 'text-danger'}"></i>
                                        ${t.categoryName}
                                    </td>
                                    <td>${t.description}</td>
                                    <td class="text-end money-cell" data-raw="${t.amount}">Loading...</td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </div>
    
    <%@ include file="/WEB-INF/views/layouts/footer.jsp" %>
    
    <script>
    document.addEventListener('DOMContentLoaded', function() {
        
        // ===== CATEGORY PIE CHART (CURRENT MONTH) =====
        <c:if test="${not empty categorySpending}">
        const categoryCtx = document.getElementById('categoryChart').getContext('2d');
        new Chart(categoryCtx, {
            type: 'doughnut',
            data: {
                labels: [
                    <c:forEach var="entry" items="${categorySpending}" varStatus="loop">
                        '${entry.key}'<c:if test="${!loop.last}">,</c:if>
                    </c:forEach>
                ],
                datasets: [{
                    data: [
                        <c:forEach var="entry" items="${categorySpending}" varStatus="loop">
                            ${entry.value}<c:if test="${!loop.last}">,</c:if>
                        </c:forEach>
                    ],
                    backgroundColor: ['#dc3545','#0d6efd','#6f42c1','#fd7e14','#20c997','#198754']
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: { legend: { position: 'bottom' } }
            }
        });
        </c:if>
        
        // ===== MONTHLY TREND BAR CHART (6 MONTHS) =====
        <c:if test="${not empty monthlyTrend}">
        const trendCtx = document.getElementById('trendChart').getContext('2d');
        new Chart(trendCtx, {
            type: 'bar',
            data: {
                labels: [
                    <c:forEach var="trend" items="${monthlyTrend}" varStatus="loop">
                        'Tháng ${trend.month}/${trend.year}'<c:if test="${!loop.last}">,</c:if>
                    </c:forEach>
                ],
                datasets: [
                    {
                        label: 'Thu nhập',
                        data: [
                            <c:forEach var="trend" items="${monthlyTrend}" varStatus="loop">
                                ${trend.income != null ? trend.income : 0}<c:if test="${!loop.last}">,</c:if>
                            </c:forEach>
                        ],
                        backgroundColor: '#198754'
                    },
                    {
                        label: 'Chi tiêu',
                        data: [
                            <c:forEach var="trend" items="${monthlyTrend}" varStatus="loop">
                                ${trend.expense != null ? trend.expense : 0}<c:if test="${!loop.last}">,</c:if>
                            </c:forEach>
                        ],
                        backgroundColor: '#dc3545'
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: { y: { beginAtZero: true } },
                plugins: { legend: { position: 'bottom' } }
            }
        });
        </c:if>
        
        // ===== MONEY FORMAT =====
        function formatVietnameseMoney(value) {
            let num = parseFloat(String(value).replace(/[^\d.-]/g, ''));
            if (isNaN(num)) return '0đ';
            let formatted = Math.abs(num).toLocaleString('vi-VN', {
                minimumFractionDigits: 0,
                maximumFractionDigits: 0
            });
            return (num < 0 ? '-' : '') + formatted + 'đ';
        }
        
        function formatAllMoney() {
            document.querySelectorAll('.money-cell[data-raw]').forEach(el => {
                let raw = el.getAttribute('data-raw');
                if (raw && raw.trim() !== '') {
                    el.textContent = formatVietnameseMoney(raw);
                }
            });
        }
        
        formatAllMoney();
    });
    </script>
</body>
</html>