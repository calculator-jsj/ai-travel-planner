# 部署指南

本文档提供详细的部署说明，包括 Docker 部署、本地部署和 API Key 配置。

## 📋 目录

- [Docker Compose 一键部署](#docker-compose-一键部署)
- [单独 Docker 部署](#单独-docker-部署)
- [本地开发部署](#本地开发部署)
- [API Key 配置](#api-key-配置)
- [数据库初始化](#数据库初始化)
- [常见问题](#常见问题)

## 🐳 Docker Compose 一键部署

这是最简单的部署方式，适合快速启动和测试。

### 前置要求

- Docker >= 20.10
- Docker Compose >= 2.0

### 步骤

1. **克隆项目**

```bash
git clone <your-repo-url>
cd ai-travel-planner
```

2. **配置环境变量**

复制环境变量示例文件：

```bash
cp env.example .env
```

编辑 `.env` 文件：

```env
# 数据库配置
MYSQL_ROOT_PASSWORD=your_secure_password
MYSQL_DATABASE=ai_travel
MYSQL_USER=travel_user
MYSQL_PASSWORD=your_secure_password

# API Keys（必须配置）
API_KEY=sk-xxxxxxxxxxxxxxxxxxxxx  # 阿里云百炼平台 API Key
GAODE_MAP_KEY=xxxxxxxxxxxxxxxxxxxxx  # 高德地图 API Key

# 前端环境变量
VITE_AMAP_KEY=xxxxxxxxxxxxxxxxxxxxx  # 高德地图 API Key（与上面相同）
VITE_API_BASE_URL=http://localhost:8080/api
```

3. **启动服务**

```bash
docker-compose up -d
```

4. **查看服务状态**

```bash
docker-compose ps
```

应该看到三个服务正在运行：
- `ai-travel-mysql` (MySQL 数据库)
- `ai-travel-backend` (后端服务)
- `ai-travel-frontend` (前端服务)

5. **查看日志**

```bash
# 查看所有服务日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f mysql
```

6. **访问应用**

- 前端：http://localhost
- 后端 API：http://localhost:8080
- API 文档：http://localhost:8080/api/docs (如果有)

### 停止服务

```bash
docker-compose down
```

### 删除数据卷（谨慎操作）

```bash
docker-compose down -v
```

## 🔧 单独 Docker 部署

如果需要单独部署每个服务：

### 1. MySQL 数据库

```bash
docker run -d \
  --name ai-travel-mysql \
  -e MYSQL_ROOT_PASSWORD=123456 \
  -e MYSQL_DATABASE=ai_travel \
  -e MYSQL_USER=travel_user \
  -e MYSQL_PASSWORD=travel_pass \
  -p 3306:3306 \
  -v mysql_data:/var/lib/mysql \
  mysql:8.0
```

等待数据库启动后，执行初始化脚本：

```bash
docker exec -i ai-travel-mysql mysql -uroot -p123456 ai_travel < backend/sql/init.sql
```

### 2. 后端服务

构建镜像：

```bash
cd backend
docker build -t ai-travel-backend:latest .
```

运行容器：

```bash
docker run -d \
  --name ai-travel-backend \
  --link ai-travel-mysql:mysql \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/ai_travel?useSSL=false&characterEncoding=utf8&serverTimezone=Asia/Shanghai \
  -e SPRING_DATASOURCE_USERNAME=travel_user \
  -e SPRING_DATASOURCE_PASSWORD=travel_pass \
  -e API-KEY=your-aliyun-api-key \
  -e GAODE-MAP-KEY=your-gaode-map-key \
  ai-travel-backend:latest
```

### 3. 前端服务

构建镜像：

```bash
cd frontend
docker build --build-arg VITE_AMAP_KEY=your-gaode-map-key -t ai-travel-frontend:latest .
```

运行容器：

```bash
docker run -d \
  --name ai-travel-frontend \
  -p 80:80 \
  ai-travel-frontend:latest
```

## 💻 本地开发部署

### 后端

1. **安装依赖**

```bash
cd backend/AI-Travel-Planner
mvn clean install
```

2. **配置数据库**

编辑 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ai_travel?useSSL=false&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
```

3. **配置 API Keys**

**方式一：环境变量（推荐）**

**Windows (PowerShell):**
```powershell
$env:API-KEY="your-aliyun-api-key"
$env:GAODE-MAP-KEY="your-gaode-map-key"
```

**Linux/Mac:**
```bash
export API-KEY=your-aliyun-api-key
export GAODE-MAP-KEY=your-gaode-map-key
```

**方式二：启动参数**

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--API-KEY=your-key --GAODE-MAP-KEY=your-key"
```

4. **初始化数据库**

```bash
mysql -u root -p ai_travel < backend/sql/init.sql
```

5. **启动服务**

```bash
mvn spring-boot:run
```

### 前端

1. **安装依赖**

```bash
cd frontend
npm install
```

2. **配置环境变量**

创建 `.env` 文件：

```env
VITE_AMAP_KEY=your-gaode-map-key
VITE_API_BASE_URL=http://localhost:8080/api
```

3. **启动开发服务器**

```bash
npm run dev
```

访问：http://localhost:5173

## 🔑 API Key 配置

### 获取 API Key

#### 1. 阿里云百炼平台 API Key

1. 访问 [阿里云百炼控制台](https://bailian.console.aliyun.com/)
2. 登录阿里云账号
3. 创建 API Key
4. 复制并保存 API Key（以 `sk-` 开头）

#### 2. 高德地图 API Key

1. 访问 [高德开放平台](https://console.amap.com/)
2. 注册/登录账号
3. 进入「控制台」→「应用管理」→「我的应用」
4. 创建新应用，选择「Web 服务」类型
5. 添加 Key，获取 API Key

### 配置方式

#### 生产环境（推荐）

使用环境变量配置：

```bash
export API_KEY=your-aliyun-api-key
export GAODE_MAP_KEY=your-gaode-map-key
```

#### 开发环境

可以在前端 UI 中配置（用户中心 → API配置），API Key 存储在浏览器本地。

## 🗄️ 数据库初始化

### 自动初始化（Docker）

如果使用 Docker Compose，数据库会自动初始化（通过 `init.sql` 脚本）。

### 手动初始化

1. **创建数据库**

```sql
CREATE DATABASE ai_travel CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. **执行初始化脚本**

```bash
mysql -u root -p ai_travel < backend/sql/init.sql
```

或者直接在 MySQL 客户端执行：

```sql
USE ai_travel;
SOURCE backend/sql/init.sql;
```

### 数据库备份

```bash
docker exec ai-travel-mysql mysqldump -u root -p ai_travel > backup.sql
```

### 数据库恢复

```bash
docker exec -i ai-travel-mysql mysql -u root -p ai_travel < backup.sql
```

## ❓ 常见问题

### 1. 后端启动失败，提示数据库连接错误

**原因**：数据库未启动或连接配置错误。

**解决**：
- 检查 MySQL 是否运行：`docker-compose ps mysql`
- 检查数据库连接配置是否正确
- 检查数据库是否已初始化

### 2. 前端无法访问后端 API

**原因**：CORS 配置或 API 地址配置错误。

**解决**：
- 检查 `frontend/.env` 中的 `VITE_API_BASE_URL` 配置
- 检查后端 CORS 配置（`CorsConfig.java`）
- 检查后端是否正常启动

### 3. 地图无法显示

**原因**：高德地图 API Key 未配置或配置错误。

**解决**：
- 检查前端 `.env` 中的 `VITE_AMAP_KEY`
- 检查高德地图 API Key 是否有效
- 检查浏览器控制台是否有错误信息

### 4. AI 功能无法使用

**原因**：阿里云 API Key 未配置或配置错误。

**解决**：
- 检查后端环境变量 `API-KEY` 是否设置
- 检查 API Key 是否有效（3个月内有效）
- 检查后端日志是否有错误信息

### 5. Docker 镜像构建失败

**原因**：网络问题或构建参数错误。

**解决**：
- 检查网络连接
- 检查 Dockerfile 语法
- 使用 `--no-cache` 重新构建：
  ```bash
  docker-compose build --no-cache
  ```

### 6. 数据库连接被拒绝

**原因**：数据库未启动或端口被占用。

**解决**：
- 检查数据库容器是否运行
- 检查端口 3306 是否被占用
- 检查数据库健康状态：`docker-compose ps mysql`

## 📝 注意事项

1. **安全性**
   - 生产环境务必修改默认密码
   - 不要将 `.env` 文件提交到代码仓库
   - 使用强密码保护数据库

2. **性能优化**
   - 生产环境建议使用独立的 MySQL 服务器
   - 前端建议使用 CDN 加速静态资源
   - 后端建议配置连接池和缓存

3. **监控**
   - 建议配置日志收集系统
   - 监控容器资源使用情况
   - 设置告警机制

## 📞 获取帮助

如遇到问题，请：
1. 查看项目 [Issues](https://github.com/your-repo/issues)
2. 提交新的 Issue
3. 联系项目维护者
