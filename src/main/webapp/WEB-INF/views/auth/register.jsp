<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng ký - FinSmart</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .register-container { max-width: 500px; margin: 50px auto; padding: 30px; border-radius: 10px; box-shadow: 0 0 20px rgba(0,0,0,0.1); }
    </style>
</head>
<body class="bg-light">
    <div class="container">
        <div class="register-container bg-white">
            <div class="text-center mb-4">
                <i class="fas fa-wallet fa-3x text-primary"></i>
                <h2 class="mt-2">Đăng ký FinSmart</h2>
            </div>
            
            <c:if test="${not empty error}">
                <div class="alert alert-danger alert-dismissible fade show">
                    <i class="fas fa-exclamation-circle"></i> ${error}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>
            
            <form action="${pageContext.request.contextPath}/auth/register" method="POST">
                <div class="mb-3">
                    <label class="form-label">Tên đăng nhập *</label>
                    <input type="text" class="form-control" name="username" value="${username}" required>
                </div>
                
                <div class="mb-3">
                    <label class="form-label">Email *</label>
                    <input type="email" class="form-control" name="email" value="${email}" required>
                </div>
                
                <div class="mb-3">
                    <label class="form-label">Họ và tên</label>
                    <input type="text" class="form-control" name="full_name" value="${fullName}">
                </div>
                
                <div class="mb-3">
                    <label class="form-label">Mật khẩu *</label>
                    <input type="password" class="form-control" name="password" required minlength="6">
                </div>
                
                <div class="mb-3">
                    <label class="form-label">Xác nhận mật khẩu *</label>
                    <input type="password" class="form-control" name="confirm_password" required>
                </div>
                
                <button type="submit" class="btn btn-primary w-100">
                    <i class="fas fa-user-plus"></i> Đăng ký
                </button>
            </form>
            
            <hr>
            <div class="text-center">
                <p>Đã có tài khoản? <a href="${pageContext.request.contextPath}/auth/login">Đăng nhập ngay</a></p>
            </div>
        </div>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>