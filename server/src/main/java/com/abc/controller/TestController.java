package com.abc.controller;

import com.abc.annotation.PermInfo;
import org.apache.shiro.authz.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * created by CaiBaoHong at 2018/4/17 14:16<br>
 */
@PermInfo(value = "测试模块模块", pval = "a:test")
@RestController
@RequestMapping("/test")
public class TestController {

    // 由于ShiroConfig中配置了该路径可以匿名访问，所以这接口不需要登录就能访问
    @GetMapping("/hello")
    public String hello() {
        return "hello spring boot";
    }

    // 如果ShiroConfig中没有配置该路径可以匿名访问，所以直接被登录过滤了。
    // 如果配置了可以匿名访问，那这里在没有登录的时候可以访问，但是用户登录后就不能访问
    @RequiresGuest
    @GetMapping("/guest")
    public String guest() {
        return "@RequiresGuest";
    }

    @RequiresAuthentication
    @GetMapping("/authn")
    public String authn() {
        return "@RequiresAuthentication";
    }

    @RequiresUser
    @GetMapping("/user")
    public String user() {
        return "@RequiresUser";
    }

    @RequiresPermissions("a:mvn:install")
    @GetMapping("/mvnInstall")
    public String mvnInstall() {
        return "mvn:install";
    }

    @RequiresPermissions("a:gradleBuild")
    @PermInfo("构建gradle")
    @GetMapping("/gradleBuild")
    public String gradleBuild() {
        return "gradleBuild";
    }


    @RequiresRoles("js")
    @GetMapping("/js")
    public String js() {
        return "js programmer";
    }


    @RequiresRoles("python")
    @GetMapping("/python")
    public String python() {
        return "python programmer";
    }


}
