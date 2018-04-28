package com.abc.service.impl;

import com.abc.dao.PermMapper;
import com.abc.entity.Perm;
import com.abc.service.PermService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PermServiceImpl extends ServiceImpl<PermMapper, Perm> implements PermService {

    @Override
    public Set<String> getPermsByUserId(String userId) {
        List<String> list = baseMapper.getPermsByUserId(userId);
        return list.stream().collect(Collectors.toSet());
    }

    @Override
    public List<Perm> getPermValsByRoleId(String roleId) {
        return baseMapper.getPermValsByRoleId(roleId);
    }

    @Override
    public void batchInsertIgnore(List<Perm> perms) {
        baseMapper.batchInsertIgnore(perms);
    }
}
