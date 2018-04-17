package com.abc.service.impl;

import com.abc.dao.UserRoleMapper;
import com.abc.entity.UserRole;
import com.abc.service.UserRoleService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {
}
