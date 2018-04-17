package com.abc.dao;

import com.abc.entity.Role;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    List<String> getRolesByUserId(@Param("userId") Long userId);


}
