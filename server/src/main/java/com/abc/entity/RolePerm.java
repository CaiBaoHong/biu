package com.abc.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;

import java.io.Serializable;

/**
 * created by CaiBaoHong at 2018/4/17 14:55<br>
 */
@TableName("role_perm")
public class RolePerm implements Serializable {

    @TableField("role_id")
    private String roleId;
    @TableField("perm_id")
    private String permId;

    public RolePerm() {
    }

    public RolePerm(String roleId, String permId) {
        this.roleId = roleId;
        this.permId = permId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getPermId() {
        return permId;
    }

    public void setPermId(String permId) {
        this.permId = permId;
    }
}
