package com.abc.controller;

import com.abc.constant.Codes;
import com.abc.entity.User;
import com.abc.vo.Json;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

/**
 * created by CaiBaoHong at 2018/4/20 17:26<br>
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    /**
     * 登录接口，由于UserService中是模拟返回用户信息的，
     * 所以用户名随意，密码123456
     *
     * @param body
     * @return
     */
    @PostMapping("/login")
    public Json login(@RequestBody String body){

        String oper = "user login";
        log.info("{}, body: {}",oper,body);

        JSONObject json = JSON.parseObject(body);
        String uname = json.getString("uname");
        String pwd = json.getString("pwd");

        if (StringUtils.isEmpty(uname)){
            return Json.fail(oper,"用户名不能为空");
        }
        if (StringUtils.isEmpty(pwd)){
            return Json.fail(oper,"密码不能为空");
        }

        Subject currentUser = SecurityUtils.getSubject();
        try {
            //登录
            currentUser.login( new UsernamePasswordToken(uname, pwd) );
            //从session取出用户信息
            User user = (User) currentUser.getPrincipal();
            if (user==null) throw new AuthenticationException();
            log.info("user login: {}, sessionId: {}",user.getUname(),currentUser.getSession().getId());
            //返回登录用户的信息给前台，含用户的所有角色和权限
            return Json.succ(oper)
                    .data("token", UUID.randomUUID().toString())
                    .data("uid",user.getUid())
                    .data("nick",user.getNick())
                    .data("roles",user.getRoles())
                    .data("perms",user.getPerms());

        } catch ( UnknownAccountException uae ) {
            log.warn("用户帐号不正确");
            return Json.fail(oper,"用户帐号或密码不正确");

        } catch ( IncorrectCredentialsException ice ) {
            log.warn("用户密码不正确");
            return Json.fail(oper,"用户帐号或密码不正确");

        } catch ( LockedAccountException lae ) {
            log.warn("用户帐号被锁定");
            return Json.fail(oper,"用户帐号被锁定不可用");

        } catch ( AuthenticationException ae ) {
            log.warn("登录出错");
            return Json.fail(oper,"登录失败："+ae.getMessage());
        }
    }

    @PostMapping("/logout")
    public Json logout(){
        String oper = "user logout";
        log.info("{}",oper);
        SecurityUtils.getSubject().logout();
        return new Json(oper);
    }

    @GetMapping("/info")
    public Json info(){
        String oper = "get user info";

        Subject subject = SecurityUtils.getSubject();

        Serializable sessionId = subject.getSession().getId();
        log.info("{}, sessionId: {}",oper,sessionId);

        //从session取出用户信息
        User user = (User) subject.getPrincipal();
        if (user==null){
            //告知前台，登录失效
            return new Json(oper,false, Codes.SESSION_TIMEOUT,"登录已失效",null);
        }else{
            //返回登录用户的信息给前台，含用户的所有角色和权限
            return Json.succ(oper)
                    .data("name",user.getUname())
                    .data("nick",user.getNick())
                    .data("avator","")
                    .data("roles",user.getRoles())
                    .data("perms",user.getPerms());
        }


    }

}
