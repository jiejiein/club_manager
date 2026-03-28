-- 数据生成脚本（最终版）

-- 清空所有表（按依赖关系顺序）
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE sys_notification_read;
TRUNCATE TABLE sys_notification;
TRUNCATE TABLE sys_payment_record;
TRUNCATE TABLE sys_payment;
TRUNCATE TABLE sys_activity_sign_up;
TRUNCATE TABLE sys_activity;
TRUNCATE TABLE sys_apply;
TRUNCATE TABLE sys_club_member;
TRUNCATE TABLE sys_club;
TRUNCATE TABLE sys_club_type;
TRUNCATE TABLE sys_user;

SET FOREIGN_KEY_CHECKS = 1;

-- 1. 先创建admin用户
INSERT INTO sys_user (id, username, password, nickname, email, phone, avatar, role, status, student_no, college, major, class_name, create_time, update_time, deleted) VALUES
(1, 'admin', '123456', '管理员', 'admin@example.com', '13800138000', 'https://api.dicebear.com/7.x/avataaars/svg?seed=admin&backgroundColor=b6e3f4', 1, 1, '20230000', '管理学院', '行政管理', '管1班', NOW(), NOW(), 0);

-- 2. 创建5个社长用户（president1-5）
INSERT INTO sys_user (id, username, password, nickname, email, phone, avatar, role, status, student_no, college, major, class_name, create_time, update_time, deleted) VALUES
(2, 'president1', '123456', '林一', 'president1@example.com', '13800138001', 'https://api.dicebear.com/7.x/avataaars/svg?seed=president1&backgroundColor=c0aede', 2, 1, '20230001', '计算机学院', '计算机科学与技术', '计科1班', NOW(), NOW(), 0),
(3, 'president2', '123456', '龙二', 'president2@example.com', '13800138002', 'https://api.dicebear.com/7.x/avataaars/svg?seed=president2&backgroundColor=ffdfbf', 2, 1, '20230002', '文学院', '汉语言文学', '文1班', NOW(), NOW(), 0),
(4, 'president3', '123456', '张三', 'president3@example.com', '13800138003', 'https://api.dicebear.com/7.x/avataaars/svg?seed=president3&backgroundColor=d1d4f9', 2, 1, '20230003', '体育学院', '体育教育', '体教1班', NOW(), NOW(), 0),
(5, 'president4', '123456', '李四', 'president4@example.com', '13800138004', 'https://api.dicebear.com/7.x/avataaars/svg?seed=president4&backgroundColor=ffd5dc', 2, 1, '20230004', '社会学院', '社会学', '社1班', NOW(), NOW(), 0),
(6, 'president5', '123456', '冯五', 'president5@example.com', '13800138005', 'https://api.dicebear.com/7.x/avataaars/svg?seed=president5&backgroundColor=b6e3f4', 2, 1, '20230005', '艺术学院', '视觉传达', '视传1班', NOW(), NOW(), 0);

