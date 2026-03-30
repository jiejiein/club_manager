/*
 Navicat Premium Data Transfer

 Source Server         : club
 Source Server Type    : MySQL
 Source Server Version : 80045
 Source Host           : localhost:3306
 Source Schema         : club_manager

 Target Server Type    : MySQL
 Target Server Version : 80045
 File Encoding         : 65001

 Date: 30/03/2026 14:07:27
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_activity
-- ----------------------------
DROP TABLE IF EXISTS `sys_activity`;
CREATE TABLE `sys_activity`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '活动ID',
  `club_id` bigint NOT NULL COMMENT '社团ID',
  `club_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '社团名称',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '活动标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '活动描述',
  `cover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '活动封面',
  `location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '活动地点',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `sign_up_start` datetime NULL DEFAULT NULL COMMENT '报名开始时间',
  `sign_up_end` datetime NULL DEFAULT NULL COMMENT '报名截止时间',
  `max_participants` int NULL DEFAULT 100 COMMENT '最大参与人数',
  `current_participants` int NULL DEFAULT 0 COMMENT '当前报名人数',
  `status` int NULL DEFAULT 0 COMMENT '状态：0草稿 1待审核 2已发布 3进行中 4已结束 5已取消',
  `reject_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '拒绝原因',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NULL DEFAULT 0 COMMENT '是否删除：0否 1是',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '活动表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_activity
-- ----------------------------
INSERT INTO `sys_activity` VALUES (1, 1, '计算机协会', '编程大赛', '面向全校学生的编程竞赛', 'cover1.png', '教学楼A101', '2026-04-01 14:00:00', '2026-04-01 18:00:00', '2026-03-23 00:00:00', '2026-03-31 23:59:59', 50, 4, 3, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity` VALUES (2, 1, '计算机协会', '算法讲座', '算法学习经验分享', 'cover2.png', '教学楼B201', '2026-04-05 15:00:00', '2026-04-05 17:00:00', '2026-03-23 00:00:00', '2026-04-04 23:59:59', 80, 3, 2, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity` VALUES (3, 2, '编程社', '代码马拉松', '24小时编程挑战', 'cover3.png', '计算机实验室', '2026-04-02 10:00:00', '2026-04-02 22:00:00', '2026-03-23 00:00:00', '2026-03-31 23:59:59', 30, 3, 2, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity` VALUES (4, 3, '文学社', '诗歌朗诵会', '诗歌爱好者的盛会', 'cover4.png', '图书馆报告厅', '2026-04-03 19:00:00', '2026-04-03 21:00:00', '2026-03-23 00:00:00', '2026-04-02 23:59:59', 100, 4, 2, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity` VALUES (5, 3, '文学社', '文学创作工作坊', '文学创作技巧分享', 'cover5.png', '文学院会议室', '2026-04-06 14:00:00', '2026-04-06 16:00:00', '2026-03-23 00:00:00', '2026-04-05 23:59:59', 20, 4, 2, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity` VALUES (6, 4, '诗词社', '诗词大会', '诗词知识竞赛', 'cover6.png', '图书馆报告厅', '2026-04-04 15:00:00', '2026-04-04 17:00:00', '2026-03-23 00:00:00', '2026-04-03 23:59:59', 50, 1, 2, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity` VALUES (7, 5, '篮球社', '篮球友谊赛', '与其他高校的篮球友谊赛', 'cover7.png', '体育馆', '2026-04-07 16:00:00', '2026-04-07 18:00:00', '2026-03-23 00:00:00', '2026-04-06 23:59:59', 20, 3, 2, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity` VALUES (8, 5, '篮球社', '篮球训练营', '篮球技巧训练', 'cover8.png', '体育馆', '2026-04-08 14:00:00', '2026-04-08 16:00:00', '2026-03-23 00:00:00', '2026-04-07 23:59:59', 30, 2, 2, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity` VALUES (9, 6, '足球社', '足球联赛', '校内足球联赛', 'cover9.png', '足球场', '2026-04-09 14:00:00', '2026-04-09 16:00:00', '2026-03-23 00:00:00', '2026-04-08 23:59:59', 22, 2, 2, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity` VALUES (10, 7, '志愿者协会', '社区志愿服务', '社区清洁和老人关怀活动', 'cover10.png', '社区中心', '2026-04-10 09:00:00', '2026-04-10 12:00:00', '2026-03-23 00:00:00', '2026-04-09 23:59:59', 30, 2, 2, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity` VALUES (11, 7, '志愿者协会', '校园环保活动', '校园环境清洁活动', 'cover11.png', '校园', '2026-04-11 14:00:00', '2026-04-11 16:00:00', '2026-03-23 00:00:00', '2026-04-10 23:59:59', 50, 2, 2, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity` VALUES (12, 8, '环保协会', '环保讲座', '环保知识讲座', 'cover12.png', '环境学院报告厅', '2026-04-12 16:00:00', '2026-04-12 18:00:00', '2026-03-23 00:00:00', '2026-04-11 23:59:59', 60, 1, 2, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity` VALUES (13, 9, '摄影社', '摄影展览', '学生摄影作品展览', 'cover13.png', '艺术楼展厅', '2026-04-13 10:00:00', '2026-04-18 17:00:00', '2026-03-23 00:00:00', '2026-04-12 23:59:59', 200, 4, 2, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity` VALUES (14, 9, '摄影社', '摄影技巧讲座', '摄影技巧分享', 'cover14.png', '艺术学院会议室', '2026-04-14 15:00:00', '2026-04-14 17:00:00', '2026-03-23 00:00:00', '2026-04-13 23:59:59', 40, 2, 2, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity` VALUES (15, 10, '动漫社', '动漫嘉年华', '动漫主题活动', 'cover15.png', '体育馆', '2026-04-15 10:00:00', '2026-04-15 17:00:00', '2026-03-23 00:00:00', '2026-04-14 23:59:59', 300, 1, 2, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);

-- ----------------------------
-- Table structure for sys_activity_sign_up
-- ----------------------------
DROP TABLE IF EXISTS `sys_activity_sign_up`;
CREATE TABLE `sys_activity_sign_up`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '报名ID',
  `activity_id` bigint NOT NULL COMMENT '活动ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户姓名',
  `status` int NULL DEFAULT 0 COMMENT '状态：0待审核 1已通过 2已拒绝',
  `sign_up_time` datetime NULL DEFAULT NULL COMMENT '报名时间',
  `check_in_time` datetime NULL DEFAULT NULL COMMENT '签到时间',
  `checked` int NULL DEFAULT 0 COMMENT '是否签到：0否 1是',
  `rating` int NULL DEFAULT NULL COMMENT '评分：1-5',
  `comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评价内容',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NULL DEFAULT 0 COMMENT '是否删除：0否 1是',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 38 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '活动报名表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_activity_sign_up
-- ----------------------------
INSERT INTO `sys_activity_sign_up` VALUES (1, 1, 7, NULL, 1, NULL, NULL, 0, NULL, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity_sign_up` VALUES (2, 2, 7, NULL, 1, NULL, NULL, 0, NULL, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity_sign_up` VALUES (3, 3, 7, NULL, 1, NULL, NULL, 0, NULL, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity_sign_up` VALUES (4, 4, 8, NULL, 1, NULL, NULL, 0, NULL, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity_sign_up` VALUES (5, 5, 8, NULL, 1, NULL, NULL, 0, NULL, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity_sign_up` VALUES (6, 7, 9, NULL, 1, NULL, NULL, 0, NULL, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity_sign_up` VALUES (7, 8, 9, NULL, 1, NULL, NULL, 0, NULL, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity_sign_up` VALUES (8, 9, 9, NULL, 1, NULL, NULL, 0, NULL, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity_sign_up` VALUES (9, 10, 10, NULL, 1, NULL, NULL, 0, NULL, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity_sign_up` VALUES (10, 11, 10, NULL, 1, NULL, NULL, 0, NULL, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity_sign_up` VALUES (11, 13, 11, NULL, 1, NULL, NULL, 0, NULL, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity_sign_up` VALUES (12, 14, 11, NULL, 1, NULL, NULL, 0, NULL, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity_sign_up` VALUES (13, 15, 11, NULL, 1, NULL, NULL, 0, NULL, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity_sign_up` VALUES (14, 1, 12, NULL, 1, NULL, NULL, 0, NULL, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity_sign_up` VALUES (15, 2, 12, NULL, 1, NULL, NULL, 0, NULL, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity_sign_up` VALUES (16, 4, 13, NULL, 1, NULL, NULL, 0, NULL, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity_sign_up` VALUES (17, 5, 13, NULL, 1, NULL, NULL, 0, NULL, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity_sign_up` VALUES (18, 6, 13, NULL, 1, NULL, NULL, 0, NULL, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity_sign_up` VALUES (19, 1, 14, NULL, 1, NULL, NULL, 0, NULL, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity_sign_up` VALUES (20, 3, 14, NULL, 1, NULL, NULL, 0, NULL, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity_sign_up` VALUES (21, 4, 15, NULL, 1, NULL, NULL, 0, NULL, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity_sign_up` VALUES (22, 5, 15, NULL, 1, NULL, NULL, 0, NULL, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity_sign_up` VALUES (23, 13, 15, NULL, 1, NULL, NULL, 0, NULL, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity_sign_up` VALUES (24, 7, 16, NULL, 1, NULL, NULL, 0, NULL, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity_sign_up` VALUES (25, 8, 16, NULL, 1, NULL, NULL, 0, NULL, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity_sign_up` VALUES (26, 10, 17, NULL, 1, NULL, NULL, 0, NULL, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity_sign_up` VALUES (27, 11, 17, NULL, 1, NULL, NULL, 0, NULL, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity_sign_up` VALUES (28, 12, 17, NULL, 1, NULL, NULL, 0, NULL, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity_sign_up` VALUES (29, 13, 18, NULL, 1, NULL, NULL, 0, NULL, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity_sign_up` VALUES (30, 14, 18, NULL, 1, NULL, NULL, 0, NULL, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity_sign_up` VALUES (31, 1, 19, NULL, 1, NULL, NULL, 0, NULL, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity_sign_up` VALUES (32, 2, 19, NULL, 1, NULL, NULL, 0, NULL, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity_sign_up` VALUES (33, 3, 19, NULL, 1, NULL, NULL, 0, NULL, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity_sign_up` VALUES (34, 4, 20, NULL, 1, NULL, NULL, 0, NULL, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity_sign_up` VALUES (35, 5, 20, NULL, 1, NULL, NULL, 0, NULL, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity_sign_up` VALUES (36, 7, 21, NULL, 1, NULL, NULL, 0, NULL, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity_sign_up` VALUES (37, 9, 21, NULL, 1, NULL, NULL, 0, NULL, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_activity_sign_up` VALUES (38, 13, 21, NULL, 1, NULL, NULL, 0, NULL, NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);

-- ----------------------------
-- Table structure for sys_apply
-- ----------------------------
DROP TABLE IF EXISTS `sys_apply`;
CREATE TABLE `sys_apply`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '申请ID',
  `club_id` bigint NOT NULL COMMENT '社团ID',
  `club_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '社团名称',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户姓名',
  `status` int NULL DEFAULT 0 COMMENT '状态：0待审核 1已通过 2已拒绝',
  `reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '申请理由',
  `reject_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '拒绝原因',
  `process_time` datetime NULL DEFAULT NULL COMMENT '处理时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NULL DEFAULT 0 COMMENT '是否删除：0否 1是',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '入团申请表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_apply
-- ----------------------------
INSERT INTO `sys_apply` VALUES (1, 1, '计算机协会', 8, '李小红', 2, '我对计算机技术很感兴趣，希望加入协会学习', NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_apply` VALUES (2, 2, '编程社', 9, '张小刚', 2, '我喜欢编程，希望能和其他编程爱好者交流', NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_apply` VALUES (3, 3, '文学社', 10, '赵小丽', 2, '我热爱文学，希望能在文学社中提高写作能力', NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_apply` VALUES (4, 4, '诗词社', 11, '陈小明', 2, '我喜欢诗词，希望能和其他诗词爱好者一起学习', NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_apply` VALUES (5, 5, '篮球社', 12, '林小红', 2, '我喜欢打篮球，希望能加入篮球社', NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_apply` VALUES (6, 6, '足球社', 13, '龙小刚', 2, '我热爱足球，希望能加入足球社', NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_apply` VALUES (7, 7, '志愿者协会', 14, '刘小丽', 2, '我想参与志愿服务，帮助他人', NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_apply` VALUES (8, 8, '环保协会', 15, '黄小明', 2, '我关心环保，希望能为环保事业贡献力量', NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_apply` VALUES (9, 9, '摄影社', 16, '杨小红', 2, '我喜欢摄影，希望能学习更多摄影技巧', NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_apply` VALUES (10, 10, '动漫社', 17, '马小刚', 2, '我喜欢动漫，希望能和其他动漫爱好者交流', NULL, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_apply` VALUES (11, 12, '222', 7, '王小明', 1, '无', NULL, '2026-03-29 03:42:54', '2026-03-29 03:42:25', '2026-03-29 03:42:25', 0);

-- ----------------------------
-- Table structure for sys_club
-- ----------------------------
DROP TABLE IF EXISTS `sys_club`;
CREATE TABLE `sys_club`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '社团ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '社团名称',
  `type_id` bigint NULL DEFAULT NULL COMMENT '类型ID',
  `type_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '类型名称',
  `president_id` bigint NULL DEFAULT NULL COMMENT '社长ID',
  `president_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '社长姓名',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '社团描述',
  `logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '社团logo',
  `status` int NULL DEFAULT 0 COMMENT '状态：0待审核 1正常运营 2已注销',
  `member_count` int NULL DEFAULT 0 COMMENT '成员数量',
  `reject_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '拒绝原因',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NULL DEFAULT 0 COMMENT '是否删除：0否 1是',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '社团表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_club
-- ----------------------------
INSERT INTO `sys_club` VALUES (1, '计算机协会', 1, '学术科技', 7, '王小明', '专注于计算机技术学习和交流', 'logo1.png', 1, 6, NULL, '2026-03-27 23:45:36', '2026-03-29 04:06:26', 0);
INSERT INTO `sys_club` VALUES (2, '编程社', 1, '学术科技', 8, '李小红', '编程爱好者的聚集地', 'logo2.png', 1, 6, NULL, '2026-03-27 23:45:36', '2026-03-29 04:06:26', 0);
INSERT INTO `sys_club` VALUES (3, '文学社', 2, '文化艺术', 9, '张小刚', '文学创作和欣赏', 'logo3.png', 1, 6, NULL, '2026-03-27 23:45:36', '2026-03-29 04:06:26', 0);
INSERT INTO `sys_club` VALUES (4, '诗词社', 2, '文化艺术', 10, '赵小丽', '诗词爱好者的交流平台', 'logo4.png', 1, 6, NULL, '2026-03-27 23:45:36', '2026-03-29 04:06:26', 0);
INSERT INTO `sys_club` VALUES (5, '篮球社', 3, '体育健身', 11, '陈小明', '篮球爱好者的聚集地', 'logo5.png', 1, 5, NULL, '2026-03-27 23:45:36', '2026-03-29 04:06:26', 0);
INSERT INTO `sys_club` VALUES (6, '足球社', 3, '体育健身', 2, '林一', '足球爱好者的乐园', 'logo6.png', 1, 4, NULL, '2026-03-27 23:45:36', '2026-03-29 04:11:53', 0);
INSERT INTO `sys_club` VALUES (7, '志愿者协会', 4, '志愿服务', 3, '龙二', '组织各类志愿服务活动', 'logo7.png', 1, 4, NULL, '2026-03-27 23:45:36', '2026-03-29 04:11:53', 0);
INSERT INTO `sys_club` VALUES (8, '环保协会', 4, '志愿服务', 4, '张三', '环保活动和宣传', 'logo8.png', 1, 4, NULL, '2026-03-27 23:45:36', '2026-03-29 04:11:53', 0);
INSERT INTO `sys_club` VALUES (9, '摄影社', 5, '兴趣爱好', 5, '李四', '摄影爱好者的交流平台', 'logo9.png', 1, 5, NULL, '2026-03-27 23:45:36', '2026-03-29 04:11:53', 0);
INSERT INTO `sys_club` VALUES (10, '动漫社', 5, '兴趣爱好', 6, '冯五', '动漫爱好者的交流社区', 'logo10.png', 1, 4, NULL, '2026-03-27 23:45:36', '2026-03-29 04:11:53', 0);
INSERT INTO `sys_club` VALUES (11, '123', 5, '兴趣爱好', 1, '管理员', '无', NULL, 2, 0, '', '2026-03-29 02:58:06', '2026-03-29 03:29:35', 1);
INSERT INTO `sys_club` VALUES (12, '222', 2, '文化艺术', 7, '王小明', '', NULL, 1, 1, NULL, '2026-03-29 03:27:52', '2026-03-29 03:47:44', 1);
INSERT INTO `sys_club` VALUES (13, '123', 1, '学术科技', 1, '管理员', '', NULL, 2, 0, '', '2026-03-29 03:41:33', '2026-03-29 03:41:42', 1);

-- ----------------------------
-- Table structure for sys_club_member
-- ----------------------------
DROP TABLE IF EXISTS `sys_club_member`;
CREATE TABLE `sys_club_member`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `club_id` bigint NOT NULL COMMENT '社团ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户姓名',
  `status` int NULL DEFAULT 1 COMMENT '状态：0禁用 1启用',
  `join_time` datetime NULL DEFAULT NULL COMMENT '加入时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NULL DEFAULT 0 COMMENT '是否删除：0否 1是',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 64 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '社团成员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_club_member
-- ----------------------------
INSERT INTO `sys_club_member` VALUES (14, 1, 12, '林小红', 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_club_member` VALUES (15, 2, 12, '林小红', 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_club_member` VALUES (16, 3, 13, '龙小刚', 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_club_member` VALUES (17, 4, 13, '龙小刚', 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_club_member` VALUES (18, 8, 13, '龙小刚', 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_club_member` VALUES (19, 1, 14, '刘小丽', 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_club_member` VALUES (20, 2, 14, '刘小丽', 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_club_member` VALUES (21, 3, 15, '黄小明', 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_club_member` VALUES (22, 4, 15, '黄小明', 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_club_member` VALUES (23, 9, 15, '黄小明', 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_club_member` VALUES (24, 5, 16, '杨小红', 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_club_member` VALUES (25, 6, 16, '杨小红', 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_club_member` VALUES (26, 7, 17, '马小刚', 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_club_member` VALUES (27, 8, 17, '马小刚', 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_club_member` VALUES (28, 10, 17, '马小刚', 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_club_member` VALUES (29, 9, 18, '牛小丽', 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_club_member` VALUES (30, 10, 18, '牛小丽', 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_club_member` VALUES (31, 1, 19, '羊小明', 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_club_member` VALUES (32, 2, 19, '羊小明', 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_club_member` VALUES (33, 5, 19, '羊小明', 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_club_member` VALUES (34, 3, 20, '猴小红', 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_club_member` VALUES (35, 4, 20, '猴小红', 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_club_member` VALUES (36, 6, 21, '鸡小刚', 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_club_member` VALUES (37, 7, 21, '鸡小刚', 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_club_member` VALUES (38, 9, 21, '鸡小刚', 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_club_member` VALUES (39, 12, 7, '王小明', 1, '2026-03-29 03:42:54', '2026-03-29 03:42:54', '2026-03-29 03:42:54', 0);
INSERT INTO `sys_club_member` VALUES (40, 1, 22, '周小明', 1, '2026-03-29 04:01:55', '2026-03-29 04:01:55', '2026-03-29 04:01:55', 0);
INSERT INTO `sys_club_member` VALUES (41, 3, 22, '周小明', 1, '2026-03-29 04:01:55', '2026-03-29 04:01:55', '2026-03-29 04:01:55', 0);
INSERT INTO `sys_club_member` VALUES (42, 5, 22, '周小明', 1, '2026-03-29 04:01:55', '2026-03-29 04:01:55', '2026-03-29 04:01:55', 0);
INSERT INTO `sys_club_member` VALUES (43, 2, 23, '吴小红', 1, '2026-03-29 04:01:55', '2026-03-29 04:01:55', '2026-03-29 04:01:55', 0);
INSERT INTO `sys_club_member` VALUES (44, 4, 23, '吴小红', 1, '2026-03-29 04:01:55', '2026-03-29 04:01:55', '2026-03-29 04:01:55', 0);
INSERT INTO `sys_club_member` VALUES (45, 5, 24, '郑小刚', 1, '2026-03-29 04:01:55', '2026-03-29 04:01:55', '2026-03-29 04:01:55', 0);
INSERT INTO `sys_club_member` VALUES (46, 6, 24, '郑小刚', 1, '2026-03-29 04:01:55', '2026-03-29 04:01:55', '2026-03-29 04:01:55', 0);
INSERT INTO `sys_club_member` VALUES (47, 7, 24, '郑小刚', 1, '2026-03-29 04:01:55', '2026-03-29 04:01:55', '2026-03-29 04:01:55', 0);
INSERT INTO `sys_club_member` VALUES (48, 7, 25, '王小丽', 1, '2026-03-29 04:01:55', '2026-03-29 04:01:55', '2026-03-29 04:01:55', 0);
INSERT INTO `sys_club_member` VALUES (49, 8, 25, '王小丽', 1, '2026-03-29 04:01:55', '2026-03-29 04:01:55', '2026-03-29 04:01:55', 0);
INSERT INTO `sys_club_member` VALUES (50, 9, 26, '冯小明', 1, '2026-03-29 04:01:55', '2026-03-29 04:01:55', '2026-03-29 04:01:55', 0);
INSERT INTO `sys_club_member` VALUES (51, 10, 26, '冯小明', 1, '2026-03-29 04:01:55', '2026-03-29 04:01:55', '2026-03-29 04:01:55', 0);
INSERT INTO `sys_club_member` VALUES (52, 1, 26, '冯小明', 1, '2026-03-29 04:01:55', '2026-03-29 04:01:55', '2026-03-29 04:01:55', 0);
INSERT INTO `sys_club_member` VALUES (53, 2, 27, '陈小红', 1, '2026-03-29 04:01:55', '2026-03-29 04:01:55', '2026-03-29 04:01:55', 0);
INSERT INTO `sys_club_member` VALUES (54, 3, 27, '陈小红', 1, '2026-03-29 04:01:55', '2026-03-29 04:01:55', '2026-03-29 04:01:55', 0);
INSERT INTO `sys_club_member` VALUES (55, 4, 28, '褚小刚', 1, '2026-03-29 04:01:55', '2026-03-29 04:01:55', '2026-03-29 04:01:55', 0);
INSERT INTO `sys_club_member` VALUES (56, 6, 28, '褚小刚', 1, '2026-03-29 04:01:55', '2026-03-29 04:01:55', '2026-03-29 04:01:55', 0);
INSERT INTO `sys_club_member` VALUES (57, 8, 28, '褚小刚', 1, '2026-03-29 04:01:55', '2026-03-29 04:01:55', '2026-03-29 04:01:55', 0);
INSERT INTO `sys_club_member` VALUES (58, 9, 29, '卫小丽', 1, '2026-03-29 04:01:55', '2026-03-29 04:01:55', '2026-03-29 04:01:55', 0);
INSERT INTO `sys_club_member` VALUES (59, 10, 29, '卫小丽', 1, '2026-03-29 04:01:55', '2026-03-29 04:01:55', '2026-03-29 04:01:55', 0);
INSERT INTO `sys_club_member` VALUES (60, 1, 30, '蒋小明', 1, '2026-03-29 04:01:55', '2026-03-29 04:01:55', '2026-03-29 04:01:55', 0);
INSERT INTO `sys_club_member` VALUES (61, 3, 30, '蒋小明', 1, '2026-03-29 04:01:55', '2026-03-29 04:01:55', '2026-03-29 04:01:55', 0);
INSERT INTO `sys_club_member` VALUES (62, 5, 30, '蒋小明', 1, '2026-03-29 04:01:55', '2026-03-29 04:01:55', '2026-03-29 04:01:55', 0);
INSERT INTO `sys_club_member` VALUES (63, 2, 31, '沈小红', 1, '2026-03-29 04:01:55', '2026-03-29 04:01:55', '2026-03-29 04:01:55', 0);
INSERT INTO `sys_club_member` VALUES (64, 4, 31, '沈小红', 1, '2026-03-29 04:01:55', '2026-03-29 04:01:55', '2026-03-29 04:01:55', 0);

-- ----------------------------
-- Table structure for sys_club_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_club_type`;
CREATE TABLE `sys_club_type`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '类型ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类型名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '类型描述',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NULL DEFAULT 0 COMMENT '是否删除：0否 1是',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '社团类型表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_club_type
-- ----------------------------
INSERT INTO `sys_club_type` VALUES (1, '学术科技', '学术研究和科技创新类社团', 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_club_type` VALUES (2, '文化艺术', '文化、艺术、音乐、舞蹈等社团', 2, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_club_type` VALUES (3, '体育健身', '各类体育活动和健身社团', 3, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_club_type` VALUES (4, '志愿服务', '公益活动和志愿服务类社团', 4, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_club_type` VALUES (5, '兴趣爱好', '各种兴趣爱好类社团', 5, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户名',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `operation` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作名称',
  `method` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请求方法',
  `params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '请求参数',
  `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `status` int NOT NULL DEFAULT 0 COMMENT '状态：0-成功，1-失败',
  `error_msg` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '错误信息',
  `execution_time` bigint NULL DEFAULT NULL COMMENT '执行时间（毫秒）',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `deleted` int NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_log
-- ----------------------------
INSERT INTO `sys_log` VALUES (1, 1, NULL, NULL, '创建社团', 'com.club.controller.ClubController.createClub()', '[ClubDTO(id=null, name=123, typeId=5, description=无, logo=null)]', '127.0.0.1', 0, NULL, 12, '2026-03-29 02:58:06', '2026-03-29 02:58:06', 0);
INSERT INTO `sys_log` VALUES (2, 1, NULL, NULL, '审核社团', 'com.club.controller.ClubController.auditClub()', '[11, 2, ]', '127.0.0.1', 0, NULL, 5, '2026-03-29 03:26:51', '2026-03-29 03:26:51', 0);
INSERT INTO `sys_log` VALUES (3, 1, NULL, NULL, '创建社团', 'com.club.controller.ClubController.createClub()', '[ClubDTO(id=null, name=222, typeId=2, description=, logo=null)]', '127.0.0.1', 0, NULL, 4, '2026-03-29 03:27:52', '2026-03-29 03:27:52', 0);
INSERT INTO `sys_log` VALUES (4, 1, NULL, NULL, '删除社团', 'com.club.controller.ClubController.deleteClub()', '[11]', '127.0.0.1', 0, NULL, 6, '2026-03-29 03:29:35', '2026-03-29 03:29:35', 0);
INSERT INTO `sys_log` VALUES (5, 1, NULL, NULL, '审核社团', 'com.club.controller.ClubController.auditClub()', '[12, 1, null]', '127.0.0.1', 0, NULL, 2, '2026-03-29 03:30:53', '2026-03-29 03:30:53', 0);
INSERT INTO `sys_log` VALUES (6, 1, NULL, NULL, '创建社团', 'com.club.controller.ClubController.createClub()', '[ClubDTO(id=null, name=123, typeId=1, description=, logo=null)]', '127.0.0.1', 0, NULL, 3, '2026-03-29 03:41:33', '2026-03-29 03:41:33', 0);
INSERT INTO `sys_log` VALUES (7, 1, NULL, NULL, '审核社团', 'com.club.controller.ClubController.auditClub()', '[13, 2, ]', '127.0.0.1', 0, NULL, 2, '2026-03-29 03:41:37', '2026-03-29 03:41:37', 0);
INSERT INTO `sys_log` VALUES (8, 1, NULL, NULL, '删除社团', 'com.club.controller.ClubController.deleteClub()', '[13]', '127.0.0.1', 0, NULL, 3, '2026-03-29 03:41:43', '2026-03-29 03:41:43', 0);
INSERT INTO `sys_log` VALUES (9, 1, NULL, NULL, '删除社团', 'com.club.controller.ClubController.deleteClub()', '[12]', '127.0.0.1', 0, NULL, 3, '2026-03-29 03:47:45', '2026-03-29 03:47:45', 0);

-- ----------------------------
-- Table structure for sys_notification
-- ----------------------------
DROP TABLE IF EXISTS `sys_notification`;
CREATE TABLE `sys_notification`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '通知内容',
  `type` int NULL DEFAULT 1 COMMENT '类型：1系统通知 2社团通知',
  `target_id` bigint NULL DEFAULT NULL COMMENT '目标ID（社团ID）',
  `sender_id` bigint NULL DEFAULT NULL COMMENT '发送者ID',
  `sender_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发送者姓名',
  `priority` int NULL DEFAULT 0 COMMENT '优先级：0普通 1重要 2紧急',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NULL DEFAULT 0 COMMENT '是否删除：0否 1是',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '通知表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_notification
-- ----------------------------
INSERT INTO `sys_notification` VALUES (1, '系统通知', '欢迎使用高校社团与活动管理平台', 1, NULL, 1, '管理员', 2, '2026-03-27 23:45:36', '2026-03-29 02:23:56', 0);
INSERT INTO `sys_notification` VALUES (2, '活动提醒', '编程大赛即将开始，请准时参加', 2, 1, 2, '林一', 1, '2026-03-27 23:45:36', '2026-03-29 02:23:58', 0);
INSERT INTO `sys_notification` VALUES (3, '社团通知', '计算机协会招新开始', 3, 1, 2, '林一', 1, '2026-03-27 23:45:36', '2026-03-29 02:24:01', 0);
INSERT INTO `sys_notification` VALUES (4, '系统通知', '平台更新公告', 1, NULL, 1, '管理员', 2, '2026-03-27 23:45:36', '2026-03-29 02:24:04', 0);
INSERT INTO `sys_notification` VALUES (5, '活动提醒', '诗歌朗诵会报名截止', 2, 4, 3, '龙二', 1, '2026-03-27 23:45:36', '2026-03-29 02:24:06', 0);
INSERT INTO `sys_notification` VALUES (6, '社团通知', '文学社举办文学创作工作坊', 3, 3, 3, '龙二', 1, '2026-03-27 23:45:36', '2026-03-29 02:24:09', 0);
INSERT INTO `sys_notification` VALUES (7, '活动提醒', '篮球友谊赛即将开始', 2, 7, 4, '张三', 1, '2026-03-27 23:45:36', '2026-03-29 02:24:13', 0);
INSERT INTO `sys_notification` VALUES (8, '系统通知', '社团申请审核结果已发布', 1, NULL, 1, '管理员', 2, '2026-03-27 23:45:36', '2026-03-29 02:24:17', 0);
INSERT INTO `sys_notification` VALUES (9, '活动提醒', '志愿者活动集合时间通知', 2, 10, 5, '李四', 1, '2026-03-27 23:45:36', '2026-03-29 02:24:37', 0);
INSERT INTO `sys_notification` VALUES (10, '社团通知', '摄影展作品征集', 3, 9, 6, '冯五', 0, '2026-03-27 23:45:36', '2026-03-29 02:24:33', 0);

-- ----------------------------
-- Table structure for sys_notification_read
-- ----------------------------
DROP TABLE IF EXISTS `sys_notification_read`;
CREATE TABLE `sys_notification_read`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `notification_id` bigint NOT NULL COMMENT '通知ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `is_read` int NULL DEFAULT 0 COMMENT '是否已读：0否 1是',
  `read_time` datetime NULL DEFAULT NULL COMMENT '阅读时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NULL DEFAULT 0 COMMENT '是否删除：0否 1是',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '通知阅读记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_notification_read
-- ----------------------------
INSERT INTO `sys_notification_read` VALUES (1, 1, 2, 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_notification_read` VALUES (2, 1, 3, 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_notification_read` VALUES (3, 1, 4, 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_notification_read` VALUES (4, 1, 5, 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_notification_read` VALUES (5, 1, 6, 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_notification_read` VALUES (6, 2, 7, 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_notification_read` VALUES (7, 3, 7, 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_notification_read` VALUES (8, 4, 2, 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_notification_read` VALUES (9, 5, 8, 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_notification_read` VALUES (10, 6, 8, 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_notification_read` VALUES (11, 7, 9, 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_notification_read` VALUES (12, 8, 10, 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_notification_read` VALUES (13, 9, 14, 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_notification_read` VALUES (14, 10, 11, 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);

-- ----------------------------
-- Table structure for sys_payment
-- ----------------------------
DROP TABLE IF EXISTS `sys_payment`;
CREATE TABLE `sys_payment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '缴费ID',
  `club_id` bigint NOT NULL COMMENT '社团ID',
  `club_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '社团名称',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '缴费标题',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '缴费说明',
  `amount` decimal(10, 2) NOT NULL COMMENT '缴费金额',
  `deadline` datetime NULL DEFAULT NULL COMMENT '截止日期',
  `status` int NULL DEFAULT 0 COMMENT '状态：0未缴费 1已缴费 2已逾期',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NULL DEFAULT 0 COMMENT '是否删除：0否 1是',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '缴费通知表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_payment
-- ----------------------------
INSERT INTO `sys_payment` VALUES (1, 1, '计算机协会', '计算机协会会费', '用于协会活动经费和设备购置', 50.00, '2026-04-30 00:00:00', 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_payment` VALUES (2, 2, '编程社', '编程社会费', '用于社团活动和学习资料购置', 40.00, '2026-04-30 00:00:00', 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_payment` VALUES (3, 3, '文学社', '文学社会费', '用于文学活动和书籍购置', 30.00, '2026-04-30 00:00:00', 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_payment` VALUES (4, 4, '诗词社', '诗词社会费', '用于诗词活动和资料购置', 25.00, '2026-04-30 00:00:00', 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_payment` VALUES (5, 5, '篮球社', '篮球社会费', '用于篮球活动和器材购置', 80.00, '2026-04-30 00:00:00', 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_payment` VALUES (6, 6, '足球社', '足球社会费', '用于足球活动和器材购置', 70.00, '2026-04-30 00:00:00', 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_payment` VALUES (7, 7, '志愿者协会', '志愿者协会会费', '用于志愿服务活动经费', 20.00, '2026-04-30 00:00:00', 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_payment` VALUES (8, 8, '环保协会', '环保协会会费', '用于环保活动和宣传经费', 25.00, '2026-04-30 00:00:00', 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_payment` VALUES (9, 9, '摄影社', '摄影社会费', '用于摄影活动和设备购置', 60.00, '2026-04-30 00:00:00', 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_payment` VALUES (10, 10, '动漫社', '动漫社会费', '用于动漫活动和材料购置', 55.00, '2026-04-30 00:00:00', 1, '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);

-- ----------------------------
-- Table structure for sys_payment_record
-- ----------------------------
DROP TABLE IF EXISTS `sys_payment_record`;
CREATE TABLE `sys_payment_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `payment_id` bigint NOT NULL COMMENT '缴费ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户姓名',
  `amount` decimal(10, 2) NOT NULL COMMENT '缴费金额',
  `status` int NULL DEFAULT 0 COMMENT '状态：0未缴费 1已缴费 2已逾期',
  `pay_time` datetime NULL DEFAULT NULL COMMENT '缴费时间',
  `transaction_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '交易流水号',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NULL DEFAULT 0 COMMENT '是否删除：0否 1是',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '缴费记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_payment_record
-- ----------------------------
INSERT INTO `sys_payment_record` VALUES (1, 1, 7, '王小明', 50.00, 1, '2026-03-27 23:45:36', 'TRX202603220001', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_payment_record` VALUES (2, 2, 7, '王小明', 40.00, 1, '2026-03-27 23:45:36', 'TRX202603220002', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_payment_record` VALUES (3, 3, 8, '李小红', 30.00, 1, '2026-03-27 23:45:36', 'TRX202603220003', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_payment_record` VALUES (4, 4, 8, '李小红', 25.00, 1, '2026-03-27 23:45:36', 'TRX202603220004', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_payment_record` VALUES (5, 5, 9, '张小刚', 80.00, 1, '2026-03-27 23:45:36', 'TRX202603220005', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_payment_record` VALUES (6, 6, 9, '张小刚', 70.00, 1, '2026-03-27 23:45:36', 'TRX202603220006', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_payment_record` VALUES (7, 7, 10, '赵小丽', 20.00, 1, '2026-03-27 23:45:36', 'TRX202603220007', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_payment_record` VALUES (8, 8, 10, '赵小丽', 25.00, 1, '2026-03-27 23:45:36', 'TRX202603220008', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_payment_record` VALUES (9, 9, 11, '陈小明', 60.00, 1, '2026-03-27 23:45:36', 'TRX202603220009', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_payment_record` VALUES (10, 10, 11, '陈小明', 55.00, 1, '2026-03-27 23:45:36', 'TRX202603220010', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_payment_record` VALUES (11, 1, 12, '林小红', 50.00, 1, '2026-03-27 23:45:36', 'TRX202603220011', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_payment_record` VALUES (12, 2, 12, '林小红', 40.00, 1, '2026-03-27 23:45:36', 'TRX202603220012', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_payment_record` VALUES (13, 3, 13, '龙小刚', 30.00, 1, '2026-03-27 23:45:36', 'TRX202603220013', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_payment_record` VALUES (14, 4, 13, '龙小刚', 25.00, 1, '2026-03-27 23:45:36', 'TRX202603220014', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_payment_record` VALUES (15, 5, 14, '刘小丽', 80.00, 1, '2026-03-27 23:45:36', 'TRX202603220015', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);

-- ----------------------------
-- Table structure for sys_president_transfer
-- ----------------------------
DROP TABLE IF EXISTS `sys_president_transfer`;
CREATE TABLE `sys_president_transfer`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `club_id` bigint NOT NULL COMMENT '社团ID',
  `club_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '社团名称',
  `current_president_id` bigint NOT NULL COMMENT '现任社长ID',
  `current_president_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '现任社长姓名',
  `new_president_id` bigint NOT NULL COMMENT '新社长ID',
  `new_president_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '新社长姓名',
  `status` int NOT NULL DEFAULT 0 COMMENT '状态：0-待审核，1-已通过，2-已拒绝',
  `reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '申请理由',
  `reject_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '拒绝理由',
  `process_time` datetime NULL DEFAULT NULL COMMENT '处理时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `deleted` int NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_club_id`(`club_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '社长转移申请表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_president_transfer
-- ----------------------------
INSERT INTO `sys_president_transfer` VALUES (1, 12, '222', 1, '管理员', 7, '王小明', 1, '12', NULL, '2026-03-29 03:43:19', '2026-03-29 03:43:12', '2026-03-29 03:43:12', 0);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
  `role` int NULL DEFAULT 3 COMMENT '角色：1管理员 2社长 3普通用户',
  `status` int NULL DEFAULT 1 COMMENT '状态：0禁用 1启用',
  `student_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学号',
  `college` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学院',
  `major` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '专业',
  `class_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '班级',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NULL DEFAULT 0 COMMENT '是否删除：0否 1是',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 31 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '123456', '管理员', 'admin@example.com', '13800138000', 'https://api.dicebear.com/7.x/avataaars/svg?seed=admin&backgroundColor=b6e3f4', 1, 1, '20230000', '管理学院', '行政管理', '管1班', '2026-03-27 23:45:36', '2026-03-27 23:45:36', 0);
INSERT INTO `sys_user` VALUES (2, 'president1', '123456', '林一', 'president1@example.com', '13800138001', 'https://api.dicebear.com/7.x/avataaars/svg?seed=president1&backgroundColor=c0aede', 2, 1, '20230001', '计算机学院', '计算机科学与技术', '计科1班', '2026-03-27 23:45:36', '2026-03-29 04:14:34', 0);
INSERT INTO `sys_user` VALUES (3, 'president2', '123456', '龙二', 'president2@example.com', '13800138002', 'https://api.dicebear.com/7.x/avataaars/svg?seed=president2&backgroundColor=ffdfbf', 2, 1, '20230002', '文学院', '汉语言文学', '文1班', '2026-03-27 23:45:36', '2026-03-29 04:14:37', 0);
INSERT INTO `sys_user` VALUES (4, 'president3', '123456', '张三', 'president3@example.com', '13800138003', 'https://api.dicebear.com/7.x/avataaars/svg?seed=president3&backgroundColor=d1d4f9', 2, 1, '20230003', '体育学院', '体育教育', '体教1班', '2026-03-27 23:45:36', '2026-03-29 04:14:39', 0);
INSERT INTO `sys_user` VALUES (5, 'president4', '123456', '李四', 'president4@example.com', '13800138004', 'https://api.dicebear.com/7.x/avataaars/svg?seed=president4&backgroundColor=ffd5dc', 2, 1, '20230004', '社会学院', '社会学', '社1班', '2026-03-27 23:45:36', '2026-03-29 04:14:41', 0);
INSERT INTO `sys_user` VALUES (6, 'president5', '123456', '冯五', 'president5@example.com', '13800138005', 'https://api.dicebear.com/7.x/avataaars/svg?seed=president5&backgroundColor=b6e3f4', 2, 1, '20230005', '艺术学院', '视觉传达', '视传1班', '2026-03-27 23:45:36', '2026-03-29 04:14:44', 0);
INSERT INTO `sys_user` VALUES (7, 'president6', '123456', '王小明', 'student1@example.com', '13800138006', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student1&backgroundColor=c0aede', 2, 1, '20230006', '计算机学院', '软件工程', '软工1班', '2026-03-27 23:45:36', '2026-03-29 04:12:10', 0);
INSERT INTO `sys_user` VALUES (8, 'president7', '123456', '李小红', 'student2@example.com', '13800138007', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student2&backgroundColor=ffdfbf', 2, 1, '20230007', '文学院', '新闻学', '新闻1班', '2026-03-27 23:45:36', '2026-03-29 04:12:10', 0);
INSERT INTO `sys_user` VALUES (9, 'president8', '123456', '张小刚', 'student3@example.com', '13800138008', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student3&backgroundColor=d1d4f9', 2, 1, '20230008', '体育学院', '运动训练', '运训1班', '2026-03-27 23:45:36', '2026-03-29 04:12:10', 0);
INSERT INTO `sys_user` VALUES (10, 'president9', '123456', '赵小丽', 'student4@example.com', '13800138009', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student4&backgroundColor=ffd5dc', 2, 1, '20230009', '社会学院', '社会工作', '社工1班', '2026-03-27 23:45:36', '2026-03-29 04:12:10', 0);
INSERT INTO `sys_user` VALUES (11, 'president10', '123456', '陈小明', 'student5@example.com', '13800138010', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student5&backgroundColor=b6e3f4', 2, 1, '20230010', '艺术学院', '美术学', '美术1班', '2026-03-27 23:45:36', '2026-03-29 04:12:10', 0);
INSERT INTO `sys_user` VALUES (12, 'student1', '123456', '林小红', 'student6@example.com', '13800138011', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student6&backgroundColor=c0aede', 3, 1, '20230011', '理学院', '数学与应用数学', '数1班', '2026-03-27 23:45:36', '2026-03-29 04:12:10', 0);
INSERT INTO `sys_user` VALUES (13, 'student2', '123456', '龙小刚', 'student7@example.com', '13800138012', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student7&backgroundColor=ffdfbf', 3, 1, '20230012', '环境学院', '环境科学', '环科1班', '2026-03-27 23:45:36', '2026-03-29 04:12:10', 0);
INSERT INTO `sys_user` VALUES (14, 'student3', '123456', '刘小丽', 'student8@example.com', '13800138013', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student8&backgroundColor=d1d4f9', 3, 1, '20230013', '计算机学院', '数据科学与大数据技术', '大数据1班', '2026-03-27 23:45:36', '2026-03-29 04:12:10', 0);
INSERT INTO `sys_user` VALUES (15, 'student4', '123456', '黄小明', 'student9@example.com', '13800138014', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student9&backgroundColor=ffd5dc', 3, 1, '20230014', '文学院', '历史学', '历史1班', '2026-03-27 23:45:36', '2026-03-29 04:12:10', 0);
INSERT INTO `sys_user` VALUES (16, 'student5', '123456', '杨小红', 'student10@example.com', '13800138015', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student10&backgroundColor=b6e3f4', 3, 1, '20230015', '体育学院', '武术与民族传统体育', '武术1班', '2026-03-27 23:45:36', '2026-03-29 04:12:10', 0);
INSERT INTO `sys_user` VALUES (17, 'student6', '123456', '马小刚', 'student11@example.com', '13800138016', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student11&backgroundColor=c0aede', 3, 1, '20230016', '社会学院', '法学', '法学1班', '2026-03-27 23:45:36', '2026-03-29 04:12:10', 0);
INSERT INTO `sys_user` VALUES (18, 'student7', '123456', '牛小丽', 'student12@example.com', '13800138017', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student12&backgroundColor=ffdfbf', 3, 1, '20230017', '艺术学院', '音乐学', '音1班', '2026-03-27 23:45:36', '2026-03-29 04:12:10', 0);
INSERT INTO `sys_user` VALUES (19, 'student8', '123456', '羊小明', 'student13@example.com', '13800138018', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student13&backgroundColor=d1d4f9', 3, 1, '20230018', '理学院', '物理学', '物理1班', '2026-03-27 23:45:36', '2026-03-29 04:12:10', 0);
INSERT INTO `sys_user` VALUES (20, 'student9', '123456', '猴小红', 'student14@example.com', '13800138019', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student14&backgroundColor=ffd5dc', 3, 1, '20230019', '环境学院', '环境工程', '环工1班', '2026-03-27 23:45:36', '2026-03-29 04:12:10', 0);
INSERT INTO `sys_user` VALUES (21, 'student10', '123456', '鸡小刚', 'student15@example.com', '13800138020', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student15&backgroundColor=b6e3f4', 3, 1, '20230020', '计算机学院', '人工智能', 'AI1班', '2026-03-27 23:45:36', '2026-03-29 04:12:10', 0);
INSERT INTO `sys_user` VALUES (22, 'student11', '123456', '周小明', 'student16@example.com', '13800138021', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student16&backgroundColor=c0aede', 3, 1, '20230021', '计算机学院', '网络工程', '网工1班', '2026-03-29 04:01:55', '2026-03-29 04:12:10', 0);
INSERT INTO `sys_user` VALUES (23, 'student12', '123456', '吴小红', 'student17@example.com', '13800138022', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student17&backgroundColor=ffdfbf', 3, 1, '20230022', '文学院', '广告学', '广告1班', '2026-03-29 04:01:55', '2026-03-29 04:12:10', 0);
INSERT INTO `sys_user` VALUES (24, 'student13', '123456', '郑小刚', 'student18@example.com', '13800138023', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student18&backgroundColor=d1d4f9', 3, 1, '20230023', '体育学院', '休闲体育', '休体1班', '2026-03-29 04:01:55', '2026-03-29 04:12:10', 0);
INSERT INTO `sys_user` VALUES (25, 'student14', '123456', '王小丽', 'student19@example.com', '13800138024', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student19&backgroundColor=ffd5dc', 3, 1, '20230024', '社会学院', '心理学', '心理1班', '2026-03-29 04:01:55', '2026-03-29 04:12:10', 0);
INSERT INTO `sys_user` VALUES (26, 'student15', '123456', '冯小明', 'student20@example.com', '13800138025', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student20&backgroundColor=b6e3f4', 3, 1, '20230025', '艺术学院', '舞蹈学', '舞蹈1班', '2026-03-29 04:01:55', '2026-03-29 04:12:10', 0);
INSERT INTO `sys_user` VALUES (27, 'student16', '123456', '陈小红', 'student21@example.com', '13800138026', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student21&backgroundColor=c0aede', 3, 1, '20230026', '理学院', '化学', '化学1班', '2026-03-29 04:01:55', '2026-03-29 04:12:10', 0);
INSERT INTO `sys_user` VALUES (28, 'student17', '123456', '褚小刚', 'student22@example.com', '13800138027', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student22&backgroundColor=ffdfbf', 3, 1, '20230027', '环境学院', '生态学', '生态1班', '2026-03-29 04:01:55', '2026-03-29 04:12:10', 0);
INSERT INTO `sys_user` VALUES (29, 'student18', '123456', '卫小丽', 'student23@example.com', '13800138028', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student23&backgroundColor=d1d4f9', 3, 1, '20230028', '计算机学院', '信息安全', '信安1班', '2026-03-29 04:01:55', '2026-03-29 04:12:10', 0);
INSERT INTO `sys_user` VALUES (30, 'student19', '123456', '蒋小明', 'student24@example.com', '13800138029', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student24&backgroundColor=ffd5dc', 3, 1, '20230029', '文学院', '哲学', '哲学1班', '2026-03-29 04:01:55', '2026-03-29 04:12:10', 0);
INSERT INTO `sys_user` VALUES (31, 'student20', '123456', '沈小红', 'student25@example.com', '13800138030', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student25&backgroundColor=b6e3f4', 3, 1, '20230030', '体育学院', '运动人体科学', '运人1班', '2026-03-29 04:01:55', '2026-03-29 04:12:10', 0);

SET FOREIGN_KEY_CHECKS = 1;
