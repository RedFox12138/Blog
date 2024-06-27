package com.Xxy.controller;

import com.Xxy.domain.ResponseResult;
import com.Xxy.domain.dto.RoleDTO;
import com.Xxy.domain.dto.RoleMenuDTO;
import com.Xxy.domain.entity.Role;
import com.Xxy.domain.vo.RoleVo;
import com.Xxy.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/role")
public class RoleController {
    @Autowired
    RoleService roleService;
    @GetMapping("/list")
    public ResponseResult RoleList(int pageNum, int pageSize, RoleDTO roleDTO)
    {
        return roleService.roleList(pageNum,pageSize,roleDTO);
    }

    @PutMapping("/changeStatus")
    public ResponseResult ChangeStatus(@RequestBody RoleDTO roleDTO)
    {
        return roleService.changeStatus(roleDTO);
    }

    @PostMapping
    public ResponseResult AddRole(@RequestBody RoleMenuDTO roleMenuDTO){
        return roleService.AddRole(roleMenuDTO);
    }

    @GetMapping("/{id}")
    public ResponseResult GetRoleList(@PathVariable("id")Long userId){
        return ResponseResult.okResult(roleService.getById(userId));
    }

    @PutMapping
    public ResponseResult UpdateRole(@RequestBody  RoleMenuDTO roleMenuDTO)
    {
        return roleService.updateRole(roleMenuDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseResult DeleteRole(@PathVariable("id")Long id)
    {
        roleService.removeById(id);
        return ResponseResult.okResult();
    }

    @GetMapping("/listAllRole")
    public ResponseResult listAllRole()
    {
        return roleService.listallRole();
    }




}
