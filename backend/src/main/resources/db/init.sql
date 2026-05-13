-- 网上商城数据库初始化脚本
-- 运行前先创建数据库: CREATE DATABASE IF NOT EXISTS shop_db DEFAULT CHARSET utf8mb4;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(50),
    email VARCHAR(100),
    phone VARCHAR(20),
    avatar VARCHAR(255),
    role TINYINT NOT NULL DEFAULT 1 COMMENT '0=admin, 1=user',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '0=disabled, 1=active',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    parent_id BIGINT DEFAULT 0,
    sort_order INT DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    category_id BIGINT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    main_image VARCHAR(255),
    images TEXT COMMENT 'JSON array of image URLs',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '0=off-shelf, 1=on-shelf',
    sales INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS cart_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    selected TINYINT NOT NULL DEFAULT 1 COMMENT '0=unchecked, 1=checked',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_product (user_id, product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(32) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status TINYINT NOT NULL DEFAULT 0 COMMENT '0=PENDING,1=PAID,2=SHIPPED,3=DELIVERED,4=COMPLETED,-1=CANCELLED',
    receiver_name VARCHAR(50) NOT NULL,
    receiver_phone VARCHAR(20) NOT NULL,
    receiver_address VARCHAR(255) NOT NULL,
    pay_time DATETIME,
    ship_time DATETIME,
    deliver_time DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    product_image VARCHAR(255),
    price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL,
    total_price DECIMAL(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ===== 初始化数据 =====

-- 管理员: admin / admin123 (BCrypt: $2a$10$IFEiLYTdj7yxBzXPM.NWnu6BUUWpkLxK7DY11aqPqjwHHxlfhdQve)
INSERT IGNORE INTO users (id, username, password, nickname, role, status) VALUES
(1, 'admin', '$2a$10$IFEiLYTdj7yxBzXPM.NWnu6BUUWpkLxK7DY11aqPqjwHHxlfhdQve', '系统管理员', 0, 1),
(2, 'user1', '$2a$10$IFEiLYTdj7yxBzXPM.NWnu6BUUWpkLxK7DY11aqPqjwHHxlfhdQve', '测试用户', 1, 1);

-- 分类
INSERT IGNORE INTO categories (id, name, parent_id, sort_order) VALUES
(1, '电子产品', 0, 1), (2, '服装鞋帽', 0, 2), (3, '图书教育', 0, 3), (4, '居家生活', 0, 4),
(5, '手机', 1, 1), (6, '电脑', 1, 2), (7, '耳机音箱', 1, 3),
(8, '男装', 2, 1), (9, '女装', 2, 2);

-- 商品
INSERT IGNORE INTO products (id, name, description, category_id, price, stock, status, sales, main_image) VALUES
(1, 'iPhone 15 智能手机', '全新 A16 芯片，4800 万像素主摄', 5, 5999.00, 100, 1, 256, '/uploads/phone.svg'),
(2, '华为 MateBook X Pro', '13代酷睿 i7，3K OLED 触控屏', 6, 8999.00, 50, 1, 128, '/uploads/laptop.svg'),
(3, 'Sony WH-1000XM5 降噪耳机', '行业领先降噪，30小时续航', 7, 2499.00, 200, 1, 512, '/uploads/headphone.svg'),
(4, '男士商务休闲夹克', '纯棉面料，韩版修身', 8, 299.00, 300, 1, 89, '/uploads/jacket.svg'),
(5, '女士碎花连衣裙', '雪纺面料，夏季新款', 9, 199.00, 500, 1, 156, '/uploads/dress.svg'),
(6, '深入理解计算机系统（第3版）', 'CSAPP 经典教材，程序员必读', 3, 139.00, 1000, 1, 1024, '/uploads/book.svg'),
(7, '小米智能音箱 Pro', '支持语音控制，HomeKit 适配', 7, 399.00, 80, 1, 320, '/uploads/speaker.svg'),
(8, 'iPad Air M2', '10.9英寸全面屏，M2 芯片加持', 5, 4399.00, 60, 1, 78, '/uploads/tablet.svg'),
(9, '简约台灯 LED 护眼', '三档色温，无频闪护眼台灯', 4, 89.00, 400, 1, 234, '/uploads/lamp.svg'),
(10, '无线蓝牙键盘', '超薄便携，兼容多设备', 7, 159.00, 180, 1, 421, '/uploads/keyboard.svg');
