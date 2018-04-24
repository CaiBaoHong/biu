package com.abc.service.impl;

import com.abc.dao.RoleMapper;
import com.abc.entity.Role;
import com.abc.service.RoleService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Override
    public Set<String> getRolesByUserId(String userId) {
        List<String> list = baseMapper.getRolesByUserId(userId);
        return list.stream().collect(Collectors.toSet());
    }

}
