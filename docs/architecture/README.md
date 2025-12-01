# AV Stream Space 架构设计文档

## 系统架构概述

AV Stream Space 采用现代化的微服务架构，结合前后端分离的设计理念，构建了一个高性能、可扩展的音视频处理平台。

## 整体架构图

```
┌─────────────────────────────────────────────────────────────────┐
│                         前端层 (Frontend)                         │
├─────────────────────────────────────────────────────────────────┤
│  Vue3 + TypeScript + Vite + Element Plus + Pinia + Vue Router    │
└─────────────────────────────────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                        网关层 (Gateway)                          │
├─────────────────────────────────────────────────────────────────┤
│  Spring Cloud Gateway + JWT认证 + 限流 + 熔断 + 负载均衡         │
└─────────────────────────────────────────────────────────────────┘
                               │
              ┌────────────────┼────────────────┐
              ▼                ▼                 ▼
┌───────────────┐  ┌───────────────┐  ┌─────────────────┐
│  认证服务      │  │  AI服务       │  │  媒体服务       │
├───────────────┤  ├───────────────┤  ├─────────────────┤
│ Spring Boot   │  │ Spring Boot   │  │ Spring Boot     │
│ JWT + RBAC    │  │ AI模型集成    │  │ FFmpeg + WebRTC │
└───────────────┘  └───────────────┘  └─────────────────┘
              │                │                 │
              ▼                ▼                 ▼
┌───────────────┐  ┌───────────────┐  ┌─────────────────┐
│  直播服务      │  │  监控服务      │  │  基础设施层      │
├───────────────┤  ├───────────────┤  ├─────────────────┤
│ Spring Boot   │  │ Spring Boot   │  │ PostgreSQL      │
│ MediaMTX      │  │ Prometheus    │  │ Redis + MongoDB │
└───────────────┘  └───────────────┘  │ MinIO + Nginx   │
                                      └─────────────────┘
```

## 技术栈选型

### 前端技术栈

| 技术 | 版本 | 用途 | 优势 |
|------|------|------|------|
| Vue3 | 3.3+ | 前端框架 | 组合式API，更好的TypeScript支持 |
| TypeScript | 5.0+ | 类型系统 | 类型安全，更好的开发体验 |
| Vite | 4.5+ | 构建工具 | 快速冷启动，热更新 |
| Element Plus | 2.3+ | UI组件库 | 丰富的组件，良好的设计 |
| Pinia | 2.1+ | 状态管理 | 轻量级，TypeScript友好 |
| Vue Router | 4.2+ | 路由管理 | 声明式路由，嵌套路由支持 |

### 后端技术栈

| 技术 | 版本 | 用途 | 优势 |
|------|------|------|------|
| Spring Boot | 3.1+ | 应用框架 | 快速开发，丰富的生态 |
| Spring Cloud | 2022.0+ | 微服务框架 | 服务治理，配置管理 |
| JPA/Hibernate | 3.1+ | ORM框架 | 对象关系映射，数据访问 |
| JWT | 0.11+ | 认证机制 | 无状态，跨域支持 |
| Redis | 7.0+ | 缓存数据库 | 高性能，丰富的数据结构 |
| PostgreSQL | 14+ | 关系数据库 | ACID事务，JSON支持 |

### AI技术栈

| 技术 | 用途 | 集成方式 |
|------|------|----------|
| Whisper | 语音识别 | Python服务 + HTTP API |
| Stable Diffusion | 图像生成 | Python服务 + HTTP API |
| TTS引擎 | 文本转语音 | Python服务 + HTTP API |
| OpenCV | 图像处理 | Java Native Interface |

### 媒体处理技术栈

| 技术 | 用途 | 特点 |
|------|------|------|
| FFmpeg | 音视频处理 | 强大的编解码能力 |
| WebRTC | 实时通信 | 低延迟，P2P通信 |
| MediaMTX | 流媒体服务器 | 轻量级，RTSP/RTMP支持 |
| MinIO | 对象存储 | S3兼容，高性能 |

## 核心模块设计

