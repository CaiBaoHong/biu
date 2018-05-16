package com.abc.controller;

import com.abc.constant.Root;
import com.abc.entity.SysUser;
import com.abc.entity.SysUserRole;
import com.abc.service.SysRoleService;
import com.abc.service.SysUserRoleService;
import com.abc.service.SysUserService;
import com.abc.util.PageUtils;
import com.abc.vo.Json;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
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
@RequestMapping("/sys_user")
public class SysUserController {

    private static final Logger log = LoggerFactory.getLogger(SysUserController.class);

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysUserRoleService sysUserRoleService;

    /**
     * 添加系统用户
     * @param body
     * @return
     */
    @PostMapping
    public Json add(@RequestBody String body) {

        String oper = "add sys user";
        log.info("{}, body: {}",oper,body);

        SysUser user = JSON.parseObject(body, SysUser.class);

        if (StringUtils.isEmpty(user.getUname())) {
            return Json.fail(oper, "用户帐号名不能为空");
        }
        if (StringUtils.isEmpty(user.getPwd())) {
            return Json.fail(oper, "密码不能为空");
        }

        SysUser userDB = sysUserService.selectOne(new EntityWrapper<SysUser>().eq("uname", user.getUname()));
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

        boolean success = sysUserService.insert(user);
        return Json.result(oper, success)
                .data("uid",user.getUid())
                .data("created",user.getCreated());
    }

    /**
     * 删除系统用户
     * @param body
     * @return
     */
    @DeleteMapping
    public Json delete(@RequestBody String body) {

        String oper = "delete user";
        log.info("{}, body: {}",oper,body);

        JSONObject jsonObj = JSON.parseObject(body);
        String uid = jsonObj.getString("uid");
        if (StringUtils.isEmpty(uid)) {
            return Json.fail(oper, "无法删除用户：参数为空（用户id）");
        }

        //限制：不能删当前登录用户
        SysUser user = (SysUser) SecurityUtils.getSubject().getPrincipal();
        if (StringUtils.equals(uid,user.getUid())){
            return Json.fail(oper, "系统限制：不能删除当前登录账号");
        }

        //检查：不能删除管理员
        boolean containRoot = sysRoleService.checkUidContainRval(uid, Root.ROLE_VAL);
        if (containRoot){
            return Json.fail(oper,"不能删除管理员用户");
        }

        boolean success = sysUserService.deleteById(uid);
        sysUserRoleService.delete(new EntityWrapper<SysUserRole>().eq("user_id",uid));
        return Json.result(oper, success);
    }

    /**
     * 更新系统用户的角色
     * @param body
     * @return
     */
    @PatchMapping("/role")
    public Json updateUserRole(@RequestBody String body) {

        String oper = "update user's roles";
        log.info("{}, body: {}",oper,body);

        JSONObject json = JSON.parseObject(body);
        final String uid = json.getString("uid");

        List<String> rids = json.getJSONArray("rids").toJavaList(String.class);

        //检查：不能含有管理员角色
        boolean containRoot = sysRoleService.checkRidsContainRval(rids, Root.ROLE_VAL);
        if (containRoot){
            return Json.fail(oper,"不能给非管理员用户赋予管理员角色");
        }

        //删除：原来绑定的角色
        boolean deleteSucc = sysUserRoleService.delete(new EntityWrapper<SysUserRole>().eq("user_id", uid));
        if (!deleteSucc) return Json.fail(oper, "无法解除原来的用户-角色关系");

        //更新：绑定新的角色
        List<SysUserRole> list = rids.stream().map(roleId -> new SysUserRole(uid, roleId)).collect(Collectors.toList());

        if (!rids.isEmpty()){
            boolean addSucc = sysUserRoleService.insertBatch(list);
            return Json.result(oper, addSucc);
        }
        return Json.succ(oper);
    }

    /**
     * 查询系统用户列表
     * @param body
     * @return
     */
    @PostMapping("/query")
    public Json query(@RequestBody String body) {
        String oper = "query user";
        log.info("{}, body: {}", oper, body);
        JSONObject json = JSON.parseObject(body);
        String nick = json.getString("nick");
        Page<SysUser> page = sysUserService.queryUserIncludeRoles(PageUtils.getPageParam(json), nick);
        return Json.succ(oper).data("page", page);
    }

    /**
     * 更新系统用户的信息
     * @return
     */
    @GetMapping("/info")
    public Json userInfo() {
        System.out.println("get user info...");
        Object userInfo = SecurityUtils.getSubject().getPrincipal();
        return Json.succ("get user info", "userInfo", userInfo);
    }

    /**
     * 更新系统用户的信息
     * @param body
     * @return
     */
    @PatchMapping("/info")
    public Json update(@RequestBody String body) {

        String oper = "update user";
        log.info("{}, body: {}", oper, body);

        SysUser user = JSON.parseObject(body, SysUser.class);

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

        //boolean success = sysUserService.update(user,new EntityWrapper<User>().eq("uid",user.getUid()));
        boolean success = sysUserService.updateById(user);

        return Json.result(oper, success).data("updated",user.getUpdated());
    }

    /**
     * 查找用户的角色
     * @param uid
     * @return
     */
    @GetMapping("/{uid}/roles")
    public Json findUserRoles(@PathVariable String uid){
        String oper = "find user roles";
        log.info("{}, uid: {}", oper, uid);
        if (StringUtils.isBlank(uid)){
            return Json.fail(oper, "无法查询当前用户的角色值：参数为空（用户id）");
        }
        List<String> rids = sysRoleService.getRoleIdsByUserId(uid);
        return Json.succ(oper,"rids",rids);
    }

}
