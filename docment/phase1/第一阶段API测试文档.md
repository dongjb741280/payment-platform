# 第一阶段API测试文档

## 概述

本文档提供了第一阶段支付平台基础服务的API测试指南，包括用户服务、商户服务和账户服务的完整测试用例。

## 服务端口

- **用户服务**: http://localhost:8081
- **商户服务**: http://localhost:8082  
- **账户服务**: http://localhost:8083

## 1. 用户服务API测试

### 1.1 用户注册

**请求**
```bash
curl -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "123456",
    "email": "test@example.com",
    "phone": "13800138000"
  }'
```

**响应**
```json
{
  "success": true,
  "code": "200",
  "message": "操作成功",
  "data": {
    "userId": "USR_20241201_001",
    "username": "testuser",
    "email": "test@example.com",
    "phone": "13800138000",
    "status": "ACTIVE"
  }
}
```

### 1.2 获取用户信息

**请求**
```bash
curl -X GET http://localhost:8081/api/users/USR_20241201_001
```

**响应**
```json
{
  "success": true,
  "code": "200",
  "message": "操作成功",
  "data": {
    "userId": "USR_20241201_001",
    "username": "testuser",
    "email": "test@example.com",
    "phone": "13800138000",
    "status": "ACTIVE"
  }
}
```

### 1.3 更新用户信息

**请求**
```bash
curl -X PUT http://localhost:8081/api/users/USR_20241201_001 \
  -H "Content-Type: application/json" \
  -d '{
    "email": "newemail@example.com",
    "phone": "13900139000"
  }'
```

### 1.4 删除用户

**请求**
```bash
curl -X DELETE http://localhost:8081/api/users/USR_20241201_001
```

## 2. 商户服务API测试

### 2.1 商户注册

**请求**
```bash
curl -X POST http://localhost:8082/api/merchants/register \
  -H "Content-Type: application/json" \
  -d '{
    "merchantName": "测试商户",
    "merchantCode": "TEST_MERCHANT_001",
    "contactName": "张三",
    "contactPhone": "13800138000",
    "contactEmail": "merchant@example.com",
    "businessLicense": "91110000000000000X",
    "legalPerson": "李四",
    "address": "北京市朝阳区测试路123号"
  }'
```

**响应**
```json
{
  "success": true,
  "code": "200",
  "message": "操作成功",
  "data": {
    "merchantId": "MCH_20241201_001",
    "merchantName": "测试商户",
    "merchantCode": "TEST_MERCHANT_001",
    "contactName": "张三",
    "contactPhone": "13800138000",
    "contactEmail": "merchant@example.com",
    "businessLicense": "91110000000000000X",
    "legalPerson": "李四",
    "address": "北京市朝阳区测试路123号",
    "status": "INACTIVE"
  }
}
```

### 2.2 获取商户信息

**请求**
```bash
curl -X GET http://localhost:8082/api/merchants/MCH_20241201_001
```

### 2.3 根据商户编码获取商户信息

**请求**
```bash
curl -X GET http://localhost:8082/api/merchants/code/TEST_MERCHANT_001
```

### 2.4 激活商户

**请求**
```bash
curl -X POST http://localhost:8082/api/merchants/MCH_20241201_001/activate
```

### 2.5 更新商户信息

**请求**
```bash
curl -X PUT http://localhost:8082/api/merchants/MCH_20241201_001 \
  -H "Content-Type: application/json" \
  -d '{
    "merchantName": "更新后的商户名称",
    "contactPhone": "13900139000"
  }'
```

### 2.6 停用商户

**请求**
```bash
curl -X POST http://localhost:8082/api/merchants/MCH_20241201_001/deactivate
```

## 3. 账户服务API测试

### 3.1 创建个人账户

**请求**
```bash
curl -X POST http://localhost:8083/api/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "USR_20241201_001",
    "accountType": "PERSONAL",
    "currency": "CNY"
  }'
```

**响应**
```json
{
  "success": true,
  "code": "200",
  "message": "操作成功",
  "data": {
    "accountId": "ACC_20241201_001",
    "userId": "USR_20241201_001",
    "merchantId": null,
    "accountType": "PERSONAL",
    "currency": "CNY",
    "status": "ACTIVE",
    "availableBalance": 0.00,
    "frozenBalance": 0.00,
    "totalBalance": 0.00
  }
}
```

### 3.2 创建商户账户

**请求**
```bash
curl -X POST http://localhost:8083/api/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "USR_20241201_001",
    "merchantId": "MCH_20241201_001",
    "accountType": "MERCHANT",
    "currency": "CNY"
  }'
```

### 3.3 获取账户信息

**请求**
```bash
curl -X GET http://localhost:8083/api/accounts/ACC_20241201_001
```

### 3.4 根据用户ID获取账户列表

**请求**
```bash
curl -X GET http://localhost:8083/api/accounts/user/USR_20241201_001
```

### 3.5 根据商户ID获取账户列表

**请求**
```bash
curl -X GET http://localhost:8083/api/accounts/merchant/MCH_20241201_001
```

### 3.6 账户充值

**请求**
```bash
curl -X POST http://localhost:8083/api/accounts/deposit \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": "ACC_20241201_001",
    "amount": 1000.00,
    "transactionType": "DEPOSIT",
    "remark": "账户充值"
  }'
```

**响应**
```json
{
  "success": true,
  "code": "200",
  "message": "操作成功",
  "data": true
}
```

### 3.7 账户扣款

**请求**
```bash
curl -X POST http://localhost:8083/api/accounts/withdraw \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": "ACC_20241201_001",
    "amount": 100.00,
    "transactionType": "WITHDRAW",
    "remark": "账户扣款"
  }'
```

### 3.8 账户冻结

