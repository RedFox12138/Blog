package com.Xxy.service.impl;

import com.Xxy.domain.ResponseResult;
import com.Xxy.domain.dto.userlistDTO;
import com.Xxy.domain.entity.Role;
import com.Xxy.domain.entity.User;
import com.Xxy.domain.entity.UserRole;
import com.Xxy.domain.vo.PageVo;
import com.Xxy.domain.vo.RoleVo;
import com.Xxy.domain.vo.UserInfoVo;
import com.Xxy.domain.vo.UserListVo;
import com.Xxy.enums.AppHttpCodeEnum;
import com.Xxy.exception.SystemException;
import com.Xxy.mapper.UserMapper;
import com.Xxy.service.UserRoleService;
import com.Xxy.service.UserService;
import com.Xxy.utils.BeanCopyUtils;
import com.Xxy.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户表(User)表服务实现类
 *
 * @author makejava
 * @since 2024-06-20 09:47:45
 */
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ResponseResult userInfo() {
        //获取当前用户id
        Long userId = SecurityUtils.getUserId();
        //根据用户id查询用户信息
        User user = getById(userId);
        UserInfoVo vo = BeanCopyUtils.copyBean(user,UserInfoVo.class);

        //封装成UserInfo
        return ResponseResult.okResult(vo);
    }

    @Override
    public ResponseResult updateUserInfo(User user) {
        updateById(user);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult register(User user) {
        //对数据进行非空判断  null或者""都认为是空的
        if(!StringUtils.hasText(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.USERNAME_NOT_NULL);
        }
        if(!StringUtils.hasText(user.getPassword())){
            throw new SystemException(AppHttpCodeEnum.PASSWORD_NOT_NULL);
        }
        if(!StringUtils.hasText(user.getNickName())){
            throw new SystemException(AppHttpCodeEnum.NIKENAME_NOT_NULL);
        }
        if(!StringUtils.hasText(user.getEmail())){
            throw new SystemException(AppHttpCodeEnum.EMAIL_NOT_NULL);
        }
        //对数据进行是否存在的判断
        if(userNameExist(user.getUserName()) )
        {
            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
        }
        if(NickNameExist(user.getNickName()) )
        {
            throw new SystemException(AppHttpCodeEnum.NIKENAME_EXIST);
        }
        //对密码进行加密
        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
        //存入数据库
        save(user);
        return null;
    }

    @Override
    public ResponseResult userList(int pageNum, int pageSize, userlistDTO userlistDTO) {
        //查询对应文章的根评论
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.hasText(userlistDTO.getStatus()), User::getStatus, userlistDTO.getStatus());
        //对articleId进行判断
        queryWrapper.like(StringUtils.hasText(userlistDTO.getUserName()), User::getUserName, userlistDTO.getUserName());
        queryWrapper.like(StringUtils.hasText(userlistDTO.getPhonenumber()), User::getPhonenumber, userlistDTO.getPhonenumber());
        //分页查询
        Page<User> page = new Page<>(pageNum, pageSize);
        page(page, queryWrapper);
        List<User> userList = list(queryWrapper);
        List<UserListVo> userListVos = BeanCopyUtils.copyBeanList(userList, UserListVo.class);
        PageVo pageVo = new PageVo(userListVos, page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    private boolean userNameExist(String userName) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName,userName);
        return count(queryWrapper)>0;
    }
    private boolean NickNameExist(String NickName) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getNickName,NickName);
        return count(queryWrapper)>0;
    }

    @Autowired
    private UserRoleService userRoleService;

    @Override
    public boolean checkUserNameUnique(String userName) {
        return count(Wrappers.<User>lambdaQuery().eq(User::getUserName,userName))==0;
    }

    @Override
    public boolean checkPhoneUnique(User user) {
        return count(Wrappers.<User>lambdaQuery().eq(User::getPhonenumber,user.getPhonenumber()))==0;
    }

    @Override
    public boolean checkEmailUnique(User user) {
        return count(Wrappers.<User>lambdaQuery().eq(User::getEmail,user.getEmail()))==0;
    }

    @Override
    @Transactional
    public ResponseResult addUser(User user) {
        //密码加密处理
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        save(user);

        if(user.getRoleIds()!=null&&user.getRoleIds().length>0){
            insertUserRole(user);
        }
        return ResponseResult.okResult();
    }

    private void insertUserRole(User user) {
        List<UserRole> sysUserRoles = Arrays.stream(user.getRoleIds())
                .map(roleId -> new UserRole(user.getId(), roleId)).collect(Collectors.toList());
        userRoleService.saveBatch(sysUserRoles);
    }

    @Override
    @Transactional
    public void updateUser(User user) {
        // 删除用户与角色关联
        LambdaQueryWrapper<UserRole> userRoleUpdateWrapper = new LambdaQueryWrapper<>();
        userRoleUpdateWrapper.eq(UserRole::getUserId,user.getId());
        userRoleService.remove(userRoleUpdateWrapper);

        // 新增用户与角色管理
        insertUserRole(user);
        // 更新用户信息
        updateById(user);
    }
}


