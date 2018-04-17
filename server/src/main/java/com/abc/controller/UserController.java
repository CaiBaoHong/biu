package com.abc.controller;

import com.abc.entity.User;
import com.abc.service.UserService;
import com.abc.vo.Json;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * created by CaiBaoHong at 2018/4/17 16:41<br>
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public Json add(@RequestBody String body){
        String oper = "add user";
        User user = JSON.parseObject(body, User.class);
        user.setCreated(new Date());
        userService.insert(user);
        return new Json(oper);
    }


}
