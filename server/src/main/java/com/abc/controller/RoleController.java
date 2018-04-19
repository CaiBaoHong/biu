package com.abc.controller;

import com.abc.entity.Role;
import com.abc.entity.RolePerm;
import com.abc.entity.UserRole;
import com.abc.service.RolePermService;
import com.abc.service.RoleService;
import com.abc.service.UserRoleService;
import com.abc.vo.Json;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
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
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private RolePermService rolePermService;

    /**
     * 新增权限
     *
     * @param body
     * @return
     */
    @PostMapping
    public Json add(@RequestBody String body) {

        String oper = "add role";
        Role role = JSON.parseObject(body, Role.class);

        if (StringUtils.isEmpty(role.getRval())) {
            return Json.fail(oper, "权限值不能为空");
        }

        Role roleDB = roleService.selectOne(new EntityWrapper<Role>().eq("rval", role.getRval()));
        if (roleDB != null) {
            return Json.fail(oper, "角色值已存在：" + role.getRval());
        }

        //保存新用户数据
        role.setCreated(new Date());
        boolean success = roleService.insert(role);
        return Json.result(oper, success);
    }


    /**
     * 更新角色的权限
     * @param body
     * @return
     */
    @PatchMapping("/perm")
    public Json updateRolePerm(@RequestBody String body) {

        String oper = "update role's permissions";
        JSONObject json = JSON.parseObject(body);
        final Long rid = json.getLong("rid");

        JSONArray pids = json.getJSONArray("pids");
        List<RolePerm> list = pids.stream().map(permId -> new RolePerm(rid, (Long) permId)).collect(Collectors.toList());

        boolean deleteSucc = rolePermService.delete(new EntityWrapper<RolePerm>().eq("role_id", rid));
        if (!deleteSucc) return Json.fail(oper,"无法解除原来的角色-权限关系");

        boolean addSucc = rolePermService.insertBatch(list);
        return Json.result(oper, addSucc);
    }





}
