#!/bin/bash

# 媒体服务启动脚本

echo "========================================"
echo "   AV Stream Media Service Startup"
echo "========================================"

# 检查Java环境
if ! command -v java &> /dev/null; then
    echo "错误: 未找到Java环境，请先安装Java 17或更高版本"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -n 1 | awk -F '"' '{print $2}')
echo "Java版本: $JAVA_VERSION"

# 检查是否支持Java 17+
JAVA_MAJOR=$(echo $JAVA_VERSION | cut -d'.' -f1)
if [ "$JAVA_MAJOR" -lt "17" ]; then
    echo "错误: 需要Java 17或更高版本，当前版本: $JAVA_VERSION"
    exit 1
fi

# 检查Maven环境
if ! command -v mvn &> /dev/null; then
    echo "错误: 未找到Maven环境，请先安装Maven"
    exit 1
fi

echo "Maven版本: $(mvn --version | head -n 1)"

# 检查数据库连接
echo "检查PostgreSQL连接..."
if ! pg_isready -h localhost -p 5432 -U postgres &> /dev/null; then
    echo "警告: PostgreSQL未运行或无法连接，请确保数据库已启动"
    echo "启动PostgreSQL: brew services start postgresql@14"
fi

# 检查Redis连接
echo "检查Redis连接..."
if ! redis-cli ping &> /dev/null; then
    echo "警告: Redis未运行或无法连接，请确保Redis已启动"
    echo "启动Redis: brew services start redis"
fi

# 检查MinIO连接
echo "检查MinIO连接..."
if ! curl -s http://localhost:9000/minio/health/live &> /dev/null; then
    echo "警告: MinIO未运行或无法连接，请确保MinIO已启动"
    echo "启动MinIO: brew services start minio/stable/minio"
fi

# 检查FFmpeg
echo "检查FFmpeg..."
if ! command -v ffmpeg &> /dev/null; then
    echo "警告: 未找到FFmpeg，音视频处理功能将受限"
    echo "安装FFmpeg: brew install ffmpeg"
else
    echo "FFmpeg版本: $(ffmpeg -version | head -n 1)"
fi

# 检查MediaMTX
echo "检查MediaMTX..."
if ! curl -s http://localhost:9997/v2/config/global/get &> /dev/null; then
    echo "警告: MediaMTX未运行或无法连接，流媒体功能将受限"
    echo "安装MediaMTX: 参考 https://github.com/mediamtx/mediamtx"
fi

# 构建项目
echo "构建媒体服务..."
mvn clean compile -q
if [ $? -ne 0 ]; then
    echo "错误: 项目构建失败"
    exit 1
fi

# 运行测试
echo "运行测试..."
mvn test -q
if [ $? -ne 0 ]; then
    echo "警告: 测试失败，继续启动服务..."
fi

# 启动服务
echo "启动媒体服务..."
echo "服务端口: 8081"
echo "上下文路径: /media"
echo "管理端点: http://localhost:8081/media/actuator"
echo "API文档: http://localhost:8081/media/swagger-ui.html"
echo ""
echo "按 Ctrl+C 停止服务"
echo ""

# 启动Spring Boot应用
mvn spring-boot:run