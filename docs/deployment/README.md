# AV Stream Space 部署手册

## 系统架构概述

AV Stream Space 采用微服务架构，包含以下核心服务：

- **前端服务**：Vue3 + TypeScript + Vite
- **网关服务**：Spring Cloud Gateway
- **认证服务**：Spring Boot + JWT
- **AI服务**：Spring Boot + AI模型集成
- **媒体服务**：Spring Boot + FFmpeg + WebRTC
- **直播服务**：Spring Boot + MediaMTX
- **监控服务**：Spring Boot + Prometheus + Grafana

## 环境要求

### 硬件要求

| 环境 | CPU | 内存 | 存储 | 网络 |
|------|-----|------|------|------|
| 开发环境 | 4核 | 8GB | 50GB | 100Mbps |
| 测试环境 | 8核 | 16GB | 100GB | 1Gbps |
| 生产环境 | 16核 | 32GB | 500GB | 10Gbps |

### 软件要求

| 组件 | 版本 | 说明 |
|------|------|------|
| JDK | 17+ | Java运行环境 |
| Node.js | 18+ | 前端运行环境 |
| Docker | 20.10+ | 容器化部署 |
| Docker Compose | 2.0+ | 容器编排 |
| Kubernetes | 1.25+ | 生产环境部署 |
| PostgreSQL | 14+ | 主数据库 |
| Redis | 7.0+ | 缓存数据库 |
| MongoDB | 6.0+ | 文档数据库 |
| MinIO | RELEASE.2023+ | 对象存储 |

## 快速部署指南

### 1. 开发环境部署

#### 1.1 克隆代码
```bash
git clone https://github.com/your-org/av-stream-space.git
cd av-stream-space
```

#### 1.2 配置环境变量
```bash
# 复制环境变量模板
cp .env.example .env

# 编辑环境变量
vim .env
```

**环境变量配置示例：**
```env
# 数据库配置
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
POSTGRES_DB=avstream
POSTGRES_USER=postgres
POSTGRES_PASSWORD=password

# Redis配置
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# MinIO配置
MINIO_ENDPOINT=http://localhost:9000
MINIO_ACCESS_KEY=minioadmin
MINIO_SECRET_KEY=minioadmin

# JWT配置
JWT_SECRET=your-jwt-secret-key
JWT_EXPIRATION=86400
```

#### 1.3 启动基础设施
```bash
# 启动数据库和中间件
cd docker
./scripts/start-infrastructure.sh
```

#### 1.4 启动后端服务
```bash
# 编译所有服务
cd backend
mvn clean package -DskipTests

# 启动网关服务
cd gateway
mvn spring-boot:run

# 启动其他服务（新终端）
cd auth-service
mvn spring-boot:run

cd ai-service
mvn spring-boot:run

cd media-service
mvn spring-boot:run

cd live-service
mvn spring-boot:run

cd monitor-service
mvn spring-boot:run
```

#### 1.5 启动前端服务
```bash
cd frontend
npm install
npm run dev
```

### 2. Docker部署

#### 2.1 构建镜像
```bash
# 构建所有服务镜像
docker-compose build
```

#### 2.2 启动服务
```bash
# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看服务日志
docker-compose logs -f
```

#### 2.3 服务访问
- 前端应用：http://localhost:3000
- 网关服务：http://localhost:8080
- 监控面板：http://localhost:3001
- MinIO管理：http://localhost:9001

### 3. Kubernetes部署

#### 3.1 创建命名空间
```bash
kubectl apply -f k8s/manifests/namespace.yaml
```

#### 3.2 部署基础设施
```bash
# 部署PostgreSQL
kubectl apply -f k8s/manifests/postgresql.yaml

# 部署Redis
kubectl apply -f k8s/manifests/redis.yaml

# 等待数据库就绪
kubectl wait --for=condition=ready pod -l app=postgresql -n avstream
```

#### 3.3 部署应用服务
```bash
# 使用Helm部署
helm install avstream k8s/charts/avstream/ -n avstream

# 或者使用manifest文件部署
kubectl apply -f k8s/manifests/
```

#### 3.4 配置Ingress
```bash
# 部署Ingress控制器（如nginx-ingress）
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/cloud/deploy.yaml

# 创建Ingress资源
kubectl apply -f k8s/manifests/ingress.yaml
```

## 配置说明

### 数据库配置

#### PostgreSQL配置
```yaml
# application.yml
spring:
  datasource:
    url: jdbc:postgresql://${POSTGRES_HOST:localhost}:${POSTGRES_PORT:5432}/${POSTGRES_DB:avstream}
    username: ${POSTGRES_USER:postgres}
    password: ${POSTGRES_PASSWORD:password}
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

#### Redis配置
```yaml
spring:
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD:}
    database: 0
    timeout: 3000ms
