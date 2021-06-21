package com.zxkj.goods.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class TestController {

    @Value("${didispace.title:}")
    private String hello;

    @GetMapping("/hello")
    public String hello() {
        return hello;
    }

    @GetMapping("/test")
    public String test() {
        return hello;
    }

}