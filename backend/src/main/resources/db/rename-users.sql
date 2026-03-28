-- 修改用户名
-- 1. 将原来的 student1-5 (ID 7-11) 改为 president6-10
-- 2. 将原来的 student6-25 (ID 12-31) 改为 student1-20

-- 第一步：将 student1-5 改为 president6-10
UPDATE sys_user SET username = 'president6' WHERE id = 7;
UPDATE sys_user SET username = 'president7' WHERE id = 8;
UPDATE sys_user SET username = 'president8' WHERE id = 9;
UPDATE sys_user SET username = 'president9' WHERE id = 10;
UPDATE sys_user SET username = 'president10' WHERE id = 11;

-- 第二步：将 student6-25 改为 student1-20
UPDATE sys_user SET username = 'student1' WHERE id = 12;
UPDATE sys_user SET username = 'student2' WHERE id = 13;
UPDATE sys_user SET username = 'student3' WHERE id = 14;
UPDATE sys_user SET username = 'student4' WHERE id = 15;
UPDATE sys_user SET username = 'student5' WHERE id = 16;
UPDATE sys_user SET username = 'student6' WHERE id = 17;
UPDATE sys_user SET username = 'student7' WHERE id = 18;
UPDATE sys_user SET username = 'student8' WHERE id = 19;
UPDATE sys_user SET username = 'student9' WHERE id = 20;
UPDATE sys_user SET username = 'student10' WHERE id = 21;
UPDATE sys_user SET username = 'student11' WHERE id = 22;
UPDATE sys_user SET username = 'student12' WHERE id = 23;
UPDATE sys_user SET username = 'student13' WHERE id = 24;
UPDATE sys_user SET username = 'student14' WHERE id = 25;
UPDATE sys_user SET username = 'student15' WHERE id = 26;
UPDATE sys_user SET username = 'student16' WHERE id = 27;
UPDATE sys_user SET username = 'student17' WHERE id = 28;
UPDATE sys_user SET username = 'student18' WHERE id = 29;
UPDATE sys_user SET username = 'student19' WHERE id = 30;
UPDATE sys_user SET username = 'student20' WHERE id = 31;
