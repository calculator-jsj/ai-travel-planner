# AI 旅行规划师

<div align="center">

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Vue](https://img.shields.io/badge/Vue-3.4+-brightgreen.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5+-green.svg)

一个基于 AI 的智能旅行规划 Web 应用，通过语音或文字输入，自动生成个性化旅行路线。

[功能特性](#功能特性) • [技术栈](#技术栈) • [快速开始](#快速开始) • [Docker 部署](#docker-部署) • [使用已构建镜像](#使用已构建的镜像助教运行) • [API Key 配置](#api-key-配置)

</div>

---

## 📖 项目简介

AI 旅行规划师是一款智能旅行规划 Web 应用，旨在简化旅行规划过程。用户可以通过**语音**或**文字**输入旅行需求（目的地、日期、预算、同行人数、旅行偏好等），AI 会自动生成详细的旅行路线，包括：

- 🗺️ **交通路线规划**
- 🏨 **住宿推荐**
- 🎯 **景点推荐**
- 🍽️ **餐饮推荐**
- 💰 **预算分析与费用管理**

所有行程信息都会在地图上直观展示，支持多设备云端同步。

## ✨ 功能特性

### 核心功能

1. **智能行程规划**
   - 支持语音输入（浏览器语音识别 API）
   - 支持文字输入
   - AI 自动生成个性化旅行路线
   - 每日行程详细规划（交通、景点、餐饮、住宿）

2. **地图可视化**
   - 基于高德地图 API 展示景点位置
   - 路线规划和导航
   - 周边服务查询（交通、酒店、餐饮）

3. **费用预算与管理**
   - AI 智能预算分析
   - 支持语音记账
   - 支出分类管理（餐饮、住宿、交通、购物等）
   - 图表统计分析（ECharts）
   - 预算超支提醒

4. **用户管理与数据存储**
   - 用户注册/登录系统
   - 用户偏好设置
   - 云端行程同步
   - 多份旅行计划管理

## 🛠️ 技术栈

### 前端
- **框架**: Vue 3 (Composition API)
- **UI 组件库**: Element Plus
- **构建工具**: Vite
- **HTTP 客户端**: Axios
- **地图服务**: 高德地图 JS API
- **图表库**: ECharts
- **路由**: Vue Router 4

### 后端
- **框架**: Spring Boot 3.5+
- **ORM**: MyBatis
- **数据库**: MySQL 8.0
- **AI 服务**: 阿里云百炼平台 (通义千问)
- **Java 版本**: JDK 17

### 部署
- **容器化**: Docker + Docker Compose
- **Web 服务器**: Nginx (前端)
- **数据库**: MySQL 8.0

## 🚀 快速开始

### 环境要求

- Node.js >= 18.0
- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Docker & Docker Compose (可选，用于容器化部署)

### 本地开发部署

#### 1. 克隆项目

```bash
git clone https://github.com/calculator-jsj/ai-travel-planner.git
cd ai-travel-planner
```

#### 2. 数据库准备

创建 MySQL 数据库：

```sql
CREATE DATABASE ai_travel CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

执行数据库初始化脚本（如果有的话，或者根据实体类手动创建表）。

#### 3. 后端配置

进入后端目录：

```bash
cd backend/AI-Travel-Planner
```

配置 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ai_travel?useSSL=false&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: your_mysql_password

langchain4j:
  open-ai:
    chat-model:
      base-url: https://dashscope.aliyuncs.com/compatible-mode/v1
      api-key: ${API-KEY}  # 通过环境变量或启动参数传入
      model-name: qwen-flash

amap:
  key: ${GAODE-MAP-KEY}  # 通过环境变量或启动参数传入

server:
  port: 8080
```

设置环境变量（或通过启动参数传入）：

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

启动后端：

```bash
mvn clean install
mvn spring-boot:run
```

#### 4. 前端配置

进入前端目录：

```bash
cd frontend
```

创建 `.env` 文件（参考 `env.example`）：

```env
VITE_AMAP_KEY=your-gaode-map-key-here
VITE_API_BASE_URL=http://localhost:8080/api
```

安装依赖并启动：

```bash
npm install
npm run dev
```

访问前端：`http://localhost:5173`

## 🐳 Docker 部署

### 使用 Docker Compose（推荐）

这是最简单的部署方式，一键启动所有服务。

#### 1. 配置环境变量

复制环境变量示例文件：

```bash
cp env.example .env
```

编辑 `.env` 文件，填入你的 API Keys：

```env
# 数据库配置
MYSQL_ROOT_PASSWORD=123456
MYSQL_DATABASE=ai_travel
MYSQL_USER=travel_user
MYSQL_PASSWORD=travel_pass

# API Keys
API_KEY=your-aliyun-api-key-here
GAODE_MAP_KEY=your-gaode-map-key-here

# 前端环境变量
VITE_AMAP_KEY=your-gaode-map-key-here
VITE_API_BASE_URL=http://localhost:8080/api
```

#### 2. 启动服务

```bash
docker-compose up -d
```

#### 3. 查看服务状态

```bash
docker-compose ps
```

#### 4. 查看日志

```bash
# 查看所有服务日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f mysql
```

#### 5. 停止服务

```bash
docker-compose down
```

#### 6. 访问应用

- 前端：http://localhost
- 后端 API：http://localhost:8080

### 使用已构建的镜像（助教运行）

如果已经通过 GitHub Actions 构建好镜像，可以直接使用镜像运行：

```bash
# 1. 配置环境变量
cp env.example .env
# 编辑 .env 文件，填入 API Keys

# 2. 使用生产环境配置文件启动
docker-compose -f docker-compose.prod.yml up -d

# 3. 初始化数据库（等待 MySQL 启动后）
docker exec -i ai-travel-mysql mysql -uroot -p123456 ai_travel < backend/sql/init.sql

# 4. 访问应用
# 前端: http://localhost
# 后端: http://localhost:8080
```

**已构建的镜像地址**：
- 后端：`crpi-92srg3fhfl4qlq1a.cn-hangzhou.personal.cr.aliyuncs.com/jsj-namespace/ai-travel-backend:latest`
- 前端：`crpi-92srg3fhfl4qlq1a.cn-hangzhou.personal.cr.aliyuncs.com/jsj-namespace/ai-travel-frontend:latest`

**详细运行指南请参考 [INSTALL_GUIDE.md](./INSTALL_GUIDE.md)**

### 单独构建 Docker 镜像

#### 构建后端镜像

```bash
cd backend
docker build -t ai-travel-backend:latest .
```

运行后端容器：

```bash
docker run -d \
  --name ai-travel-backend \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/ai_travel?useSSL=false&characterEncoding=utf8 \
  -e SPRING_DATASOURCE_USERNAME=travel_user \
  -e SPRING_DATASOURCE_PASSWORD=travel_pass \
  -e API-KEY=your-aliyun-api-key \
  -e GAODE-MAP-KEY=your-gaode-map-key \
  ai-travel-backend:latest
```

#### 构建前端镜像

```bash
cd frontend
docker build --build-arg VITE_AMAP_KEY=your-gaode-map-key -t ai-travel-frontend:latest .
```

运行前端容器：

```bash
docker run -d \
  --name ai-travel-frontend \
  -p 80:80 \
  ai-travel-frontend:latest
```

### 推送到阿里云镜像仓库

如果需要将镜像推送到阿里云容器镜像服务：

```bash
# 登录阿里云容器镜像服务
docker login --username=your-username registry.cn-hangzhou.aliyuncs.com

# 标记镜像
docker tag ai-travel-backend:latest registry.cn-hangzhou.aliyuncs.com/your-namespace/ai-travel-backend:latest
docker tag ai-travel-frontend:latest registry.cn-hangzhou.aliyuncs.com/your-namespace/ai-travel-frontend:latest

# 推送镜像
docker push registry.cn-hangzhou.aliyuncs.com/your-namespace/ai-travel-backend:latest
docker push registry.cn-hangzhou.aliyuncs.com/your-namespace/ai-travel-frontend:latest
```

## 🔑 API Key 配置

### 获取 API Key

#### 1. 阿里云百炼平台 API Key

用于 AI 行程规划和预算分析。

1. 访问 [阿里云百炼控制台](https://bailian.console.aliyun.com/)
2. 登录并创建 API Key
3. 复制 API Key 备用

#### 2. 高德地图 API Key

用于地图展示和地理位置服务。

1. 访问 [高德开放平台](https://console.amap.com/)
2. 注册/登录账号
3. 创建应用，选择「Web 服务」类型
4. 获取 API Key

### 配置方式

#### 方式一：环境变量（推荐用于生产环境）

通过 `.env` 文件或系统环境变量配置（见 [Docker 部署](#docker-部署)）。

#### 方式二：前端 UI 配置（用于开发测试）

1. 登录应用
2. 进入「用户中心」
3. 切换到「API配置」标签页
4. 输入并保存 API Keys

**注意**：前端 UI 配置的 API Key 仅存储在浏览器本地，不会上传到服务器。这种方式仅适用于开发测试，生产环境请使用环境变量配置。

## 📁 项目结构

```
ai-travel-planner/
├── backend/                    # 后端代码
│   └── AI-Travel-Planner/
│       ├── src/
│       │   ├── main/
│       │   │   ├── java/
│       │   │   │   └── com/ai/aitravelplanner/
│       │   │   │       ├── config/         # 配置类
│       │   │   │       ├── controller/      # 控制器
│       │   │   │       ├── service/         # 业务逻辑
│       │   │   │       ├── mapper/          # MyBatis Mapper
│       │   │   │       ├── entity/          # 实体类
│       │   │   │       └── dto/             # 数据传输对象
│       │   │   └── resources/
│       │   │       ├── application.yml      # 配置文件
│       │   │       └── mapper/              # MyBatis XML
│       └── pom.xml
├── frontend/                   # 前端代码
│   ├── src/
│   │   ├── api/               # API 接口
│   │   ├── components/        # 组件
│   │   ├── pages/             # 页面
│   │   ├── router/            # 路由配置
│   │   └── main.js           # 入口文件
│   ├── Dockerfile
│   ├── nginx.conf
│   └── package.json
├── docker-compose.yml         # Docker Compose 配置
├── env.example                # 环境变量示例
├── .dockerignore
└── README.md                  # 本文档
```

## 🔧 开发指南

### 后端开发

```bash
cd backend/AI-Travel-Planner
mvn clean install
mvn spring-boot:run
```

### 前端开发

```bash
cd frontend
npm install
npm run dev
```

### 构建生产版本

**前端：**
```bash
cd frontend
npm run build
```

**后端：**
```bash
cd backend/AI-Travel-Planner
mvn clean package -DskipTests
```

## 📝 注意事项

### 安全提示

⚠️ **重要**：不要将 API Key 提交到公开的代码仓库！

- 使用 `.env` 文件或环境变量管理敏感信息
- 确保 `.env` 文件已添加到 `.gitignore`
- 生产环境请使用环境变量或密钥管理服务

### 数据库

- 首次运行前，请确保 MySQL 数据库已创建
- 建议使用 UTF8MB4 字符集以支持 emoji 等特殊字符

### 语音识别

- 目前使用浏览器原生 `webkitSpeechRecognition` API
- 需要 HTTPS 环境或 localhost 才能使用
- 建议 Chrome/Edge 浏览器以获得最佳体验

---

**注意**：本项目仅用于学习和教育目的。在生产环境中使用前，请确保已进行充分的安全测试和性能优化。