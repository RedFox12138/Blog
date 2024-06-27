package com.Xxy.service;

import com.Xxy.domain.ResponseResult;
import com.Xxy.domain.entity.User;

public interface LoginService {
    ResponseResult login(User user);

    ResponseResult logout();
}
