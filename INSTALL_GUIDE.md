# 项目运行指南（助教版）

本文档提供使用已构建好的 Docker 镜像快速运行项目的详细步骤。

## 📦 前置要求

- Docker >= 20.10
- Docker Compose >= 2.0

## 🚀 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/calculator-jsj/ai-travel-planner.git
cd ai-travel-planner
```

### 2. 配置环境变量

复制环境变量示例文件：

```bash
cp env.example .env
```

编辑 `.env` 文件，配置以下内容：

```env
# 数据库配置（使用默认值或自定义）
MYSQL_ROOT_PASSWORD=123456
MYSQL_DATABASE=ai_travel
MYSQL_USER=travel_user
MYSQL_PASSWORD=travel_pass

# ========== 必须配置：API Keys ==========
# 阿里云百炼平台 API Key（用于AI行程规划和预算分析）
API_KEY=your-aliyun-api-key-here

# 高德地图 API Key（用于地图展示和地理位置服务）
GAODE_MAP_KEY=your-gaode-map-key-here
```

**重要**：
- `API_KEY` 和 `GAODE_MAP_KEY` 必须配置，否则部分功能无法使用
- API Key 请参考 README.md 中的获取方法
- 如果助教有阿里云百炼平台的 key，可以直接使用

### 3. 拉取 Docker 镜像

如果还没有拉取镜像，需要先登录阿里云容器镜像服务：

```bash
# 登录阿里云容器镜像服务（如果需要）
docker login --username=your-username crpi-92srg3fhfl4qlq1a.cn-hangzhou.personal.cr.aliyuncs.com
```

拉取镜像：

```bash
docker pull crpi-92srg3fhfl4qlq1a.cn-hangzhou.personal.cr.aliyuncs.com/jsj-namespace/ai-travel-backend:latest
docker pull crpi-92srg3fhfl4qlq1a.cn-hangzhou.personal.cr.aliyuncs.com/jsj-namespace/ai-travel-frontend:latest
```

### 4. 启动服务

使用已构建好的镜像启动所有服务：

```bash
docker-compose -f docker-compose.prod.yml up -d
```
#### 如果启动mysql报错，可能是因为3306端口被占用，需要先停止占用该端口的进程。
```bash
#查找占用 3306 端口的进程
netstat -tuln | grep 3306

#停止占用该端口的进程（例如 PID 为 1234）
kill -9 1234
```
### 5. 初始化数据库

等待 MySQL 服务启动完成后（约 30 秒），执行数据库初始化脚本：

```bash
# 直接使用项目包含的 SQL 脚本
docker exec -i ai-travel-mysql mysql -uroot -p123456 ai_travel < backend/sql/init.sql

# 如果 travel_user 或 root 使用 caching_sha2_password，Spring Boot 连接可能出错。推荐改成 mysql_native_password：
docker exec -it ai-travel-mysql mysql -uroot -p123456
ALTER USER 'travel_user'@'%' IDENTIFIED WITH mysql_native_password BY 'travel_pass';
FLUSH PRIVILEGES;


### 6. 检查服务状态

```bash
docker-compose -f docker-compose.prod.yml ps

# 必要时可以重启后端服务，确保数据库初始化完成：
docker restart ai-travel-backend
```

应该看到三个服务都在运行：
- `ai-travel-mysql` (MySQL 数据库)
- `ai-travel-backend` (后端服务，端口 8080)
- `ai-travel-frontend` (前端服务，端口 80)

### 7. 查看日志（验证服务是否正常）

```bash
# 查看所有服务日志
docker-compose -f docker-compose.prod.yml logs -f

# 查看后端日志（检查是否连接数据库成功）
docker-compose -f docker-compose.prod.yml logs backend

# 查看前端日志
docker-compose -f docker-compose.prod.yml logs frontend

# 查看数据库日志
docker-compose -f docker-compose.prod.yml logs mysql
```

### 8. 访问应用

- **前端地址**：http://localhost
- **后端 API**：http://localhost:8080/api

### 9. 测试功能

1. **注册/登录**：访问 http://localhost，注册新用户或登录
2. **创建行程**：使用语音或文字输入创建旅行计划
3. **查看地图**：创建行程后，在地图上查看景点路线
4. **预算管理**：记录旅行支出，查看 AI 预算分析

## 🔍 验证服务是否正常运行

### 检查数据库连接

查看后端日志，应该看到类似信息：
```
HikariPool-1 - Starting...
HikariPool-1 - Start completed.
```

如果没有错误信息，说明数据库连接成功。

### 检查前端服务

直接访问 http://localhost，应该能看到登录页面。

## 🛠️ 常见问题

### 1. 数据库连接失败

**问题**：后端日志显示无法连接数据库

**解决**：
```bash
# 检查 MySQL 容器是否运行
docker ps | grep mysql

# 检查 MySQL 日志
docker-compose -f docker-compose.prod.yml logs mysql

# 重启服务
docker-compose -f docker-compose.prod.yml restart backend
```

### 2. 镜像拉取失败

**问题**：无法拉取阿里云镜像

**解决**：
```bash
# 登录阿里云容器镜像服务
docker login --username=your-username crpi-92srg3fhfl4qlq1a.cn-hangzhou.personal.cr.aliyuncs.com

# 或者使用本地构建（如果有源代码）
docker-compose up -d
```

### 3. 前端无法访问后端 API

**问题**：前端显示 API 调用失败

**解决**：
- 检查后端服务是否正常运行：`docker-compose -f docker-compose.prod.yml ps backend`
- 检查后端日志：`docker-compose -f docker-compose.prod.yml logs backend`
- 检查网络连接：确保前端和后端在同一个 Docker 网络中

### 4. 数据库表不存在

**问题**：访问应用时提示表不存在

**解决**：
- 检查是否执行了数据库初始化脚本
- 手动执行 SQL 创建表（见步骤 5）

### 5. API Key 未配置

**问题**：AI 功能无法使用，地图无法显示

**解决**：
- 检查 `.env` 文件中的 `API_KEY` 和 `GAODE_MAP_KEY` 是否配置
- 确保 API Key 有效（3个月内有效）
- 重启服务：`docker-compose -f docker-compose.prod.yml restart`

## 📝 停止服务

```bash
docker-compose -f docker-compose.prod.yml down
```

## 🗑️ 清理数据（重新开始）

```bash
# 停止并删除所有容器和数据卷
docker-compose -f docker-compose.prod.yml down -v

# 重新启动
docker-compose -f docker-compose.prod.yml up -d
```

## 📞 需要帮助？

如果遇到问题：
1. 查看日志：`docker-compose -f docker-compose.prod.yml logs -f`
2. 检查服务状态：`docker-compose -f docker-compose.prod.yml ps`
3. 查看项目 README.md 获取更多信息
4. 提交 Issue 到 GitHub 仓库

---

**镜像地址**：
- 后端：`crpi-92srg3fhfl4qlq1a.cn-hangzhou.personal.cr.aliyuncs.com/jsj-namespace/ai-travel-backend:latest`
- 前端：`crpi-92srg3fhfl4qlq1a.cn-hangzhou.personal.cr.aliyuncs.com/jsj-namespace/ai-travel-frontend:latest`
