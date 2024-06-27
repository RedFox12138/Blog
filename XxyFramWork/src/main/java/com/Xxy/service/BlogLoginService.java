package com.Xxy.service;

import com.Xxy.domain.ResponseResult;
import com.Xxy.domain.entity.User;

public interface BlogLoginService {
    ResponseResult login(User user);

    ResponseResult logout();
}
