package com.abc.controller;

import com.abc.annotation.PermInfo;
import com.abc.constant.PermType;
import com.abc.entity.SysPerm;
import com.abc.service.SysPermService;
import com.abc.vo.Json;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.annotation.AnnotationBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

/**
 * created by CaiBaoHong at 2018/4/17 16:41<br>
 */
@RestController()
@RequestMapping("/sys_perm")
@PermInfo(value = "权限模块",pval = "api:sys:perm")
public class SysPermController {

    private static final Logger log = LoggerFactory.getLogger(SysPermController.class);

    @Autowired
    private SysPermService permService;

    /**
     * 列出所有类型的权限：菜单、按钮、接口
     * @return
     */
    @GetMapping("/list/all")
    public Json listMenuButtonPermission(){
        String oper = "list menu,button,api permissions";
        EntityWrapper<SysPerm> params = new EntityWrapper<>();
        params.in("ptype", new Integer[]{PermType.MENU,PermType.BUTTON,PermType.API});
        List<SysPerm> list = permService.selectList(params);
        Map<Integer, List<SysPerm>> permMap = list.stream().collect(Collectors.groupingBy(SysPerm::getPtype));
        List<SysPerm> buttonPermList = permMap.get(PermType.BUTTON);
        Map<String, List<SysPerm>> buttonsGroupedByParent = buttonPermList.stream().collect(Collectors.groupingBy(SysPerm::getParent));
        return Json.succ(oper,"permMap",permMap).data("btnPermMap",buttonsGroupedByParent);
    }

    /**
     * 批量保存权限数据
     * @return
     */
    @PostMapping("/batch_save")
    public Json batchSavePerms(@RequestBody String body){
        String oper = "batch save perm data";
        log.info("{}, body: {}", oper, body);
        List<SysPerm> notSyncedPerms = JSON.parseArray(body, SysPerm.class);
        permService.batchInsertIgnore(notSyncedPerms);
        return Json.succ(oper);
    }


    /**
     * 新增权限
     *
     * @param body
     * @return
     */
    @PostMapping
    public Json add(@RequestBody String body) {

        String oper = "add permission";
        SysPerm perm = JSON.parseObject(body, SysPerm.class);

        if (StringUtils.isEmpty(perm.getPval())) {
            return Json.fail(oper, "权限值不能为空");
        }

        EntityWrapper<SysPerm> params = new EntityWrapper<>();
        params.eq("pval",perm.getPval());
        params.setSqlSelect("pname,pval");
        SysPerm permDB = permService.selectOne(params);

        if (permDB != null) {
            return Json.fail(oper, "权限值已存在："+permDB.getPname()+"（"+perm.getPval()+"）");
        }

        //保存
        perm.setCreated(new Date());
        boolean success = permService.insert(perm);
        return Json.result(oper, success)
                .data("created", perm.getCreated());
    }

    /**
     * 根据权限值删除权限
     * @param body
     * @return
     */
    @DeleteMapping
    public Json delete(@RequestBody String body) {
        String oper = "delete permission";
        log.info("{}, body: {}", oper, body);
        JSONObject jsonObj = JSON.parseObject(body);
        String pval = jsonObj.getString("pval");
        if (StringUtils.isBlank(pval)) {
            return Json.fail(oper, "无法删除权限：参数为空（权限值）");
        }
        boolean success = permService.deleteById(pval);
        return Json.result(oper, success);
    }

    /**
     * 查询权限
     *
     * @param body
     * @return
     */
    @RequiresPermissions("perm:query")
    @PostMapping("/query")
    public Json query(@RequestBody String body) {

        String oper = "query permission";
        log.info("{}, body: {}", oper, body);

        JSONObject json = JSON.parseObject(body);
        String pname = json.getString("pname");
        String pval = json.getString("pval");

        int current = json.getIntValue("current");
        int size = json.getIntValue("size");
        if (current == 0) current = 1;
        if (size == 0) size = 10;

        Wrapper<SysPerm> queryParams = new EntityWrapper<>();
        queryParams.orderBy("created", false);
        queryParams.orderBy("updated", false);
        if (StringUtils.isNotBlank(pname)) {
            queryParams.like("pname", pname);
        }
        if (StringUtils.isNotBlank(pval)) {
            queryParams.like("pval", pval);
        }
        Page<SysPerm> page = permService.selectPage(new Page<>(current, size), queryParams);
        return Json.succ(oper).data("page", page);
    }


