package com.finsmart.config;

/**
 * Application configuration constants
 * Central place for app-wide settings
 */
public class AppConfig {
    
    // ✅ Database - THÊM UTF-8 ENCODING
    public static final String DB_URL = "jdbc:mysql://localhost:3306/finsmart_db?" +
                                        "useUnicode=true&" +
                                        "characterEncoding=UTF-8&" +
                                        "useSSL=false&" +
                                        "serverTimezone=Asia/Ho_Chi_Minh&" +
                                        "allowPublicKeyRetrieval=true";
    public static final String DB_USER = "root";
    public static final String DB_PASSWORD = "";
    
    // App settings
    public static final String APP_NAME = "FinSmart";
    public static final String APP_VERSION = "1.0.0";
    
    // Default values
    public static final int DEFAULT_USER_ID = 1;  // For testing without auth
    public static final int TRANSACTIONS_PER_PAGE = 10;
    public static final int RECENT_TRANSACTIONS_LIMIT = 5;
    
    // Money format
    public static final String MONEY_PATTERN = "#,###";
    public static final String CURRENCY_SYMBOL = "đ";
    public static final String LOCALE_VI = "vi_VN";
    
    // Date format
    public static final String DATE_PATTERN = "dd/MM/yyyy";
    
    // Session keys
    public static final String SESSION_USER = "currentUser";
    public static final String SESSION_ERROR = "errorMessage";
    
    // Request parameters
    public static final String PARAM_USER_ID = "user_id";
    public static final String PARAM_CATEGORY_ID = "category_id";
    public static final String PARAM_AMOUNT = "amount";
    public static final String PARAM_BUDGET_LIMIT = "budget_limit";
    
    // Redirect paths
    public static final String PATH_DASHBOARD = "/dashboard";
    public static final String PATH_TRANSACTIONS = "/transactions";
    public static final String PATH_CATEGORIES = "/categories";
    public static final String PATH_BUDGETS = "/budgets";
    public static final String PATH_LOGIN = "/auth/login";
    
    private AppConfig() {
        // Prevent instantiation
    }
}