package com.abc.service;

import com.abc.entity.SysPerm;
import com.abc.vo.AuthVo;
import com.baomidou.mybatisplus.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface SysPermService extends IService<SysPerm> {

    Set<AuthVo> getPermsByUserId(String userId);

    List<SysPerm> getPermsByRoleId(String roleId);

    void saveOrUpdate(List<SysPerm> perms);



}
