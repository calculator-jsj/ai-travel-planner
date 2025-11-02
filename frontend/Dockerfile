# 前端 Dockerfile
# 构建阶段
FROM node:20-alpine AS build

WORKDIR /app

# 复制 package 文件
COPY package*.json ./

# 安装依赖
RUN npm ci

# 复制源代码
COPY . .

# 构建参数（用于注入环境变量）
ARG VITE_AMAP_KEY
ARG VITE_API_BASE_URL=http://localhost:8080/api

# 设置环境变量
ENV VITE_AMAP_KEY=$VITE_AMAP_KEY
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL

# 构建应用
RUN npm run build

# 生产阶段
FROM nginx:alpine

# 复制构建产物到 nginx
COPY --from=build /app/dist /usr/share/nginx/html

# 复制 nginx 配置文件（如果需要自定义）
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 暴露端口
EXPOSE 80

# 启动 nginx
CMD ["nginx", "-g", "daemon off;"]
