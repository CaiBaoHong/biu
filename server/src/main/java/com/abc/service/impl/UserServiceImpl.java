package com.abc.service.impl;

import com.abc.dao.UserMapper;
import com.abc.entity.User;
import com.abc.service.UserService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


}
