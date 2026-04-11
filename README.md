1. Java JDK 8+  
Kiểm tra đã cài chưa:
```
powershell  
java -version
```
Nếu chưa có:    
Tải tại: https://www.oracle.com/java/technologies/javase-jdk8-downloads.html  
Hoặc dùng JDK 11: https://www.oracle.com/java/technologies/javase-jdk11-downloads.html  
Cài đặt → Restart máy  

3. Apache Tomcat 9+  
```
# Kiểm tra xem Tomcat đã cài chưa
Test-Path "C:\Program Files\Apache\Tomcat9"
```
Nếu chưa có:  
Tải tại: https://tomcat.apache.org/download-90.cgi
Chọn "32-bit/64-bit Windows Service Installer"
Cài đặt vào: C:\Program Files\Apache\Tomcat9
Cấu hình port: 8080 (default)  

3. MySQL 8.0+ (mới nhất cũng dc)
   
Nếu chưa có:
Tải tại: https://dev.mysql.com/downloads/installer/
Chọn "MySQL Installer for Windows"
Cài đặt MySQL Server 8.0
Nhớ password root (sẽ dùng sau)

5. Maven 3.6+  
```
mvn --version
```

Nếu chưa có:  
Tải tại: https://maven.apache.org/download.cgi  
Giải nén vào: C:\Program Files\Apache\maven  
Add to PATH:  
System Properties → Environment Variables  
Path → Edit → New → C:\Program Files\Apache\maven\bin  


# BƯỚC 1: CLONE REPOSITORY  
```
# Tạo folder cho project
cd D:\PTIT\Kì 6\OOP
```
```
# Clone repository  
git clone https://github.com/cqdatt/finsmart-project.git
```

```
# Vào folder project
cd finsmart-project
```
BƯỚC 2: TẠO DATABASE

Tạo database:

