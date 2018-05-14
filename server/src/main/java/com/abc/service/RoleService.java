package com.abc.service;

import com.abc.entity.Perm;
import com.abc.entity.Role;
import com.abc.vo.AuthVo;
import com.baomidou.mybatisplus.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface RoleService extends IService<Role> {

    Set<AuthVo> getRolesByUserId(String userId);

    List<String> getRoleIdsByUserId(String userId);

}
