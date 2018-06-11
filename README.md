# biu

>Biu，boot和vue的连读而想到的名字。一个基于Spring Boot和Vue的Web开发脚手架，整合和最基础的RBAC权限控制，包括：菜单权限、按钮权限、接口权限。

- 前端使用vue-cli，后端使用Spring Boot，两个全家桶强强联合。
- 用简单优雅的方式整合shiro
- 使用Gradle持续构建特性，开发时修改java代码无需重启
- 使用vue-element-admin做前端模板，摆脱写jQuery的痛苦
- 多种灵活形式的前后端分离方式，包括开发阶段的前后端分离和部署的前后端分离

**效果图：**

![p1](_doc/image/preview_1.png)
![p2](_doc/image/preview_2.png)
![p3](_doc/image/preview_3.png)
![p4](_doc/image/preview_4.png)
![p5](_doc/image/preview_5.png)
![p6](_doc/image/preview_6.png)

## 整合shiro

网上找到的文章，基本上都不是基于Spring Boot的starter来整合的，所以代码量比较多，而且比较繁杂。
其实shiro官方提供了starter，可以让我们优雅地把shiro整合到spring boot中。请看官网相关文档：
[Integrating Apache Shiro into Spring-Boot Applications](https://shiro.apache.org/spring-boot.html)

