# biu

>Biu，boot和vue的连读而想到的名字。一个基于Spring Boot和Vue的Web开发脚手架。

- 前端使用vue-cli，后端使用Spring Boot，两个全家桶强强联合。
- 用简单优雅的方式整合shiro
- 使用Gradle持续构建特性，开发时修改java代码无需重启
- 使用vue-element-admin做前端模板，摆脱写jQuery的痛苦
- 多种灵活形式的前后端分离方式，包括开发阶段的前后端分离和部署的前后端分离

## 整合shiro

网上找到的文章，基本上都不是基于Spring Boot的starter来整合的，所以代码量比较多，而且比较繁杂。
其实shiro官方提供了starter，可以让我们优雅地把shiro整合到spring boot中。请看官网相关文档：
[Integrating Apache Shiro into Spring-Boot Applications](https://shiro.apache.org/spring-boot.html)

3种整合方式：

1.基于url过滤的访问控制

引入`shiro-spring-boot-web-starter`：

~~~
//在build.gradle中
dependencies {
    `compile 'org.apache.shiro:shiro-spring-boot-web-starter:1.4.0'`
}
~~~

在spring容器中配置url过滤：

~~~
@Bean
public ShiroFilterChainDefinition shiroFilterChainDefinition() {
    DefaultShiroFilterChainDefinition chain = new DefaultShiroFilterChainDefinition();
    //可公访问的页面
    chain.addPathDefinition("/api/v1/user/login", "anon");
    chain.addPathDefinition("/api/v1/page/**", "anon");
    chain.addPathDefinition("/api/v1/test/**", "anon");
    //其它接口登录才能访问
    chain.addPathDefinition("/api/v1/**", "authc");
    return chain;
}
~~~

在spring boot配置文件中配置跳转页面：
~~~
shiro:
  loginUrl: /page/401
  successUrl: /page/index
  unauthorizedUrl: /page/403
~~~



2.基于注解的访问控制
