package com.abc.service.impl;

import com.abc.dao.RoleMapper;
import com.abc.entity.Perm;
import com.abc.entity.Role;
import com.abc.service.RoleService;
import com.abc.vo.AuthVo;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Override
    public Set<AuthVo> getRolesByUserId(String userId) {
        List<Role> list = baseMapper.getRolesByUserId(userId);
        return list.stream().map(r->new AuthVo(r.getRname(),r.getRval())).collect(Collectors.toSet());
    }

    @Override
    public List<String> getRoleIdsByUserId(String userId) {
        return baseMapper.getRoleIdsByUserId(userId);
    }

}
