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




}
