package com.abc.service.impl;

import com.abc.dao.SysPermMapper;
import com.abc.entity.SysPerm;
import com.abc.service.SysPermService;
import com.abc.vo.AuthVo;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SysPermServiceImpl extends ServiceImpl<SysPermMapper, SysPerm> implements SysPermService {

    @Override
    public Set<AuthVo> getPermsByUserId(String userId) {
        List<SysPerm> list = baseMapper.getPermsByUserId(userId);
        return list.stream().map(p->new AuthVo(p.getPname(),p.getPval())).collect(Collectors.toSet());
    }

    @Override
    public List<SysPerm> getPermValsByRoleId(String roleId) {
        return baseMapper.getPermValsByRoleId(roleId);
    }

    @Override
    public void batchInsertIgnore(List<SysPerm> perms) {
        baseMapper.batchInsertIgnore(perms);
    }

    @Override
    public boolean updateByPermId(SysPerm perm) {
        int updateAffect = baseMapper.updateByPermId(perm);
        return updateAffect > 0;
    }
}
