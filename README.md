# 音视频驾驶舱平台 (AV Stream Cockpit Platform)

## 项目简介

音视频驾驶舱平台是一个高科技风格的音视频处理、分发和分析的一站式解决方案。平台覆盖音视频处理的全生命周期，包括直播、剪辑、监控、AI生成等多种场景，打造集采集、处理、分发、分析于一体的音视频智能操作中心。

## 技术栈

### 后端技术栈
- **框架**: Spring Boot 3.x + Spring Cloud + Spring WebFlux
- **安全**: Spring Security OAuth2 + JWT
- **数据库**: PostgreSQL 15 + Redis 7 + MongoDB + InfluxDB
- **消息队列**: RabbitMQ + Kafka
- **存储**: MinIO + NFS
- **音视频处理**: FFmpeg + GStreamer + WebRTC + MediaMTX
- **AI能力**: Whisper + Stable Diffusion + TTS
- **部署**: Docker + Kubernetes + Helm
- **监控**: Prometheus + Grafana + ELK

### 前端技术栈
- **框架**: Vue3 + TypeScript + Pinia + Vite
- **UI组件**: Element Plus
- **可视化**: Three.js + ECharts
- **音视频**: WebRTC + MediaRecorder API
- **样式**: SCSS + Tailwind CSS

## 项目结构

```
├── backend/                    # 后端微服务
│   ├── auth-service/           # 认证授权服务
│   ├── media-service/          # 媒体资产管理服务
│   ├── live-service/           # 直播流管理服务
│   ├── ai-service/             # AI生成服务
│   ├── monitor-service/        # 监控告警服务
│   └── gateway/                # API网关
├── frontend/                   # 前端应用
│   ├── src/
│   │   ├── views/              # 页面视图
│   │   ├── components/         # 组件
│   │   ├── api/                # API接口
│   │   └── utils/              # 工具函数
├── docs/                       # 完整文档体系
│   ├── api/                    # API接口文档
│   ├── architecture/           # 架构设计文档
│   ├── deployment/             # 部署手册
│   └── user-guide/             # 用户手册
├── docker/                     # Docker配置
└── k8s/                        # Kubernetes配置
```

## 核心功能模块

### 1. 全景驾驶舱
- 3D音视频流拓扑可视化
- 实时系统监控仪表盘
- 全局搜索与快捷操作

### 2. 直播控制中心
- 多画面监控墙
- 实时信号质量监控
- 导播切换与互动管理

### 3. 媒体编辑工作站
- 专业时间线编辑器
- 多轨道音视频编辑
- AI智能剪辑功能

### 4. AI创作工作室
- 文字转语音生成
- 图像生成视频
- 智能内容分析

### 5. 监控调度中心
- GIS地图可视化
- 智能告警管理
- 应急预案调度

## 快速开始

### 环境要求
- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Redis 6.0+
- Docker & Docker Compose

### 启动步骤

1. **克隆项目**
```bash
git clone https://github.com/avstream/av-stream-space.git
cd av-stream-space
```

2. **后端启动**
```bash
cd backend
# 配置数据库连接信息
cp application-example.yml application.yml
# 启动所有服务
docker-compose up -d
```

3. **前端启动**
```bash
cd frontend
npm install
npm run dev
```

4. **访问系统**
- 前端地址：http://localhost:3000
- 后端API文档：http://localhost:8080/swagger-ui.html

## 文档体系

项目提供完整的文档体系：

- 📖 **API文档** (`docs/api/`) - 详细的接口说明和使用示例
- 🏗️ **架构文档** (`docs/architecture/`) - 系统架构设计和技术选型
- 🚀 **部署文档** (`docs/deployment/`) - 环境部署和运维指南
- 👨‍💻 **用户手册** (`docs/user-guide/`) - 功能使用和操作指南

## 核心特性

### 技术特性
- 🔥 **微服务架构** - 基于Spring Cloud的分布式架构
- 🚀 **前后端分离** - Vue3 + SpringBoot 3.x
- 🎯 **AI集成** - 集成语音识别、图像处理等AI能力
- 📊 **实时监控** - 完善的系统监控和告警机制
- 🔒 **安全可靠** - 多层次安全防护和数据加密

