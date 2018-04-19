package com.abc.controller;

import com.abc.entity.User;
import com.abc.entity.UserRole;
import com.abc.service.UserRoleService;
import com.abc.service.UserService;
import com.abc.vo.Json;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mysql.jdbc.log.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * created by CaiBaoHong at 2018/4/17 16:41<br>
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private UserRoleService userRoleService;

    /**
     * 用户注册
     * @param body
     * @return
     */
    @PostMapping
    public Json add(@RequestBody String body){

        String oper = "add user";
        User user = JSON.parseObject(body, User.class);

        if (StringUtils.isEmpty(user.getUname())){
            return Json.fail(oper,"用户帐号名不能为空");
        }
        if (StringUtils.isEmpty(user.getPwd())){
            return Json.fail(oper,"密码不能为空");
        }

        User userDB = userService.selectOne(new EntityWrapper<User>().eq("uname",user.getUname()));
        if (userDB!=null){
            return Json.fail(oper,"用户已注册");
        }

        //密码加密
        RandomNumberGenerator saltGen = new SecureRandomNumberGenerator();
        String salt = saltGen.nextBytes().toBase64();
        String hashedPwd = new Sha256Hash(user.getPwd(), salt, 1024).toBase64();
        //保存新用户数据
        user.setPwd(hashedPwd);
        user.setSalt(salt);
        user.setCreated(new Date());

        boolean success = userService.insert(user);
        return Json.result(oper,success);
    }


    /**
     * 更新用户的角色
     * @param body
     * @return
     */
    @PatchMapping("/role")
    public Json updateUserRole(@RequestBody String body) {

        String oper = "update user's roles";
        JSONObject json = JSON.parseObject(body);
        final Long uid = json.getLong("uid");

        JSONArray rids = json.getJSONArray("rids");
        List<UserRole> list = rids.stream().map(roleId -> new UserRole(uid, (Long) roleId)).collect(Collectors.toList());

        boolean deleteSucc = userRoleService.delete(new EntityWrapper<UserRole>().eq("user_id", uid));
        if (!deleteSucc) return Json.fail(oper,"无法解除原来的用户-角色关系");

        boolean addSucc = userRoleService.insertBatch(list);
        return Json.result(oper, addSucc);
    }

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
            //返回登录用户的信息给前台，含用户的所有角色和权限
            return Json.succ(oper)
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


}
