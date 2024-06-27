package com.Xxy.controller;

import com.Xxy.domain.ResponseResult;
import com.Xxy.domain.entity.User;
import com.Xxy.enums.AppHttpCodeEnum;
import com.Xxy.exception.SystemException;
import com.Xxy.service.BlogLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/")
public class BlogLoginController {
    @Autowired
    private BlogLoginService blogLoginService;

    @PostMapping("/login")
    public ResponseResult login(@RequestBody User user){
        if(!StringUtils.hasText(user.getUserName()))
        {
            //提示必须要传用户名
           throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);

        }
        return blogLoginService.login(user);
    }

    @PostMapping("/logout")
    public ResponseResult logout(){
        return blogLoginService.logout();

    }
}
