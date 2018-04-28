package com.abc.dao;

import com.abc.entity.Perm;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PermMapper extends BaseMapper<Perm> {

    List<String> getPermsByUserId(@Param("userId") String userId);

    List<Perm> getPermValsByRoleId(@Param("roleId") String roleId);

    void batchInsertIgnore(@Param("perms") List<Perm> perms);

}