-- 3. 创建15个学生用户（student1-15）
INSERT INTO sys_user (id, username, password, nickname, email, phone, avatar, role, status, student_no, college, major, class_name, create_time, update_time, deleted) VALUES
(7, 'student1', '123456', '王小明', 'student1@example.com', '13800138006', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student1&backgroundColor=c0aede', 3, 1, '20230006', '计算机学院', '软件工程', '软工1班', NOW(), NOW(), 0),
(8, 'student2', '123456', '李小红', 'student2@example.com', '13800138007', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student2&backgroundColor=ffdfbf', 3, 1, '20230007', '文学院', '新闻学', '新闻1班', NOW(), NOW(), 0),
(9, 'student3', '123456', '张小刚', 'student3@example.com', '13800138008', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student3&backgroundColor=d1d4f9', 3, 1, '20230008', '体育学院', '运动训练', '运训1班', NOW(), NOW(), 0),
(10, 'student4', '123456', '赵小丽', 'student4@example.com', '13800138009', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student4&backgroundColor=ffd5dc', 3, 1, '20230009', '社会学院', '社会工作', '社工1班', NOW(), NOW(), 0),
(11, 'student5', '123456', '陈小明', 'student5@example.com', '13800138010', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student5&backgroundColor=b6e3f4', 3, 1, '20230010', '艺术学院', '美术学', '美术1班', NOW(), NOW(), 0),
(12, 'student6', '123456', '林小红', 'student6@example.com', '13800138011', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student6&backgroundColor=c0aede', 3, 1, '20230011', '理学院', '数学与应用数学', '数1班', NOW(), NOW(), 0),
(13, 'student7', '123456', '龙小刚', 'student7@example.com', '13800138012', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student7&backgroundColor=ffdfbf', 3, 1, '20230012', '环境学院', '环境科学', '环科1班', NOW(), NOW(), 0),
(14, 'student8', '123456', '刘小丽', 'student8@example.com', '13800138013', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student8&backgroundColor=d1d4f9', 3, 1, '20230013', '计算机学院', '数据科学与大数据技术', '大数据1班', NOW(), NOW(), 0),
(15, 'student9', '123456', '黄小明', 'student9@example.com', '13800138014', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student9&backgroundColor=ffd5dc', 3, 1, '20230014', '文学院', '历史学', '历史1班', NOW(), NOW(), 0),
(16, 'student10', '123456', '杨小红', 'student10@example.com', '13800138015', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student10&backgroundColor=b6e3f4', 3, 1, '20230015', '体育学院', '武术与民族传统体育', '武术1班', NOW(), NOW(), 0),
(17, 'student11', '123456', '马小刚', 'student11@example.com', '13800138016', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student11&backgroundColor=c0aede', 3, 1, '20230016', '社会学院', '法学', '法学1班', NOW(), NOW(), 0),
(18, 'student12', '123456', '牛小丽', 'student12@example.com', '13800138017', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student12&backgroundColor=ffdfbf', 3, 1, '20230017', '艺术学院', '音乐学', '音1班', NOW(), NOW(), 0),
(19, 'student13', '123456', '羊小明', 'student13@example.com', '13800138018', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student13&backgroundColor=d1d4f9', 3, 1, '20230018', '理学院', '物理学', '物理1班', NOW(), NOW(), 0),
(20, 'student14', '123456', '猴小红', 'student14@example.com', '13800138019', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student14&backgroundColor=ffd5dc', 3, 1, '20230019', '环境学院', '环境工程', '环工1班', NOW(), NOW(), 0),
(21, 'student15', '123456', '鸡小刚', 'student15@example.com', '13800138020', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student15&backgroundColor=b6e3f4', 3, 1, '20230020', '计算机学院', '人工智能', 'AI1班', NOW(), NOW(), 0);

-- 4. 创建5个社团类型
INSERT INTO sys_club_type (id, name, description, sort, create_time, update_time, deleted) VALUES
(1, '学术科技', '学术研究和科技创新类社团', 1, NOW(), NOW(), 0),
(2, '文化艺术', '文化、艺术、音乐、舞蹈等社团', 2, NOW(), NOW(), 0),
(3, '体育健身', '各类体育活动和健身社团', 3, NOW(), NOW(), 0),
(4, '志愿服务', '公益活动和志愿服务类社团', 4, NOW(), NOW(), 0),
(5, '兴趣爱好', '各种兴趣爱好类社团', 5, NOW(), NOW(), 0);

-- 5. 创建10个社团，每个社长担任2个
INSERT INTO sys_club (id, name, type_id, type_name, president_id, president_name, description, logo, status, member_count, reject_reason, create_time, update_time, deleted) VALUES
-- 林一（president1）的社团
(1, '计算机协会', 1, '学术科技', 2, '林一', '专注于计算机技术学习和交流', 'logo1.png', 1, 5, NULL, NOW(), NOW(), 0),
(2, '编程社', 1, '学术科技', 2, '林一', '编程爱好者的聚集地', 'logo2.png', 1, 8, NULL, NOW(), NOW(), 0),
-- 龙二（president2）的社团
(3, '文学社', 2, '文化艺术', 3, '龙二', '文学创作和欣赏', 'logo3.png', 1, 12, NULL, NOW(), NOW(), 0),
(4, '诗词社', 2, '文化艺术', 3, '龙二', '诗词爱好者的交流平台', 'logo4.png', 1, 6, NULL, NOW(), NOW(), 0),
-- 张三（president3）的社团
(5, '篮球社', 3, '体育健身', 4, '张三', '篮球爱好者的聚集地', 'logo5.png', 1, 15, NULL, NOW(), NOW(), 0),
(6, '足球社', 3, '体育健身', 4, '张三', '足球爱好者的乐园', 'logo6.png', 1, 10, NULL, NOW(), NOW(), 0),
-- 李四（president4）的社团
(7, '志愿者协会', 4, '志愿服务', 5, '李四', '组织各类志愿服务活动', 'logo7.png', 1, 20, NULL, NOW(), NOW(), 0),
(8, '环保协会', 4, '志愿服务', 5, '李四', '环保活动和宣传', 'logo8.png', 1, 12, NULL, NOW(), NOW(), 0),
-- 冯五（president5）的社团
(9, '摄影社', 5, '兴趣爱好', 6, '冯五', '摄影爱好者的交流平台', 'logo9.png', 1, 8, NULL, NOW(), NOW(), 0),
(10, '动漫社', 5, '兴趣爱好', 6, '冯五', '动漫爱好者的交流社区', 'logo10.png', 1, 15, NULL, NOW(), NOW(), 0);

-- 6. 创建社团成员关系（学生随机加入社团，每人最多3个）
INSERT INTO sys_club_member (id, club_id, user_id, user_name, status, join_time, create_time, update_time, deleted) VALUES
-- student1 加入 3 个社团
(1, 1, 7, '王小明', 1, NOW(), NOW(), NOW(), 0),
(2, 2, 7, '王小明', 1, NOW(), NOW(), NOW(), 0),
(3, 5, 7, '王小明', 1, NOW(), NOW(), NOW(), 0),
-- student2 加入 2 个社团
(4, 3, 8, '李小红', 1, NOW(), NOW(), NOW(), 0),
(5, 4, 8, '李小红', 1, NOW(), NOW(), NOW(), 0),
-- student3 加入 3 个社团
(6, 5, 9, '张小刚', 1, NOW(), NOW(), NOW(), 0),
(7, 6, 9, '张小刚', 1, NOW(), NOW(), NOW(), 0),
(8, 7, 9, '张小刚', 1, NOW(), NOW(), NOW(), 0),
-- student4 加入 2 个社团
(9, 7, 10, '赵小丽', 1, NOW(), NOW(), NOW(), 0),
(10, 8, 10, '赵小丽', 1, NOW(), NOW(), NOW(), 0),
-- student5 加入 3 个社团
(11, 9, 11, '陈小明', 1, NOW(), NOW(), NOW(), 0),
(12, 10, 11, '陈小明', 1, NOW(), NOW(), NOW(), 0),
(13, 3, 11, '陈小明', 1, NOW(), NOW(), NOW(), 0),
-- student6 加入 2 个社团
(14, 1, 12, '林小红', 1, NOW(), NOW(), NOW(), 0),
(15, 2, 12, '林小红', 1, NOW(), NOW(), NOW(), 0),
-- student7 加入 3 个社团
(16, 3, 13, '龙小刚', 1, NOW(), NOW(), NOW(), 0),
(17, 4, 13, '龙小刚', 1, NOW(), NOW(), NOW(), 0),
(18, 8, 13, '龙小刚', 1, NOW(), NOW(), NOW(), 0),
-- student8 加入 2 个社团
(19, 1, 14, '刘小丽', 1, NOW(), NOW(), NOW(), 0),
(20, 2, 14, '刘小丽', 1, NOW(), NOW(), NOW(), 0),
-- student9 加入 3 个社团
(21, 3, 15, '黄小明', 1, NOW(), NOW(), NOW(), 0),
(22, 4, 15, '黄小明', 1, NOW(), NOW(), NOW(), 0),
(23, 9, 15, '黄小明', 1, NOW(), NOW(), NOW(), 0),
-- student10 加入 2 个社团
(24, 5, 16, '杨小红', 1, NOW(), NOW(), NOW(), 0),
(25, 6, 16, '杨小红', 1, NOW(), NOW(), NOW(), 0),
-- student11 加入 3 个社团
(26, 7, 17, '马小刚', 1, NOW(), NOW(), NOW(), 0),
(27, 8, 17, '马小刚', 1, NOW(), NOW(), NOW(), 0),
(28, 10, 17, '马小刚', 1, NOW(), NOW(), NOW(), 0),
-- student12 加入 2 个社团
(29, 9, 18, '牛小丽', 1, NOW(), NOW(), NOW(), 0),
(30, 10, 18, '牛小丽', 1, NOW(), NOW(), NOW(), 0),
-- student13 加入 3 个社团
(31, 1, 19, '羊小明', 1, NOW(), NOW(), NOW(), 0),
(32, 2, 19, '羊小明', 1, NOW(), NOW(), NOW(), 0),
(33, 5, 19, '羊小明', 1, NOW(), NOW(), NOW(), 0),
-- student14 加入 2 个社团
(34, 3, 20, '猴小红', 1, NOW(), NOW(), NOW(), 0),
(35, 4, 20, '猴小红', 1, NOW(), NOW(), NOW(), 0),
-- student15 加入 3 个社团
(36, 6, 21, '鸡小刚', 1, NOW(), NOW(), NOW(), 0),
(37, 7, 21, '鸡小刚', 1, NOW(), NOW(), NOW(), 0),
(38, 9, 21, '鸡小刚', 1, NOW(), NOW(), NOW(), 0);

-- 7. 创建15个活动，每个社团1-2个
INSERT INTO sys_activity (id, club_id, club_name, title, description, cover, location, start_time, end_time, sign_up_start, sign_up_end, max_participants, current_participants, status, reject_reason, create_time, update_time, deleted) VALUES
-- 计算机协会活动
(1, 1, '计算机协会', '编程大赛', '面向全校学生的编程竞赛', 'cover1.png', '教学楼A101', '2026-04-01 14:00:00', '2026-04-01 18:00:00', '2026-03-23 00:00:00', '2026-03-31 23:59:59', 50, 25, 3, NULL, NOW(), NOW(), 0),
(2, 1, '计算机协会', '算法讲座', '算法学习经验分享', 'cover2.png', '教学楼B201', '2026-04-05 15:00:00', '2026-04-05 17:00:00', '2026-03-23 00:00:00', '2026-04-04 23:59:59', 80, 40, 2, NULL, NOW(), NOW(), 0),
-- 编程社活动
(3, 2, '编程社', '代码马拉松', '24小时编程挑战', 'cover3.png', '计算机实验室', '2026-04-02 10:00:00', '2026-04-02 22:00:00', '2026-03-23 00:00:00', '2026-03-31 23:59:59', 30, 15, 2, NULL, NOW(), NOW(), 0),
-- 文学社活动
(4, 3, '文学社', '诗歌朗诵会', '诗歌爱好者的盛会', 'cover4.png', '图书馆报告厅', '2026-04-03 19:00:00', '2026-04-03 21:00:00', '2026-03-23 00:00:00', '2026-04-02 23:59:59', 100, 45, 2, NULL, NOW(), NOW(), 0),
(5, 3, '文学社', '文学创作工作坊', '文学创作技巧分享', 'cover5.png', '文学院会议室', '2026-04-06 14:00:00', '2026-04-06 16:00:00', '2026-03-23 00:00:00', '2026-04-05 23:59:59', 20, 10, 2, NULL, NOW(), NOW(), 0),
-- 诗词社活动
(6, 4, '诗词社', '诗词大会', '诗词知识竞赛', 'cover6.png', '图书馆报告厅', '2026-04-04 15:00:00', '2026-04-04 17:00:00', '2026-03-23 00:00:00', '2026-04-03 23:59:59', 50, 20, 2, NULL, NOW(), NOW(), 0),
-- 篮球社活动
(7, 5, '篮球社', '篮球友谊赛', '与其他高校的篮球友谊赛', 'cover7.png', '体育馆', '2026-04-07 16:00:00', '2026-04-07 18:00:00', '2026-03-23 00:00:00', '2026-04-06 23:59:59', 20, 15, 2, NULL, NOW(), NOW(), 0),
(8, 5, '篮球社', '篮球训练营', '篮球技巧训练', 'cover8.png', '体育馆', '2026-04-08 14:00:00', '2026-04-08 16:00:00', '2026-03-23 00:00:00', '2026-04-07 23:59:59', 30, 18, 2, NULL, NOW(), NOW(), 0),
-- 足球社活动
(9, 6, '足球社', '足球联赛', '校内足球联赛', 'cover9.png', '足球场', '2026-04-09 14:00:00', '2026-04-09 16:00:00', '2026-03-23 00:00:00', '2026-04-08 23:59:59', 22, 18, 2, NULL, NOW(), NOW(), 0),
-- 志愿者协会活动
(10, 7, '志愿者协会', '社区志愿服务', '社区清洁和老人关怀活动', 'cover10.png', '社区中心', '2026-04-10 09:00:00', '2026-04-10 12:00:00', '2026-03-23 00:00:00', '2026-04-09 23:59:59', 30, 20, 2, NULL, NOW(), NOW(), 0),
(11, 7, '志愿者协会', '校园环保活动', '校园环境清洁活动', 'cover11.png', '校园', '2026-04-11 14:00:00', '2026-04-11 16:00:00', '2026-03-23 00:00:00', '2026-04-10 23:59:59', 50, 25, 2, NULL, NOW(), NOW(), 0),
-- 环保协会活动
(12, 8, '环保协会', '环保讲座', '环保知识讲座', 'cover12.png', '环境学院报告厅', '2026-04-12 16:00:00', '2026-04-12 18:00:00', '2026-03-23 00:00:00', '2026-04-11 23:59:59', 60, 25, 2, NULL, NOW(), NOW(), 0),
-- 摄影社活动
(13, 9, '摄影社', '摄影展览', '学生摄影作品展览', 'cover13.png', '艺术楼展厅', '2026-04-13 10:00:00', '2026-04-18 17:00:00', '2026-03-23 00:00:00', '2026-04-12 23:59:59', 200, 80, 2, NULL, NOW(), NOW(), 0),
(14, 9, '摄影社', '摄影技巧讲座', '摄影技巧分享', 'cover14.png', '艺术学院会议室', '2026-04-14 15:00:00', '2026-04-14 17:00:00', '2026-03-23 00:00:00', '2026-04-13 23:59:59', 40, 20, 2, NULL, NOW(), NOW(), 0),
-- 动漫社活动
(15, 10, '动漫社', '动漫嘉年华', '动漫主题活动', 'cover15.png', '体育馆', '2026-04-15 10:00:00', '2026-04-15 17:00:00', '2026-03-23 00:00:00', '2026-04-14 23:59:59', 300, 150, 2, NULL, NOW(), NOW(), 0);

-- 8. 创建活动报名（每个学生报名2-3个活动）
INSERT INTO sys_activity_sign_up (id, activity_id, user_id, status, create_time, update_time, deleted) VALUES
-- student1 报名
(1, 1, 7, 1, NOW(), NOW(), 0),
(2, 2, 7, 1, NOW(), NOW(), 0),
(3, 3, 7, 1, NOW(), NOW(), 0),
-- student2 报名
(4, 4, 8, 1, NOW(), NOW(), 0),
(5, 5, 8, 1, NOW(), NOW(), 0),
-- student3 报名
(6, 7, 9, 1, NOW(), NOW(), 0),
(7, 8, 9, 1, NOW(), NOW(), 0),
(8, 9, 9, 1, NOW(), NOW(), 0),
-- student4 报名
(9, 10, 10, 1, NOW(), NOW(), 0),
(10, 11, 10, 1, NOW(), NOW(), 0),
-- student5 报名
(11, 13, 11, 1, NOW(), NOW(), 0),
(12, 14, 11, 1, NOW(), NOW(), 0),
(13, 15, 11, 1, NOW(), NOW(), 0),
-- student6 报名
(14, 1, 12, 1, NOW(), NOW(), 0),
(15, 2, 12, 1, NOW(), NOW(), 0),
-- student7 报名
(16, 4, 13, 1, NOW(), NOW(), 0),
(17, 5, 13, 1, NOW(), NOW(), 0),
(18, 6, 13, 1, NOW(), NOW(), 0),
-- student8 报名
(19, 1, 14, 1, NOW(), NOW(), 0),
(20, 3, 14, 1, NOW(), NOW(), 0),
-- student9 报名
(21, 4, 15, 1, NOW(), NOW(), 0),
(22, 5, 15, 1, NOW(), NOW(), 0),
(23, 13, 15, 1, NOW(), NOW(), 0),
-- student10 报名
(24, 7, 16, 1, NOW(), NOW(), 0),
(25, 8, 16, 1, NOW(), NOW(), 0),
-- student11 报名
(26, 10, 17, 1, NOW(), NOW(), 0),
(27, 11, 17, 1, NOW(), NOW(), 0),
(28, 12, 17, 1, NOW(), NOW(), 0),
-- student12 报名
(29, 13, 18, 1, NOW(), NOW(), 0),
(30, 14, 18, 1, NOW(), NOW(), 0),
-- student13 报名
(31, 1, 19, 1, NOW(), NOW(), 0),
(32, 2, 19, 1, NOW(), NOW(), 0),
(33, 3, 19, 1, NOW(), NOW(), 0),
-- student14 报名
(34, 4, 20, 1, NOW(), NOW(), 0),
(35, 5, 20, 1, NOW(), NOW(), 0),
-- student15 报名
(36, 7, 21, 1, NOW(), NOW(), 0),
(37, 9, 21, 1, NOW(), NOW(), 0),
(38, 13, 21, 1, NOW(), NOW(), 0);

-- 9. 创建入团申请
INSERT INTO sys_apply (id, club_id, club_name, user_id, user_name, status, reason, reject_reason, process_time, create_time, update_time, deleted) VALUES
(1, 1, '计算机协会', 8, '李小红', 2, '我对计算机技术很感兴趣，希望加入协会学习', NULL, NOW(), NOW(), NOW(), 0),
(2, 2, '编程社', 9, '张小刚', 2, '我喜欢编程，希望能和其他编程爱好者交流', NULL, NOW(), NOW(), NOW(), 0),
(3, 3, '文学社', 10, '赵小丽', 2, '我热爱文学，希望能在文学社中提高写作能力', NULL, NOW(), NOW(), NOW(), 0),
(4, 4, '诗词社', 11, '陈小明', 2, '我喜欢诗词，希望能和其他诗词爱好者一起学习', NULL, NOW(), NOW(), NOW(), 0),
(5, 5, '篮球社', 12, '林小红', 2, '我喜欢打篮球，希望能加入篮球社', NULL, NOW(), NOW(), NOW(), 0),
(6, 6, '足球社', 13, '龙小刚', 2, '我热爱足球，希望能加入足球社', NULL, NOW(), NOW(), NOW(), 0),
(7, 7, '志愿者协会', 14, '刘小丽', 2, '我想参与志愿服务，帮助他人', NULL, NOW(), NOW(), NOW(), 0),
(8, 8, '环保协会', 15, '黄小明', 2, '我关心环保，希望能为环保事业贡献力量', NULL, NOW(), NOW(), NOW(), 0),
(9, 9, '摄影社', 16, '杨小红', 2, '我喜欢摄影，希望能学习更多摄影技巧', NULL, NOW(), NOW(), NOW(), 0),
(10, 10, '动漫社', 17, '马小刚', 2, '我喜欢动漫，希望能和其他动漫爱好者交流', NULL, NOW(), NOW(), NOW(), 0);

-- 10. 创建通知
INSERT INTO sys_notification (id, title, content, type, target_id, sender_id, sender_name, priority, create_time, update_time, deleted) VALUES
(1, '系统通知', '欢迎使用高校社团与活动管理平台', 1, NULL, 1, '管理员', 1, NOW(), NOW(), 0),
(2, '活动提醒', '编程大赛即将开始，请准时参加', 2, 1, 2, '林一', 2, NOW(), NOW(), 0),
(3, '社团通知', '计算机协会招新开始', 3, 1, 2, '林一', 2, NOW(), NOW(), 0),
(4, '系统通知', '平台更新公告', 1, NULL, 1, '管理员', 1, NOW(), NOW(), 0),
(5, '活动提醒', '诗歌朗诵会报名截止', 2, 4, 3, '龙二', 2, NOW(), NOW(), 0),
(6, '社团通知', '文学社举办文学创作工作坊', 3, 3, 3, '龙二', 2, NOW(), NOW(), 0),
(7, '活动提醒', '篮球友谊赛即将开始', 2, 7, 4, '张三', 2, NOW(), NOW(), 0),
(8, '系统通知', '社团申请审核结果已发布', 1, NULL, 1, '管理员', 1, NOW(), NOW(), 0),
(9, '活动提醒', '志愿者活动集合时间通知', 2, 10, 5, '李四', 2, NOW(), NOW(), 0),
(10, '社团通知', '摄影展作品征集', 3, 9, 6, '冯五', 2, NOW(), NOW(), 0);

-- 11. 创建通知阅读记录
INSERT INTO sys_notification_read (id, notification_id, user_id, is_read, read_time, create_time, update_time, deleted) VALUES
(1, 1, 2, 1, NOW(), NOW(), NOW(), 0),
(2, 1, 3, 1, NOW(), NOW(), NOW(), 0),
(3, 1, 4, 1, NOW(), NOW(), NOW(), 0),
(4, 1, 5, 1, NOW(), NOW(), NOW(), 0),
(5, 1, 6, 1, NOW(), NOW(), NOW(), 0),
(6, 2, 7, 1, NOW(), NOW(), NOW(), 0),
(7, 3, 7, 1, NOW(), NOW(), NOW(), 0),
(8, 4, 2, 1, NOW(), NOW(), NOW(), 0),
(9, 5, 8, 1, NOW(), NOW(), NOW(), 0),
(10, 6, 8, 1, NOW(), NOW(), NOW(), 0),
(11, 7, 9, 1, NOW(), NOW(), NOW(), 0),
(12, 8, 10, 1, NOW(), NOW(), NOW(), 0),
(13, 9, 14, 1, NOW(), NOW(), NOW(), 0),
(14, 10, 11, 1, NOW(), NOW(), NOW(), 0);

-- 12. 创建缴费项目
INSERT INTO sys_payment (id, club_id, club_name, title, description, amount, deadline, status, create_time, update_time, deleted) VALUES
(1, 1, '计算机协会', '计算机协会会费', '用于协会活动经费和设备购置', 50.00, '2026-04-30', 1, NOW(), NOW(), 0),
(2, 2, '编程社', '编程社会费', '用于社团活动和学习资料购置', 40.00, '2026-04-30', 1, NOW(), NOW(), 0),
(3, 3, '文学社', '文学社会费', '用于文学活动和书籍购置', 30.00, '2026-04-30', 1, NOW(), NOW(), 0),
(4, 4, '诗词社', '诗词社会费', '用于诗词活动和资料购置', 25.00, '2026-04-30', 1, NOW(), NOW(), 0),
(5, 5, '篮球社', '篮球社会费', '用于篮球活动和器材购置', 80.00, '2026-04-30', 1, NOW(), NOW(), 0),
(6, 6, '足球社', '足球社会费', '用于足球活动和器材购置', 70.00, '2026-04-30', 1, NOW(), NOW(), 0),
(7, 7, '志愿者协会', '志愿者协会会费', '用于志愿服务活动经费', 20.00, '2026-04-30', 1, NOW(), NOW(), 0),
(8, 8, '环保协会', '环保协会会费', '用于环保活动和宣传经费', 25.00, '2026-04-30', 1, NOW(), NOW(), 0),
(9, 9, '摄影社', '摄影社会费', '用于摄影活动和设备购置', 60.00, '2026-04-30', 1, NOW(), NOW(), 0),
(10, 10, '动漫社', '动漫社会费', '用于动漫活动和材料购置', 55.00, '2026-04-30', 1, NOW(), NOW(), 0);

-- 13. 创建缴费记录
INSERT INTO sys_payment_record (id, payment_id, user_id, user_name, amount, status, pay_time, transaction_no, create_time, update_time, deleted) VALUES
(1, 1, 7, '王小明', 50.00, 1, NOW(), 'TRX202603220001', NOW(), NOW(), 0),
(2, 2, 7, '王小明', 40.00, 1, NOW(), 'TRX202603220002', NOW(), NOW(), 0),
(3, 3, 8, '李小红', 30.00, 1, NOW(), 'TRX202603220003', NOW(), NOW(), 0),
(4, 4, 8, '李小红', 25.00, 1, NOW(), 'TRX202603220004', NOW(), NOW(), 0),
(5, 5, 9, '张小刚', 80.00, 1, NOW(), 'TRX202603220005', NOW(), NOW(), 0),
(6, 6, 9, '张小刚', 70.00, 1, NOW(), 'TRX202603220006', NOW(), NOW(), 0),
(7, 7, 10, '赵小丽', 20.00, 1, NOW(), 'TRX202603220007', NOW(), NOW(), 0),
(8, 8, 10, '赵小丽', 25.00, 1, NOW(), 'TRX202603220008', NOW(), NOW(), 0),
(9, 9, 11, '陈小明', 60.00, 1, NOW(), 'TRX202603220009', NOW(), NOW(), 0),
(10, 10, 11, '陈小明', 55.00, 1, NOW(), 'TRX202603220010', NOW(), NOW(), 0),
(11, 1, 12, '林小红', 50.00, 1, NOW(), 'TRX202603220011', NOW(), NOW(), 0),
(12, 2, 12, '林小红', 40.00, 1, NOW(), 'TRX202603220012', NOW(), NOW(), 0),
(13, 3, 13, '龙小刚', 30.00, 1, NOW(), 'TRX202603220013', NOW(), NOW(), 0),
(14, 4, 13, '龙小刚', 25.00, 1, NOW(), 'TRX202603220014', NOW(), NOW(), 0),
(15, 5, 14, '刘小丽', 80.00, 1, NOW(), 'TRX202603220015', NOW(), NOW(), 0);

-- 14. 更新社团成员数
UPDATE sys_club SET member_count = (SELECT COUNT(*) FROM sys_club_member WHERE club_id = sys_club.id AND status = 1) WHERE id IN (1,2,3,4,5,6,7,8,9,10);

-- 15. 更新活动当前参与人数
UPDATE sys_activity SET current_participants = (SELECT COUNT(*) FROM sys_activity_sign_up WHERE activity_id = sys_activity.id AND status = 1) WHERE id IN (1,2,3,4,5,6,7,8,9,10,11,12,13,14,15);
