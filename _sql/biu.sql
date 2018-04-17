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
  `pid` bigint(15) NOT NULL COMMENT '权限id',
  `pname` varchar(50) DEFAULT NULL COMMENT '权限名称',
  `ptype` int(3) DEFAULT NULL COMMENT '权限类型：1.菜单；2.按钮',
  `pval` varchar(30) DEFAULT NULL COMMENT '权限值，shiro的权限控制表达式',
  `created` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `updated` timestamp NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`pid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `perm` */

/*Table structure for table `role` */

DROP TABLE IF EXISTS `role`;

CREATE TABLE `role` (
  `rid` bigint(15) NOT NULL COMMENT '角色id',
  `rname` varchar(50) DEFAULT NULL COMMENT '角色名，用于显示',
  `rdesc` varchar(100) DEFAULT NULL COMMENT '角色描述',
  `rval` varchar(100) DEFAULT NULL COMMENT '角色值，用于权限判断',
  `created` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `updated` timestamp NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`rid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `role` */

/*Table structure for table `role_perm` */

DROP TABLE IF EXISTS `role_perm`;

CREATE TABLE `role_perm` (
  `role_id` bigint(15) NOT NULL,
  `perm_id` bigint(15) NOT NULL,
  PRIMARY KEY (`role_id`,`perm_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `role_perm` */

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `uid` bigint(15) NOT NULL COMMENT '用户id',
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

insert  into `user`(`uid`,`uname`,`nick`,`pwd`,`salt`,`created`,`updated`) values (986177923098808322,'admin','菜','123456',NULL,'2018-04-17 17:41:53',NULL);

/*Table structure for table `user_role` */

DROP TABLE IF EXISTS `user_role`;

CREATE TABLE `user_role` (
  `user_id` bigint(15) NOT NULL,
  `role_id` bigint(15) NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `user_role` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
