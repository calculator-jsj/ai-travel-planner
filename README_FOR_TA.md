# 📋 助教运行指南

## 快速开始（3步）

### 1️⃣ 配置环境变量

```bash
git clone https://github.com/calculator-jsj/ai-travel-planner.git
cd ai-travel-planner
cp env.example .env
```

编辑 `.env` 文件，**必须配置 API Keys**：

```env
# API Keys（必须配置，否则部分功能无法使用）
API_KEY=your-aliyun-api-key-here        # 阿里云百炼平台 API Key（如有）
GAODE_MAP_KEY=your-gaode-map-key-here    # 高德地图 API Key
```

### 2️⃣ 启动服务

```bash
docker-compose -f docker-compose.prod.yml up -d
```

### 3️⃣ 初始化数据库

等待 30 秒后执行：

```bash
docker exec -i ai-travel-mysql mysql -uroot -p123456 ai_travel < backend/sql/init.sql
```

## ✅ 验证

- 前端：http://localhost
- 后端：http://localhost:8080/api

## 📦 使用的镜像

- **后端**：`crpi-92srg3fhfl4qlq1a.cn-hangzhou.personal.cr.aliyuncs.com/jsj-namespace/ai-travel-backend:latest`
- **前端**：`crpi-92srg3fhfl4qlq1a.cn-hangzhou.personal.cr.aliyuncs.com/jsj-namespace/ai-travel-frontend:latest`

## 🔍 检查服务

```bash
# 查看服务状态
docker-compose -f docker-compose.prod.yml ps

# 查看日志
docker-compose -f docker-compose.prod.yml logs -f
```

## ⚠️ 注意事项

1. **API Keys 必须配置**：如果没有配置，AI 功能和地图功能将无法使用
2. **数据库初始化**：首次运行必须执行数据库初始化脚本
3. **端口占用**：确保 80 和 8080 端口未被占用

## 🐛 常见问题

### 数据库连接失败
```bash
docker-compose -f docker-compose.prod.yml logs mysql
docker-compose -f docker-compose.prod.yml restart backend
```

### 镜像拉取失败
如果无法拉取镜像，可以使用本地构建：
```bash
docker-compose up -d
```

## 📚 详细文档

- [INSTALL_GUIDE.md](./INSTALL_GUIDE.md) - 完整安装指南
- [RUN.md](./RUN.md) - 快速运行指南
- [README.md](./README.md) - 项目完整文档

