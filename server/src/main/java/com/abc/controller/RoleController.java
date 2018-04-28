package com.abc.controller;

import com.abc.entity.Perm;
import com.abc.entity.Role;
import com.abc.entity.RolePerm;
import com.abc.service.PermService;
import com.abc.service.RolePermService;
import com.abc.service.RoleService;
import com.abc.service.UserRoleService;
import com.abc.vo.Json;
import com.abc.vo.UpdateRolePermVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.IdWorker;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * created by CaiBaoHong at 2018/4/17 16:41<br>
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    private static final Logger log = LoggerFactory.getLogger(RoleController.class);

    @Autowired
    private RoleService roleService;
    @Autowired
    private PermService permService;
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

        if (StringUtils.isBlank(role.getRval())) {
            return Json.fail(oper, "权限值不能为空");
        }

        Role roleDB = roleService.selectOne(new EntityWrapper<Role>().eq("rval", role.getRval()));
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
    @RequiresPermissions("role:query")
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

        Wrapper<Role> queryParams = new EntityWrapper<>();
        queryParams.orderBy("created", false);
        queryParams.orderBy("updated", false);
        if (StringUtils.isNotBlank(rname)) {
            queryParams.like("rname", rname);
        }
        Page<Role> page = roleService.selectPage(new Page<>(current, size), queryParams);
        return Json.succ(oper).data("page", page);
    }

    /**
     * 更新角色
     *
     * @param body
     * @return
     */
    @RequiresPermissions("role:update")
    @PatchMapping("/info")
    public Json update(@RequestBody String body) {

        String oper = "update role";
        log.info("{}, body: {}", oper, body);

        Role role = JSON.parseObject(body, Role.class);
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
            perm.setPid(IdWorker.getIdStr());
            perm.setCreated(now);
            perm.setUpdated(now);
        });

        // 先插入权限值，保证perm表上有值可以做role_perm表的关联
        permService.batchInsertIgnore(vo.getPerms());


        // 再查询出来，目的是拿到所有pval对应的pid
        List<String> pvals = vo.getPerms().stream().map(Perm::getPval).collect(Collectors.toList());
        Wrapper<Perm> param = new EntityWrapper<Perm>().in("pval", pvals);
        param.setSqlSelect("pid");
        List<Object> pids = permService.selectObjs(param);

        // 更新角色的权限值
        String rid = vo.getRid();


        List<RolePerm> list = pids.stream().map(pid -> new RolePerm(rid, (String) pid)).collect(Collectors.toList());

        boolean deleteSucc = rolePermService.delete(new EntityWrapper<RolePerm>().eq("role_id", rid));
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
        List<Role> list = roleService.selectList(new EntityWrapper<Role>());
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
        List<Perm> pvals = permService.getPermValsByRoleId(rid);
        return Json.succ(oper,"pvals",pvals);
    }

}
