package com.abc.service;

import com.abc.entity.Perm;
import com.baomidou.mybatisplus.service.IService;

import java.util.Set;

public interface PermService extends IService<Perm> {

    Set<String> getPermsByUserId(String userId);

}
