package com.Xxy.service;

import com.Xxy.domain.ResponseResult;
import com.Xxy.domain.dto.userlistDTO;
import com.Xxy.domain.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;


/**
 * 用户表(User)表服务接口
 *
 * @author makejava
 * @since 2024-06-20 09:47:22
 */
public interface UserService extends IService<User> {

    ResponseResult userInfo();

    ResponseResult updateUserInfo(User user);

    ResponseResult register(User user);

    ResponseResult userList(int pageNum, int pageSize, userlistDTO userlistDTO);

    boolean checkUserNameUnique(String userName);
    boolean checkPhoneUnique(User user);
    boolean checkEmailUnique(User user);
    ResponseResult addUser(User user);

    void updateUser(User user);
}

