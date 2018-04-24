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

/*Table structure for table `perm` */

DROP TABLE IF EXISTS `perm`;

CREATE TABLE `perm` (
  `pid` varchar(25) NOT NULL COMMENT '权限id',
  `pname` varchar(50) DEFAULT NULL COMMENT '权限名称',
  `ptype` int(3) DEFAULT NULL COMMENT '权限类型：1.菜单；2.按钮 3.测试',
  `pval` varchar(50) NOT NULL COMMENT '权限值，shiro的权限控制表达式',
  `created` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `updated` timestamp NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`pid`),
  UNIQUE KEY `pval` (`pval`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `perm` */

insert  into `perm`(`pid`,`pname`,`ptype`,`pval`,`created`,`updated`) values ('986901751202504706','编辑网页',3,'html:edit','2018-04-19 17:38:08',NULL),('986901927438770178','控制硬件',3,'hardware:control','2018-04-19 17:38:50',NULL),('986902011056414721','Maven构建',3,'mvn:install','2018-04-19 17:39:10',NULL),('986902101733072897','Gradle构建',3,'gradle:build','2018-04-19 17:39:31',NULL),('986902284776693761','服务端开发',3,'server:develop','2018-04-19 17:40:15',NULL),('986902613505269762','小米售后',3,'mi:service','2018-04-19 17:41:33',NULL),('986902613505888888','所有权限',0,'*','2018-04-19 18:14:12',NULL),('988593652938973185','菜单1',1,'menu:1','2018-04-24 09:41:08',NULL),('988593707624308737','菜单2',1,'menu:2','2018-04-24 09:41:22',NULL),('988593733205368833','菜单3',1,'menu:3','2018-04-24 09:41:28',NULL),('988596886235652098','菜单4',1,'menu:4','2018-04-24 09:54:00',NULL),('988629762511609857','按钮1',2,'btn:1','2018-04-24 12:04:38',NULL),('988629812436410369','按钮2',2,'btn:2','2018-04-24 12:04:50',NULL),('988629841578434561','按钮3',2,'btn:3','2018-04-24 12:04:57',NULL),('988629870254891009','按钮4',2,'btn:4','2018-04-24 12:05:04',NULL);

/*Table structure for table `role` */

DROP TABLE IF EXISTS `role`;

CREATE TABLE `role` (
  `rid` varchar(25) NOT NULL COMMENT '角色id',
  `rname` varchar(50) DEFAULT NULL COMMENT '角色名，用于显示',
  `rdesc` varchar(100) DEFAULT NULL COMMENT '角色描述',
  `rval` varchar(100) NOT NULL COMMENT '角色值，用于权限判断',
  `created` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `updated` timestamp NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`rid`),
  UNIQUE KEY `rval` (`rval`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `role` */

insert  into `role`(`rid`,`rname`,`rdesc`,`rval`,`created`,`updated`) values ('986899638422528002','JavaScript程序员','能写前端','js','2018-04-19 17:29:44',NULL),('986900014999724033','Java程序员','能写后台，给前台提供数据','java','2018-04-19 17:31:14',NULL),('986900390847111169','C++程序员','能控制硬件','cpp','2018-04-19 17:32:44',NULL),('986900849410367490','超级管理员','具有本系统中最高权限','root','2018-04-19 17:34:33',NULL),('986900849410368549','小米CEO','小米的拼命三郎','mi','2018-04-19 17:49:32',NULL),('986925949410368893','编辑','小编','editor','2018-04-24 09:27:45',NULL);

/*Table structure for table `role_perm` */

DROP TABLE IF EXISTS `role_perm`;

CREATE TABLE `role_perm` (
  `role_id` varchar(25) NOT NULL,
  `perm_id` varchar(25) NOT NULL,
  PRIMARY KEY (`role_id`,`perm_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `role_perm` */

insert  into `role_perm`(`role_id`,`perm_id`) values ('986899638422528002','986901751202504706'),('986900014999724033','986902011056414721'),('986900014999724033','986902101733072897'),('986900014999724033','986902284776693761'),('986900390847111169','986901927438770178'),('986900849410367490','986902613505888888'),('986900849410367490','988593652938973185'),('986900849410367490','988593707624308737'),('986900849410367490','988593733205368833'),('986900849410367490','988629762511609857'),('986900849410367490','988629812436410369'),('986900849410367490','988629841578434561'),('986900849410367490','988629870254891009'),('986900849410368549','986902613505269762'),('986925949410368893','988593707624308737'),('986925949410368893','988629812436410369'),('986925949410368893','988629841578434561');

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `uid` varchar(25) NOT NULL COMMENT '用户id',
  `uname` varchar(50) DEFAULT NULL COMMENT '登录名，不可改',
  `nick` varchar(50) DEFAULT NULL COMMENT '用户昵称，可改',
  `pwd` varchar(200) DEFAULT NULL COMMENT '已加密的登录密码',
  `salt` varchar(200) DEFAULT NULL COMMENT '加密盐值',
  `created` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `updated` timestamp NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`uid`),
  UNIQUE KEY `uname` (`uname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `user` */

insert  into `user`(`uid`,`uname`,`nick`,`pwd`,`salt`,`created`,`updated`) values ('986177923098808322','admin','雷军','J/ms7qTJtqmysekuY8/v1TAS+VKqXdH5sB7ulXZOWho=','wxKYXuTPST5SG0jMQzVPsg==','2018-04-17 17:41:53','2018-04-19 17:08:15'),('986417752352923650','ma','马化腾','J/ms7qTJtqmysekuY8/v1TAS+VKqXdH5sB7ulXZOWho=','wxKYXuTPST5SG0jMQzVPsg==','2018-04-18 09:34:53','2018-04-25 00:11:31'),('986898575929827329','editor','菜鸟','7ujptuUyNUJqABXa7MGc8gd6AsjclUw7CZbh3v0fPjU=','sNIJ758vFuX6BKKU6bEJ3Q==','2018-04-19 17:25:31','2018-04-25 00:11:26'),('988816358184984577','hello','hello1','Q7oVyGSZn4LCIZhVQmapdldJu+v3ho3eK+0pdpGOf78=','DqvWmCjWStnEjU1TmsUwnQ==','2018-04-25 00:26:06','2018-04-25 00:27:20'),('988816595007971330','world','world2','yFcdxt89P4gFqguXLRGgXgzLhNLmycZbk+Cv2TcX7Tw=','LB5vcn18mQFa9knkL5aX/A==','2018-04-25 00:27:02','2018-04-25 00:27:25');

/*Table structure for table `user_role` */

DROP TABLE IF EXISTS `user_role`;

CREATE TABLE `user_role` (
  `user_id` varchar(25) NOT NULL,
  `role_id` varchar(25) NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `user_role` */

insert  into `user_role`(`user_id`,`role_id`) values ('986177923098808322','986900014999724033'),('986177923098808322','986900849410367490'),('986177923098808322','986900849410368549'),('986898575929827329','986925949410368893');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
