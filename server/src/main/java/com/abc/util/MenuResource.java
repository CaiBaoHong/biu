package com.abc.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MenuResource implements Serializable{

    private static final long serialVersionUID = 8772422008310126262L;
    private String menuId;
    private String parentId;
    private String menuName;
    private String menuCode;
    private String reqUrl;
    private Integer orderNum;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
    private List<MenuResource> children = new ArrayList<>();

    public MenuResource() {
    }

    public MenuResource(MenuResource m) {
        this.menuId = m.getMenuId();
        this.parentId = m.getParentId();
        this.menuName = m.getMenuName();
        this.menuCode = m.getMenuCode();
        this.reqUrl =  m.getReqUrl();
        this.orderNum = m.getOrderNum();
        this.createdAt = m.getCreatedAt();
        this.updatedAt = m.getUpdatedAt();
        this.deletedAt = m.getDeletedAt();
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
    }

    public String getReqUrl() {
        return reqUrl;
    }

    public void setReqUrl(String reqUrl) {
        this.reqUrl = reqUrl;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public List<MenuResource> getChildren() {
        return children;
    }

    public void setChildren(List<MenuResource> children) {
        this.children = children;
    }
}
