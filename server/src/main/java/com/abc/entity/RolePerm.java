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
    private Long roleId;
    @TableField("perm_id")
    private Long permId;

    public RolePerm() {
    }

    public RolePerm(Long roleId, Long permId) {
        this.roleId = roleId;
        this.permId = permId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getPermId() {
        return permId;
    }

    public void setPermId(Long permId) {
        this.permId = permId;
    }
}
