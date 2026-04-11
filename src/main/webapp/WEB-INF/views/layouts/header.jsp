<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>FinSmart - Quản lý tài chính cá nhân</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f8f9fa;
            overflow-x: hidden;
        }
        .sidebar {
            min-height: 100vh;
            background: linear-gradient(180deg, #0d6efd 0%, #0dcaf0 100%);
            box-shadow: 2px 0 10px rgba(0,0,0,0.1);
            padding-top: 15px;
        }
        .sidebar .nav-link {
            color: rgba(255,255,255,0.9);
            padding: 14px 20px;  /* ✅ TĂNG PADDING */
            margin: 5px 0;        /* ✅ TĂNG MARGIN */
            border-radius: 10px;
            transition: all 0.3s;
            font-size: 1.05rem;   /* ✅ TĂNG FONT SIZE */
            font-weight: 500;
        }
        .sidebar .nav-link:hover, .sidebar .nav-link.active {
            background-color: rgba(255,255,255,0.25);
            color: white;
            transform: translateX(5px);
        }
        .sidebar .nav-link i {
            margin-right: 12px;
            width: 22px;
            font-size: 1.1rem;
        }
        .main-content {
            padding: 20px ;  /*  GIẢM PADDING */
            margin-left: 0;
        }
        .top-navbar {
            background-color: white;
            box-shadow: 0 2px 4px rgba(0,0,0,0.08);
            padding: 10px 20px;
            margin-bottom: 0;
        }
        .brand-logo {
            font-size: 22px;      /* ✅ TĂNG FONT LOGO */
            font-weight: 700;
            color: #0d6efd;
            text-decoration: none;
        }
        .brand-logo i {
            margin-right: 8px;
        }
        .user-dropdown {
            color: #212529;
            text-decoration: none;
            font-weight: 500;
            font-size: 1rem;
        }
        .user-dropdown:hover {
            color: #0d6efd;
        }
    </style>
</head>
<body>
    <!-- Top Navbar (CHỈ LOGO + USER) -->
    <nav class="top-navbar">
        <div class="container-fluid">
            <div class="d-flex justify-content-between align-items-center">
                <!-- Logo -->
                <a class="brand-logo" href="${pageContext.request.contextPath}/dashboard">
                    <i class="fas fa-wallet"></i>FinSmart
                </a>
                
                <!-- User Dropdown -->
                <div class="dropdown">
                    <button class="btn btn-link text-decoration-none dropdown-toggle user-dropdown" 
                            type="button" 
                            id="userDropdown" 
                            data-bs-toggle="dropdown" 
                            aria-expanded="false">
                        <i class="fas fa-user-circle me-2"></i>
                        <span class="fw-semibold">
                            ${sessionScope.displayName != null ? sessionScope.displayName : 
                              sessionScope.currentUser != null ? sessionScope.currentUser.fullName : 'User'}
                        </span>
                    </button>
                    <ul class="dropdown-menu dropdown-menu-end shadow" aria-labelledby="userDropdown" style="min-width: 150px;">
                        <li>
                            <a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/logout">
                                <i class="fas fa-sign-out-alt me-2"></i>Đăng xuất
                            </a>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    </nav>
    
    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar - GIẢM WIDTH XUỐNG -->
<nav class="col-md-2 col-lg-2 d-md-block sidebar py-3" style="min-width: 200px;">
    <div class="position-sticky">
        <ul class="nav flex-column px-2">
            <li class="nav-item">
                <a class="nav-link ${request.requestURI.contains('/dashboard') ? 'active' : ''}" 
                   href="${pageContext.request.contextPath}/dashboard">
                    <i class="fas fa-home"></i>Trang chủ
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${request.requestURI.contains('/transactions') ? 'active' : ''}" 
                   href="${pageContext.request.contextPath}/transactions">
                    <i class="fas fa-exchange-alt"></i>Giao dịch
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${request.requestURI.contains('/categories') ? 'active' : ''}" 
                   href="${pageContext.request.contextPath}/categories">
                    <i class="fas fa-folder"></i>Danh mục
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${request.requestURI.contains('/budgets') ? 'active' : ''}" 
                   href="${pageContext.request.contextPath}/budgets">
                    <i class="fas fa-piggy-bank"></i>Ngân sách
                </a>
            </li>
        </ul>
    </div>
</nav>

<!-- Main Content - TĂNG WIDTH -->
<main class="col-md-10 ms-sm-auto col-lg-10 main-content">