**请求**
```bash
curl -X POST "http://localhost:8083/api/accounts/ACC_20241201_001/freeze?amount=50.00"
```

### 3.9 账户解冻

**请求**
```bash
curl -X POST "http://localhost:8083/api/accounts/ACC_20241201_001/unfreeze?amount=50.00"
```

### 3.10 获取账户余额

**请求**
```bash
curl -X GET http://localhost:8083/api/accounts/ACC_20241201_001/balance
```

**响应**
```json
{
  "success": true,
  "code": "200",
  "message": "操作成功",
  "data": {
    "accountId": "ACC_20241201_001",
    "userId": "USR_20241201_001",
    "merchantId": null,
    "accountType": "PERSONAL",
    "currency": "CNY",
    "status": "ACTIVE",
    "availableBalance": 850.00,
    "frozenBalance": 50.00,
    "totalBalance": 900.00
  }
}
```

## 4. 完整业务流程测试

### 4.1 用户注册 -> 创建账户 -> 充值 -> 扣款流程

```bash
# 1. 用户注册
curl -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser2",
    "password": "123456",
    "email": "test2@example.com",
    "phone": "13800138001"
  }'

# 2. 创建个人账户
curl -X POST http://localhost:8083/api/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "USR_20241201_002",
    "accountType": "PERSONAL",
    "currency": "CNY"
  }'

# 3. 账户充值
curl -X POST http://localhost:8083/api/accounts/deposit \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": "ACC_20241201_002",
    "amount": 2000.00,
    "transactionType": "DEPOSIT",
    "remark": "初始充值"
  }'

# 4. 账户扣款
curl -X POST http://localhost:8083/api/accounts/withdraw \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": "ACC_20241201_002",
    "amount": 500.00,
    "transactionType": "WITHDRAW",
    "remark": "消费扣款"
  }'

# 5. 查看最终余额
curl -X GET http://localhost:8083/api/accounts/ACC_20241201_002/balance
```

### 4.2 商户注册 -> 创建商户账户 -> 激活流程

```bash
# 1. 商户注册
curl -X POST http://localhost:8082/api/merchants/register \
  -H "Content-Type: application/json" \
  -d '{
    "merchantName": "测试商户2",
    "merchantCode": "TEST_MERCHANT_002",
    "contactName": "王五",
    "contactPhone": "13800138002",
    "contactEmail": "merchant2@example.com",
    "businessLicense": "91110000000000001X",
    "legalPerson": "赵六",
    "address": "上海市浦东新区测试路456号"
  }'

# 2. 创建商户账户
curl -X POST http://localhost:8083/api/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "USR_20241201_002",
    "merchantId": "MCH_20241201_002",
    "accountType": "MERCHANT",
    "currency": "CNY"
  }'

# 3. 激活商户
curl -X POST http://localhost:8082/api/merchants/MCH_20241201_002/activate

# 4. 查看商户账户
curl -X GET http://localhost:8083/api/accounts/merchant/MCH_20241201_002
```

## 5. 错误处理测试

### 5.1 重复注册测试

```bash
# 尝试用相同的用户名注册
curl -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "123456",
    "email": "test3@example.com",
    "phone": "13800138003"
  }'
```

**预期响应**
```json
{
  "success": false,
  "code": "USER_REGISTER_FAILED",
  "message": "用户名已存在"
}
```

### 5.2 余额不足测试

```bash
# 尝试扣款超过余额
curl -X POST http://localhost:8083/api/accounts/withdraw \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": "ACC_20241201_002",
    "amount": 2000.00,
    "transactionType": "WITHDRAW",
    "remark": "超额扣款测试"
  }'
```

**预期响应**
```json
{
  "success": false,
  "code": "ACCOUNT_WITHDRAW_FAILED",
  "message": "余额不足"
}
```

## 6. 性能测试

### 6.1 并发用户注册测试

```bash
# 使用Apache Bench进行并发测试
ab -n 100 -c 10 -H "Content-Type: application/json" \
  -p user_register.json \
  http://localhost:8081/api/users/register
```

### 6.2 并发账户操作测试

```bash
# 并发充值测试
ab -n 50 -c 5 -H "Content-Type: application/json" \
  -p deposit.json \
  http://localhost:8083/api/accounts/deposit
```

## 7. 监控和日志

### 7.1 查看服务状态

```bash
# 检查用户服务健康状态
curl http://localhost:8081/actuator/health

# 检查商户服务健康状态
curl http://localhost:8082/actuator/health

# 检查账户服务健康状态
curl http://localhost:8083/actuator/health
```

### 7.2 查看日志

```bash
# 查看用户服务日志
docker logs payment-user-1

# 查看商户服务日志
docker logs payment-merchant-1

# 查看账户服务日志
docker logs payment-account-1
```

## 8. 数据库验证

### 8.1 连接数据库

```bash
mysql -h localhost -P 3306 -u root -p123456 payment_platform
```

### 8.2 查看数据

```sql
-- 查看用户数据
SELECT * FROM t_user;

-- 查看商户数据
SELECT * FROM t_merchant;

-- 查看账户数据
SELECT * FROM t_account;

-- 查看账户余额数据
SELECT * FROM t_account_balance;

-- 查看账户流水数据
SELECT * FROM t_account_flow ORDER BY create_time DESC LIMIT 10;
```

## 总结

第一阶段的基础服务已经完成，包括：

1. **用户服务** - 用户注册、管理、认证
2. **商户服务** - 商户注册、管理、激活
3. **账户服务** - 账户创建、余额管理、流水记录

所有服务都提供了完整的REST API，支持CRUD操作，并具备完善的错误处理机制。接下来可以进入第二阶段的核心支付功能开发。
