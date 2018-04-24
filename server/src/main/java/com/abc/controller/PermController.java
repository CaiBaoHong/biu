package com.abc.controller;

import com.abc.entity.Perm;
import com.abc.service.PermService;
import com.abc.service.RolePermService;
import com.abc.vo.Json;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * created by CaiBaoHong at 2018/4/17 16:41<br>
 */
@RestController
@RequestMapping("/perm")
public class PermController {

    @Autowired
    private PermService permService;
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

        String oper = "add permission";
        Perm perm = JSON.parseObject(body, Perm.class);

        if (StringUtils.isEmpty(perm.getPval())) {
            return Json.fail(oper, "权限值不能为空");
        }

        Perm permDB = permService.selectOne(new EntityWrapper<Perm>().eq("pval", perm.getPval()));
        if (permDB != null) {
            return Json.fail(oper, "权限值已存在：" + perm.getPval());
        }

        //保存新用户数据
        perm.setCreated(new Date());
        boolean success = permService.insert(perm);
        return Json.result(oper, success).data("permId",perm.getPid());
    }


}
