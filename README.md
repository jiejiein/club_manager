# 高校社团管理与活动预约平台

基于 SpringBoot + Vue3 的前后端分离架构，实现高校社团管理与活动预约系统。

## 技术栈

### 后端
- SpringBoot 2.7.18
- Java 17
- MyBatis-Plus 3.5.3.1
- MySQL 8.0.26
- Spring Security + JWT

### 前端
- Vue 3.3.4
- Vue Router 4.2.4
- Pinia 2.1.6
- Element Plus 2.3.12
- ECharts 5.4.3
- Axios 1.5.0

## 项目结构

```
club_manager/
├── backend/                    # 后端项目
│   ├── src/main/java/com/club/
│   │   ├── annotation/        # 自定义注解（@Log 操作日志）
│   │   ├── aspect/            # AOP切面（日志记录）
│   │   ├── common/            # 公共类（BaseService、BusinessException、ErrorCode）
│   │   ├── config/            # 配置类
│   │   ├── controller/        # 控制器
│   │   ├── dto/               # 数据传输对象
│   │   ├── entity/            # 实体类
│   │   ├── enums/             # 枚举类
│   │   ├── exception/         # 异常处理（GlobalExceptionHandler）
│   │   ├── mapper/            # MyBatis Mapper
│   │   ├── security/          # 安全配置（JWT认证）
│   │   ├── service/           # 服务层
│   │   └── vo/                # 视图对象
│   └── src/main/resources/
│       ├── application.yml    # 配置文件
│       └── db/               # 数据库脚本目录
│           ├── init.sql               # 表结构 + 基础数据
│           ├── data-generator-final.sql # 完整测试数据
│           ├── update-avatars.sql     # 头像更新脚本
│           ├── add-more-students.sql  # 补充学生数据
│           ├── assign-new-presidents.sql # 分配新社长
│           └── rename-users.sql       # 用户名重命名
│
└── frontend/                   # 前端项目
    ├── src/
    │   ├── api/               # API接口
    │   ├── composables/       # 组合式函数（useDialog、usePagination、useSearch）
    │   ├── layout/            # 布局组件
    │   ├── router/            # 路由配置
    │   ├── stores/            # 状态管理（Pinia）
    │   ├── styles/            # 样式文件（含暗黑模式 dark.scss）
    │   ├── utils/             # 工具函数
    │   └── views/             # 页面组件
    └── vite.config.js         # Vite配置
```

## 功能模块

### 管理员模块
- 用户管理：查看、禁用/启用、分配权限
- 社团管理：创建、审核、编辑、注销社团
- 社团类型管理：维护社团分类体系
- 活动管理：审核全平台活动
- 通知管理：发布系统级通知
- 申请管理：处理所有入团申请
- 费用管理：查看全平台缴费记录
- 数据看板：展示宏观数据

### 社长模块
- 入团申请处理：审核申请、批量操作
- 成员管理：查询、移除本社团成员
- 活动管理：发布活动、审核报名、活动签到
- 通知管理：发布社团专属通知
- 费用管理：发布缴费通知、查看缴费记录
- 社团数据看板

### 普通用户模块
- 入团申请：浏览社团、提交申请
- 活动报名：报名活动、参与签到
- 个人中心：修改信息、查看记录
- 通知查询：查看通知、标记已读
- 缴费管理：查看缴费通知、在线缴费

## 快速开始

### 1. 数据库配置

```sql
-- 在MySQL中执行
CREATE DATABASE IF NOT EXISTS club_manager DEFAULT CHARACTER SET utf8mb4;
```

然后按以下顺序执行数据库脚本：

1. **初始化表结构**: 执行 `backend/src/main/resources/db/init.sql` 创建所有表和基础数据
2. **导入测试数据**: 执行 `backend/src/main/resources/db/data-generator-final.sql` 导入完整的测试数据（会先清空现有数据）
3. **更新头像**: 执行 `backend/src/main/resources/db/update-avatars.sql` 为用户设置随机头像（可选）

### 2. 后端启动

```bash
cd backend

# 修改数据库配置
# 编辑 src/main/resources/application.yml
# 修改数据库连接信息

# 使用Maven启动
mvn spring-boot:run
```

后端默认运行在 http://localhost:8080

### 3. 前端启动

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端默认运行在 http://localhost:3000

### 4. 默认账号

**管理员账号:**
- 用户名: `admin`
- 密码: `123456`

**测试账号（由 data-generator-final.sql + 补充脚本生成）:**
- 社长账号: `president1` ~ `president10` (密码: `123456`)
- 学生账号: `student1` ~ `student20` (密码: `123456`)
- 共计: 1个管理员 + 10个社长 + 20个学生 = 31个用户

### 5. 数据库脚本说明

| 脚本文件 | 用途 | 执行时机 |
|---------|------|---------|
| `init.sql` | 创建13张表结构 + 初始化基础数据 + 测试数据（使用Navicat导出的完整脚本） | 首次建库时执行 |

**注意事项:**
- `init.sql` 包含完整的建表语句和初始数据，执行一次即可完成数据库初始化
- 默认账号：admin / president / student，初始密码均为 666666
- 脚本执行后，所有用户密码统一为 `123456`
- 补充脚本(add-more-students.sql → assign-new-presidents.sql → rename-users.sql)需按顺序执行

## API接口

所有API接口前缀为 `/api`，需要登录认证的接口需在请求头携带JWT Token：

```
Authorization: Bearer <token>
```

主要接口：
- `/api/auth/*` - 认证相关（登录、注册）
- `/api/user/*` - 用户管理
- `/api/club/*` - 社团管理
- `/api/activity/*` - 活动管理
- `/api/apply/*` - 申请管理
- `/api/notification/*` - 通知管理
- `/api/payment/*` - 缴费管理
- `/api/dashboard/*` - 数据看板

## 开发环境要求

- JDK 17+
- Node.js 16+
- MySQL 8.0+
- Maven 3.6+

---

## 优化内容

### 后端优化

#### 公共类封装 (`common` 包)
- **BaseService**: 基础服务类，封装获取当前用户、角色判断等通用方法
- **BusinessException**: 业务异常类，统一异常处理
- **ErrorCode**: 错误码枚举，定义系统所有错误码（系统错误、申请/活动/社团/用户/缴费相关业务错误）

#### AOP日志记录
- **@Log 注解**: 自定义注解标记需要记录日志的方法
- **LogAspect**: AOP切面实现，自动记录操作人、IP、执行时间、参数等信息

### 前端优化

#### 组合式函数 (`composables` 包)
- **useDialog**: 弹窗逻辑封装，统一管理弹窗的打开/关闭/编辑模式
- **usePagination**: 分页逻辑封装，统一封装分页查询、页码切换、选择等功能
- **useSearch**: 搜索逻辑封装，统一封装搜索表单、重置、参数过滤等功能

#### 暗黑模式（夜间模式）
- 基于 Element Plus 官方暗黑主题 + 自定义样式覆盖
- 所有页面硬编码颜色替换为 CSS 变量
- 平滑的主题切换过渡动画
- 主题状态持久化到 localStorage
- 详见《夜间模式优化完整文档.md》

### 项目优化详情
- 详见《项目完整优化文档.md》
