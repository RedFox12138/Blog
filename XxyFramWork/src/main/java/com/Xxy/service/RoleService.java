package com.Xxy.service;

import com.Xxy.domain.ResponseResult;
import com.Xxy.domain.dto.RoleDTO;
import com.Xxy.domain.dto.RoleMenuDTO;
import com.Xxy.domain.entity.Role;
import com.Xxy.domain.vo.RoleVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


/**
 * 角色信息表(Role)表服务接口
 *
 * @author makejava
 * @since 2024-06-22 17:13:42
 */
public interface RoleService extends IService<Role> {

    ResponseResult roleList(int pageNum, int pageSize, RoleDTO roleDTO);

    List<String> selectRoleKeyByUserId(Long id);

    ResponseResult changeStatus(RoleDTO roleDTO);

    ResponseResult AddRole(RoleMenuDTO roleMenuDTO);

    ResponseResult GetroleMenuTree(Long userId);

    ResponseResult updateRole(RoleMenuDTO roleMenuDTO);

    ResponseResult listallRole();

    //修改用户-①根据id查询用户信息
    List<Long> selectRoleIdByUserId(Long userId);
}

