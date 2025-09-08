-- 性能优化索引补充脚本（幂等执行：若已存在请忽略报错或手动检查）
USE payment_platform;

-- 加速按状态扫描支付明细（定时任务/批处理）
CREATE INDEX idx_payment_detail_status ON t_payment_detail(status);

-- 支付主单：按状态+时间的列表/统计
CREATE INDEX idx_payment_main_status_time ON t_payment_main(status, create_time);

-- 交易订单：按状态+时间的列表/统计
CREATE INDEX idx_trade_order_status_time ON t_trade_order(status, create_time);

-- 若已存在相同或等价索引，请忽略或先删除再创建：
-- DROP INDEX idx_payment_detail_status ON t_payment_detail;
-- DROP INDEX idx_payment_main_status_time ON t_payment_main;
-- DROP INDEX idx_trade_order_status_time ON t_trade_order;


