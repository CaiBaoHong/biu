package com.abc.service;

import com.abc.entity.SysPerm;
import com.abc.vo.AuthVo;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;
import java.util.Set;

public interface SysPermService extends IService<SysPerm> {

    Set<AuthVo> getPermsByUserId(String userId);

    List<SysPerm> getPermValsByRoleId(String roleId);

    void batchInsertIgnore(List<SysPerm> perms);

}