```

### 文件存储配置

#### MinIO配置
```yaml
minio:
  endpoint: ${MINIO_ENDPOINT:http://localhost:9000}
  accessKey: ${MINIO_ACCESS_KEY:minioadmin}
  secretKey: ${MINIO_SECRET_KEY:minioadmin}
  bucket:
    media: media-bucket
    ai: ai-bucket
    live: live-bucket
```

### 安全配置

#### JWT配置
```yaml
jwt:
  secret: ${JWT_SECRET:your-jwt-secret-key}
  expiration: ${JWT_EXPIRATION:86400}
```

#### CORS配置
```yaml
spring:
  web:
    cors:
      allowed-origins: "*"
      allowed-methods: "GET,POST,PUT,DELETE,OPTIONS"
      allowed-headers: "*"
      allow-credentials: true
```

## 监控和日志

### Prometheus配置

#### 应用指标暴露
```java
@Configuration
public class MetricsConfig {
    
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config().commonTags(
            "application", "av-stream-space",
            "environment", System.getenv("SPRING_PROFILES_ACTIVE")
        );
    }
}
```

#### Grafana仪表板
- 系统监控：CPU、内存、磁盘使用率
- 应用监控：请求量、响应时间、错误率
- 业务监控：用户活跃度、处理成功率

### 日志配置

#### Logback配置
```xml
<configuration>
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/application.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/application.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
</configuration>
```

## 备份和恢复

### 数据库备份
```bash
# PostgreSQL备份
pg_dump -h localhost -U postgres avstream > backup_$(date +%Y%m%d).sql

# MongoDB备份
mongodump --uri="mongodb://localhost:27017/avstream" --out=backup_$(date +%Y%m%d)
```

### 文件备份
```bash
# MinIO备份
mc mirror minio/media-bucket backup/media-bucket
mc mirror minio/ai-bucket backup/ai-bucket
```

### 恢复流程
```bash
# PostgreSQL恢复
psql -h localhost -U postgres avstream < backup_20240101.sql

# MongoDB恢复
mongorestore --uri="mongodb://localhost:27017/avstream" backup_20240101/
```

## 高级部署配置

### 高可用部署

#### 数据库集群配置
```yaml
# PostgreSQL主从配置
postgresql:
  primary:
    host: postgres-primary
    port: 5432
  replicas:
    - host: postgres-replica-1
      port: 5432
    - host: postgres-replica-2
      port: 5432
```

#### Redis集群配置
```yaml
# Redis哨兵模式
redis:
  sentinels:
    - host: redis-sentinel-1
      port: 26379
    - host: redis-sentinel-2
      port: 26379
    - host: redis-sentinel-3
      port: 26379
  masterName: mymaster
```

### 性能优化配置

#### JVM参数优化
```bash
# 生产环境JVM参数
java -Xms4g -Xmx4g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:ParallelGCThreads=4 -XX:ConcGCThreads=2 -jar app.jar
```

#### 数据库连接池优化
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 10
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

### 安全配置

#### SSL/TLS配置
```yaml
server:
  ssl:
    key-store: classpath:keystore.p12
    key-store-password: changeit
    key-store-type: PKCS12
    key-alias: tomcat
  port: 8443
```

#### 防火墙规则
```bash
# 开放必要端口
ufw allow 22/tcp    # SSH
ufw allow 80/tcp    # HTTP
ufw allow 443/tcp   # HTTPS
ufw allow 8080/tcp  # 应用端口
ufw allow 5432/tcp  # PostgreSQL
ufw allow 6379/tcp  # Redis
ufw enable
```

## 故障排除

### 常见问题

#### 服务启动失败
```bash
# 检查服务日志
docker-compose logs service-name

# 检查端口占用
netstat -tulpn | grep :8080

# 检查资源使用
docker stats
```

#### 数据库连接失败
```bash
# 检查数据库状态
systemctl status postgresql

# 检查连接信息
psql -h localhost -U postgres -d avstream

# 检查网络连通性
telnet postgres-host 5432
```

#### 文件上传失败
```bash
# 检查MinIO状态
curl http://localhost:9000/minio/health/live

# 检查存储空间
df -h /data/minio

# 检查权限
ls -la /data/minio
```

#### 内存不足问题
```bash
# 检查内存使用
free -h

# 检查JVM内存
docker exec container-name jstat -gcutil 1

# 调整JVM参数
java -Xms2g -Xmx2g -XX:+UseG1GC -jar app.jar
```

### 性能问题排查

#### 慢查询分析
```sql
-- 启用慢查询日志
SET slow_query_log = ON;
SET long_query_time = 2;

-- 分析慢查询
EXPLAIN ANALYZE SELECT * FROM users WHERE email LIKE '%test%';

-- 创建索引优化
CREATE INDEX idx_users_email ON users(email);
```

#### 应用性能分析
```bash
# 使用jstack分析线程
jstack <pid> > thread_dump.txt

