# AV Stream Space API 文档

## 概述

AV Stream Space 是一个集成了音视频处理、直播管理、AI服务的全栈应用平台。本文档详细介绍了系统的API接口规范和使用方法。

## 认证服务 (auth-service)

### 用户认证

#### 用户登录
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password123"
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userInfo": {
      "id": 1,
      "username": "admin",
      "email": "admin@example.com",
      "role": "ADMIN"
    }
  },
  "timestamp": 1650000000000
}
```

#### 用户注册
```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "username": "newuser",
  "password": "password123",
  "email": "newuser@example.com"
}
```

#### 获取当前用户信息
```http
GET /api/v1/auth/me
Authorization: Bearer {token}
```

## AI服务 (ai-service)

### 语音处理

#### 语音识别
```http
POST /api/v1/ai/speech/recognition
Content-Type: multipart/form-data
Authorization: Bearer {token}

{
  "audioFile": "音频文件",
  "language": "zh-CN"
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "语音识别成功",
  "data": {
    "text": "识别出的文本内容",
    "confidence": 0.95,
    "duration": 5.2
  },
  "timestamp": 1650000000000
}
```

#### 文本转语音
```http
POST /api/v1/ai/speech/synthesis
Content-Type: application/json
Authorization: Bearer {token}

{
  "text": "要转换为语音的文本",
  "voice": "zh-CN-XiaoxiaoNeural",
  "speed": 1.0
}
```

#### 音频增强
```http
POST /api/v1/ai/speech/enhancement
Content-Type: multipart/form-data
Authorization: Bearer {token}

{
  "audioFile": "音频文件",
  "enhancementType": "NOISE_REDUCTION"
}
```

### 图像处理

#### 图像识别
```http
POST /api/v1/ai/image/recognition
Content-Type: multipart/form-data
Authorization: Bearer {token}

{
  "imageFile": "图像文件",
  "recognitionType": "OBJECT_DETECTION"
}
```

#### 图像生成
```http
POST /api/v1/ai/image/generation
Content-Type: application/json
Authorization: Bearer {token}

{
  "prompt": "一只可爱的猫咪在花园里玩耍",
  "style": "REALISTIC",
  "size": "1024x1024"
}
```

#### 风格转换
```http
POST /api/v1/ai/image/style-transfer
Content-Type: multipart/form-data
Authorization: Bearer {token}

{
  "contentImage": "内容图像",
  "styleImage": "风格图像",
  "strength": 0.7
}
```

## 媒体服务 (media-service)

### 视频处理

#### 视频转码
```http
POST /api/v1/media/video/transcode
Content-Type: multipart/form-data
Authorization: Bearer {token}

{
  "videoFile": "视频文件",
  "targetFormat": "mp4",
  "quality": "high"
}
```

#### 视频剪辑
```http
POST /api/v1/media/video/clip
Content-Type: application/json
Authorization: Bearer {token}

{
  "videoId": "视频ID",
  "startTime": 10,
  "endTime": 30
}
```

### 音频处理

#### 音频提取
```http
POST /api/v1/media/audio/extract
Content-Type: multipart/form-data
Authorization: Bearer {token}

{
  "videoFile": "视频文件",
  "audioFormat": "mp3"
}
```

## 直播服务 (live-service)

### 直播管理

#### 创建直播
```http
POST /api/v1/live/streams
Content-Type: application/json
Authorization: Bearer {token}

{
  "title": "直播标题",
  "description": "直播描述",
  "category": "教育"
}
```

#### 获取直播列表
```http
GET /api/v1/live/streams
Authorization: Bearer {token}
```

#### 开始直播
```http
POST /api/v1/live/streams/{streamId}/start
Authorization: Bearer {token}
```

#### 结束直播
```http
POST /api/v1/live/streams/{streamId}/stop
Authorization: Bearer {token}
```

## 错误码说明

| 错误码 | 说明 | 解决方案 |
|--------|------|----------|
| 200 | 成功 | - |
| 400 | 请求参数错误 | 检查请求参数格式 |
| 401 | 未授权 | 检查token是否有效 |
| 403 | 权限不足 | 检查用户权限 |
| 404 | 资源不存在 | 检查资源ID是否正确 |
| 500 | 服务器内部错误 | 联系系统管理员 |
| 10001 | 用户不存在 | 检查用户名是否正确 |
| 10002 | 密码错误 | 检查密码是否正确 |
| 20001 | 文件格式不支持 | 检查文件格式 |
| 20002 | 文件大小超限 | 检查文件大小限制 |

## 认证方式

所有API请求都需要在Header中包含认证信息：
```
Authorization: Bearer {jwt_token}
```

## 响应格式

所有API响应都遵循统一的格式：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1650000000000
}
```

## 限流策略

- 普通用户：100次/分钟
- VIP用户：500次/分钟
- 管理员：1000次/分钟

## 版本控制

当前API版本：v1
所有API路径都以 `/api/v1/` 开头

