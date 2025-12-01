#!/bin/bash

# AV Stream基础设施停止脚本
# 停止所有数据库、存储和监控服务

echo "🛑 停止AV Stream基础设施服务..."

# 检查Docker是否运行
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker未运行"
    exit 1
fi

# 停止服务
echo "🐳 停止Docker Compose服务..."
docker-compose -f docker-compose-infra.yml down

echo "✅ 基础设施服务已停止"
echo ""
echo "💡 使用 './start-infrastructure.sh' 重新启动服务"