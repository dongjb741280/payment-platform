#!/bin/bash

# 支付平台停止脚本

echo "=========================================="
echo "Payment Platform Stop Script"
echo "=========================================="

# 停止用户服务
echo "Stopping user service..."
USER_SERVICE_PID=$(ps aux | grep 'payment-user-1.0.0-SNAPSHOT.jar' | grep -v grep | awk '{print $2}')
if [ ! -z "$USER_SERVICE_PID" ]; then
    kill $USER_SERVICE_PID
    echo "✓ User service stopped (PID: $USER_SERVICE_PID)"
else
    echo "User service is not running"
fi

# 停止基础设施服务
echo "Stopping infrastructure services..."
docker-compose down

echo "=========================================="
echo "All services stopped successfully!"
echo "=========================================="
