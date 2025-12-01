#!/bin/bash

# AV Stream Space - 智能音视频流媒体平台停止脚本

echo "🛑 停止 AV Stream Space 智能音视频流媒体平台..."
echo "=================================================="

# 检查Docker是否安装
if ! command -v docker &> /dev/null; then
    echo "❌ Docker未安装"
    exit 1
fi

# 设置环境变量
export COMPOSE_PROJECT_NAME=avstream

# 停止微服务
echo "🔻 停止微服务..."
docker-compose down

# 停止基础设施服务
echo "🔻 停止基础设施服务..."
docker-compose -f docker/docker-compose-infra.yml down

# 清理容器和网络
echo "🧹 清理容器和网络..."
docker system prune -f

# 显示剩余容器
echo "🔍 检查剩余容器..."
echo ""
remaining_containers=$(docker ps -a --format "table {{.Names}}\t{{.Status}}" | grep avstream)

if [ -z "$remaining_containers" ]; then
    echo "✅ 所有服务已停止"
else
    echo "⚠️  以下容器仍在运行:"
    echo "$remaining_containers"
    echo ""
    echo "💡 如需强制停止，请运行: docker stop [容器名]"
fi

echo ""
echo "🔚 AV Stream Space 已停止"
echo ""
echo "📋 下次启动:"
echo "   启动所有服务: ./start.sh"
echo "   仅启动基础设施: docker-compose -f docker/docker-compose-infra.yml up -d"
echo "   仅启动微服务: docker-compose up -d"