    /**
     * 更新，只允许更新权限名称，不允许更新权限值
     * @param body
     * @return
     */
    @RequiresPermissions("perm:update")
    @PatchMapping("/info")
    public Json update(@RequestBody String body) {

        String oper = "update permission";
        log.info("{}, body: {}", oper, body);

        SysPerm perm = JSON.parseObject(body, SysPerm.class);
        if (StringUtils.isBlank(perm.getPval())) {
            return Json.fail(oper, "无法更新权限：参数为空（权限值）");
        }

        SysPerm updateData = new SysPerm();
        updateData.setPval(perm.getPval());
        updateData.setPname(perm.getPname());
        updateData.setUpdated(new Date());
        boolean success = permService.updateById(updateData);
        return Json.result(oper, success).data("updated", perm.getUpdated());
    }


    @Autowired
    private ApplicationContext context;

    @GetMapping("/meta/api")
    public Json listApiPermMetadata() {

        String oper = "list api permission metadata";
        log.info(oper);

        String basicPackage = ClassUtils.getPackageName(this.getClass());
        Map<String, Object> map = context.getBeansWithAnnotation(Controller.class);
        Collection<Object> beans = map.values();
        beans.stream().filter( b-> StringUtils.equals(basicPackage,ClassUtils.getPackageName(b.getClass())))
                .forEach(bean->{
                    Class<?> clz = bean.getClass();
                    SysPerm modulePerm = getModulePerm(clz);
                    System.out.println("模块权限："+modulePerm.getPname()+", "+modulePerm.getPval());
                });

        /*
        for (Object bean : beans) {
            Class<?> clz = bean.getClass();
            System.out.println("class name: "+clz.getSimpleName());
            Class<?> superclass = clz.getSuperclass();
            System.out.println("superclass : "+superclass.getSimpleName());
            Annotation[] annos = superclass.getAnnotations();
            for (Annotation anno : annos) {
                System.out.println("anno:"+ anno.annotationType().getSimpleName());
                PermInfo pm = AnnotationUtils.getAnnotation(anno, PermInfo.class);
                if (pm!=null){
                    System.out.println("pm: "+pm.value());
                }

            }

        }
        */

        return Json.succ(oper);
    }

    public SysPerm getModulePerm(Class<?> clz){
        SysPerm perm = new SysPerm();
        //首选值
        PermInfo piAnno = AnnotationUtils.getAnnotation(clz, PermInfo.class);
        if (piAnno==null){
            piAnno = AnnotationUtils.getAnnotation(clz.getSuperclass(), PermInfo.class);
        }
        String pnamePrimary = null;
        String pvalPrimary = null;
        if (piAnno!=null && piAnno.value()!=null){
            pnamePrimary = piAnno.value();
            pvalPrimary = piAnno.pval();
        }
        //备选值
        String pnameSub = ClassUtils.getShortName(clz);
        RequestMapping rmAnno = AnnotationUtils.getAnnotation(clz, RequestMapping.class);
        if(rmAnno==null){
            rmAnno = AnnotationUtils.getAnnotation(clz.getSuperclass(), RequestMapping.class);
        }
        String pvalSub = rmAnno.value()[0];
        //赋值
        if (StringUtils.isNotBlank(pnamePrimary)){
            perm.setPname(pnamePrimary);
        }else{
            perm.setPname(pnameSub);
        }
        if (StringUtils.isNotBlank(pvalPrimary)){
            perm.setPval(pvalPrimary);
        }else{
            perm.setPval(pvalSub);
        }
        perm.setPtype(PermType.API);
        return perm;
    }



}
