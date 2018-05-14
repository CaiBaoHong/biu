package com.abc.service;

import com.abc.entity.Perm;
import com.abc.vo.AuthVo;
import com.baomidou.mybatisplus.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface PermService extends IService<Perm> {

    Set<AuthVo> getPermsByUserId(String userId);

    List<Perm> getPermValsByRoleId(String roleId);

    void batchInsertIgnore(List<Perm> perms);

}