# 使用jmap分析内存
jmap -heap <pid>

# 使用jstat分析GC
jstat -gc <pid> 1000 10
```

### 日志分析

#### 错误日志分析
```bash
# 查看错误日志
tail -f logs/application.log | grep -i error

# 分析日志模式
grep "ERROR" logs/application.log | awk '{print $5}' | sort | uniq -c | sort -nr
```

#### 监控告警分析
```bash
# 查看Prometheus指标
curl http://localhost:9090/api/v1/query?query=up

# 查看Grafana仪表板
open http://localhost:3000
```

## 自动化运维

### 健康检查脚本
```bash
#!/bin/bash
# health-check.sh

# 检查服务状态
services=("postgresql" "redis" "minio" "gateway" "auth-service")

for service in "${services[@]}"; do
    if systemctl is-active --quiet $service; then
        echo "✓ $service is running"
    else
        echo "✗ $service is not running"
        exit 1
    fi
done

# 检查端口监听
ports=(5432 6379 9000 8080)

for port in "${ports[@]}"; do
    if netstat -tulpn | grep ":$port " > /dev/null; then
        echo "✓ Port $port is listening"
    else
        echo "✗ Port $port is not listening"
        exit 1
    fi
done

echo "所有服务运行正常"
```

### 备份脚本
```bash
#!/bin/bash
# backup.sh

DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/backup/$DATE"

mkdir -p $BACKUP_DIR

# 备份PostgreSQL
pg_dump -h localhost -U postgres avstream > $BACKUP_DIR/avstream_$DATE.sql

# 备份MongoDB
mongodump --uri="mongodb://localhost:27017/avstream" --out=$BACKUP_DIR/mongodb_$DATE

# 备份MinIO数据
mc mirror minio/media-bucket $BACKUP_DIR/minio/media-bucket
mc mirror minio/ai-bucket $BACKUP_DIR/minio/ai-bucket

# 压缩备份文件
tar -czf $BACKUP_DIR.tar.gz $BACKUP_DIR

# 清理临时文件
rm -rf $BACKUP_DIR

echo "备份完成: $BACKUP_DIR.tar.gz"
```

### 监控脚本
```bash
#!/bin/bash
# monitor.sh

# 检查CPU使用率
CPU_USAGE=$(top -bn1 | grep "Cpu(s)" | awk '{print $2}' | cut -d'%' -f1)
if (( $(echo "$CPU_USAGE > 80" | bc -l) )); then
    echo "警告: CPU使用率过高: $CPU_USAGE%"
fi

# 检查内存使用率
MEM_USAGE=$(free | grep Mem | awk '{print $3/$2 * 100.0}')
if (( $(echo "$MEM_USAGE > 85" | bc -l) )); then
    echo "警告: 内存使用率过高: $MEM_USAGE%"
fi

# 检查磁盘空间
DISK_USAGE=$(df / | awk 'NR==2 {print $5}' | cut -d'%' -f1)
if [ $DISK_USAGE -gt 90 ]; then
    echo "警告: 磁盘空间不足: $DISK_USAGE%"
fi
```

## 灾难恢复

### 恢复流程
1. **评估损失**：确定受影响的服务和数据
2. **启动备份环境**：使用备份启动临时服务
3. **数据恢复**：从备份恢复数据库和文件
4. **服务恢复**：逐步恢复各服务功能
5. **验证功能**：确保所有功能正常
6. **切换流量**：将流量切换到恢复的环境

### 恢复时间目标 (RTO)
- **关键服务**：4小时内恢复
- **非关键服务**：8小时内恢复
- **完整系统**：24小时内恢复

### 恢复点目标 (RPO)
- **业务数据**：15分钟数据丢失
- **用户数据**：1小时数据丢失
- **系统配置**：实时同步

### 性能优化

#### JVM参数优化
```bash
# 生产环境JVM参数
java -Xms2g -Xmx2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -jar app.jar
```

#### 数据库优化
```sql
-- 创建索引
CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_media_created ON media_files(created_at);
```

## 版本升级

### 升级流程
1. 备份当前数据和配置
2. 停止当前服务
3. 更新代码和镜像
4. 执行数据库迁移
5. 启动新版本服务
6. 验证功能正常

### 回滚流程
1. 停止当前服务
2. 恢复备份数据
3. 启动旧版本服务
4. 验证功能正常

## 安全建议

1. **定期更新**：及时更新系统补丁和依赖库
2. **访问控制**：限制不必要的端口和服务访问
3. **数据加密**：敏感数据加密存储和传输
4. **监控告警**：设置安全事件监控和告警
5. **备份策略**：定期备份重要数据

---

**注意：** 生产环境部署前请进行充分测试，确保系统稳定性和安全性。