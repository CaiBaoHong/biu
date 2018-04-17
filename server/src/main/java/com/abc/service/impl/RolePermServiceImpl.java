package com.abc.service.impl;

import com.abc.dao.RolePermMapper;
import com.abc.entity.RolePerm;
import com.abc.service.RolePermService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class RolePermServiceImpl extends ServiceImpl<RolePermMapper, RolePerm> implements RolePermService {
}
