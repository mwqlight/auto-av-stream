# AI服务 (AI Service)

基于SpringBoot 3.0+的AI能力服务，提供语音识别、图像生成、文本转语音等AI功能。

## 功能特性

- 🎤 **语音识别** - 基于Whisper模型的音频转文本
- 🎨 **图像生成** - 基于Stable Diffusion的文本到图像生成  
- 🔊 **文本转语音** - 基于TTS模型的文本到语音转换
- 🔄 **图像处理** - 风格转换、超分辨率、图像修复
- 📊 **监控指标** - Prometheus + Grafana监控体系
- 🐳 **容器化部署** - Docker + Docker Compose
- 🔐 **安全认证** - JWT + Redis缓存

## 技术栈

### 后端技术
- **Spring Boot 3.0** - 应用框架
- **Spring Data JPA** - 数据访问层
- **PostgreSQL** - 主数据库
- **Redis** - 缓存和会话存储
- **Spring Security** - 安全认证
- **SpringDoc OpenAPI 3.0** - API文档
- **Micrometer** - 应用监控

### AI模型
- **Whisper** - 语音识别模型
- **Stable Diffusion** - 图像生成模型  
- **TTS (Text-to-Speech)** - 文本转语音模型

### 部署工具
- **Docker** - 容器化
- **Docker Compose** - 服务编排
- **Prometheus** - 监控系统
- **Grafana** - 可视化仪表板

## 快速开始

### 环境要求

- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- PostgreSQL 15+
- Redis 7+

### 本地开发

1. **克隆项目**
```bash
git clone <repository-url>
cd backend/ai-service
```

2. **配置数据库**
```bash
# 启动PostgreSQL和Redis
docker-compose up -d postgres redis
```

3. **应用配置**
```bash
# 复制配置文件
cp src/main/resources/application.yml.example src/main/resources/application.yml

# 编辑配置（根据实际情况修改）
vi src/main/resources/application.yml
```

4. **构建项目**
```bash
mvn clean package -DskipTests
```

5. **启动服务**
```bash
java -jar target/ai-service-1.0.0.jar
```

### Docker部署

1. **构建镜像**
```bash
docker-compose build
```

2. **启动服务**
```bash
docker-compose up -d
```

3. **查看服务状态**
```bash
docker-compose ps
```

4. **查看日志**
```bash
docker-compose logs -f ai-service
```

## API文档

服务启动后，可通过以下地址访问API文档：

- **Swagger UI**: http://localhost:8080/ai-service/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/ai-service/v3/api-docs

## 监控系统

### Prometheus
- 地址: http://localhost:9090
- 配置: `monitoring/prometheus.yml`

### Grafana
- 地址: http://localhost:3000
- 用户名: `admin`
- 密码: `admin123`

### 监控指标
- 应用健康状态
- 请求量统计
- 响应时间分布
- 错误率监控
- JVM性能指标

## 数据库初始化

项目启动时会自动创建必要的表结构。如需手动初始化：

```sql
-- 创建数据库
CREATE DATABASE ai_service;

-- 创建表结构（JPA会自动创建）
-- 查看表结构
\dt ai_request_log;
```

## 配置说明

### 核心配置

```yaml
ai:
  whisper:
    model-path: "/models/whisper"
    model-size: "base"
  stable-diffusion:
    model-path: "/models/stable-diffusion"
    model-name: "stable-diffusion-v1-5"
  tts:
    model-path: "/models/tts"
    model-name: "vits"
```

### 服务配置

```yaml
ai:
  service:
    max-concurrent-requests: 10
    request-timeout: 300000
    cache-enabled: true
    cache-ttl: 3600
```

## API使用示例

### 语音识别

```bash
curl -X POST "http://localhost:8080/ai-service/api/v1/ai/speech-recognition" \
  -H "Content-Type: multipart/form-data" \
  -F "audioFile=@/path/to/audio.wav" \
  -F "language=zh-CN" \
  -F "withTimestamps=false"
```

### 图像生成

```bash
curl -X POST "http://localhost:8080/ai-service/api/v1/ai/image-generation" \
  -H "Content-Type: application/json" \
  -d '{
    "prompt": "一只可爱的猫咪在花园里玩耍",
    "width": 512,
    "height": 512,
    "numImages": 1
  }'
```

### 文本转语音

```bash
curl -X POST "http://localhost:8080/ai-service/api/v1/ai/text-to-speech" \
  -H "Content-Type: application/json" \
  -d '{
    "text": "你好，这是一个语音合成示例",
    "language": "zh-CN",
    "speaker": "female",
    "speed": 1.0
  }'
```

## 故障排除

### 常见问题

1. **模型加载失败**
   - 检查模型文件路径是否正确
   - 确认模型文件完整性
   - 查看日志中的具体错误信息

2. **内存不足**
   - 调整JVM内存参数：`-Xmx4g -Xms2g`
   - 减少并发请求数
   - 启用模型缓存

3. **数据库连接失败**
   - 检查PostgreSQL服务状态
   - 验证数据库连接配置
   - 查看网络连接情况

### 日志查看

```bash
# 查看应用日志
tail -f logs/ai-service.log

# 查看Docker容器日志
docker-compose logs ai-service

# 查看特定级别的日志
grep "ERROR" logs/ai-service.log
```

## 开发指南

### 项目结构

```
src/main/java/com/avstream/ai/
├── config/          # 配置类
├── controller/      # 控制器层
├── dto/            # 数据传输对象
├── entity/         # 实体类
├── repository/     # 数据访问层
├── service/        # 业务逻辑层
├── util/           # 工具类
└── Application.java # 启动类
```

### 代码规范

- 遵循Spring Boot最佳实践
- 使用Lombok减少样板代码
- 统一异常处理机制
- 完整的API文档注解
- 单元测试覆盖率≥70%

### 添加新功能

1. 在`dto/`包中定义请求/响应对象
2. 在`service/`包中实现业务逻辑
3. 在`controller/`包中暴露API接口
4. 添加单元测试
5. 更新API文档

## 性能优化

### 缓存策略
- 使用Redis缓存频繁访问的数据
- 设置合理的缓存过期时间
- 实现缓存穿透保护

### 并发控制
- 限制最大并发请求数
- 使用线程池管理资源
- 实现请求超时机制

### 资源管理
- 及时释放临时文件
- 优化模型加载策略
- 监控内存使用情况

## 安全考虑

### 认证授权
- 使用JWT进行身份验证
- 实现基于角色的访问控制
- 保护敏感API接口

### 数据安全
- 验证用户输入数据
- 防止SQL注入攻击
- 加密敏感配置信息

### 网络安全
- 使用HTTPS加密传输
- 配置防火墙规则
- 定期安全扫描

## 贡献指南

1. Fork项目
2. 创建功能分支
3. 提交代码变更
4. 编写测试用例
5. 提交Pull Request

## 许可证

本项目采用MIT许可证。详情请查看LICENSE文件。

## 联系方式

- 项目主页: [项目地址]
- 问题反馈: [Issues页面]
- 邮箱: [联系邮箱]

---

**注意**: 本服务需要较大的计算资源，建议在具备GPU的服务器上部署以获得更好的性能。