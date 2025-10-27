# AI 旅行规划师

## 项目简介

AI 旅行规划师是一款智能旅行规划 Web 应用，用户可以通过文字或语音输入旅行计划，AI 自动生成每日行程，包括交通、景点、餐饮和住宿推荐，并在地图上展示景点路线。支持行程管理、预算管理和用户中心功能。

## 技术栈

* 前端：Vue 3 + Element Plus + Axios + 高德地图 API
* 后端：Spring Boot + MyBatis + MySQL + AI 行程生成服务
* 构建工具：Vite

## 功能概览

* 用户注册/登录
* 创建智能行程（文字/语音输入，由 AI 生成行程内容）
* 行程管理与地图回顾
* 预算管理与报表展示
* 用户偏好设置与收藏行程

## 运行步骤

### 前端

```bash
cd frontend
npm install
npm run dev
```

### 后端

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

访问前端：`http://localhost:5173`

## 数据库

* 使用 MySQL，包含用户表、行程表、景点表、预算表等。
* 提供基础测试数据，便于调试。

## 注意事项

* 高德地图 API 需要注册 Key
* AI 行程生成需要后端服务支持
* 前后端接口路径保持一致，如 `/api/users`、`/api/plans`