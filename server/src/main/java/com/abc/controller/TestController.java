package com.abc.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * created by CaiBaoHong at 2018/4/17 14:16<br>
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/hello")
    public String hello(){
        return "hello spring ";
    }

}
