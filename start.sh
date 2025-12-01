#!/bin/bash

# AV Stream Space - 智能音视频流媒体平台启动脚本

echo "🚀 启动 AV Stream Space 智能音视频流媒体平台..."
echo "=================================================="

# 检查Docker是否安装
if ! command -v docker &> /dev/null; then
    echo "❌ Docker未安装，请先安装Docker"
    exit 1
fi

# 检查Docker Compose是否安装
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose未安装，请先安装Docker Compose"
    exit 1
fi

# 设置环境变量
export COMPOSE_PROJECT_NAME=avstream

# 创建必要的目录
echo "📁 创建必要的目录..."
mkdir -p logs
mkdir -p data/postgres data/redis data/minio data/mongodb data/prometheus data/grafana

# 构建并启动服务
echo "🔨 构建和启动服务..."
echo ""
echo "📊 基础设施服务:"
echo "   - PostgreSQL数据库 (端口: 5432)"
echo "   - Redis缓存 (端口: 6379)"
echo "   - MinIO对象存储 (端口: 9000/9001)"
echo "   - MongoDB文档数据库 (端口: 27017)"
echo "   - MediaMTX流媒体服务器 (端口: 1935/8888/8889)"
echo "   - Eureka服务注册中心 (端口: 8761)"
echo "   - Config配置中心 (端口: 8888)"
echo ""
echo "🔧 微服务:"
echo "   - 网关服务 (端口: 8080)"
echo "   - 认证服务 (端口: 8081)"
echo "   - 媒体服务 (端口: 8082)"
echo "   - 直播服务 (端口: 8083)"
echo "   - AI服务 (端口: 8084)"
echo "   - 监控服务 (端口: 8085)"
echo ""
echo "🌐 前端应用:"
echo "   - Vue3前端应用 (端口: 80)"
echo ""
echo "📈 监控系统:"
echo "   - Prometheus (端口: 9090)"
echo "   - Grafana (端口: 3000)"
echo "   - Zipkin (端口: 9411)"
echo "   - Kibana (端口: 5601)"
echo ""

# 启动基础设施服务
echo "🔄 启动基础设施服务..."
docker-compose -f docker/docker-compose-infra.yml up -d

# 等待基础设施服务启动
echo "⏳ 等待基础设施服务启动..."
sleep 30

# 构建和启动微服务
echo "🔨 构建和启动微服务..."
docker-compose build --parallel
docker-compose up -d

# 等待服务启动
echo "⏳ 等待服务启动..."
sleep 60

# 检查服务状态
echo "🔍 检查服务状态..."
echo ""

services=("postgres" "redis" "minio" "mongodb" "eureka" "gateway" "auth-service" "media-service" "live-service" "ai-service" "monitor-service" "frontend")

for service in "${services[@]}"; do
    if docker-compose ps | grep -q "${service}.*Up"; then
        echo "✅ ${service} - 运行正常"
    else
        echo "❌ ${service} - 启动失败"
    fi
done

echo ""
echo "🎉 AV Stream Space 启动完成！"
echo ""
echo "🌐 访问地址:"
echo "   前端应用: http://localhost"
echo "   网关服务: http://localhost:8080"
echo "   Eureka注册中心: http://localhost:8761"
echo "   Grafana监控: http://localhost:3000 (admin/admin123)"
echo "   MinIO管理: http://localhost:9001 (minioadmin/minioadmin123)"
echo ""
echo "📋 常用命令:"
echo "   查看日志: docker-compose logs -f [服务名]"
echo "   停止服务: docker-compose down"
echo "   重启服务: docker-compose restart [服务名]"
echo "   查看状态: docker-compose ps"
echo ""
echo "🔧 开发环境:"
echo "   后端开发: ./backend/[service-name]/mvn spring-boot:run"
echo "   前端开发: cd frontend && npm run dev"
echo ""

# 显示实时日志
echo "📝 显示实时日志 (Ctrl+C 退出)..."
docker-compose logs -f --tail=10