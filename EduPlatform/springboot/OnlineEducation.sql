DROP DATABASE IF EXISTS `OnlineEducation`;
CREATE DATABASE `OnlineEducation`;
use `OnlineEducation`;

DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
`id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
`username` varchar(255) COLLATE utf8mb4_unicode_ci COMMENT '账号',
`password` varchar(255) COLLATE utf8mb4_unicode_ci COMMENT '密码',
`name` varchar(255) COLLATE utf8mb4_unicode_ci COMMENT '名称',
`avatar` varchar(255) COLLATE utf8mb4_unicode_ci COMMENT '头像',
`role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'admin' COMMENT '角色标识',
PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='管理员表';

INSERT INTO `admin` (username, password, name, avatar, role) VALUES ('admin', '123456', '管理员', 'http://localhost:9090/files/qy-default.png', 'admin');

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
`id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
`username` varchar(255) COLLATE utf8mb4_unicode_ci COMMENT '账号',
`password` varchar(255) COLLATE utf8mb4_unicode_ci COMMENT '密码',
`name` varchar(255) COLLATE utf8mb4_unicode_ci COMMENT '名称',
`avatar` varchar(255) COLLATE utf8mb4_unicode_ci COMMENT '头像',
`phone` varchar(255) COLLATE utf8mb4_unicode_ci COMMENT '手机',
`email` varchar(255) COLLATE utf8mb4_unicode_ci COMMENT '邮箱',
`role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'user' COMMENT '角色标识',
PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='用户表';

DROP TABLE IF EXISTS `teacher`;
CREATE TABLE `teacher` (
                        `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                        `username` varchar(255) COLLATE utf8mb4_unicode_ci COMMENT '账号',
                        `password` varchar(255) COLLATE utf8mb4_unicode_ci COMMENT '密码',
                        `name` varchar(255) COLLATE utf8mb4_unicode_ci COMMENT '名称',
                        `avatar` varchar(255) COLLATE utf8mb4_unicode_ci COMMENT '头像',
                        `phone` varchar(255) COLLATE utf8mb4_unicode_ci COMMENT '手机',
                        `email` varchar(255) COLLATE utf8mb4_unicode_ci COMMENT '邮箱',
                        `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'teacher' COMMENT '角色标识',
                        PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='教师表';

-- 添加课程表
DROP TABLE IF EXISTS `course`;
CREATE TABLE `course` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `name` varchar(255) NOT NULL COMMENT '课程名称',
  `description` text COMMENT '课程描述',
  `cover` varchar(255) DEFAULT NULL COMMENT '课程封面',
  `teacher_id` int(11) NOT NULL COMMENT '教师ID',
  `teacher_name` varchar(255) NOT NULL COMMENT '教师名称',
  `status` int(1) DEFAULT '0' COMMENT '课程状态：0-未发布，1-已发布',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- 添加课程内容表
DROP TABLE IF EXISTS `course_content`;
CREATE TABLE `course_content` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '内容ID',
  `course_id` int(11) NOT NULL COMMENT '课程ID',
  `title` varchar(255) NOT NULL COMMENT '章节标题',
  `content` text COMMENT '章节内容',
  `video` varchar(255) DEFAULT NULL COMMENT '视频链接',
  `attachment` varchar(255) DEFAULT NULL COMMENT '附件链接',
  `sort` int(11) DEFAULT '0' COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_course_id` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程内容表';

-- 添加习题表
DROP TABLE IF EXISTS `exercise`;
CREATE TABLE `exercise` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '习题ID',
  `title` varchar(255) NOT NULL COMMENT '题目标题',
  `content` text COMMENT '题目内容',
  `answer` text COMMENT '参考答案',
  `type` int(1) DEFAULT '0' COMMENT '题目类型：0-选择题，1-填空题，2-简答题',
  `options` text COMMENT '选项（选择题使用）',
  `course_id` int(11) NOT NULL COMMENT '所属课程ID',
  `teacher_id` int(11) NOT NULL COMMENT '创建教师ID',
  `difficulty` int(1) DEFAULT '0' COMMENT '难度：0-简单，1-中等，2-困难',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_course_id` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='习题表';

-- 添加学生答题记录表
DROP TABLE IF EXISTS `exercise_record`;
CREATE TABLE `exercise_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `exercise_id` int(11) NOT NULL COMMENT '习题ID',
  `user_id` int(11) NOT NULL COMMENT '学生ID',
  `user_answer` text COMMENT '学生答案',
  `score` int(11) DEFAULT '0' COMMENT '得分',
  `is_correct` tinyint(1) DEFAULT '0' COMMENT '是否正确：0-错误，1-正确',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_exercise_id` (`exercise_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='答题记录表';

-- 添加学习动态表
DROP TABLE IF EXISTS `post`;
CREATE TABLE `post` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '动态ID',
  `user_id` int(11) NOT NULL COMMENT '发布用户ID',
  `content` text NOT NULL COMMENT '动态内容',
  `images` text COMMENT '图片列表（JSON格式）',
  `like_count` int(11) DEFAULT '0' COMMENT '点赞数',
  `comment_count` int(11) DEFAULT '0' COMMENT '评论数',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习动态表';

-- 添加动态评论表
DROP TABLE IF EXISTS `post_comment`;
CREATE TABLE `post_comment` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `post_id` int(11) NOT NULL COMMENT '动态ID',
  `user_id` int(11) NOT NULL COMMENT '评论用户ID',
  `content` text NOT NULL COMMENT '评论内容',
  `parent_id` int(11) DEFAULT NULL COMMENT '父评论ID（用于回复）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_post_id` (`post_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动态评论表';

-- 添加动态点赞表
DROP TABLE IF EXISTS `post_like`;
CREATE TABLE `post_like` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `post_id` int(11) NOT NULL COMMENT '动态ID',
  `user_id` int(11) NOT NULL COMMENT '点赞用户ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_post_user` (`post_id`,`user_id`),
  KEY `idx_post_id` (`post_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动态点赞表';


