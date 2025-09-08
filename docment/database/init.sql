-- 支付平台数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS payment_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE payment_platform;

-- 用户表
CREATE TABLE t_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id VARCHAR(32) NOT NULL UNIQUE COMMENT '用户ID',
    username VARCHAR(64) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(128) NOT NULL COMMENT '密码',
    salt VARCHAR(32) NOT NULL COMMENT '盐值',
    real_name VARCHAR(64) COMMENT '真实姓名',
    id_card VARCHAR(18) COMMENT '身份证号',
    phone VARCHAR(11) COMMENT '手机号',
    email VARCHAR(128) COMMENT '邮箱',
    status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识：0-未删除，1-已删除',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_phone (phone),
    INDEX idx_email (email),
    INDEX idx_create_time (create_time),
    INDEX idx_deleted (deleted)
) COMMENT '用户表';

-- 商户表
CREATE TABLE t_merchant (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    merchant_id VARCHAR(32) NOT NULL UNIQUE COMMENT '商户ID',
    merchant_name VARCHAR(128) NOT NULL COMMENT '商户名称',
    merchant_code VARCHAR(64) NOT NULL UNIQUE COMMENT '商户编码',
    contact_name VARCHAR(64) COMMENT '联系人姓名',
    contact_phone VARCHAR(11) COMMENT '联系人手机',
    contact_email VARCHAR(128) COMMENT '联系人邮箱',
    business_license VARCHAR(64) COMMENT '营业执照号',
    legal_person VARCHAR(64) COMMENT '法人姓名',
    address TEXT COMMENT '地址',
    public_key TEXT COMMENT '公钥',
    private_key TEXT COMMENT '私钥',
    status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识：0-未删除，1-已删除',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_merchant_code (merchant_code),
    INDEX idx_merchant_name (merchant_name),
    INDEX idx_create_time (create_time),
    INDEX idx_deleted (deleted)
) COMMENT '商户表';

-- 账户表
CREATE TABLE t_account (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    account_no VARCHAR(32) NOT NULL UNIQUE COMMENT '账户号',
    account_type VARCHAR(16) NOT NULL COMMENT '账户类型',
    user_id VARCHAR(32) COMMENT '用户ID',
    merchant_id VARCHAR(32) COMMENT '商户ID',
    balance DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT '账户余额',
    frozen_amount DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT '冻结金额',
    available_amount DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT '可用余额',
    currency VARCHAR(3) NOT NULL DEFAULT 'CNY' COMMENT '币种',
    status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识：0-未删除，1-已删除',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_merchant_id (merchant_id),
    INDEX idx_account_type (account_type),
    INDEX idx_create_time (create_time),
    INDEX idx_deleted (deleted)
) COMMENT '账户表';

-- 账户流水表
CREATE TABLE t_account_flow (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    flow_no VARCHAR(32) NOT NULL UNIQUE COMMENT '流水号',
    account_no VARCHAR(32) NOT NULL COMMENT '账户号',
    transaction_type VARCHAR(16) NOT NULL COMMENT '交易类型',
    amount DECIMAL(15,2) NOT NULL COMMENT '交易金额',
    balance_before DECIMAL(15,2) NOT NULL COMMENT '交易前余额',
    balance_after DECIMAL(15,2) NOT NULL COMMENT '交易后余额',
    frozen_before DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT '交易前冻结金额',
    frozen_after DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT '交易后冻结金额',
    currency VARCHAR(3) NOT NULL DEFAULT 'CNY' COMMENT '币种',
    remark VARCHAR(255) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_account_no (account_no),
    INDEX idx_transaction_type (transaction_type),
    INDEX idx_create_time (create_time)
) COMMENT '账户流水表';

-- 交易订单表
CREATE TABLE t_trade_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    trade_order_no VARCHAR(32) NOT NULL UNIQUE COMMENT '交易订单号',
    merchant_id VARCHAR(32) NOT NULL COMMENT '商户ID',
    merchant_order_no VARCHAR(64) NOT NULL COMMENT '商户订单号',
    user_id VARCHAR(32) COMMENT '用户ID',
    amount DECIMAL(15,2) NOT NULL COMMENT '订单金额',
    currency VARCHAR(3) NOT NULL DEFAULT 'CNY' COMMENT '币种',
    subject VARCHAR(128) COMMENT '订单标题',
    body TEXT COMMENT '订单描述',
    status VARCHAR(16) NOT NULL DEFAULT 'INIT' COMMENT '订单状态',
    expire_time DATETIME COMMENT '过期时间',
    notify_url VARCHAR(255) COMMENT '通知地址',
    return_url VARCHAR(255) COMMENT '返回地址',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_merchant_order (merchant_id, merchant_order_no),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) COMMENT '交易订单表';

