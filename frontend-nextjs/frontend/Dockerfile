FROM node:18-alpine

WORKDIR /app

# 安装 pnpm
RUN npm install -g pnpm

# 复制包管理文件
COPY package.json pnpm-lock.yaml* ./

# 安装依赖
RUN pnpm install --frozen-lockfile

# 复制源代码
COPY . .

# 详细调试信息
RUN echo "=== 当前目录结构 ==="
RUN ls -la

RUN echo "=== lib 目录内容 ==="
RUN ls -la lib/

RUN echo "=== 检查 utils.ts 是否存在 ==="
RUN find . -name "utils.ts" -type f

RUN echo "=== 检查 tsconfig.json 路径配置 ==="
RUN cat tsconfig.json | grep -A 5 -B 5 "paths"

RUN echo "=== 检查导入语句 ==="
RUN grep -n "from.*@/lib/utils" components/ui/toast.tsx

RUN echo "=== 尝试直接导入测试 ==="
RUN node -e "console.log('测试导入:', require.resolve('./lib/utils.ts'))" || echo "直接导入失败"

# 构建应用
RUN pnpm build

EXPOSE 3000
CMD ["pnpm", "start"]

# FROM node:18-alpine
# WORKDIR /app
# COPY . .

# # 必须使用 ARG 和 ENV 组合
# ARG NEXT_PUBLIC_API_BASE_URL
# ENV NEXT_PUBLIC_API_BASE_URL=${NEXT_PUBLIC_API_BASE_URL}

# RUN npm install
# RUN npm run build

# EXPOSE 3000
# CMD ["npm", "start"]