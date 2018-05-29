package com.abc.controller;

import com.abc.annotation.PermInfo;
import com.abc.entity.SysPerm;
import com.abc.entity.SysRole;
import com.abc.entity.SysRolePerm;
import com.abc.service.SysPermService;
import com.abc.service.SysRolePermService;
import com.abc.service.SysRoleService;
import com.abc.service.SysUserRoleService;
import com.abc.vo.Json;
import com.abc.vo.UpdateRolePermVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
@PermInfo(value = "系统角色模块", pval = "a:sys:role")
@RestController
@RequestMapping("/sys_role")
public class SysRoleController {

    private static final Logger log = LoggerFactory.getLogger(SysRoleController.class);

    @Autowired
    private SysRoleService roleService;
    @Autowired
    private SysPermService permService;
    @Autowired
    private SysUserRoleService userRoleService;
    @Autowired
    private SysRolePermService rolePermService;

    /**
     * 新增权限
     *
     * @param body
     * @return
     */
    @PostMapping
    public Json add(@RequestBody String body) {

        String oper = "add role";
        SysRole role = JSON.parseObject(body, SysRole.class);

        if (StringUtils.isBlank(role.getRval())) {
            return Json.fail(oper, "权限值不能为空");
        }

        SysRole roleDB = roleService.selectOne(new EntityWrapper<SysRole>().eq("rval", role.getRval()));
        if (roleDB != null) {
            return Json.fail(oper, "角色值已存在：" + role.getRval());
        }

        //保存新用户数据
        role.setCreated(new Date());
        boolean success = roleService.insert(role);
        return Json.result(oper, success)
                .data("rid",role.getRid())
                .data("created",role.getCreated());
    }

    @DeleteMapping
    public Json delete(@RequestBody String body) {

        String oper = "delete role";
        log.info("{}, body: {}", oper, body);

        JSONObject jsonObj = JSON.parseObject(body);
        String rid = jsonObj.getString("rid");

        if (StringUtils.isBlank(rid)) {
            return Json.fail(oper, "无法删除角色：参数为空（角色id）");
        }

        boolean success = roleService.deleteById(rid);
        return Json.result(oper, success);
    }

    /**
     * 查询角色
     *
     * @param body
     * @return
     */
    @RequiresPermissions("a:role:query")
    @PostMapping("/query")
    public Json query(@RequestBody String body) {

        String oper = "query role";
        log.info("{}, body: {}", oper, body);

        JSONObject json = JSON.parseObject(body);
        String rname = json.getString("rname");

        int current = json.getIntValue("current");
        int size = json.getIntValue("size");
        if (current == 0) current = 1;
        if (size == 0) size = 10;

        Wrapper<SysRole> queryParams = new EntityWrapper<>();
        queryParams.orderBy("created", false);
        queryParams.orderBy("updated", false);
        if (StringUtils.isNotBlank(rname)) {
            queryParams.like("rname", rname);
        }
        Page<SysRole> page = roleService.selectPage(new Page<>(current, size), queryParams);
        return Json.succ(oper).data("page", page);
    }

    /**
     * 更新角色
     *
     * @param body
     * @return
     */
    @RequiresPermissions("a:role:update")
    @PatchMapping("/info")
    public Json update(@RequestBody String body) {

        String oper = "update role";
        log.info("{}, body: {}", oper, body);

        SysRole role = JSON.parseObject(body, SysRole.class);
        if (StringUtils.isBlank(role.getRid())) {
            return Json.fail(oper, "无法更新角色：参数为空（角色id）");
        }
        role.setUpdated(new Date());
        boolean success = roleService.updateById(role);
        return Json.result(oper, success).data("updated", role.getUpdated());
    }


    /**
     * 更新角色的权限
     *
     * @param vo
     * @return
     */
    @PatchMapping("/perm")
    public Json updateRolePerm(@RequestBody UpdateRolePermVo vo) {

        String oper = "update role's permissions";

        if (StringUtils.isBlank(vo.getRid())) {
            return Json.fail(oper, "无法更新角色的权限：参数为空（角色id）");
        }
        if (vo.getPerms().isEmpty()) {
            return Json.fail(oper, "无法更新角色的权限：参数为空（权限数据）");
        }

        //手动生成id，因为@TableId不支持批量生成
        Date now = new Date();
        vo.getPerms().stream().forEach(perm -> {
            perm.setCreated(now);
            perm.setUpdated(now);
        });

        // 先插入权限值，保证perm表上有值可以做role_perm表的关联
        permService.saveOrUpdate(vo.getPerms());


        // 再查询出来，目的是拿到所有pval对应的pid
        List<String> pvals = vo.getPerms().stream().map(SysPerm::getPval).collect(Collectors.toList());
        Wrapper<SysPerm> param = new EntityWrapper<SysPerm>().in("pval", pvals);
        param.setSqlSelect("pid");
        List<Object> pids = permService.selectObjs(param);

        // 更新角色的权限值
        String rid = vo.getRid();


        List<SysRolePerm> list = pids.stream().map(pid -> new SysRolePerm(rid, (String) pid)).collect(Collectors.toList());

        boolean deleteSucc = rolePermService.delete(new EntityWrapper<SysRolePerm>().eq("role_id", rid));
        if (!deleteSucc) return Json.fail(oper, "无法解除原来的角色-权限关系");

        boolean addSucc = rolePermService.insertBatch(list);
        return Json.result(oper, addSucc);

    }

    /**
     * 列出除了管理员之外的所有角色
     * @return
     */
    @GetMapping("/list")
    public Json listRoles() {
        String oper = "list all roles except admin";
        List<SysRole> list = roleService.selectList(new EntityWrapper<SysRole>());
        return Json.result(oper, true,"list",list);
    }

    /**
     * 查找角色的权限
     * @param rid
     * @return
     */
    @GetMapping("/{rid}/perms")
    public Json findRolePerms(@PathVariable String rid){
        String oper = "find role perms";
        log.info("{}, rid: {}", oper, rid);
        if (StringUtils.isBlank(rid)){
            return Json.fail(oper, "无法查询当前角色的权限值：参数为空（角色id）");
        }
        List<SysPerm> pvals = permService.getPermValsByRoleId(rid);
        return Json.succ(oper,"pvals",pvals);
    }

}