### 1. 认证授权模块

#### 架构设计
```java
// 核心类图
AuthenticationFilter ───> JwtUtil ───> UserDetailsService
         │                      │
         ▼                      ▼
AuthenticationEntryPoint  AccessDeniedHandler
```

#### 功能特性
- **多因素认证**：支持密码、短信、邮箱验证
- **RBAC权限模型**：基于角色的访问控制
- **会话管理**：分布式会话，支持踢人下线
- **安全审计**：操作日志记录，安全事件监控

### 2. AI服务模块

#### 服务架构
```
AIController ───> AIService ───> AIServiceImpl ───> ExternalAIService
       │               │                │
       ▼               ▼                ▼
  API Gateway     Business Logic   AI Model Integration
```

#### 处理流程
1. **请求接收**：接收前端上传的文件或文本
2. **预处理**：文件格式验证，数据清洗
3. **AI处理**：调用相应的AI模型进行处理
4. **后处理**：结果格式化，质量评估
5. **结果返回**：返回处理结果和元数据

### 3. 媒体处理模块

#### 处理管道
```
Input File → Validation → Preprocessing → Core Processing → Postprocessing → Output
     │           │              │              │                │           │
     ▼           ▼              ▼              ▼                ▼           ▼
  File Type   Size Check   Format Convert   FFmpeg Process   Quality Check  Storage
```

#### 性能优化
- **异步处理**：长时间任务异步执行
- **批量处理**：支持批量文件处理
- **缓存策略**：处理结果缓存，减少重复计算
- **并行处理**：多核CPU并行处理

### 4. 直播管理模块

#### 推拉流架构
```
推流端 → MediaMTX服务器 → CDN边缘节点 → 播放端
   │          │                │           │
   ▼          ▼                ▼           ▼
 OBS/FFmpeg  RTMP/RTSP       HLS/DASH   Web/H5/App
```

#### 功能特性
- **低延迟直播**：WebRTC技术，延迟<1秒
- **多协议支持**：RTMP、RTSP、HLS、DASH
- **质量监控**：实时码率、帧率、延迟监控
- **录制回放**：直播录制，点播回放

## 数据存储设计

### 数据库设计原则

#### 1. 数据分片策略
```sql
-- 用户表按ID分片
CREATE TABLE users_00 (id BIGINT PRIMARY KEY, ...);
CREATE TABLE users_01 (id BIGINT PRIMARY KEY, ...);

-- 媒体文件按时间分片
CREATE TABLE media_files_202401 (id BIGINT, created_at TIMESTAMP, ...);
CREATE TABLE media_files_202402 (id BIGINT, created_at TIMESTAMP, ...);
```

#### 2. 索引设计
```sql
-- 复合索引设计
CREATE INDEX idx_user_email_status ON users(email, status);
CREATE INDEX idx_media_type_created ON media_files(type, created_at);

-- 覆盖索引
CREATE INDEX idx_ai_history_user_type ON ai_processing_history(user_id, type) INCLUDE (result);
```

### 缓存策略

#### 多级缓存架构
```java
// 缓存层级设计
Local Cache (Caffeine) → Distributed Cache (Redis) → Database
       │                      │                       │
       ▼                      ▼                       ▼
   高频数据               共享数据                持久化数据
```

#### 缓存失效策略
- **TTL过期**：设置合理的过期时间
- **写时失效**：数据更新时主动失效缓存
- **懒加载**：缓存未命中时从数据库加载

## 安全架构设计

### 1. 网络安全

#### 网络隔离
```
公网区域 ──→ 防火墙 ──→ DMZ区域 ──→ 内网区域
   │           │           │           │
   ▼           ▼           ▼           ▼
 前端应用    网关服务    业务服务    数据库服务
```

#### 通信加密
- **HTTPS/TLS**：所有外部通信使用TLS加密
- **服务间认证**：mTLS双向认证
- **数据加密**：敏感数据AES-256加密存储

### 2. 应用安全

#### 输入验证
```java
// 统一的参数验证
@Validated
public class UserDTO {
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;
    
    @Email
    private String email;
}
```

