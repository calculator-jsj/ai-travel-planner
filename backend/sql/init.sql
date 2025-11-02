-- AI 旅行规划师数据库初始化脚本
-- 数据库: ai_travel
-- 字符集: utf8mb4

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码',
  `email` VARCHAR(100) COMMENT '邮箱',
  `avatar` VARCHAR(255) COMMENT '头像URL',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 行程表
CREATE TABLE IF NOT EXISTS `plan` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `title` VARCHAR(200) NOT NULL COMMENT '行程标题',
  `start_date` DATE COMMENT '开始日期',
  `end_date` DATE COMMENT '结束日期',
  `days` INT DEFAULT 1 COMMENT '天数',
  `budget` DECIMAL(10,2) DEFAULT 0.00 COMMENT '预算',
  `destination` VARCHAR(200) COMMENT '目的地',
  `people` INT DEFAULT 1 COMMENT '人数',
  `preferences` TEXT COMMENT '偏好（JSON格式）',
  `plan_data` TEXT COMMENT '行程数据（JSON格式）',
  `status` INT DEFAULT 1 COMMENT '状态：1-进行中，2-已完成，3-已取消',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_status` (`status`),
  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='行程表';

-- 景点表
CREATE TABLE IF NOT EXISTS `spot` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `plan_id` BIGINT NOT NULL COMMENT '行程ID',
  `day` INT NOT NULL COMMENT '第几天',
  `name` VARCHAR(200) NOT NULL COMMENT '景点名称',
  `lng` DECIMAL(10,7) COMMENT '经度',
  `lat` DECIMAL(10,7) COMMENT '纬度',
  `type` VARCHAR(50) COMMENT '类型（景点/餐饮/住宿/交通）',
  `description` TEXT COMMENT '描述',
  `order_index` INT DEFAULT 0 COMMENT '排序索引',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_plan_id` (`plan_id`),
  INDEX `idx_day` (`day`),
  FOREIGN KEY (`plan_id`) REFERENCES `plan`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='景点表';

-- 预算表
CREATE TABLE IF NOT EXISTS `budget` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `plan_id` BIGINT NOT NULL COMMENT '行程ID',
  `category` VARCHAR(50) NOT NULL COMMENT '类别（餐饮/住宿/交通/购物/娱乐/门票/其他）',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '金额',
  `remark` VARCHAR(500) COMMENT '备注',
  `expense_date` DATE COMMENT '支出日期',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_plan_id` (`plan_id`),
  INDEX `idx_category` (`category`),
  INDEX `idx_expense_date` (`expense_date`),
  FOREIGN KEY (`plan_id`) REFERENCES `plan`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预算表';

-- 用户偏好表
CREATE TABLE IF NOT EXISTS `user_preference` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL UNIQUE COMMENT '用户ID',
  `preferences` TEXT COMMENT '偏好列表（JSON格式）',
  `travel_style` VARCHAR(50) COMMENT '旅行风格',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_user_id` (`user_id`),
  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户偏好表';

-- 插入测试数据（可选）
-- 注意：实际部署时请删除或修改此部分

-- 测试用户（密码：123456，实际使用时请使用加密后的密码）
-- INSERT INTO `user` (`username`, `password`, `email`) VALUES
-- ('test', '123456', 'test@example.com');

