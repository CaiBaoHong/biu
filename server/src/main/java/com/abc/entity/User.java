package com.abc.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * created by CaiBaoHong at 2018/4/17 14:55<br>
 */
@TableName("user")
public class User extends Model<User> {

    @TableId(type = IdType.ID_WORKER)
    private Long uid;       // 用户id
    private String uname;   // 登录名，不可改
    private String nick;    // 用户昵称，可改
    private String pwd;     // 已加密的登录密码
    private String salt;    // 加密盐值
    private Date created;   // 创建时间
    private Date updated;   // 修改时间

    @Override
    protected Serializable pkVal() {
        return null;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
}