-- 支付主单表
CREATE TABLE t_payment_main (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    payment_main_no VARCHAR(32) NOT NULL UNIQUE COMMENT '支付主单号',
    trade_order_no VARCHAR(32) NOT NULL COMMENT '交易订单号',
    amount DECIMAL(15,2) NOT NULL COMMENT '支付金额',
    currency VARCHAR(3) NOT NULL DEFAULT 'CNY' COMMENT '币种',
    status VARCHAR(16) NOT NULL DEFAULT 'INIT' COMMENT '支付状态',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_trade_order (trade_order_no),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) COMMENT '支付主单表';

-- 支付明细表
CREATE TABLE t_payment_detail (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    payment_detail_no VARCHAR(32) NOT NULL UNIQUE COMMENT '支付明细号',
    payment_main_no VARCHAR(32) NOT NULL COMMENT '支付主单号',
    payment_method VARCHAR(16) NOT NULL COMMENT '支付方式',
    amount DECIMAL(15,2) NOT NULL COMMENT '支付金额',
    currency VARCHAR(3) NOT NULL DEFAULT 'CNY' COMMENT '币种',
    status VARCHAR(16) NOT NULL DEFAULT 'INIT' COMMENT '支付状态',
    channel_code VARCHAR(16) COMMENT '渠道编码',
    channel_order_no VARCHAR(64) COMMENT '渠道订单号',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_payment_main (payment_main_no),
    INDEX idx_payment_method (payment_method),
    INDEX idx_channel_code (channel_code),
    INDEX idx_create_time (create_time)
) COMMENT '支付明细表';

-- 渠道配置表
CREATE TABLE t_channel_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    channel_code VARCHAR(16) NOT NULL UNIQUE COMMENT '渠道编码',
    channel_name VARCHAR(64) NOT NULL COMMENT '渠道名称',
    channel_type VARCHAR(16) NOT NULL COMMENT '渠道类型',
    config_data TEXT COMMENT '配置数据',
    status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_channel_type (channel_type),
    INDEX idx_status (status)
) COMMENT '渠道配置表';

-- 幂等表
CREATE TABLE t_idempotent (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    unique_key VARCHAR(128) NOT NULL COMMENT '唯一键',
    app_name VARCHAR(32) NOT NULL COMMENT '应用名称',
    ext_info TEXT COMMENT '扩展信息',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_unique_key_app (unique_key, app_name),
    INDEX idx_app_name (app_name),
    INDEX idx_create_time (create_time)
) COMMENT '幂等表';

-- 密钥配置表
CREATE TABLE t_key_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    key_id VARCHAR(32) NOT NULL UNIQUE COMMENT '密钥ID',
    key_type VARCHAR(16) NOT NULL COMMENT '密钥类型',
    key_name VARCHAR(64) NOT NULL COMMENT '密钥名称',
    encrypted_key TEXT NOT NULL COMMENT '加密后的密钥',
    algorithm VARCHAR(32) NOT NULL COMMENT '加密算法',
    key_length INT NOT NULL COMMENT '密钥长度',
    status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_key_type (key_type),
    INDEX idx_status (status)
) COMMENT '密钥配置表';

-- 系统配置表
CREATE TABLE t_system_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    config_key VARCHAR(64) NOT NULL UNIQUE COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    config_desc VARCHAR(255) COMMENT '配置描述',
    config_type VARCHAR(16) NOT NULL DEFAULT 'STRING' COMMENT '配置类型',
    status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_config_type (config_type),
    INDEX idx_status (status)
) COMMENT '系统配置表';

-- 插入初始数据

-- 插入系统配置
INSERT INTO t_system_config (config_key, config_value, config_desc, config_type) VALUES
('system.name', 'Payment Platform', '系统名称', 'STRING'),
('system.version', '1.0.0', '系统版本', 'STRING'),
('system.env', 'test', '系统环境', 'STRING'),
('business.id.length', '32', '业务ID长度', 'INTEGER'),
('cache.default.expire', '3600', '默认缓存过期时间(秒)', 'INTEGER');

-- 插入渠道配置
INSERT INTO t_channel_config (channel_code, channel_name, channel_type, config_data) VALUES
('ALIPAY', '支付宝', 'ONLINE', '{"appId":"","privateKey":"","publicKey":"","gatewayUrl":""}'),
('WECHAT', '微信支付', 'ONLINE', '{"appId":"","mchId":"","apiKey":"","gatewayUrl":""}'),
('UNIONPAY', '银联支付', 'ONLINE', '{"merId":"","certId":"","gatewayUrl":""}'),
('BALANCE', '余额支付', 'BALANCE', '{}');

-- 插入密钥配置
INSERT INTO t_key_config (key_id, key_type, key_name, encrypted_key, algorithm, key_length) VALUES
('MASTER_KEY_001', 'MASTER', '主密钥', 'encrypted_master_key_data', 'AES', 256),
('WORKING_KEY_001', 'WORKING', '工作密钥', 'encrypted_working_key_data', 'AES', 256),
('SIGN_KEY_001', 'SIGN', '签名密钥', 'encrypted_sign_key_data', 'RSA', 2048);

