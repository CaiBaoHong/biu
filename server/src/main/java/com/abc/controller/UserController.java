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
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
     *
     * @param body
     * @return
     */
    @PostMapping
    public Json add(@RequestBody String body) {

        String oper = "add user";
        log.info("{}, body: {}",oper,body);

        User user = JSON.parseObject(body, User.class);

        if (StringUtils.isEmpty(user.getUname())) {
            return Json.fail(oper, "用户帐号名不能为空");
        }
        if (StringUtils.isEmpty(user.getPwd())) {
            return Json.fail(oper, "密码不能为空");
        }

        User userDB = userService.selectOne(new EntityWrapper<User>().eq("uname", user.getUname()));
        if (userDB != null) {
            return Json.fail(oper, "用户已注册");
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
        return Json.result(oper, success)
                .data("uid",user.getUid())
                .data("created",user.getCreated());
    }

    @DeleteMapping
    public Json delete(@RequestBody String body) {

        String oper = "delete user";
        log.info("{}, body: {}",oper,body);

        JSONObject jsonObj = JSON.parseObject(body);
        String uid = jsonObj.getString("uid");

        if (StringUtils.isEmpty(uid)) {
            return Json.fail(oper, "无法删除用户：参数为空（用户id）");
        }

        boolean success = userService.deleteById(uid);
        return Json.result(oper, success);
    }


    /**
     * 更新用户的角色
     *
     * @param body
     * @return
     */
    @PatchMapping("/role")
    public Json updateUserRole(@RequestBody String body) {

        String oper = "update user's roles";
        JSONObject json = JSON.parseObject(body);
        final String uid = json.getString("uid");

        JSONArray rids = json.getJSONArray("rids");
        List<UserRole> list = rids.stream().map(roleId -> new UserRole(uid, (String) roleId)).collect(Collectors.toList());

        boolean deleteSucc = userRoleService.delete(new EntityWrapper<UserRole>().eq("user_id", uid));
        if (!deleteSucc) return Json.fail(oper, "无法解除原来的用户-角色关系");

        boolean addSucc = userRoleService.insertBatch(list);
        return Json.result(oper, addSucc);
    }

    @RequiresPermissions("user:info")
    @GetMapping("/info")
    public Json userInfo() {
        System.out.println("get user info...");
        Object userInfo = SecurityUtils.getSubject().getPrincipal();
        return Json.succ("get user info", "userInfo", userInfo);
    }

    @RequiresPermissions("user:query")
    @PostMapping("/query")
    public Json query(@RequestBody String body) {

        String oper = "query user";
        log.info("{}, body: {}", oper, body);

        JSONObject json = JSON.parseObject(body);
        String nick = json.getString("nick");

        int current = json.getIntValue("current");
        int size = json.getIntValue("size");
        if (current == 0) current = 1;
        if (size == 0) size = 10;

        Wrapper<User> queryParams = new EntityWrapper<>();
        queryParams.orderBy("created", false);
        queryParams.orderBy("updated", false);
        queryParams.setSqlSelect("uid","uname","nick","created","updated");
        if (StringUtils.isNotBlank(nick)) {
            queryParams.like("nick", nick);
        }
        Page<User> page = userService.selectPage(new Page<>(current, size), queryParams);
        return Json.succ(oper).data("page", page);
    }

    /**
     *
     * @param body
     * @return
     */
    @RequiresPermissions("user:update")
    @PatchMapping("/info")
    public Json update(@RequestBody String body) {

        String oper = "update user";
        log.info("{}, body: {}", oper, body);

        User user = JSON.parseObject(body, User.class);

        if (StringUtils.isNotBlank(user.getPwd())){
            //密码加密
            RandomNumberGenerator saltGen = new SecureRandomNumberGenerator();
            String salt = saltGen.nextBytes().toBase64();
            String hashedPwd = new Sha256Hash(user.getPwd(), salt, 1024).toBase64();
            user.setPwd(hashedPwd);
            user.setSalt(salt);
        }else{
            user.setPwd(null);
            user.setSalt(null);
        }

        user.setUname(null);
        user.setCreated(null);
        user.setUpdated(new Date());

        //boolean success = userService.update(user,new EntityWrapper<User>().eq("uid",user.getUid()));
        boolean success = userService.updateById(user);

        return Json.result(oper, success).data("updated",user.getUpdated());
    }




}
