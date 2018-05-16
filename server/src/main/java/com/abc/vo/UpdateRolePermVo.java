package com.abc.vo;

import com.abc.entity.SysPerm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UpdateRolePermVo implements Serializable{

    private String rid;
    private List<SysPerm> perms = new ArrayList<>();

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public List<SysPerm> getPerms() {
        return perms;
    }

    public void setPerms(List<SysPerm> perms) {
        this.perms = perms;
    }
}
