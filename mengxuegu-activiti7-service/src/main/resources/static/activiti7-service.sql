/*
Navicat MySQL Data Transfer

Source Server         : 开发环境
Source Server Version : 50730
Source Host           : localhost:3306
Source Database       : activiti7-service-teacher

Target Server Type    : MYSQL
Target Server Version : 50730
File Encoding         : 65001

*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for mxg_leave
-- ----------------------------
DROP TABLE IF EXISTS `mxg_leave`;
CREATE TABLE `mxg_leave` (
  `id` varchar(255) NOT NULL COMMENT '主键ID',
  `username` varchar(40) DEFAULT NULL COMMENT '申请人用户名',
  `duration` double(7,2) DEFAULT NULL COMMENT '请假时长，单位：天',
  `principal` varchar(50) DEFAULT NULL COMMENT '工作委托人',
  `contact_phone` varchar(50) DEFAULT NULL COMMENT '联系电话',
  `leave_type` tinyint(3) DEFAULT NULL COMMENT '请假类型：1病假，2事假，3年假，4婚假，5产假，6丧假，7探亲，8调休，9其他',
  `title` varchar(255) DEFAULT NULL COMMENT '标题',
  `leave_reason` varchar(1000) DEFAULT NULL COMMENT '请假原因',
  `start_date` datetime DEFAULT NULL COMMENT '请假开始时间',
  `end_date` datetime DEFAULT NULL COMMENT '请假开始时间',
  `create_date` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='请假业务表';

-- ----------------------------
-- Records of mxg_leave
-- ----------------------------

-- ----------------------------
-- Table structure for mxg_loan
-- ----------------------------
DROP TABLE IF EXISTS `mxg_loan`;
CREATE TABLE `mxg_loan` (
  `id` varchar(255) NOT NULL COMMENT '主键ID',
  `user_id` varchar(40) CHARACTER SET latin1 DEFAULT '' COMMENT '申请人ID',
  `nick_name` varchar(40) DEFAULT NULL COMMENT '申请人昵称',
  `money` double(15,2) DEFAULT NULL COMMENT '借款金额',
  `purpose` varchar(1000) DEFAULT NULL COMMENT '借款用途',
  `remark` varchar(1000) DEFAULT NULL COMMENT '备注',
  `loan_date` datetime DEFAULT NULL COMMENT '借款日期',
  `create_date` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='借款业务表';

-- ----------------------------
-- Records of mxg_loan
-- ----------------------------

-- ----------------------------
-- Table structure for mxg_business_status
-- ----------------------------
DROP TABLE IF EXISTS `mxg_business_status`;
CREATE TABLE `mxg_business_status` (
  `business_key` varchar(60) NOT NULL COMMENT '业务ID',
  `process_instance_id` varchar(60) DEFAULT NULL COMMENT '流程实例ID',
  `status` tinyint(3) DEFAULT NULL COMMENT '状态',
  `create_date` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`business_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='业务状态实体表';

-- ----------------------------
-- Table structure for mxg_process_config
-- ----------------------------
DROP TABLE IF EXISTS `mxg_process_config`;
CREATE TABLE `mxg_process_config` (
  `id` varchar(60) NOT NULL COMMENT '主键id',
  `process_key` varchar(60) DEFAULT NULL COMMENT '流程定义KEY',
  `business_route` varchar(100) DEFAULT NULL COMMENT '业务申请路由名',
  `form_name` varchar(100) DEFAULT NULL COMMENT '关联表单组件名',
  `create_date` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流程定义配置表';

-- ----------------------------
-- Records of mxg_process_config
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` varchar(40) NOT NULL COMMENT '用户 ID',
  `username` varchar(60) DEFAULT NULL COMMENT '用户名',
  `password` varchar(60) DEFAULT NULL COMMENT '密码，加密存储, admin/1234',
  `nick_name` varchar(60) DEFAULT NULL COMMENT '昵称',
  `image_url` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '头像url',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `username` (`username`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户信息表';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES ('11', 'meng', '$2a$10$EHkq/r6PTCCYEDOQYNi/ZORb8S.G8yxu4jsxqvjGWEytyrrPx5FVS', '梦', 'group1/M00/00/00/J2y7ZGA2IheAESCfAABBqgX_-Lk92.jpeg');
INSERT INTO `sys_user` VALUES ('12', 'xue', '$2a$10$EHkq/r6PTCCYEDOQYNi/ZORb8S.G8yxu4jsxqvjGWEytyrrPx5FVS', '学', 'group1/M00/00/00/J2y7ZGA2IheAESCfAABBqgX_-Lk92.jpeg');
INSERT INTO `sys_user` VALUES ('13', 'gu', '$2a$10$EHkq/r6PTCCYEDOQYNi/ZORb8S.G8yxu4jsxqvjGWEytyrrPx5FVS', '谷', 'group1/M00/00/00/J2y7ZGA2IheAESCfAABBqgX_-Lk92.jpeg');
INSERT INTO `sys_user` VALUES ('14', 'mengxuegu', '$2a$10$EHkq/r6PTCCYEDOQYNi/ZORb8S.G8yxu4jsxqvjGWEytyrrPx5FVS', '梦学谷', 'group1/M00/00/00/J2y7ZGA2IheAESCfAABBqgX_-Lk92.jpeg');