-- 创建视图
CREATE VIEW v_account_balance AS
SELECT 
    a.account_no,
    a.account_type,
    a.user_id,
    a.merchant_id,
    a.balance,
    a.frozen_amount,
    a.available_amount,
    a.currency,
    a.status,
    a.create_time,
    a.update_time
FROM t_account a
WHERE a.status = 'ACTIVE';

-- 创建存储过程：更新账户余额
DELIMITER //
CREATE PROCEDURE UpdateAccountBalance(
    IN p_account_no VARCHAR(32),
    IN p_amount DECIMAL(15,2),
    IN p_transaction_type VARCHAR(16),
    IN p_remark VARCHAR(255),
    OUT p_result INT,
    OUT p_message VARCHAR(255)
)
BEGIN
    DECLARE v_balance_before DECIMAL(15,2) DEFAULT 0;
    DECLARE v_balance_after DECIMAL(15,2) DEFAULT 0;
    DECLARE v_frozen_before DECIMAL(15,2) DEFAULT 0;
    DECLARE v_frozen_after DECIMAL(15,2) DEFAULT 0;
    DECLARE v_flow_no VARCHAR(32);
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_result = -1;
        SET p_message = 'SQL异常';
    END;
    
    START TRANSACTION;
    
    -- 获取当前余额
    SELECT balance, frozen_amount INTO v_balance_before, v_frozen_before
    FROM t_account 
    WHERE account_no = p_account_no AND status = 'ACTIVE'
    FOR UPDATE;
    
    IF v_balance_before IS NULL THEN
        SET p_result = -1;
        SET p_message = '账户不存在或已冻结';
        ROLLBACK;
    ELSE
        -- 计算新余额
        IF p_transaction_type IN ('PAYMENT', 'WITHDRAW', 'TRANSFER_OUT') THEN
            SET v_balance_after = v_balance_before - p_amount;
        ELSEIF p_transaction_type IN ('RECHARGE', 'REFUND', 'TRANSFER_IN') THEN
            SET v_balance_after = v_balance_before + p_amount;
        ELSE
            SET p_result = -1;
            SET p_message = '不支持的交易类型';
            ROLLBACK;
        END IF;
        
        -- 检查余额是否足够
        IF v_balance_after < 0 THEN
            SET p_result = -1;
            SET p_message = '余额不足';
            ROLLBACK;
        ELSE
            -- 更新账户余额
            UPDATE t_account 
            SET balance = v_balance_after, 
                available_amount = v_balance_after - frozen_amount,
                update_time = NOW()
            WHERE account_no = p_account_no;
            
            -- 生成流水号
            SET v_flow_no = CONCAT('FL', DATE_FORMAT(NOW(), '%Y%m%d'), LPAD(LAST_INSERT_ID(), 8, '0'));
            
            -- 插入流水记录
            INSERT INTO t_account_flow (
                flow_no, account_no, transaction_type, amount,
                balance_before, balance_after, frozen_before, frozen_after,
                remark, create_time
            ) VALUES (
                v_flow_no, p_account_no, p_transaction_type, p_amount,
                v_balance_before, v_balance_after, v_frozen_before, v_frozen_after,
                p_remark, NOW()
            );
            
            SET p_result = 0;
            SET p_message = '操作成功';
            COMMIT;
        END IF;
    END IF;
END //
DELIMITER ;

-- 创建触发器：自动更新账户可用余额
DELIMITER //
CREATE TRIGGER tr_account_balance_update
BEFORE UPDATE ON t_account
FOR EACH ROW
BEGIN
    SET NEW.available_amount = NEW.balance - NEW.frozen_amount;
END //
DELIMITER ;

-- 创建索引优化
CREATE INDEX idx_trade_order_merchant_time ON t_trade_order(merchant_id, create_time);
CREATE INDEX idx_payment_main_trade_order ON t_payment_main(trade_order_no);
CREATE INDEX idx_account_flow_account_time ON t_account_flow(account_no, create_time);
CREATE INDEX idx_payment_detail_main_method ON t_payment_detail(payment_main_no, payment_method);

-- 创建分区表（按月分区）
-- ALTER TABLE t_account_flow PARTITION BY RANGE (YEAR(create_time) * 100 + MONTH(create_time)) (
--     PARTITION p202401 VALUES LESS THAN (202402),
--     PARTITION p202402 VALUES LESS THAN (202403),
--     PARTITION p202403 VALUES LESS THAN (202404),
--     PARTITION p202404 VALUES LESS THAN (202405),
--     PARTITION p202405 VALUES LESS THAN (202406),
--     PARTITION p202406 VALUES LESS THAN (202407),
--     PARTITION p202407 VALUES LESS THAN (202408),
--     PARTITION p202408 VALUES LESS THAN (202409),
--     PARTITION p202409 VALUES LESS THAN (202410),
--     PARTITION p202410 VALUES LESS THAN (202411),
--     PARTITION p202411 VALUES LESS THAN (202412),
--     PARTITION p202412 VALUES LESS THAN (202501),
--     PARTITION p_future VALUES LESS THAN MAXVALUE
-- );