#### SQL注入防护
```java
// 使用预编译语句
@Query("SELECT u FROM User u WHERE u.username = :username")
User findByUsername(@Param("username") String username);
```

#### XSS防护
```javascript
// 前端输入过滤
const sanitizeHtml = (html) => {
    return DOMPurify.sanitize(html);
};
```

## 性能优化策略

### 1. 前端性能优化

#### 代码分割
```javascript
// 路由懒加载
const AIService = () => import('@/views/AIService/index.vue');
const MediaService = () => import('@/views/MediaService/index.vue');
```

#### 资源优化
- **图片懒加载**：Intersection Observer API
- **代码压缩**：Tree Shaking + Minification
- **CDN加速**：静态资源CDN分发

### 2. 后端性能优化

#### 数据库优化
```java
// 连接池配置
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
```

#### 异步处理
```java
// 异步方法注解
@Async("taskExecutor")
@Transactional
public CompletableFuture<ProcessResult> processMedia(MediaFile file) {
    // 异步处理逻辑
}
```

## 监控和告警

### 1. 应用监控

#### 指标收集
```java
// 自定义指标
@Bean
public MeterRegistryCustomizer<MeterRegistry> metrics() {
    return registry -> {
        registry.config().commonTags("application", "av-stream-space");
    };
}
```

#### 健康检查
```java
// 健康检查端点
@Component
public class DatabaseHealthIndicator implements HealthIndicator {
    
    @Override
    public Health health() {
        // 数据库连接检查
        return Health.up().withDetail("database", "connected").build();
    }
}
```

### 2. 业务监控

#### 关键指标
- **用户活跃度**：DAU/MAU，会话时长
- **处理成功率**：各服务处理成功率
- **响应时间**：P50/P95/P99响应时间
- **错误率**：各服务错误率统计

## 扩展性设计

### 1. 水平扩展

#### 无状态设计
```java
// 无状态服务设计
@Service
public class AIService {
    
    // 不依赖本地状态
    public ProcessResult process(Request request) {
        // 业务逻辑
    }
}
```

#### 服务发现
```yaml
# 服务注册配置
spring:
  cloud:
    consul:
      host: localhost
      port: 8500
    discovery:
      instance-id: ${spring.application.name}:${random.value}
```

### 2. 垂直扩展

#### 资源隔离
```yaml
# 容器资源限制
resources:
  requests:
    memory: "512Mi"
    cpu: "250m"
  limits:
    memory: "1Gi"
    cpu: "500m"
```

## 容错设计

### 1. 熔断机制
```java
// Resilience4j熔断配置
@CircuitBreaker(name = "aiService", fallbackMethod = "fallbackProcess")
public ProcessResult process(Request request) {
    // 业务逻辑
}

public ProcessResult fallbackProcess(Request request, Exception e) {
    // 降级处理
    return ProcessResult.error("服务暂时不可用");
}
```

### 2. 重试策略
```java
// 重试配置
@Retry(name = "externalService", fallbackMethod = "fallback")
public ExternalResponse callExternalService(Request request) {
    // 外部服务调用
}
```

## 开发规范

### 代码规范

#### 后端代码规范
```java
// 1. 命名规范
public class UserServiceImpl implements UserService {
    private static final int MAX_RETRY_COUNT = 3;
    
    public List<UserDTO> queryUserList(String username) {
        // 业务逻辑
    }
}

// 2. 异常处理规范
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        return ResponseEntity.status(e.getCode()).body(ErrorResponse.of(e));
    }
}

// 3. 日志规范
@Slf4j
@Service
public class UserService {
    public User createUser(UserCreateRequest request) {
        log.info("开始创建用户，用户名：{}", request.getUsername());
        try {
            // 业务逻辑
            log.info("用户创建成功，用户ID：{}", user.getId());
            return user;
        } catch (Exception e) {
            log.error("用户创建失败，用户名：{}", request.getUsername(), e);
            throw e;
        }
    }
}
```

