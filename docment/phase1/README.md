# Payment Platform - Phase 1

## 项目概述

这是支付平台项目的第一阶段实现，主要完成基础架构搭建和核心服务开发。

## 第一阶段完成内容

### ✅ 已完成功能

1. **项目框架搭建**
   - Maven多模块项目结构
   - Spring Boot 3.2.0 + Spring Cloud 2023.0.0
   - 统一依赖管理和版本控制

2. **公共组件开发**
   - 业务ID生成器（32位业务ID）
   - 状态机框架
   - 统一响应结果类
   - 全局异常处理器
   - 业务异常类

3. **数据库设计**
   - 完整的数据库表结构设计
   - 初始化脚本
   - 索引优化
   - 存储过程和触发器

4. **用户服务开发**
   - 用户注册、查询、更新、删除
   - 用户状态管理（冻结/解冻）
   - 密码管理
   - 数据脱敏
   - 缓存集成

5. **开发环境搭建**
   - Docker Compose环境配置
   - MySQL、Redis、Nacos、Kafka等基础设施
   - Prometheus + Grafana监控
   - 启动和停止脚本

## 技术栈

- **后端框架**: Spring Boot 3.2.0, Spring Cloud 2023.0.0
- **数据库**: MySQL 8.0, MyBatis Plus 3.5.4
- **缓存**: Redis 7.x
- **注册中心**: Nacos 2.2.0
- **消息队列**: Kafka 3.6.0
- **监控**: Prometheus + Grafana
- **容器化**: Docker + Docker Compose
- **构建工具**: Maven 3.8+

## 项目结构

```
payment-platform/
├── payment-common/          # 公共组件模块
├── payment-user/           # 用户服务模块
├── database/               # 数据库脚本
├── docker-compose.yml      # 容器编排配置
├── start.sh               # 启动脚本
├── stop.sh                # 停止脚本
└── README.md              # 项目说明
```

## 快速开始

### 环境要求

- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- MySQL 8.0
- Redis 7.x

### 启动步骤

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd payment-platform
   ```

2. **启动基础设施服务**
   ```bash
   chmod +x start.sh
   ./start.sh
   ```

3. **验证服务状态**
   - MySQL: localhost:3306
   - Redis: localhost:6379
   - Nacos: http://localhost:8848/nacos
   - 用户服务: http://localhost:8081/payment-user

### 停止服务

```bash
chmod +x stop.sh
./stop.sh
```

## API文档

### 用户服务API

#### 用户注册
```http
POST /api/user/register
Content-Type: application/json

{
    "username": "testuser",
    "password": "123456",
    "confirmPassword": "123456",
    "realName": "测试用户",
    "phone": "13800138000",
    "email": "test@example.com"
}
```

#### 查询用户
```http
GET /api/user/{userId}
```

#### 分页查询用户
```http
GET /api/user/page?pageNum=1&pageSize=20
```

#### 更新用户信息
```http
PUT /api/user/{userId}
Content-Type: application/json

{
    "realName": "新姓名",
    "phone": "13900139000",
    "email": "new@example.com"
}
```

#### 冻结/解冻用户
```http
PUT /api/user/{userId}/freeze
PUT /api/user/{userId}/unfreeze
```

## 数据库设计

### 核心表结构

- `t_user` - 用户表
- `t_merchant` - 商户表
- `t_account` - 账户表
- `t_account_flow` - 账户流水表
- `t_trade_order` - 交易订单表
- `t_payment_main` - 支付主单表
- `t_payment_detail` - 支付明细表
- `t_channel_config` - 渠道配置表
- `t_idempotent` - 幂等表
- `t_key_config` - 密钥配置表
- `t_system_config` - 系统配置表

### 业务ID设计

32位业务ID格式：
```
YYYYMMDD + 数据版本(1位) + 系统版本(1位) + 系统标识(3位) + 业务标识(2位) + 
机房位(2位) + 分库位(2位) + 分表位(2位) + 环境位(1位) + 预留位(2位) + 序列号(8位)
```

## 监控和运维

### 监控地址

- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin)
- **Nacos控制台**: http://localhost:8848/nacos (nacos/nacos)

### 日志文件

- 用户服务日志: `logs/user-service.log`
- 应用日志: `logs/payment-user.log`

### 健康检查

```http
GET /actuator/health
```

## 开发指南

### 代码规范

1. 使用Lombok减少样板代码
2. 统一异常处理
3. 参数校验使用Bean Validation
4. 数据库操作使用MyBatis Plus
5. 缓存使用Redis
6. 日志使用SLF4J + Logback

### 测试

```bash
# 运行单元测试
mvn test

# 运行集成测试
mvn verify
```

### 构建

```bash
# 清理并构建
mvn clean package

