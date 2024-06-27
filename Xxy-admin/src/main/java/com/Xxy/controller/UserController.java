package com.Xxy.controller;

import com.Xxy.domain.ResponseResult;
import com.Xxy.domain.dto.userlistDTO;
import com.Xxy.domain.entity.Role;
import com.Xxy.domain.entity.User;
import com.Xxy.domain.vo.UserInfoAndRoleIdsVo;
import com.Xxy.enums.AppHttpCodeEnum;
import com.Xxy.exception.SystemException;
import com.Xxy.service.RoleService;
import com.Xxy.service.UserService;
import com.Xxy.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/user")
public class UserController {
    @Autowired
    private UserService userService;
    @GetMapping("/list")
    public ResponseResult UserList(int pageNum, int pageSize, userlistDTO userlistDTO)
    {
        return userService.userList(pageNum,pageSize,userlistDTO);
    }

    @PostMapping
    public ResponseResult AddUser(@RequestBody User user)
    {
        if(!StringUtils.hasText(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
        }
        if (!userService.checkUserNameUnique(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
        }
        if (!userService.checkPhoneUnique(user)){
            throw new SystemException(AppHttpCodeEnum.PHONENUMBER_EXIST);
        }
        if (!userService.checkEmailUnique(user)){
            throw new SystemException(AppHttpCodeEnum.EMAIL_EXIST);
        }
        return userService.addUser(user);
    }

    @DeleteMapping("/{userIds}")
    public ResponseResult DeleteUser(@PathVariable("userIds") List<Long> userIds)
    {
        if(userIds.contains(SecurityUtils.getUserId())){
            return ResponseResult.errorResult(500,"不能删除当前你正在使用的用户");
        }
        userService.removeByIds(userIds);
        return ResponseResult.okResult();
    }
    @Autowired
    private RoleService roleService;
    @GetMapping(value = { "/{userId}" })
    public ResponseResult getUserInfoAndRoleIds(@PathVariable(value = "userId") Long userId) {
        List<Role> roles = (List)roleService.listallRole().getData();
        User user = userService.getById(userId);
        //当前用户所具有的角色id列表
        List<Long> roleIds = roleService.selectRoleIdByUserId(userId);

        UserInfoAndRoleIdsVo vo = new UserInfoAndRoleIdsVo(user,roles,roleIds);
        return ResponseResult.okResult(vo);
    }

    //-------------------------修改用户-②更新用户信息--------------------------------

    @PutMapping
    public ResponseResult edit(@RequestBody User user) {
        userService.updateUser(user);
        return ResponseResult.okResult();
    }

}
