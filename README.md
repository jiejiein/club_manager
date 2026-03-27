# 🎓 高校社团管理系统

基于 **SpringBoot + Vue3** 的前后端分离社团管理系统

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Vue3 · Element Plus · Pinia · Axios · SCSS |
| 后端 | SpringBoot 2.7 · Spring Security · JWT · MyBatis-Plus |
| 数据库 | MySQL 8.0 |
| 构建 | Maven · Vite |

## 项目结构

```
club_manager/
├── backend/                    # 后端
│   └── src/main/
│       ├── java/com/club/
│       │   ├── controller/     # 接口层
│       │   ├── service/        # 业务层
│       │   │   └── impl/
│       │   ├── mapper/         # 数据访问层
│       │   ├── entity/         # 数据模型层 - 实体
│       │   ├── dto/            # 数据模型层 - 入参
│       │   ├── vo/             # 数据模型层 - 视图
│       │   ├── security/       # 支撑层 - 认证
│       │   ├── config/         # 支撑层 - 配置
│       │   ├── annotation/     # 支撑层 - 注解
│       │   ├── aspect/         # 支撑层 - 切面
│       │   ├── common/         # 支撑层 - 通用
│       │   ├── enums/          # 支撑层 - 枚举
│       │   └── exception/      # 支撑层 - 异常
│       └── resources/
│           ├── db/             # SQL 脚本
│           └── application.yml
│
├── frontend/                   # 前端
│   └── src/
│       ├── api/                # 接口请求
│       ├── components/         # 公共组件
│       ├── layout/             # 布局组件
│       ├── router/             # 路由配置
│       ├── stores/             # 状态管理
│       ├── styles/             # 全局样式
│       ├── utils/              # 工具函数
│       └── views/              # 页面视图
│
└── .gitignore
```

## 功能模块

| 角色 | 功能 |
|------|------|
| 管理员 | 用户管理、社团审核、系统日志、数据统计 |
| 社长 | 社团管理、活动发布、成员管理、费用管理 |
| 普通用户 | 浏览社团、申请入团、活动报名、个人信息 |

## 快速开始

### 环境要求

- JDK 1.8+
- Node.js 16+
- MySQL 8.0+

### 初始化数据库

```bash
# 按顺序执行 SQL 脚本
mysql -u root -p < backend/src/main/resources/db/init.sql
mysql -u root -p < backend/src/main/resources/db/data-generator-final.sql
```

### 启动后端

```bash
cd backend
mvn spring-boot:run
# 后端运行在 http://localhost:8080
```

### 启动前端

```bash
cd frontend
npm install
npm run dev
# 前端运行在 http://localhost:5173
```

## API 文档

接口采用 RESTful 风格，统一返回格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": { }
}
```

## 相关文档

- [系统架构说明](./架构说明.md) — 用于论文撰写及答辩准备