# 跳过测试构建
mvn clean package -DskipTests
```

## 下一步计划
 
## 标准响应与错误码

接口统一返回 `Result<T>`：
```json
{
  "code": "0000",
  "message": "操作成功",
  "data": { },
  "requestId": "",
  "timestamp": 1710000000000
}
```

常见错误码：
- 0000：成功
- 3000：业务错误
- 4000：参数错误
- 5000：系统错误

国际化消息：
- 通过 `Accept-Language` 头切换中英文（默认英文，`zh-CN` 中文）。
- 常用键：`order.not.exist`、`order.status.invalid`、`order.owner.mismatch`、`risk.denied`。

### 第二阶段：核心支付功能 (6-8周)

- [ ] 收单平台开发
- [ ] 收银台开发
- [ ] 支付引擎开发
- [ ] 基础支付流程实现
- [ ] 状态机实现

## 第二阶段联调指南（端到端演示）

以下步骤演示“收单 → 收银台 → 引擎 → 轮询结果”的闭环流程（已内置简单模拟器，约每2分钟将支付中订单置为成功）。

### 服务端口
- 收单服务 `payment-acquiring`: http://localhost:8083
- 收银台服务 `payment-checkout`: http://localhost:8084
- 支付引擎 `payment-engine`: http://localhost:8085

请确保本地 MySQL/Redis 已启动，且使用了 `database/init.sql` 初始化库表。

### 健康与版本检查

- 收单健康检查：`GET http://localhost:8083/api/acquiring/health`
- 收银台健康检查：`GET http://localhost:8084/api/checkout/health`
- 引擎健康检查：`GET http://localhost:8085/api/engine/health`

- 收银台版本：`GET http://localhost:8084/api/checkout/version`
- 引擎版本：`GET http://localhost:8085/api/engine/version`
 - 收单版本：`GET http://localhost:8083/api/acquiring/version`

### 切换响应语言（Accept-Language）

在请求头中设置：
```bash
curl -H 'Accept-Language: zh-CN' "http://localhost:8084/api/checkout/accept?tradeOrderNo=xxx&paymentMethod=BALANCE"
curl -H 'Accept-Language: en'    "http://localhost:8084/api/checkout/accept?tradeOrderNo=xxx&paymentMethod=BALANCE"
```

### 1. 创建交易订单（收单）
```bash
curl -X POST http://localhost:8083/api/acquiring/order/create \
  -H 'Content-Type: application/json' \
  -d '{
        "merchantId": "M0001",
        "merchantOrderNo": "MO202409010001",
        "userId": "U0001",
        "amount":  "100.00",
        "currency": "CNY",
        "subject":  "测试订单",
        "body":     "一笔用于联调演示的订单"
      }'
```
响应示例（摘录）：
```json
{
  "code": "0000",
  "data": {
    "tradeOrderNo": "...",
    "status": "INIT"
  }
}
```

### 2. 收银台咨询支付方式（排序已内置）
```bash
curl "http://localhost:8084/api/checkout/consult?tradeOrderNo=<替换为上一步的tradeOrderNo>"
```

### 3. 受理支付（触发引擎受理）
```bash
curl -X POST "http://localhost:8084/api/checkout/accept?tradeOrderNo=<tradeOrderNo>&paymentMethod=BALANCE"
```
受理后：
- 引擎会创建 `t_payment_main/t_payment_detail`，并将主单置为 `PAYING`
- 收单的 `t_trade_order.status` 被置为 `PAYING`

### 4. 轮询支付结果（收银台转发引擎结果）
```bash
curl "http://localhost:8084/api/checkout/poll?tradeOrderNo=<tradeOrderNo>"
```
默认模拟器每2分钟执行一次，将 `PAYING` → `PAID/SUCCESS`：
- 引擎：`t_payment_main/t_payment_detail` 状态置为 `PAID`
- 收单：`t_trade_order.status` 置为 `SUCCESS`

如需快速观察，可多次轮询，直至返回 `PAID`。

### 5. 关闭订单（可选）
```bash
curl -X POST "http://localhost:8083/api/acquiring/order/<tradeOrderNo>/close"
```

### 常见问题
- 如果接口报错或查询为空，请确认：
  1) 服务是否都已启动；2) 数据库连接是否可用；3) 是否已执行 `init.sql`；
  4) `application.yml` 中端口/数据源/Redis 配置与本地一致。

### 第三阶段：渠道对接 (4-6周)

- [ ] 渠道网关开发
- [ ] 主要支付渠道对接
- [ ] 报文网关开发
- [ ] 渠道路由实现

## 常见问题

### Q: 如何修改数据库连接配置？

A: 修改 `payment-user/src/main/resources/application.yml` 中的数据库配置。

### Q: 如何添加新的API接口？

A: 在对应的Controller类中添加新的方法，使用Spring MVC注解。

### Q: 如何添加新的数据库表？

A: 在 `database/init.sql` 中添加建表语句，然后创建对应的Entity和Mapper。

### Q: 如何查看服务日志？

A: 查看 `logs/user-service.log` 文件，或使用 `docker logs payment-mysql` 等命令。

## 联系方式

如有问题，请联系开发团队。

---

**注意**: 这是第一阶段的基础实现，后续阶段会逐步完善支付功能。
