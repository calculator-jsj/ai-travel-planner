# 🚀 快速运行指南（助教版）

## 一、准备工作

1. **确保已安装 Docker 和 Docker Compose**
   ```bash
   docker --version
   docker-compose --version
   ```

2. **克隆项目**
   ```bash
   git clone https://github.com/calculator-jsj/ai-travel-planner.git
   cd ai-travel-planner
   ```

## 二、配置环境变量

```bash
# 复制环境变量文件
cp env.example .env
```

编辑 `.env` 文件，**必须配置以下 API Keys**：

```env
# 数据库配置（默认即可）
MYSQL_ROOT_PASSWORD=123456
MYSQL_DATABASE=ai_travel
MYSQL_USER=travel_user
MYSQL_PASSWORD=travel_pass

# ========== 必须配置的 API Keys ==========
API_KEY=your-aliyun-api-key-here        # 阿里云百炼平台 API Key
GAODE_MAP_KEY=your-gaode-map-key-here   # 高德地图 API Key
```

**如果助教有阿里云百炼平台的 key，请填入 `API_KEY`**  
**高德地图 API Key 需要自行申请或使用测试 key**

## 三、启动服务

### 方式一：使用已构建的镜像（推荐）

```bash
# 启动所有服务
docker-compose -f docker-compose.prod.yml up -d

# 等待 MySQL 启动（约 30 秒）后，初始化数据库
docker exec -i ai-travel-mysql mysql -uroot -p123456 ai_travel < backend/sql/init.sql
```

### 方式二：本地构建（如果镜像无法拉取）

```bash
docker-compose up -d
```

## 四、检查服务状态

```bash
# 查看所有服务状态
docker-compose ps

# 查看服务日志
docker-compose logs -f
```

## 五、访问应用

- **前端地址**：http://localhost
- **后端 API**：http://localhost:8080/api

## 六、验证功能

1. 访问 http://localhost，应该能看到登录页面
2. 注册一个新用户
3. 尝试创建行程（需要 API Key 配置正确）
4. 查看地图功能（需要高德地图 Key）

## 常见问题

### Q: 数据库连接失败？
```bash
# 检查 MySQL 是否运行
docker ps | grep mysql

# 查看 MySQL 日志
docker-compose logs mysql
```

### Q: 前端无法访问？
```bash
# 检查前端容器
docker ps | grep frontend

# 查看前端日志
docker-compose logs frontend
```

### Q: AI 功能无法使用？
- 检查 `.env` 文件中的 `API_KEY` 是否配置
- 确保 API Key 有效（3个月内）

### Q: 地图无法显示？
- 检查 `.env` 文件中的 `GAODE_MAP_KEY` 是否配置
- 检查浏览器控制台是否有错误

## 停止服务

```bash
docker-compose -f docker-compose.prod.yml down
```

## 重新开始（清理所有数据）

```bash
docker-compose -f docker-compose.prod.yml down -v
docker-compose -f docker-compose.prod.yml up -d
```

---

**详细文档**：查看 [INSTALL_GUIDE.md](./INSTALL_GUIDE.md) 获取完整部署说明

