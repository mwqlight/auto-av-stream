-- AV Stream 数据库初始化脚本
-- 创建所有微服务所需的数据库和用户

-- 创建主数据库用户
CREATE USER avstream_user WITH PASSWORD 'avstream_password';

-- 创建认证服务数据库
CREATE DATABASE avstream_auth;
GRANT ALL PRIVILEGES ON DATABASE avstream_auth TO avstream_user;

-- 创建媒体服务数据库
CREATE DATABASE avstream_media;
GRANT ALL PRIVILEGES ON DATABASE avstream_media TO avstream_user;

-- 创建AI服务数据库
CREATE DATABASE avstream_ai;
GRANT ALL PRIVILEGES ON DATABASE avstream_ai TO avstream_user;

-- 创建直播服务数据库
CREATE DATABASE avstream_live;
GRANT ALL PRIVILEGES ON DATABASE avstream_live TO avstream_user;

-- 创建监控服务数据库
CREATE DATABASE avstream_monitor;
GRANT ALL PRIVILEGES ON DATABASE avstream_monitor TO avstream_user;

-- 创建网关服务数据库
CREATE DATABASE avstream_gateway;
GRANT ALL PRIVILEGES ON DATABASE avstream_gateway TO avstream_user;

-- 连接到认证服务数据库并创建表结构
\c avstream_auth;

-- 用户表
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    phone VARCHAR(20),
    avatar_url VARCHAR(255),
    enabled BOOLEAN DEFAULT true,
    account_non_expired BOOLEAN DEFAULT true,
    credentials_non_expired BOOLEAN DEFAULT true,
    account_non_locked BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP
);

-- 角色表
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 权限表
CREATE TABLE permissions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description VARCHAR(255),
    resource VARCHAR(50),
    action VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 用户角色关联表
CREATE TABLE user_roles (
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT REFERENCES roles(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, role_id)
);

-- 角色权限关联表
CREATE TABLE role_permissions (
    role_id BIGINT REFERENCES roles(id) ON DELETE CASCADE,
    permission_id BIGINT REFERENCES permissions(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (role_id, permission_id)
);

-- 登录日志表
CREATE TABLE login_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    ip_address VARCHAR(45),
    user_agent TEXT,
    login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    success BOOLEAN DEFAULT true,
    failure_reason VARCHAR(255)
);

-- 初始化默认角色和权限
INSERT INTO roles (name, description) VALUES 
('ROLE_ADMIN', '系统管理员'),
('ROLE_USER', '普通用户'),
('ROLE_GUEST', '访客用户');

INSERT INTO permissions (name, description, resource, action) VALUES
('user:read', '读取用户信息', 'user', 'read'),
('user:write', '写入用户信息', 'user', 'write'),
('user:delete', '删除用户', 'user', 'delete'),
('media:read', '读取媒体文件', 'media', 'read'),
('media:write', '上传媒体文件', 'media', 'write'),
('media:delete', '删除媒体文件', 'media', 'delete'),
('live:read', '查看直播', 'live', 'read'),
('live:write', '创建直播', 'live', 'write'),
('live:delete', '删除直播', 'live', 'delete'),
('ai:read', '使用AI服务', 'ai', 'read'),
('ai:write', '管理AI服务', 'ai', 'write');

-- 为管理员角色分配所有权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p WHERE r.name = 'ROLE_ADMIN';

-- 为用户角色分配基本权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.name = 'ROLE_USER' AND p.name IN ('user:read', 'media:read', 'media:write', 'live:read', 'ai:read');

-- 为访客角色分配只读权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.name = 'ROLE_GUEST' AND p.name IN ('media:read', 'live:read');

-- 创建默认管理员用户（密码：admin123，使用BCrypt加密）
INSERT INTO users (username, email, password, first_name, last_name) VALUES
('admin', 'admin@avstream.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTV5UiC', '系统', '管理员');

-- 为管理员用户分配管理员角色
INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN';

-- 授予用户权限
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO avstream_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO avstream_user;

-- 创建索引
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_login_logs_user_id ON login_logs(user_id);
CREATE INDEX idx_login_logs_login_time ON login_logs(login_time);