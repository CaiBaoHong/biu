/*
SQLyog Ultimate v12.09 (64 bit)
MySQL - 5.7.21-log : Database - biu
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`biu` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `biu`;

/*Table structure for table `sys_perm` */

DROP TABLE IF EXISTS `sys_perm`;

CREATE TABLE `sys_perm` (
  `pval` varchar(50) NOT NULL COMMENT '权限值，shiro的权限控制表达式',
  `parent` varchar(25) DEFAULT NULL COMMENT '父权限id',
  `pname` varchar(50) DEFAULT NULL COMMENT '权限名称',
  `ptype` int(3) DEFAULT NULL COMMENT '权限类型：1.菜单 2.按钮 3.接口',
  `created` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `updated` timestamp NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`pval`),
  UNIQUE KEY `pval` (`pval`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `sys_perm` */

insert  into `sys_perm`(`pval`,`parent`,`pname`,`ptype`,`created`,`updated`) values ('*',NULL,'所有权限',0,'2018-04-19 18:14:12','2018-05-23 09:18:57'),('api:sys:user',NULL,'用户管理模块',3,'2018-05-23 09:31:09',NULL),('api:sys:user:add','api:sys:user','添加用户接口',3,'2018-05-23 09:31:11',NULL),('api:sys:user:del','api:sys:user','删除用户接口',3,'2018-05-23 09:31:13',NULL),('menu1',NULL,'菜单1',1,NULL,NULL),('menu2',NULL,'菜单2',1,NULL,NULL),('menu3',NULL,'菜单3',1,NULL,NULL),('menu3:1','menu3','菜单3-1',1,NULL,NULL),('menu3:2','menu3','菜单3-2',1,NULL,NULL),('menu3:3','menu3','菜单3-3',1,NULL,NULL),('menu4',NULL,'菜单4',1,NULL,NULL),('menu4:1','menu4','菜单4-1',1,NULL,NULL),('menu4:1:a','menu4:1','菜单4-1-a',1,NULL,NULL),('menu4:1:b','menu4:1','菜单4-1-b',1,NULL,NULL),('menu4:1:c','menu4:1','菜单4-1-c',1,NULL,NULL),('menu4:2','menu4','菜单4-2',1,NULL,NULL),('sys',NULL,'系统',1,'2018-05-23 09:27:14',NULL),('sys:perm','sys','权限管理',1,NULL,NULL),('sys:perm_meta','sys','权限数据',1,NULL,NULL),('sys:role','sys','角色管理',1,NULL,NULL),('sys:user','sys','用户管理',1,'2018-05-23 09:27:16',NULL),('sys:user:add','sys:user','添加用户',2,'2018-05-23 09:31:14',NULL),('sys:user:del','sys:user','删除用户',2,'2018-05-23 09:31:16',NULL);

/*Table structure for table `sys_role` */

DROP TABLE IF EXISTS `sys_role`;

CREATE TABLE `sys_role` (
  `rid` varchar(25) NOT NULL COMMENT '角色id',
  `rname` varchar(50) DEFAULT NULL COMMENT '角色名，用于显示',
  `rdesc` varchar(100) DEFAULT NULL COMMENT '角色描述',
  `rval` varchar(100) NOT NULL COMMENT '角色值，用于权限判断',
  `created` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `updated` timestamp NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`rid`),
  UNIQUE KEY `rval` (`rval`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `sys_role` */

insert  into `sys_role`(`rid`,`rname`,`rdesc`,`rval`,`created`,`updated`) values ('999999888888777777','超级管理员','具有本系统中最高权限','root','2018-04-19 17:34:33',NULL);

/*Table structure for table `sys_role_perm` */

DROP TABLE IF EXISTS `sys_role_perm`;

CREATE TABLE `sys_role_perm` (
  `role_id` varchar(25) NOT NULL,
  `perm_val` varchar(50) NOT NULL,
  PRIMARY KEY (`role_id`,`perm_val`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `sys_role_perm` */

insert  into `sys_role_perm`(`role_id`,`perm_val`) values ('999999888888777777','*');

/*Table structure for table `sys_user` */

DROP TABLE IF EXISTS `sys_user`;

CREATE TABLE `sys_user` (
  `uid` varchar(25) NOT NULL COMMENT '用户id',
  `uname` varchar(50) DEFAULT NULL COMMENT '登录名，不可改',
  `nick` varchar(50) DEFAULT NULL COMMENT '用户昵称，可改',
  `pwd` varchar(200) DEFAULT NULL COMMENT '已加密的登录密码',
  `salt` varchar(200) DEFAULT NULL COMMENT '加密盐值',
  `lock` tinyint(1) DEFAULT NULL COMMENT '是否锁定',
  `created` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `updated` timestamp NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`uid`),
  UNIQUE KEY `uname` (`uname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `sys_user` */

insert  into `sys_user`(`uid`,`uname`,`nick`,`pwd`,`salt`,`lock`,`created`,`updated`) values ('986177923098808322','admin','刘备','J/ms7qTJtqmysekuY8/v1TAS+VKqXdH5sB7ulXZOWho=','wxKYXuTPST5SG0jMQzVPsg==',0,'2018-04-17 17:41:53','2018-04-19 17:08:15'),('986417752352923650','guanyu','关羽','J/ms7qTJtqmysekuY8/v1TAS+VKqXdH5sB7ulXZOWho=','wxKYXuTPST5SG0jMQzVPsg==',0,'2018-04-18 09:34:53','2018-04-25 00:11:31'),('986898575929827329','zhangfei','张飞','J/ms7qTJtqmysekuY8/v1TAS+VKqXdH5sB7ulXZOWho=','wxKYXuTPST5SG0jMQzVPsg==',0,'2018-04-19 17:25:31','2018-04-25 00:11:26');

/*Table structure for table `sys_user_role` */

DROP TABLE IF EXISTS `sys_user_role`;

CREATE TABLE `sys_user_role` (
  `user_id` varchar(25) NOT NULL,
  `role_id` varchar(25) NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `sys_user_role` */

insert  into `sys_user_role`(`user_id`,`role_id`) values ('986177923098808322','999999888888777777');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
