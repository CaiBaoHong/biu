package com.abc.controller;

import com.abc.vo.Json;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/page")
public class PageController {

    // 50008:非法的token; 50012:其他客户端登录了;  50014:Token 过期了;
    // 暂时用50014代表未登录

    public static final int TOKEN_EXPIRE = 50014;

    @RequestMapping("/login")
    public Json login() {
        return Json.fail("redirect to login page").code(TOKEN_EXPIRE).msg("please login");
    }

    @RequestMapping("/index")
    public Json index() {
        return Json.succ("index page", "login succ via rest api");
    }

    @RequestMapping("/401")
    public Json page401() {
        return Json.fail("redirect to 401 page").code(TOKEN_EXPIRE).msg("please login");
    }

}