### 业务特性
- 🎥 **音视频处理** - 支持多种格式的音视频处理
- 📡 **直播管理** - 完整的直播推流和播放管理
- 🤖 **AI增强** - AI驱动的智能处理和分析
- 📈 **数据分析** - 多维度的使用统计和分析

## 项目状态

### 当前版本：v1.0.0 (2024年1月)

#### ✅ 已完成功能
- **认证服务**：用户注册、登录、权限管理
- **AI服务**：语音识别、图像处理、文本分析
- **媒体服务**：音视频上传、转码、处理
- **直播服务**：推流管理、播放控制、录制功能
- **监控服务**：系统监控、性能指标、告警管理
- **前端应用**：响应式界面、组件化开发、状态管理

#### 🔄 进行中功能
- **性能优化**：系统响应时间优化
- **安全增强**：多因素认证、数据加密
- **移动端适配**：移动端界面优化

#### 📋 计划功能
- **微前端架构**：模块联邦实现
- **AI模型训练**：自定义模型训练平台
- **大数据分析**：用户行为分析系统

### 技术栈状态

| 技术组件 | 状态 | 版本 | 备注 |
|---------|------|------|------|
| Spring Boot | ✅ 完成 | 3.1.0 | 后端核心框架 |
| Vue 3 | ✅ 完成 | 3.3.0 | 前端核心框架 |
| MySQL | ✅ 完成 | 8.0.0 | 主数据库 |
| Redis | ✅ 完成 | 7.0.0 | 缓存数据库 |
| Docker | ✅ 完成 | 24.0.0 | 容器化部署 |
| Kubernetes | 🔄 进行中 | 1.27.0 | 生产环境部署 |

## 部署状态

### 开发环境
- ✅ 本地开发环境搭建完成
- ✅ Docker Compose一键部署
- ✅ 前后端联调测试通过

### 测试环境
- 🔄 自动化测试环境搭建中
- 🔄 性能测试环境准备中

### 生产环境
- 📋 生产环境部署规划中
- 📋 高可用架构设计进行中

## 性能指标

### 后端性能
- API响应时间：P95 ≤ 300ms
- 并发用户数：支持1000+并发
- 系统可用性：99.9%

### 前端性能
- 首屏加载时间：≤ 2.5s
- 页面响应时间：≤ 1.0s
- 移动端适配：完全响应式

## 安全特性

### 已实现安全措施
- ✅ JWT认证授权
- ✅ RBAC权限控制
- ✅ SQL注入防护
- ✅ XSS攻击防护
- ✅ CSRF攻击防护
- ✅ 数据加密存储

### 计划安全措施
- 🔄 多因素认证
- 🔄 安全审计日志
- 🔄 漏洞扫描系统

## 监控告警

### 系统监控
- ✅ CPU、内存、磁盘监控
- ✅ 服务健康状态监控
- ✅ 业务指标监控
- ✅ 错误日志收集

### 告警机制
- ✅ 关键指标阈值告警
- ✅ 服务异常告警
- ✅ 性能瓶颈告警

## 项目文档

本项目提供完整的文档体系，帮助您快速上手和使用系统：

### 📚 核心文档

- **📖 [用户手册](docs/user-guide/README.md)** - 详细的功能使用指南和操作说明
- **🔧 [部署手册](docs/deployment/README.md)** - 完整的部署指南和配置说明
- **🌐 [API文档](docs/api/README.md)** - 详细的API接口说明和使用示例
- **🏗️ [架构设计](docs/architecture/README.md)** - 系统架构设计和技术选型说明

### 🎯 开发规范

本项目遵循 **5A6S 开发规范**:

- **5A**: Architecture, API, Automation, Assurance, Agility
- **6S**: Structure, Standards, Security, Stability, Scalability, Sustainability

详细规范请参考架构设计文档中的开发规范章节。

## 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 许可证

本项目采用 MIT 许可证。详情请查看 [LICENSE](LICENSE) 文件。

## 联系方式

- 项目主页: [项目主页链接]
- 问题反馈: [GitHub Issues]
- 邮箱: [联系邮箱]

---

**音视频驾驶舱平台** - 让音视频处理更智能、更高效！
