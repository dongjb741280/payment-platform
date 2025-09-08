# 支付系统 Swagger API 文档

## 概述

本支付系统已全面集成 Swagger 支持，为所有微服务提供完整的 API 文档和在线测试功能。

## 服务端口和 Swagger 访问地址

| 服务名称 | 端口 | Swagger UI 地址 | API 文档地址 |
|---------|------|----------------|-------------|
| 用户服务 (payment-user) | 8081 | http://localhost:8081/payment-user/swagger-ui/index.html | http://localhost:8081/payment-user/v2/api-docs |
| 商户服务 (payment-merchant) | 8082 | http://localhost:8082/swagger-ui/index.html | http://localhost:8082/v2/api-docs |
| 账户服务 (payment-account) | 8083 | http://localhost:8083/swagger-ui/index.html | http://localhost:8083/v2/api-docs |
| 收单服务 (payment-acquiring) | 8084 | http://localhost:8084/swagger-ui/index.html | http://localhost:8084/v2/api-docs |
| 收银台服务 (payment-checkout) | 8085 | http://localhost:8085/swagger-ui/index.html | http://localhost:8085/v2/api-docs |
| 支付引擎服务 (payment-engine) | 8086 | http://localhost:8086/swagger-ui/index.html | http://localhost:8086/v2/api-docs |

## 功能特性

### 1. 完整的 API 文档
- 所有 REST API 接口都有详细的文档说明
- 包含请求参数、响应格式、错误码等信息
- 支持中文描述，便于理解

### 2. 在线测试功能
- 直接在 Swagger UI 中测试 API 接口
- 支持各种 HTTP 方法（GET、POST、PUT、DELETE）
- 自动生成请求示例

### 3. 接口分组
- 按服务模块分组展示 API
- 每个服务都有独立的 Swagger 配置
- 清晰的接口分类和标签

## 主要 API 模块

### 用户管理 (payment-user)
- 用户注册、登录、信息查询
- 用户状态管理（冻结、解冻）
- 密码修改、信息更新

### 商户管理 (payment-merchant)
- 商户注册、信息管理
- 商户状态控制（激活、停用）
- 商户信息查询和更新

### 账户管理 (payment-account)
- 账户创建、查询、删除
- 余额操作（充值、扣款）
- 账户冻结、解冻功能

### 交易订单管理 (payment-acquiring)
- 交易订单创建、查询
- 订单状态管理
- 商户订单分页查询

### 收银台管理 (payment-checkout)
- 支付方式咨询
- 支付受理和结果查询
- 订单管理和关闭

### 支付引擎管理 (payment-engine)
- 支付受理处理
- 支付结果查询
- 支付状态管理

## 使用说明

1. **启动服务**
   ```bash
   # 启动所有微服务
   ./start.sh
   ```

2. **访问 Swagger UI**
   - 打开浏览器访问上述任意一个 Swagger UI 地址
   - 例如：http://localhost:8081/payment-user/swagger-ui.html

3. **测试 API**
   - 在 Swagger UI 中选择要测试的接口
   - 点击 "Try it out" 按钮
   - 填写请求参数
   - 点击 "Execute" 执行请求

4. **查看 API 文档**
   - 访问对应的 API 文档地址获取 JSON 格式的文档
   - 可用于生成客户端 SDK 或集成到其他工具

## 技术实现

### 依赖配置
- 使用 SpringFox 3.0.0 版本
- 集成到 payment-common 公共模块
- 各微服务通过依赖继承获得 Swagger 支持

### 注解使用
- `@Api`: 标记控制器类，设置分组标签
- `@ApiOperation`: 标记接口方法，设置接口描述
- `@ApiParam`: 标记参数，设置参数描述

### 配置说明
- 每个微服务都有独立的 Swagger 配置类
- 支持自定义 API 信息和扫描包路径
- 统一的 Swagger UI 访问路径配置

## 注意事项

1. **服务启动顺序**
   - 确保数据库和 Redis 服务已启动
   - 按依赖关系启动各微服务

2. **网络访问**
   - 确保防火墙允许对应端口访问
   - 本地开发环境默认允许所有访问

3. **API 测试**
   - 测试前确保相关数据已准备
   - 注意接口的依赖关系和数据一致性

## 扩展功能

### 自定义配置
可以通过修改各服务的 `SwaggerConfig.java` 文件来自定义：
- API 文档标题和描述
- 扫描的包路径
- 接口分组规则

### 安全配置
生产环境建议：
- 限制 Swagger UI 的访问权限
- 使用认证机制保护 API 文档
- 禁用或限制在线测试功能

## 故障排除

### 常见问题
1. **Swagger UI 无法访问**
   - 检查服务是否正常启动
   - 确认端口配置是否正确
   - 查看服务日志排查错误

2. **API 文档不完整**
   - 检查 Swagger 注解是否正确添加
   - 确认扫描包路径配置
   - 验证依赖是否正确引入

3. **接口测试失败**
   - 检查请求参数格式
   - 确认服务间调用配置
   - 查看服务日志获取详细错误信息

---

通过 Swagger 集成，开发团队可以更高效地进行 API 开发和测试，提升整体开发效率和代码质量。
