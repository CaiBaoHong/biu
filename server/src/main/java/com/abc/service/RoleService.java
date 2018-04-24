package com.abc.service;

import com.abc.entity.Role;
import com.baomidou.mybatisplus.service.IService;

import java.util.Set;

public interface RoleService extends IService<Role> {

    Set<String> getRolesByUserId(String userId);

}