## 具体服务API详情

### 认证服务详细API

#### 用户管理
```http
# 获取用户列表
GET /api/v1/auth/users?page=1&size=20
Authorization: Bearer {token}

# 创建用户
POST /api/v1/auth/users
Content-Type: application/json
Authorization: Bearer {token}

{
  "username": "newuser",
  "password": "password123",
  "email": "newuser@example.com",
  "role": "USER"
}

# 更新用户
PUT /api/v1/auth/users/{userId}
Content-Type: application/json
Authorization: Bearer {token}

{
  "email": "updated@example.com",
  "role": "ADMIN"
}

# 删除用户
DELETE /api/v1/auth/users/{userId}
Authorization: Bearer {token}
```

#### 权限管理
```http
# 获取权限列表
GET /api/v1/auth/permissions
Authorization: Bearer {token}

# 分配权限
POST /api/v1/auth/users/{userId}/permissions
Content-Type: application/json
Authorization: Bearer {token}

{
  "permissions": ["user:read", "user:write"]
}
```

### AI服务详细API

#### 批量处理
```http
# 批量语音识别
POST /api/v1/ai/speech/batch-recognition
Content-Type: multipart/form-data
Authorization: Bearer {token}

{
  "audioFiles": [文件1, 文件2, 文件3],
  "language": "zh-CN",
  "batchSize": 5
}

# 批量图像生成
POST /api/v1/ai/image/batch-generation
Content-Type: application/json
Authorization: Bearer {token}

{
  "prompts": [
    {"prompt": "描述1", "style": "REALISTIC"},
    {"prompt": "描述2", "style": "ANIME"}
  ],
  "batchSize": 3
}
```

#### 处理历史
```http
# 获取处理历史
GET /api/v1/ai/history?type=SPEECH&page=1&size=10
Authorization: Bearer {token}

# 删除历史记录
DELETE /api/v1/ai/history/{recordId}
Authorization: Bearer {token}
```

### 媒体服务详细API

#### 视频处理高级功能
```http
# 视频水印添加
POST /api/v1/media/video/watermark
Content-Type: multipart/form-data
Authorization: Bearer {token}

{
  "videoFile": "视频文件",
  "watermarkImage": "水印图片",
  "position": "TOP_RIGHT",
  "opacity": 0.7
}

# 视频截图
POST /api/v1/media/video/screenshot
Content-Type: application/json
Authorization: Bearer {token}

{
  "videoId": "视频ID",
  "timestamp": 15.5,
  "quality": "HIGH"
}
```

#### 音频处理高级功能
```http
# 音频合并
POST /api/v1/media/audio/merge
Content-Type: multipart/form-data
Authorization: Bearer {token}

{
  "audioFiles": [文件1, 文件2],
  "outputFormat": "mp3",
  "fadeIn": 2,
  "fadeOut": 3
}

# 音频分割
POST /api/v1/media/audio/split
Content-Type: application/json
Authorization: Bearer {token}

{
  "audioId": "音频ID",
  "segments": [
    {"start": 0, "end": 30},
    {"start": 30, "end": 60}
  ]
}
```

### 直播服务详细API

#### 直播统计
```http
# 获取直播统计数据
GET /api/v1/live/streams/{streamId}/statistics
Authorization: Bearer {token}

# 获取直播质量报告
GET /api/v1/live/streams/{streamId}/quality-report
Authorization: Bearer {token}

# 获取观众列表
GET /api/v1/live/streams/{streamId}/viewers
Authorization: Bearer {token}
```

#### 直播录制
```http
# 开始录制
POST /api/v1/live/streams/{streamId}/recording/start
Authorization: Bearer {token}

# 停止录制
POST /api/v1/live/streams/{streamId}/recording/stop
Authorization: Bearer {token}

# 获取录制列表
GET /api/v1/live/recordings?streamId={streamId}&page=1&size=10
Authorization: Bearer {token}
```

### 监控服务API

#### 系统监控
```http
# 获取系统指标
GET /api/v1/monitor/system
Authorization: Bearer {token}

# 获取服务状态
GET /api/v1/monitor/services
Authorization: Bearer {token}

# 获取性能指标
GET /api/v1/monitor/performance
Authorization: Bearer {token}
```

#### 告警管理
```http
# 获取告警列表
GET /api/v1/monitor/alerts?status=ACTIVE&page=1&size=20
Authorization: Bearer {token}

# 创建告警规则
POST /api/v1/monitor/alerts/rules
Content-Type: application/json
Authorization: Bearer {token}

{
  "name": "CPU使用率过高",
  "metric": "system.cpu.usage",
  "threshold": 80,
  "operator": "GREATER_THAN",
  "duration": 300
}

# 处理告警
PUT /api/v1/monitor/alerts/{alertId}
Content-Type: application/json
Authorization: Bearer {token}

{
  "status": "RESOLVED",
  "resolution": "已重启服务"
}
```