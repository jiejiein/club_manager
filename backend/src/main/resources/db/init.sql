-- 创建数据库
CREATE DATABASE IF NOT EXISTS club_manager DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE club_manager;

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(255) COMMENT '头像',
    role INT DEFAULT 3 COMMENT '角色：1管理员 2社长 3普通用户',
    status INT DEFAULT 1 COMMENT '状态：0禁用 1启用',
    student_no VARCHAR(50) COMMENT '学号',
    college VARCHAR(100) COMMENT '学院',
    major VARCHAR(100) COMMENT '专业',
    class_name VARCHAR(100) COMMENT '班级',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '是否删除：0否 1是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 社团类型表
CREATE TABLE IF NOT EXISTS sys_club_type (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '类型ID',
    name VARCHAR(50) NOT NULL COMMENT '类型名称',
    description VARCHAR(255) COMMENT '类型描述',
    sort INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '是否删除：0否 1是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社团类型表';

-- 社团表
CREATE TABLE IF NOT EXISTS sys_club (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '社团ID',
    name VARCHAR(100) NOT NULL COMMENT '社团名称',
    type_id BIGINT COMMENT '类型ID',
    type_name VARCHAR(50) COMMENT '类型名称',
    president_id BIGINT COMMENT '社长ID',
    president_name VARCHAR(50) COMMENT '社长姓名',
    description TEXT COMMENT '社团描述',
    logo VARCHAR(255) COMMENT '社团logo',
    status INT DEFAULT 0 COMMENT '状态：0待审核 1正常运营 2已注销',
    member_count INT DEFAULT 0 COMMENT '成员数量',
    reject_reason VARCHAR(255) COMMENT '拒绝原因',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '是否删除：0否 1是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社团表';

-- 社团成员表
CREATE TABLE IF NOT EXISTS sys_club_member (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    club_id BIGINT NOT NULL COMMENT '社团ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    user_name VARCHAR(50) COMMENT '用户姓名',
    status INT DEFAULT 1 COMMENT '状态：0禁用 1启用',
    join_time DATETIME COMMENT '加入时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '是否删除：0否 1是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社团成员表';

-- 活动表
CREATE TABLE IF NOT EXISTS sys_activity (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '活动ID',
    club_id BIGINT NOT NULL COMMENT '社团ID',
    club_name VARCHAR(100) COMMENT '社团名称',
    title VARCHAR(200) NOT NULL COMMENT '活动标题',
    description TEXT COMMENT '活动描述',
    cover VARCHAR(255) COMMENT '活动封面',
    location VARCHAR(200) COMMENT '活动地点',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    sign_up_start DATETIME COMMENT '报名开始时间',
    sign_up_end DATETIME COMMENT '报名截止时间',
    max_participants INT DEFAULT 100 COMMENT '最大参与人数',
    current_participants INT DEFAULT 0 COMMENT '当前报名人数',
    status INT DEFAULT 0 COMMENT '状态：0草稿 1待审核 2已发布 3进行中 4已结束 5已取消',
    reject_reason VARCHAR(255) COMMENT '拒绝原因',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '是否删除：0否 1是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动表';

-- 活动报名表
CREATE TABLE IF NOT EXISTS sys_activity_sign_up (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '报名ID',
    activity_id BIGINT NOT NULL COMMENT '活动ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    user_name VARCHAR(50) COMMENT '用户姓名',
    status INT DEFAULT 0 COMMENT '状态：0待审核 1已通过 2已拒绝',
    sign_up_time DATETIME COMMENT '报名时间',
    check_in_time DATETIME COMMENT '签到时间',
    checked INT DEFAULT 0 COMMENT '是否签到：0否 1是',
    rating INT COMMENT '评分：1-5',
    comment VARCHAR(500) COMMENT '评价内容',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '是否删除：0否 1是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动报名表';

-- 入团申请表
CREATE TABLE IF NOT EXISTS sys_apply (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '申请ID',
    club_id BIGINT NOT NULL COMMENT '社团ID',
    club_name VARCHAR(100) COMMENT '社团名称',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    user_name VARCHAR(50) COMMENT '用户姓名',
    status INT DEFAULT 0 COMMENT '状态：0待审核 1已通过 2已拒绝',
    reason VARCHAR(500) COMMENT '申请理由',
    reject_reason VARCHAR(255) COMMENT '拒绝原因',
    process_time DATETIME COMMENT '处理时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '是否删除：0否 1是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入团申请表';

-- 通知表
CREATE TABLE IF NOT EXISTS sys_notification (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '通知ID',
    title VARCHAR(200) NOT NULL COMMENT '通知标题',
    content TEXT COMMENT '通知内容',
    type INT DEFAULT 1 COMMENT '类型：1系统通知 2社团通知',
    target_id BIGINT COMMENT '目标ID（社团ID）',
    sender_id BIGINT COMMENT '发送者ID',
    sender_name VARCHAR(50) COMMENT '发送者姓名',
    priority INT DEFAULT 0 COMMENT '优先级：0普通 1重要 2紧急',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '是否删除：0否 1是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';

-- 通知阅读记录表
CREATE TABLE IF NOT EXISTS sys_notification_read (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    notification_id BIGINT NOT NULL COMMENT '通知ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    is_read INT DEFAULT 0 COMMENT '是否已读：0否 1是',
    read_time DATETIME COMMENT '阅读时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '是否删除：0否 1是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知阅读记录表';

-- 缴费通知表
CREATE TABLE IF NOT EXISTS sys_payment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '缴费ID',
    club_id BIGINT NOT NULL COMMENT '社团ID',
    club_name VARCHAR(100) COMMENT '社团名称',
    title VARCHAR(200) NOT NULL COMMENT '缴费标题',
    description VARCHAR(500) COMMENT '缴费说明',
    amount DECIMAL(10,2) NOT NULL COMMENT '缴费金额',
    deadline DATETIME COMMENT '截止日期',
    status INT DEFAULT 0 COMMENT '状态：0未缴费 1已缴费 2已逾期',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '是否删除：0否 1是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='缴费通知表';

-- 缴费记录表
CREATE TABLE IF NOT EXISTS sys_payment_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    payment_id BIGINT NOT NULL COMMENT '缴费ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    user_name VARCHAR(50) COMMENT '用户姓名',
    amount DECIMAL(10,2) NOT NULL COMMENT '缴费金额',
    status INT DEFAULT 0 COMMENT '状态：0未缴费 1已缴费 2已逾期',
    pay_time DATETIME COMMENT '缴费时间',
    transaction_no VARCHAR(100) COMMENT '交易流水号',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '是否删除：0否 1是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='缴费记录表';

-- 系统日志表
CREATE TABLE IF NOT EXISTS sys_log (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT DEFAULT NULL COMMENT '用户ID',
    username VARCHAR(50) DEFAULT NULL COMMENT '用户名',
    nickname VARCHAR(50) DEFAULT NULL COMMENT '用户昵称',
    operation VARCHAR(100) DEFAULT NULL COMMENT '操作名称',
    method VARCHAR(200) DEFAULT NULL COMMENT '请求方法',
    params TEXT COMMENT '请求参数',
    ip VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    status INT NOT NULL DEFAULT '0' COMMENT '状态：0-成功，1-失败',
    error_msg VARCHAR(500) DEFAULT NULL COMMENT '错误信息',
    execution_time BIGINT DEFAULT NULL COMMENT '执行时间（毫秒）',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    deleted INT NOT NULL DEFAULT '0' COMMENT '删除标记：0-未删除，1-已删除',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_create_time (create_time),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统日志表';

-- 社长转移申请表
CREATE TABLE IF NOT EXISTS sys_president_transfer (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    club_id BIGINT NOT NULL COMMENT '社团ID',
    club_name VARCHAR(100) DEFAULT NULL COMMENT '社团名称',
    current_president_id BIGINT NOT NULL COMMENT '现任社长ID',
    current_president_name VARCHAR(50) DEFAULT NULL COMMENT '现任社长姓名',
    new_president_id BIGINT NOT NULL COMMENT '新社长ID',
    new_president_name VARCHAR(50) DEFAULT NULL COMMENT '新社长姓名',
    status INT NOT NULL DEFAULT '0' COMMENT '状态：0-待审核，1-已通过，2-已拒绝',
    reason VARCHAR(500) DEFAULT NULL COMMENT '申请理由',
    reject_reason VARCHAR(500) DEFAULT NULL COMMENT '拒绝理由',
    process_time DATETIME DEFAULT NULL COMMENT '处理时间',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    deleted INT NOT NULL DEFAULT '0' COMMENT '删除标记：0-未删除，1-已删除',
    PRIMARY KEY (id),
    KEY idx_club_id (club_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社长转移申请表';

-- 初始化账号（密码均为：666666）
INSERT INTO sys_user (username, password, nickname, role, status) VALUES 
('admin', '666666', '系统管理员', 1, 1),
('president', '666666', '社长测试', 2, 1),
('student', '666666', '学生测试', 3, 1);

-- 初始化社团类型
INSERT INTO sys_club_type (name, description, sort) VALUES 
('学术科技类', '学术研究、科技创新类社团', 1),
('文化艺术类', '文学艺术、文化传承类社团', 2),
('体育竞技类', '体育运动、竞技比赛类社团', 3),
('社会实践类', '志愿服务、社会实践类社团', 4),
('兴趣爱好类', '兴趣爱好、娱乐休闲类社团', 5);