#### 前端代码规范
```vue
<template>
  <!-- 组件命名规范 -->
  <UserProfileCard 
    :user="user"
    @update="handleUpdate"
  />
</template>

<script setup lang="ts">
// 1. 类型定义
interface User {
  id: number
  name: string
  email: string
}

interface Props {
  user: User
}

// 2. Props定义
const props = withDefaults(defineProps<Props>(), {
  user: () => ({
    id: 0,
    name: '',
    email: ''
  })
})

// 3. 事件定义
const emit = defineEmits<{
  (e: 'update', value: User): void
}>()

// 4. 组合式API组织
const { loading, data, fetchData } = useUserData()

// 5. 生命周期
onMounted(() => {
  fetchData()
})
</script>
```

### 数据库设计规范

#### 表设计规范
```sql
-- 1. 命名规范
CREATE TABLE user_profile (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    email VARCHAR(100) NOT NULL COMMENT '邮箱',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    UNIQUE KEY uk_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- 2. 索引设计原则
-- 主键索引：自增ID
-- 唯一索引：邮箱、用户名等唯一字段
-- 普通索引：查询频繁的字段
-- 联合索引：多字段查询场景
```

#### 查询优化规范
```sql
-- 1. 避免全表扫描
SELECT * FROM user WHERE username = ? AND status = 1; -- 好
SELECT * FROM user WHERE status = 1; -- 可能不好

-- 2. 合理使用索引
EXPLAIN SELECT * FROM user WHERE username = 'test';

-- 3. 避免SELECT *
SELECT id, username, email FROM user WHERE id = ?;
```

### API设计规范

#### RESTful API规范
```yaml
# 1. 资源命名
/users              # 用户集合
/users/{id}         # 特定用户
/users/{id}/orders  # 用户的订单

# 2. HTTP方法使用
GET     /users              # 查询用户列表
POST    /users              # 创建用户
GET     /users/{id}         # 查询特定用户
PUT     /users/{id}         # 更新用户（全量）
PATCH   /users/{id}         # 更新用户（部分）
DELETE  /users/{id}         # 删除用户

# 3. 响应格式
{
  "code": 200,
  "message": "成功",
  "data": {
    "items": [],
    "total": 0,
    "page": 1,
    "size": 10
  },
  "timestamp": 1640995200000
}
```

#### 错误码规范
```java
public enum ErrorCode {
    // 系统错误 (10000-19999)
    SYSTEM_ERROR(10000, "系统错误"),
    PARAM_ERROR(10001, "参数错误"),
    
    // 业务错误 (20000-29999)
    USER_NOT_FOUND(20001, "用户不存在"),
    USER_EXISTS(20002, "用户已存在"),
    
    // 权限错误 (30000-39999)
    UNAUTHORIZED(30001, "未授权"),
    FORBIDDEN(30002, "禁止访问");
    
    private final int code;
    private final String message;
    
    // 构造方法等
}
```

### 安全规范

#### 认证授权规范
```java
// 1. JWT认证
@Component
public class JwtTokenProvider {
    public String generateToken(UserDetails userDetails) {
        // 生成JWT token
    }
    
    public boolean validateToken(String token) {
        // 验证token
    }
}

// 2. 权限控制
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        // 管理员权限
    }
    
    @PreAuthorize("hasPermission('user:delete')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        // 删除用户权限
    }
}
```

#### 数据安全规范
```java
// 1. 密码加密
@Service
public class PasswordService {
    
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
    
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}

// 2. SQL注入防护
@Mapper
public interface UserMapper {
    // 使用#{}防止SQL注入
    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(@Param("username") String username);
}
```

### 性能优化规范

#### 后端性能优化
```java
// 1. 缓存使用
@Service
public class UserService {
    
    @Cacheable(value = "users", key = "#id")
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    
    @CacheEvict(value = "users", key = "#user.id")
    public User updateUser(User user) {
        return userRepository.save(user);
    }
}

// 2. 异步处理
@Service
public class FileProcessService {
    
    @Async("fileProcessExecutor")
    public CompletableFuture<ProcessResult> processFile(File file) {
        // 异步文件处理
        return CompletableFuture.completedFuture(result);
    }
}
```

