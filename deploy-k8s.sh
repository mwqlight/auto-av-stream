#!/bin/bash

# AV Stream Space - Kubernetes部署脚本

echo "🚀 部署 AV Stream Space 到 Kubernetes..."
echo "=================================================="

# 检查kubectl是否安装
if ! command -v kubectl &> /dev/null; then
    echo "❌ kubectl未安装，请先安装kubectl"
    exit 1
fi

# 检查Helm是否安装
if ! command -v helm &> /dev/null; then
    echo "❌ Helm未安装，请先安装Helm"
    exit 1
fi

# 检查Kubernetes集群连接
echo "🔍 检查Kubernetes集群连接..."
if ! kubectl cluster-info &> /dev/null; then
    echo "❌ 无法连接到Kubernetes集群"
    exit 1
fi

echo "✅ 集群连接正常"

# 创建命名空间
echo "📁 创建命名空间..."
kubectl apply -f k8s/manifests/namespace.yaml

# 部署基础设施服务
echo "🔧 部署基础设施服务..."

# PostgreSQL数据库
echo "📊 部署PostgreSQL..."
kubectl apply -f k8s/manifests/postgresql.yaml

# Redis缓存
echo "🔴 部署Redis..."
kubectl apply -f k8s/manifests/redis.yaml

# 等待基础设施服务启动
echo "⏳ 等待基础设施服务启动..."
sleep 30

# 检查服务状态
echo "🔍 检查基础设施服务状态..."
kubectl get pods -n avstream -l app=postgresql
kubectl get pods -n avstream -l app=redis

# 构建Docker镜像（可选）
if [ "$1" = "--build" ]; then
    echo "🔨 构建Docker镜像..."
    
    # 构建后端服务镜像
    for service in gateway auth-service media-service live-service ai-service monitor-service; do
        echo "📦 构建 ${service}..."
        docker build -t avstream/${service}:latest ./backend/${service}
    done
    
    # 构建前端镜像
    echo "📦 构建前端应用..."
    docker build -t avstream/frontend:latest ./frontend
    
    # 推送镜像到仓库（可选）
    if [ "$2" = "--push" ]; then
        echo "📤 推送镜像到仓库..."
        # 这里需要配置镜像仓库地址
        echo "💡 请配置镜像仓库地址后取消注释推送命令"
        # docker push avstream/gateway:latest
        # docker push avstream/auth-service:latest
        # docker push avstream/media-service:latest
        # docker push avstream/live-service:latest
        # docker push avstream/ai-service:latest
        # docker push avstream/monitor-service:latest
        # docker push avstream/frontend:latest
    fi
fi

# 部署微服务
echo "🔧 部署微服务..."

# 网关服务
echo "🚪 部署网关服务..."
kubectl apply -f k8s/manifests/gateway.yaml

# 认证服务
echo "🔐 部署认证服务..."
kubectl apply -f k8s/manifests/auth-service.yaml

# 媒体服务
echo "🎥 部署媒体服务..."
kubectl apply -f k8s/manifests/media-service.yaml

# 前端应用
echo "🌐 部署前端应用..."
kubectl apply -f k8s/manifests/frontend.yaml

# 等待服务启动
echo "⏳ 等待服务启动..."
sleep 60

# 检查部署状态
echo "🔍 检查部署状态..."
echo ""

kubectl get deployments -n avstream
kubectl get services -n avstream
kubectl get pods -n avstream

echo ""
echo "🎉 AV Stream Space 已部署到 Kubernetes！"
echo ""
echo "🌐 访问地址:"
echo "   前端应用: $(kubectl get service frontend -n avstream -o jsonpath='{.status.loadBalancer.ingress[0].ip}')"
echo "   网关服务: $(kubectl get service gateway -n avstream -o jsonpath='{.status.loadBalancer.ingress[0].ip}'):80"
echo ""
echo "📋 常用命令:"
echo "   查看Pod状态: kubectl get pods -n avstream"
echo "   查看服务: kubectl get services -n avstream"
echo "   查看日志: kubectl logs -f [pod-name] -n avstream"
echo "   进入Pod: kubectl exec -it [pod-name] -n avstream -- /bin/bash"
echo "   删除部署: kubectl delete namespace avstream"
echo ""
echo "🔧 使用Helm部署（推荐）:"
echo "   helm install avstream ./k8s/charts/avstream -n avstream"
echo "   helm upgrade avstream ./k8s/charts/avstream -n avstream"
echo "   helm uninstall avstream -n avstream"
echo ""

# 显示实时日志
echo "📝 显示实时日志 (Ctrl+C 退出)..."
kubectl logs -f -l app=gateway -n avstream --tail=10