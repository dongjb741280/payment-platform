#!/bin/bash

# 支付平台第一阶段启动脚本

echo "=========================================="
echo "Payment Platform Phase 1 Startup Script"
echo "=========================================="

# 检查Docker是否运行
if ! docker info > /dev/null 2>&1; then
    echo "Error: Docker is not running. Please start Docker first."
    exit 1
fi

# 检查Java环境
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed or not in PATH."
    exit 1
fi

# 检查Maven环境
if ! command -v mvn &> /dev/null; then
    echo "Error: Maven is not installed or not in PATH."
    exit 1
fi

echo "Starting infrastructure services..."

# 启动基础设施服务
docker-compose up -d mysql redis nacos kafka prometheus grafana

echo "Waiting for services to be ready..."
sleep 30

# 检查服务状态
echo "Checking service status..."

# 检查MySQL
if docker exec payment-mysql mysqladmin ping -h localhost --silent; then
    echo "✓ MySQL is ready"
else
    echo "✗ MySQL is not ready"
    exit 1
fi

# 检查Redis
if docker exec payment-redis redis-cli ping | grep -q PONG; then
    echo "✓ Redis is ready"
else
    echo "✗ Redis is not ready"
    exit 1
fi

# 检查Nacos
if curl -s http://localhost:8848/nacos/v1/console/health/readiness | grep -q UP; then
    echo "✓ Nacos is ready"
else
    echo "✗ Nacos is not ready"
    exit 1
fi

echo "Building and starting user service..."

# 构建用户服务
cd payment-user
mvn clean package -DskipTests

if [ $? -eq 0 ]; then
    echo "✓ User service built successfully"
else
    echo "✗ User service build failed"
    exit 1
fi

# 启动用户服务
echo "Starting user service..."
nohup java -jar target/payment-user-1.0.0-SNAPSHOT.jar > ../logs/user-service.log 2>&1 &
USER_SERVICE_PID=$!

echo "User service started with PID: $USER_SERVICE_PID"
echo "Log file: logs/user-service.log"

# 等待服务启动
echo "Waiting for user service to start..."
sleep 20

# 检查用户服务健康状态
if curl -s http://localhost:8081/payment-user/actuator/health | grep -q UP; then
    echo "✓ User service is ready"
else
    echo "✗ User service is not ready"
    echo "Please check the log file: logs/user-service.log"
    exit 1
fi

echo "=========================================="
echo "Payment Platform Phase 1 Started Successfully!"
echo "=========================================="
echo "Services:"
echo "  - MySQL: localhost:3306"
echo "  - Redis: localhost:6379"
echo "  - Nacos: http://localhost:8848/nacos"
echo "  - Kafka: localhost:9092"
echo "  - Prometheus: http://localhost:9090"
echo "  - Grafana: http://localhost:3000 (admin/admin)"
echo "  - User Service: http://localhost:8081/payment-user"
echo ""
echo "API Documentation:"
echo "  - User Service API: http://localhost:8081/payment-user/swagger-ui.html"
echo ""
echo "To stop all services, run: ./stop.sh"
echo "=========================================="
