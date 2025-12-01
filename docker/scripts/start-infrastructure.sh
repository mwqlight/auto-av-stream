#!/bin/bash

# AV Stream基础设施启动脚本
# 启动所有数据库、存储和监控服务

set -e

echo "🚀 启动AV Stream基础设施服务..."

# 检查Docker是否运行
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker未运行，请先启动Docker"
    exit 1
fi

# 创建必要的目录
echo "📁 创建必要的目录..."
mkdir -p ./config/grafana/provisioning
mkdir -p ./config/grafana/dashboards

# 检查配置文件是否存在
if [ ! -f "./config/prometheus.yml" ]; then
    echo "❌ Prometheus配置文件不存在"
    exit 1
fi

if [ ! -f "./config/redis.conf" ]; then
    echo "❌ Redis配置文件不存在"
    exit 1
fi

if [ ! -f "./config/minio-config.json" ]; then
    echo "❌ MinIO配置文件不存在"
    exit 1
fi

# 启动基础设施服务
echo "🐳 启动Docker Compose服务..."
docker-compose -f docker-compose-infra.yml up -d

# 等待服务启动
echo "⏳ 等待服务启动..."
sleep 30

# 检查服务状态
echo "🔍 检查服务状态..."

services=("postgres" "redis" "minio" "mongodb" "mediamtx" "eureka" "prometheus" "grafana")

for service in "${services[@]}"; do
    if docker ps | grep -q "avstream-$service"; then
        echo "✅ $service 服务运行正常"
    else
        echo "❌ $service 服务启动失败"
    fi
done

# 显示服务访问信息
echo ""
echo "🎉 AV Stream基础设施启动完成！"
echo ""
echo "📊 服务访问地址："
echo "   - PostgreSQL数据库: localhost:5432"
echo "   - Redis缓存: localhost:6379"
echo "   - MinIO对象存储: http://localhost:9000 (Console: http://localhost:9001)"
echo "   - MongoDB文档数据库: localhost:27017"
echo "   - MediaMTX流媒体服务器: RTMP:1935, HLS:8888, WebRTC:8889"
echo "   - Eureka服务注册中心: http://localhost:8761"
echo "   - Prometheus监控: http://localhost:9090"
echo "   - Grafana仪表板: http://localhost:3000 (admin/admin123)"
echo "   - Zipkin分布式追踪: http://localhost:9411"
echo "   - Elasticsearch: http://localhost:9200"
echo "   - Kibana日志分析: http://localhost:5601"
echo ""
echo "🔧 监控导出器端口："
echo "   - Node Exporter: 9100"
echo "   - cAdvisor: 8080"
echo "   - PostgreSQL Exporter: 9187"
echo "   - Redis Exporter: 9121"
echo "   - MongoDB Exporter: 9216"
echo ""
echo "💡 使用 'docker-compose -f docker-compose-infra.yml logs' 查看服务日志"
echo "💡 使用 'docker-compose -f docker-compose-infra.yml down' 停止服务"