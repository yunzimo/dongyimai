package com.offcn.sellergoods.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    @RequestMapping("/showName")
    public Map showName(){
        String loginName= SecurityContextHolder.getContext().getAuthentication().getName();
        Map<String, Object> login = new HashMap();
        login.put("loginName",loginName);
        return login;
    }
}
