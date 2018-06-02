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
  `ptype` int(3) DEFAULT NULL COMMENT '权限类型：1.菜单 2.按钮 3.接口 4.特殊',
  `leaf` tinyint(1) DEFAULT NULL COMMENT '是否叶子节点',
  `created` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `updated` timestamp NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`pval`),
  UNIQUE KEY `pval` (`pval`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='权限';

/*Data for the table `sys_perm` */

insert  into `sys_perm`(`pval`,`parent`,`pname`,`ptype`,`leaf`,`created`,`updated`) values ('*',NULL,'所有权限',0,NULL,'2018-04-19 18:14:12',NULL),('a:auth',NULL,'登录模块',3,1,NULL,NULL),('a:gradleBuild','a:test','构建gradle',3,1,NULL,NULL),('a:mvn:install','a:test','mvnInstall',3,1,NULL,NULL),('a:option',NULL,'选项模块',3,1,NULL,NULL),('a:perm:query','a:sys:perm','查询sys_perm',3,1,NULL,NULL),('a:perm:update','a:sys:perm','update',3,1,NULL,NULL),('a:role:query','a:sys:role','query',3,1,NULL,NULL),('a:role:update','a:sys:role','update',3,1,NULL,NULL),('a:sys:perm',NULL,'系统权限模块',3,0,NULL,NULL),('a:sys:role',NULL,'系统角色模块',3,0,NULL,NULL),('a:sys:接口',NULL,'系统用户模块',3,1,NULL,NULL),('a:test',NULL,'测试模块模块',3,0,NULL,NULL),('b:user:add','m:sys:user','添加用户',2,NULL,'2018-06-02 11:00:37',NULL),('b:user:delete','m:sys:user','删除用户',2,NULL,'2018-06-02 11:00:56',NULL),('m:menu1',NULL,'菜单1',1,1,NULL,NULL),('m:menu2',NULL,'菜单2',1,1,NULL,NULL),('m:menu3',NULL,'菜单3',1,0,NULL,NULL),('m:menu3:1','m:menu3','菜单3-1',1,1,NULL,NULL),('m:menu3:2','m:menu3','菜单3-2',1,1,NULL,NULL),('m:menu3:3','m:menu3','菜单3-3',1,1,NULL,NULL),('m:menu4',NULL,'菜单4',1,0,NULL,NULL),('m:menu4:1','m:menu4','菜单4-1',1,0,NULL,NULL),('m:menu4:1:a','m:menu4:1','菜单4-1-a',1,1,NULL,NULL),('m:menu4:1:b','m:menu4:1','菜单4-1-b',1,1,NULL,NULL),('m:menu4:1:c','m:menu4:1','菜单4-1-c',1,1,NULL,NULL),('m:menu4:2','m:menu4','菜单4-2',1,1,NULL,NULL),('m:sys',NULL,'系统',1,0,NULL,NULL),('m:sys:perm','m:sys','权限管理',1,1,NULL,NULL),('m:sys:role','m:sys','角色管理',1,1,NULL,NULL),('m:sys:user','m:sys','用户管理',1,1,NULL,NULL);

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

insert  into `sys_role`(`rid`,`rname`,`rdesc`,`rval`,`created`,`updated`) values ('1002748319131680769','普通用户','具有一般的权限，不具备系统菜单权限1','common','2018-06-02 11:06:44','2018-06-02 11:10:57'),('1002806178141937666','财务','拥有财务相关权限','finance','2018-06-02 14:56:39',NULL),('1002806220860923906','仓管','拥有财务相关权限','stock','2018-06-02 14:56:49',NULL),('1002806266750803970','销售','拥有财务相关权限','sale','2018-06-02 14:57:00',NULL),('1002807171923550210','文员','拥有文员相关的权限','stuff','2018-06-02 15:00:36',NULL),('1002807288885911553','啊','asdf','sdf','2018-06-02 15:01:04',NULL),('1002807344665960449','阿斯达','sdfwerty','sgsf','2018-06-02 15:01:17',NULL),('1002807369559154689','阿斯蒂芬','撒旦法GV','asdfgewrgr','2018-06-02 15:01:23',NULL),('1002807394460737537','阿斯蒂芬','颂德歌功','asdgwergreh','2018-06-02 15:01:29',NULL),('1002807427771899906','asddfgswf','阿道夫噶','drgregerg','2018-06-02 15:01:37',NULL),('999999888888777777','超级管理员','具有本系统中最高权限','root','2018-04-19 17:34:33',NULL);

/*Table structure for table `sys_role_perm` */

DROP TABLE IF EXISTS `sys_role_perm`;

CREATE TABLE `sys_role_perm` (
  `role_id` varchar(25) NOT NULL,
  `perm_val` varchar(25) NOT NULL,
  `perm_type` int(5) DEFAULT NULL,
  PRIMARY KEY (`role_id`,`perm_val`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `sys_role_perm` */

insert  into `sys_role_perm`(`role_id`,`perm_val`,`perm_type`) values ('1002748319131680769','a:perm:update',3),('1002748319131680769','a:sys:perm',3),('1002748319131680769','b:user:add',2),('1002748319131680769','m:sys',1),('1002748319131680769','m:sys:user',1),('999999888888777777','*',NULL);

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

insert  into `sys_user`(`uid`,`uname`,`nick`,`pwd`,`salt`,`lock`,`created`,`updated`) values ('1002748017179541505','guanyu','关羽','n2Wd7JramFVrHcijY4KW1rNTGKnwyYPJ0RDYvy2BdK0=','aem4EsAFae5rObEdZP4Xlw==',NULL,'2018-06-02 11:05:32','2018-06-02 14:40:01'),('1002748102537822209','zhangfei','张飞','g+aRBmgVTTPkNLNwJfM64D8rwH94WEgDgckQ4fuQp6w=','Sqhvxsnc0HZSQEFKjBB9zQ==',NULL,'2018-06-02 11:05:52',NULL),('986177923098808322','admin','刘备','J/ms7qTJtqmysekuY8/v1TAS+VKqXdH5sB7ulXZOWho=','wxKYXuTPST5SG0jMQzVPsg==',0,'2018-04-17 17:41:53','2018-04-19 17:08:15');

/*Table structure for table `sys_user_role` */

DROP TABLE IF EXISTS `sys_user_role`;

CREATE TABLE `sys_user_role` (
  `user_id` varchar(25) NOT NULL,
  `role_id` varchar(25) NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `sys_user_role` */

insert  into `sys_user_role`(`user_id`,`role_id`) values ('1002748017179541505','1002748319131680769'),('1002748102537822209','1002748319131680769'),('986177923098808322','999999888888777777');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