#### 前端性能优化
```vue
<template>
  <!-- 1. 图片懒加载 -->
  <img v-lazy="imageUrl" alt="图片" />
  
  <!-- 2. 虚拟滚动 -->
  <VirtualList :items="largeList" :item-size="50">
    <template #default="{ item }">
      <div>{{ item.name }}</div>
    </template>
  </VirtualList>
  
  <!-- 3. 代码分割 -->
  <Suspense>
    <template #default>
      <AsyncComponent />
    </template>
    <template #fallback>
      <LoadingSpinner />
    </template>
  </Suspense>
</template>

<script setup>
// 4. 按需导入
import { debounce, throttle } from 'lodash-es'

// 5. 组件懒加载
const AsyncComponent = defineAsyncComponent(() => 
  import('./AsyncComponent.vue')
)
</script>
```

### 测试规范

#### 单元测试规范
```java
@SpringBootTest
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    void shouldCreateUserSuccessfully() {
        // Given
        UserCreateRequest request = new UserCreateRequest("test", "test@example.com");
        User expectedUser = new User(1L, "test", "test@example.com");
        
        when(userRepository.save(any(User.class))).thenReturn(expectedUser);
        
        // When
        User result = userService.createUser(request);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("test");
        verify(userRepository).save(any(User.class));
    }
}
```

#### 集成测试规范
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class UserControllerIntegrationTest {
    
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");
    
    @Test
    void shouldGetUserById() {
        // 集成测试逻辑
    }
}
```

### 部署规范

#### Dockerfile规范
```dockerfile
# 多阶段构建
FROM maven:3.8-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jre-slim
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### Kubernetes部署规范
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: user-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: user-service
  template:
    metadata:
      labels:
        app: user-service
    spec:
      containers:
      - name: user-service
        image: user-service:latest
        ports:
        - containerPort: 8080
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
```

## 最佳实践

### 微服务最佳实践
1. **服务粒度**：单个服务不超过5个核心实体
2. **数据一致性**：使用Saga模式处理分布式事务
3. **服务发现**：使用Consul或Eureka进行服务注册发现
4. **配置管理**：使用配置中心统一管理配置

### 数据库最佳实践
1. **读写分离**：主从复制，读写分离
2. **分库分表**：数据量大的表进行分片
3. **索引优化**：合理创建和使用索引
4. **连接池**：使用连接池管理数据库连接

### 缓存最佳实践
1. **多级缓存**：本地缓存+分布式缓存
2. **缓存策略**：设置合理的过期时间
3. **缓存穿透**：使用布隆过滤器防止穿透
4. **缓存雪崩**：设置不同的过期时间

### 监控最佳实践
1. **指标收集**：收集关键业务和技术指标
2. **日志聚合**：使用ELK栈进行日志分析
3. **链路追踪**：使用Jaeger或Zipkin追踪请求
4. **告警规则**：设置合理的告警阈值

## 部署架构

### 1. 开发环境
```yaml
# docker-compose.yml 开发环境
version: '3.8'
services:
  postgres:
    image: postgres:14
    environment:
      POSTGRES_DB: avstream
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: password
```

### 2. 生产环境
```yaml
# Kubernetes部署
apiVersion: apps/v1
kind: Deployment
metadata:
  name: ai-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: ai-service
  template:
    metadata:
      labels:
        app: ai-service
    spec:
      containers:
      - name: ai-service
        image: avstream/ai-service:latest
        ports:
        - containerPort: 8080
```

## 总结

AV Stream Space 架构设计遵循了现代化微服务架构的最佳实践，具备以下特点：

1. **高可用性**：多副本部署，故障自动恢复
2. **可扩展性**：水平扩展支持，弹性伸缩
3. **安全性**：多层次安全防护，数据加密
4. **性能优化**：缓存策略，异步处理
5. **可维护性**：模块化设计，清晰的责任边界

该架构能够支撑大规模的音视频处理需求，为业务发展提供了坚实的技术基础。