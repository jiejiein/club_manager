-- 添加10个新的学生用户 (student16-25)
-- 用于补充社团成员数量

INSERT INTO sys_user (id, username, password, nickname, email, phone, avatar, role, status, student_no, college, major, class_name, create_time, update_time, deleted) VALUES
(22, 'student16', '123456', '周小明', 'student16@example.com', '13800138021', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student16&backgroundColor=c0aede', 3, 1, '20230021', '计算机学院', '网络工程', '网工1班', NOW(), NOW(), 0),
(23, 'student17', '123456', '吴小红', 'student17@example.com', '13800138022', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student17&backgroundColor=ffdfbf', 3, 1, '20230022', '文学院', '广告学', '广告1班', NOW(), NOW(), 0),
(24, 'student18', '123456', '郑小刚', 'student18@example.com', '13800138023', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student18&backgroundColor=d1d4f9', 3, 1, '20230023', '体育学院', '休闲体育', '休体1班', NOW(), NOW(), 0),
(25, 'student19', '123456', '王小丽', 'student19@example.com', '13800138024', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student19&backgroundColor=ffd5dc', 3, 1, '20230024', '社会学院', '心理学', '心理1班', NOW(), NOW(), 0),
(26, 'student20', '123456', '冯小明', 'student20@example.com', '13800138025', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student20&backgroundColor=b6e3f4', 3, 1, '20230025', '艺术学院', '舞蹈学', '舞蹈1班', NOW(), NOW(), 0),
(27, 'student21', '123456', '陈小红', 'student21@example.com', '13800138026', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student21&backgroundColor=c0aede', 3, 1, '20230026', '理学院', '化学', '化学1班', NOW(), NOW(), 0),
(28, 'student22', '123456', '褚小刚', 'student22@example.com', '13800138027', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student22&backgroundColor=ffdfbf', 3, 1, '20230027', '环境学院', '生态学', '生态1班', NOW(), NOW(), 0),
(29, 'student23', '123456', '卫小丽', 'student23@example.com', '13800138028', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student23&backgroundColor=d1d4f9', 3, 1, '20230028', '计算机学院', '信息安全', '信安1班', NOW(), NOW(), 0),
(30, 'student24', '123456', '蒋小明', 'student24@example.com', '13800138029', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student24&backgroundColor=ffd5dc', 3, 1, '20230029', '文学院', '哲学', '哲学1班', NOW(), NOW(), 0),
(31, 'student25', '123456', '沈小红', 'student25@example.com', '13800138030', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student25&backgroundColor=b6e3f4', 3, 1, '20230030', '体育学院', '运动人体科学', '运人1班', NOW(), NOW(), 0);

-- 将这些新学生随机分配到各个社团（每人加入2-3个社团）
INSERT INTO sys_club_member (club_id, user_id, user_name, status, join_time, create_time, update_time, deleted) VALUES
-- student16 加入 3 个社团
(1, 22, '周小明', 1, NOW(), NOW(), NOW(), 0),
(3, 22, '周小明', 1, NOW(), NOW(), NOW(), 0),
(5, 22, '周小明', 1, NOW(), NOW(), NOW(), 0),
-- student17 加入 2 个社团
(2, 23, '吴小红', 1, NOW(), NOW(), NOW(), 0),
(4, 23, '吴小红', 1, NOW(), NOW(), NOW(), 0),
-- student18 加入 3 个社团
(5, 24, '郑小刚', 1, NOW(), NOW(), NOW(), 0),
(6, 24, '郑小刚', 1, NOW(), NOW(), NOW(), 0),
(7, 24, '郑小刚', 1, NOW(), NOW(), NOW(), 0),
-- student19 加入 2 个社团
(7, 25, '王小丽', 1, NOW(), NOW(), NOW(), 0),
(8, 25, '王小丽', 1, NOW(), NOW(), NOW(), 0),
-- student20 加入 3 个社团
(9, 26, '冯小明', 1, NOW(), NOW(), NOW(), 0),
(10, 26, '冯小明', 1, NOW(), NOW(), NOW(), 0),
(1, 26, '冯小明', 1, NOW(), NOW(), NOW(), 0),
-- student21 加入 2 个社团
(2, 27, '陈小红', 1, NOW(), NOW(), NOW(), 0),
(3, 27, '陈小红', 1, NOW(), NOW(), NOW(), 0),
-- student22 加入 3 个社团
(4, 28, '褚小刚', 1, NOW(), NOW(), NOW(), 0),
(6, 28, '褚小刚', 1, NOW(), NOW(), NOW(), 0),
(8, 28, '褚小刚', 1, NOW(), NOW(), NOW(), 0),
-- student23 加入 2 个社团
(9, 29, '卫小丽', 1, NOW(), NOW(), NOW(), 0),
(10, 29, '卫小丽', 1, NOW(), NOW(), NOW(), 0),
-- student24 加入 3 个社团
(1, 30, '蒋小明', 1, NOW(), NOW(), NOW(), 0),
(3, 30, '蒋小明', 1, NOW(), NOW(), NOW(), 0),
(5, 30, '蒋小明', 1, NOW(), NOW(), NOW(), 0),
-- student25 加入 2 个社团
(2, 31, '沈小红', 1, NOW(), NOW(), NOW(), 0),
(4, 31, '沈小红', 1, NOW(), NOW(), NOW(), 0);

-- 更新各社团的成员数量
UPDATE sys_club SET member_count = (
    SELECT COUNT(*) FROM sys_club_member 
    WHERE sys_club_member.club_id = sys_club.id AND sys_club_member.deleted = 0
);
