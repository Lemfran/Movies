-- CUIT 影评系统数据库建表脚本
-- 数据库: yingpingxitong
-- 字符集: utf8mb4

CREATE DATABASE IF NOT EXISTS yingpingxitong
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE yingpingxitong;

-- ==================== movies 表 ====================
-- 电影信息表
CREATE TABLE IF NOT EXISTS movies (
    movieId INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    releaseDate DATE,
    runtime INT,
    posterImage VARCHAR(500),
    averageScore DOUBLE DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==================== users 表 ====================
-- 用户信息表
CREATE TABLE IF NOT EXISTS users (
    userId INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL UNIQUE,
    nickname VARCHAR(100),
    avatar VARCHAR(500),
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    manager TINYINT DEFAULT 0 COMMENT '是否为管理员: 0-否, 1-是',
    permission TINYINT DEFAULT 0 COMMENT '审核权限: 0-未授权, 1-已授权'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==================== reviews 表 ====================
-- 影评表（支持一级回复: parent_id 指向原评论ID）
CREATE TABLE IF NOT EXISTS reviews (
    reviewId INT PRIMARY KEY AUTO_INCREMENT,
    movieId INT,
    userId INT,
    content TEXT,
    score INT COMMENT '评分 1-5',
    parent_id INT DEFAULT NULL COMMENT '父评论ID, NULL表示顶级影评',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    createdAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_movieId (movieId),
    INDEX idx_userId (userId),
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==================== review_likes 表 ====================
-- 评论点赞记录表（联合主键保证同一用户只能点赞一次）
CREATE TABLE IF NOT EXISTS review_likes (
    review_id INT NOT NULL,
    user_id INT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (review_id, user_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==================== logs 表 ====================
-- 系统操作日志表（程序运行时自动写入）
CREATE TABLE IF NOT EXISTS logs (
    id INT PRIMARY KEY AUTO_INCREMENT,
    methodName VARCHAR(255) COMMENT '操作方法',
    userName VARCHAR(100) COMMENT '操作用户',
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
