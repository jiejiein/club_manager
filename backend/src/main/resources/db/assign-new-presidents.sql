-- 将 student1-5 (ID 7-11) 设置为新的社长
-- 每人担任一个社团的社长

-- 1. 更新用户角色为社长
UPDATE sys_user SET role = 2 WHERE id IN (7, 8, 9, 10, 11);

-- 2. 重新分配社团社长
-- student1 (ID=7, 王小明) -> 计算机协会 (ID=1)
UPDATE sys_club SET 
    president_id = 7, 
    president_name = '王小明' 
WHERE id = 1;

-- student2 (ID=8, 李小红) -> 编程社 (ID=2)
UPDATE sys_club SET 
    president_id = 8, 
    president_name = '李小红' 
WHERE id = 2;

-- student3 (ID=9, 张小刚) -> 文学社 (ID=3)
UPDATE sys_club SET 
    president_id = 9, 
    president_name = '张小刚' 
WHERE id = 3;

-- student4 (ID=10, 赵小丽) -> 诗词社 (ID=4)
UPDATE sys_club SET 
    president_id = 10, 
    president_name = '赵小丽' 
WHERE id = 4;

-- student5 (ID=11, 陈小明) -> 篮球社 (ID=5)
UPDATE sys_club SET 
    president_id = 11, 
    president_name = '陈小明' 
WHERE id = 5;

-- 3. 删除这些学生作为普通成员的记录（因为他们现在是社长了）
-- 删除 student1 在社团1的成员记录
DELETE FROM sys_club_member WHERE user_id = 7 AND club_id = 1;
-- 删除 student1 在社团2的成员记录
DELETE FROM sys_club_member WHERE user_id = 7 AND club_id = 2;
-- 删除 student1 在社团5的成员记录
DELETE FROM sys_club_member WHERE user_id = 7 AND club_id = 5;

-- 删除 student2 在社团3的成员记录
DELETE FROM sys_club_member WHERE user_id = 8 AND club_id = 3;
-- 删除 student2 在社团4的成员记录
DELETE FROM sys_club_member WHERE user_id = 8 AND club_id = 4;

-- 删除 student3 在社团5的成员记录
DELETE FROM sys_club_member WHERE user_id = 9 AND club_id = 5;
-- 删除 student3 在社团6的成员记录
DELETE FROM sys_club_member WHERE user_id = 9 AND club_id = 6;
-- 删除 student3 在社团7的成员记录
DELETE FROM sys_club_member WHERE user_id = 9 AND club_id = 7;

-- 删除 student4 在社团7的成员记录
DELETE FROM sys_club_member WHERE user_id = 10 AND club_id = 7;
-- 删除 student4 在社团8的成员记录
DELETE FROM sys_club_member WHERE user_id = 10 AND club_id = 8;

-- 删除 student5 在社团9的成员记录
DELETE FROM sys_club_member WHERE user_id = 11 AND club_id = 9;
-- 删除 student5 在社团10的成员记录
DELETE FROM sys_club_member WHERE user_id = 11 AND club_id = 10;
-- 删除 student5 在社团3的成员记录
DELETE FROM sys_club_member WHERE user_id = 11 AND club_id = 3;

-- 4. 更新各社团的成员数量
UPDATE sys_club SET member_count = (
    SELECT COUNT(*) FROM sys_club_member 
    WHERE sys_club_member.club_id = sys_club.id AND sys_club_member.deleted = 0
);

-- 5. 将 president1-5 (ID 2-6) 分配为剩余5个社团的社长
-- 注意：他们保持社长角色(role=2)，只是换了社团

-- president1 (ID=2, 林一) -> 足球社 (ID=6)
UPDATE sys_club SET 
    president_id = 2, 
    president_name = '林一' 
WHERE id = 6;

-- president2 (ID=3, 龙二) -> 志愿者协会 (ID=7)
UPDATE sys_club SET 
    president_id = 3, 
    president_name = '龙二' 
WHERE id = 7;

-- president3 (ID=4, 张三) -> 环保协会 (ID=8)
UPDATE sys_club SET 
    president_id = 4, 
    president_name = '张三' 
WHERE id = 8;

-- president4 (ID=5, 李四) -> 摄影社 (ID=9)
UPDATE sys_club SET 
    president_id = 5, 
    president_name = '李四' 
WHERE id = 9;

-- president5 (ID=6, 冯五) -> 动漫社 (ID=10)
UPDATE sys_club SET 
    president_id = 6, 
    president_name = '冯五' 
WHERE id = 10;
