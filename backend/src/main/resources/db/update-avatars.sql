-- 更新用户头像为随机AI头像
-- 使用DiceBear API生成多样化头像

-- 更新管理员头像
UPDATE sys_user SET avatar = 'https://api.dicebear.com/7.x/avataaars/svg?seed=admin&backgroundColor=b6e3f4' WHERE id = 1;

-- 更新社长头像 (使用不同风格)
UPDATE sys_user SET avatar = 'https://api.dicebear.com/7.x/avataaars/svg?seed=president1&backgroundColor=c0aede' WHERE id = 2;
UPDATE sys_user SET avatar = 'https://api.dicebear.com/7.x/avataaars/svg?seed=president2&backgroundColor=ffdfbf' WHERE id = 3;
UPDATE sys_user SET avatar = 'https://api.dicebear.com/7.x/avataaars/svg?seed=president3&backgroundColor=d1d4f9' WHERE id = 4;
UPDATE sys_user SET avatar = 'https://api.dicebear.com/7.x/avataaars/svg?seed=president4&backgroundColor=ffd5dc' WHERE id = 5;
UPDATE sys_user SET avatar = 'https://api.dicebear.com/7.x/avataaars/svg?seed=president5&backgroundColor=b6e3f4' WHERE id = 6;

-- 更新学生头像 (使用多样化风格)
UPDATE sys_user SET avatar = 'https://api.dicebear.com/7.x/avataaars/svg?seed=student1&backgroundColor=c0aede' WHERE id = 7;
UPDATE sys_user SET avatar = 'https://api.dicebear.com/7.x/avataaars/svg?seed=student2&backgroundColor=ffdfbf' WHERE id = 8;
UPDATE sys_user SET avatar = 'https://api.dicebear.com/7.x/avataaars/svg?seed=student3&backgroundColor=d1d4f9' WHERE id = 9;
UPDATE sys_user SET avatar = 'https://api.dicebear.com/7.x/avataaars/svg?seed=student4&backgroundColor=ffd5dc' WHERE id = 10;
UPDATE sys_user SET avatar = 'https://api.dicebear.com/7.x/avataaars/svg?seed=student5&backgroundColor=b6e3f4' WHERE id = 11;
UPDATE sys_user SET avatar = 'https://api.dicebear.com/7.x/avataaars/svg?seed=student6&backgroundColor=c0aede' WHERE id = 12;
UPDATE sys_user SET avatar = 'https://api.dicebear.com/7.x/avataaars/svg?seed=student7&backgroundColor=ffdfbf' WHERE id = 13;
UPDATE sys_user SET avatar = 'https://api.dicebear.com/7.x/avataaars/svg?seed=student8&backgroundColor=d1d4f9' WHERE id = 14;
UPDATE sys_user SET avatar = 'https://api.dicebear.com/7.x/avataaars/svg?seed=student9&backgroundColor=ffd5dc' WHERE id = 15;
UPDATE sys_user SET avatar = 'https://api.dicebear.com/7.x/avataaars/svg?seed=student10&backgroundColor=b6e3f4' WHERE id = 16;
UPDATE sys_user SET avatar = 'https://api.dicebear.com/7.x/avataaars/svg?seed=student11&backgroundColor=c0aede' WHERE id = 17;
UPDATE sys_user SET avatar = 'https://api.dicebear.com/7.x/avataaars/svg?seed=student12&backgroundColor=ffdfbf' WHERE id = 18;
UPDATE sys_user SET avatar = 'https://api.dicebear.com/7.x/avataaars/svg?seed=student13&backgroundColor=d1d4f9' WHERE id = 19;
UPDATE sys_user SET avatar = 'https://api.dicebear.com/7.x/avataaars/svg?seed=student14&backgroundColor=ffd5dc' WHERE id = 20;
UPDATE sys_user SET avatar = 'https://api.dicebear.com/7.x/avataaars/svg?seed=student15&backgroundColor=b6e3f4' WHERE id = 21;

-- 验证更新结果
SELECT id, username, nickname, avatar, role FROM sys_user;
