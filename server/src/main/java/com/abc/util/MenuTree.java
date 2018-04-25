package com.abc.util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 生成菜单树的工具类
 *
 * 为避免线程安全问题，建议每次都new一个新对象出来，再调用menuList()方法
 *
 */
public class MenuTree {

    public List<MenuResource> cache;//把菜单列表缓存起来

    /**
     * 生成菜单树
     * @param menus
     * @return
     */
    public List<MenuResource> menuList(List<MenuResource> menus) {
        List<MenuResource> list = new ArrayList<>();
        this.cache = menus;
        for (MenuResource m : menus) {
            //顶级菜单
            if (StringUtils.isBlank(m.getParentId())) {
                MenuResource topMenu = new MenuResource(m);
                topMenu.setChildren(menuChild(m.getMenuId()));
                list.add(topMenu);
            }
        }
        return list;
    }


    /**
     * 给定一个父级菜单id，查找有所子菜单
     *
     * @param id
     * @return
     */
    private List<MenuResource> menuChild(String id) {
        List<MenuResource> lists = new ArrayList<>();
        for (MenuResource m : cache) {
            if (StringUtils.equals(id,m.getParentId())) {
                MenuResource childMenu = new MenuResource(m);
                childMenu.setChildren(menuChild(m.getMenuId()));
                lists.add(childMenu);
            }
        }
        return lists;

    }

}