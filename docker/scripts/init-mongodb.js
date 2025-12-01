// MongoDB初始化脚本
// 创建AV Stream项目所需的集合和索引

db = db.getSiblingDB('avstream');

// 创建AI服务日志集合
db.createCollection('ai_logs', {
    validator: {
        $jsonSchema: {
            bsonType: "object",
            required: ["service", "action", "timestamp", "status"],
            properties: {
                service: {
                    bsonType: "string",
                    description: "AI服务名称"
                },
                action: {
                    bsonType: "string",
                    description: "操作类型"
                },
                timestamp: {
                    bsonType: "date",
                    description: "时间戳"
                },
                status: {
                    bsonType: "string",
                    enum: ["success", "error", "pending"],
                    description: "操作状态"
                },
                userId: {
                    bsonType: "string",
                    description: "用户ID"
                },
                requestData: {
                    bsonType: "object",
                    description: "请求数据"
                },
                responseData: {
                    bsonType: "object",
                    description: "响应数据"
                },
                errorMessage: {
                    bsonType: "string",
                    description: "错误信息"
                },
                processingTime: {
                    bsonType: "int",
                    description: "处理时间(毫秒)"
                }
            }
        }
    }
});

// 创建监控数据集合
db.createCollection('monitor_metrics', {
    timeseries: {
        timeField: "timestamp",
        metaField: "metadata",
        granularity: "minutes"
    },
    validator: {
        $jsonSchema: {
            bsonType: "object",
            required: ["metricName", "timestamp", "value"],
            properties: {
                metricName: {
                    bsonType: "string",
                    description: "指标名称"
                },
                timestamp: {
                    bsonType: "date",
                    description: "时间戳"
                },
                value: {
                    bsonType: "number",
                    description: "指标值"
                },
                metadata: {
                    bsonType: "object",
                    description: "元数据"
                },
                tags: {
                    bsonType: "object",
                    description: "标签"
                }
            }
        }
    }
});

// 创建系统日志集合
db.createCollection('system_logs', {
    validator: {
        $jsonSchema: {
            bsonType: "object",
            required: ["level", "message", "timestamp"],
            properties: {
                level: {
                    bsonType: "string",
                    enum: ["DEBUG", "INFO", "WARN", "ERROR", "FATAL"],
                    description: "日志级别"
                },
                message: {
                    bsonType: "string",
                    description: "日志消息"
                },
                timestamp: {
                    bsonType: "date",
                    description: "时间戳"
                },
                service: {
                    bsonType: "string",
                    description: "服务名称"
                },
                userId: {
                    bsonType: "string",
                    description: "用户ID"
                },
                ipAddress: {
                    bsonType: "string",
                    description: "IP地址"
                },
                requestId: {
                    bsonType: "string",
                    description: "请求ID"
                },
                exception: {
                    bsonType: "string",
                    description: "异常信息"
                }
            }
        }
    }
});

// 创建索引
// AI日志索引
db.ai_logs.createIndex({ "timestamp": -1 });
db.ai_logs.createIndex({ "service": 1, "timestamp": -1 });
db.ai_logs.createIndex({ "userId": 1, "timestamp": -1 });
db.ai_logs.createIndex({ "status": 1, "timestamp": -1 });

// 监控指标索引
db.monitor_metrics.createIndex({ "metricName": 1, "timestamp": -1 });
db.monitor_metrics.createIndex({ "timestamp": -1 });

// 系统日志索引
db.system_logs.createIndex({ "timestamp": -1 });
db.system_logs.createIndex({ "level": 1, "timestamp": -1 });
db.system_logs.createIndex({ "service": 1, "timestamp": -1 });

// 创建用户和权限
if (db.getUser('avstream_user') == null) {
    db.createUser({
        user: "avstream_user",
        pwd: "avstream_password",
        roles: [
            {
                role: "readWrite",
                db: "avstream"
            }
        ]
    });
}

print("MongoDB初始化完成");