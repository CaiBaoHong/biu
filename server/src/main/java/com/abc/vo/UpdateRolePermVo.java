package com.abc.vo;

import com.abc.entity.Perm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UpdateRolePermVo implements Serializable{

    private String rid;
    private List<Perm> perms = new ArrayList<>();

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public List<Perm> getPerms() {
        return perms;
    }

    public void setPerms(List<Perm> perms) {
        this.perms = perms;
    }
}