```
-- Tạo database
DROP DATABASE IF EXISTS finsmart_db;

CREATE DATABASE IF NOT EXISTS finsmart_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE finsmart_db;

-- 1. Bảng Users
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(100),
    avatar VARCHAR(255) DEFAULT 'default-avatar.png',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email)
);

-- 2. Bảng Categories
CREATE TABLE categories (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NULL,
    name VARCHAR(100) NOT NULL,
    type ENUM('INCOME', 'EXPENSE') NOT NULL,
    icon VARCHAR(50) DEFAULT 'fas fa-tag',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE INDEX idx_cat_user_name_type (user_id, name, type),
    INDEX idx_cat_type (type)
);

-- 3. Bảng Transactions 
CREATE TABLE transactions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    category_id INT NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    transaction_date DATE NOT NULL,
    description TEXT,
    type ENUM('INCOME', 'EXPENSE') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id),  -- ✅ CHỈ REFERENCE id
    INDEX idx_trans_user_date (user_id, transaction_date),
    INDEX idx_trans_category (category_id)
);

-- 4. Bảng Budgets
CREATE TABLE budgets (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    category_id INT NULL,
    month INT NOT NULL,
    year INT NOT NULL,
    amount_limit DECIMAL(15, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id),
    UNIQUE INDEX unique_budget (user_id, category_id, month, year),
    INDEX idx_budget_period (user_id, year, month),
    CHECK (month BETWEEN 1 AND 12)
);

-- 5. Bảng Notifications (Optional)
CREATE TABLE notifications (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_noti_user_read (user_id, is_read)
);

-- Insert dữ liệu mẫu: Categories hệ thống
INSERT INTO categories (user_id, name, type, icon) VALUES
(NULL, 'Lương', 'INCOME', 'fas fa-wallet'),
(NULL, 'Thưởng', 'INCOME', 'fas fa-gift'),
(NULL, 'Freelance', 'INCOME', 'fas fa-laptop'),
(NULL, 'Ăn uống', 'EXPENSE', 'fas fa-utensils'),
(NULL, 'Di chuyển', 'EXPENSE', 'fas fa-car'),
(NULL, 'Mua sắm', 'EXPENSE', 'fas fa-shopping-cart'),
(NULL, 'Giải trí', 'EXPENSE', 'fas fa-film'),
(NULL, 'Hóa đơn', 'EXPENSE', 'fas fa-file-invoice'),
(NULL, 'Y tế', 'EXPENSE', 'fas fa-heartbeat'),
(NULL, 'Học tập', 'EXPENSE', 'fas fa-book');

-- Tạo user test (password: '123456' đã hash với BCrypt)
INSERT INTO users (username, password, email, full_name) VALUES
('testuser', '$2a$12$KIXxPZk8Z9vZ7Y8qJ5X5uO7vZ8qJ5X5uO7vZ8qJ5X5uO7vZ8qJ5X5', 'test@example.com', 'Test User');

-- Insert vài giao dịch mẫu cho user test (user_id = 1)
INSERT INTO transactions (user_id, category_id, type, amount, transaction_date, description) VALUES
(1, 4, 'EXPENSE', 50000, CURDATE() - INTERVAL 1 DAY, 'Ăn trưa văn phòng'),
(1, 5, 'EXPENSE', 30000, CURDATE() - INTERVAL 2 DAY, 'Grab đi làm'),
(1, 1, 'INCOME', 10000000, CURDATE() - INTERVAL 5 DAY, 'Lương tháng 3'),
(1, 6, 'EXPENSE', 200000, CURDATE() - INTERVAL 3 DAY, 'Mua áo mới'),
(1, 7, 'EXPENSE', 100000, CURDATE() - INTERVAL 1 DAY, 'Xem phim với bạn');

-- Insert ngân sách mẫu
INSERT INTO budgets (user_id, category_id, month, year, amount_limit) VALUES
(1, 4, MONTH(CURDATE()), YEAR(CURDATE()), 2000000),
(1, NULL, MONTH(CURDATE()), YEAR(CURDATE()), 10000000);

-- Verify data
SELECT 'Users:' as table_name, COUNT(*) as count FROM users
UNION ALL
SELECT 'Categories:', COUNT(*) FROM categories
UNION ALL
SELECT 'Transactions:', COUNT(*) FROM transactions
UNION ALL
SELECT 'Budgets:', COUNT(*) FROM budgets;
```

 BƯỚC 3: CẤU HÌNH ỨNG DỤNG  
 3.1 Chỉnh file db.properties  
 ```
 # Database Configuration
 
db.url=jdbc:mysql://localhost:3306/finsmart_db?useSSL=false&serverTimezone=Asia/Ho_Chi_Minh&allowPublicKeyRetrieval=true
db.username=root
db.password=YOUR_PASSWORD_HERE
db.driver=com.mysql.cj.jdbc.Driver
```
⚠️ Thay YOUR_PASSWORD_HERE bằng password MySQL của mình!  

BƯỚC 4: BUILD PROJECT
```
cd "D:\PTIT\Kì 6\OOP\finsmart-project"

# Clean & Build
mvn clean package

```
Kiểm tra WAR file  

```
# Kiểm tra file WAR đã được tạo
ls target/finsmart.war 
```

BƯỚC 5: DEPLOY LÊN TOMCAT

Stop Tomcat (nếu đang chạy)
```
cd "C:\Program Files\Apache\Tomcat9\bin"
.\shutdown.bat

# Xóa WAR file cũ
Remove-Item "C:\Program Files\Apache\Tomcat9\webapps\finsmart.war" -Force -ErrorAction SilentlyContinue

# Xóa folder extract cũ
Remove-Item "C:\Program Files\Apache\Tomcat9\webapps\finsmart" -Recurse -Force -ErrorAction SilentlyContinue

# Copy WAR file mới
Copy-Item "D:\PTIT\Kì 6\OOP\finsmart-project\target\finsmart.war" `
          "C:\Program Files\Apache\Tomcat9\webapps\finsmart.war" `
          -Force
```
Start Tomcat  
```
cd "C:\Program Files\Apache\Tomcat9\bin"
.\startup.bat
```

### Quick Start: 
```
# 1. Về project folder  
cd "D:\PTIT\Kì 6\OOP\finsmart-project"
# 2. Clean và build
>> mvn clean package -DskipTests
```
Tiếp
```
# 4. Copy fresh WAR
Copy-Item "D:\PTIT\Kì 6\OOP\finsmart-project\target\finsmart.war" ` "C:\Program Files\Apache\Tomcat9\webapps\finsmart.war" -Force
 # 5. Wait for extraction
Start-Sleep -Seconds 10 
# 6. Start Tomcat
.\startup.bat
Start-Sleep -Seconds 15
```
