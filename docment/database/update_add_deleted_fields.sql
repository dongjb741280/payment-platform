-- 为现有表添加逻辑删除字段的更新脚本
USE payment_platform;

-- 为用户表添加deleted字段
ALTER TABLE t_user ADD COLUMN deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识：0-未删除，1-已删除';
ALTER TABLE t_user ADD INDEX idx_deleted (deleted);

-- 为商户表添加deleted字段
ALTER TABLE t_merchant ADD COLUMN deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识：0-未删除，1-已删除';
ALTER TABLE t_merchant ADD INDEX idx_deleted (deleted);

-- 为账户表添加deleted字段
ALTER TABLE t_account ADD COLUMN deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识：0-未删除，1-已删除';
ALTER TABLE t_account ADD INDEX idx_deleted (deleted);

-- 显示更新结果
SELECT 'Database update completed successfully' as result;
