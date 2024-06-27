package com.Xxy.service.impl;

import com.Xxy.constants.SystemConstants;
import com.Xxy.domain.ResponseResult;
import com.Xxy.domain.dto.RoleDTO;
import com.Xxy.domain.dto.RoleMenuDTO;
import com.Xxy.domain.entity.Comment;
import com.Xxy.domain.entity.Menu;
import com.Xxy.domain.entity.Role;
import com.Xxy.domain.entity.RoleMenu;
import com.Xxy.domain.vo.CommentVo;
import com.Xxy.domain.vo.MenuVo;
import com.Xxy.domain.vo.PageVo;
import com.Xxy.domain.vo.RoleVo;
import com.Xxy.mapper.RoleMapper;
import com.Xxy.mapper.RoleMenuMapper;
import com.Xxy.service.RoleMenuService;
import com.Xxy.service.RoleService;
import com.Xxy.utils.BeanCopyUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色信息表(Role)表服务实现类
 *
 * @author makejava
 * @since 2024-06-22 17:13:42
 */
@Service("roleService")
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    @Autowired
    private RoleMenuService roleMenuService;
    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Override
    public ResponseResult roleList(int pageNum, int pageSize, RoleDTO roleDTO) {
        //查询对应文章的根评论
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.hasText(roleDTO.getStatus()), Role::getStatus, roleDTO.getStatus());
        //对articleId进行判断
        queryWrapper.like(StringUtils.hasText(roleDTO.getRoleName()), Role::getRoleName, roleDTO.getRoleName());
        //分页查询
        Page<Role> page = new Page<>(pageNum, pageSize);
        page(page, queryWrapper);
        List<Role> Rolelist = list(queryWrapper);
        List<RoleVo> roleVos = BeanCopyUtils.copyBeanList(Rolelist, RoleVo.class);
        PageVo pageVo = new PageVo(roleVos, page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    @Override
    public List<String> selectRoleKeyByUserId(Long id) {
        //判断是否是管理员  如果是返回集合中只需要有admin
        if (id == 1L) {
            List<String> roleKeys = new ArrayList<>();
            roleKeys.add("admin");
            return roleKeys;
        }
        //如果不是，查询当前用户所具有的角色信息
        return getBaseMapper().selectRoleKeyByUserId(id);
    }

    @Override
    public ResponseResult changeStatus(RoleDTO roleDTO) {
        Role role = getById(roleDTO.getRoleid());
        role.setStatus(roleDTO.getStatus());
        updateById(role);
        return ResponseResult.okResult();
    }

    @Override
    @Transactional
    public ResponseResult AddRole(RoleMenuDTO roleMenuDTO) {
        Role role = new Role();
        role.setRoleName(roleMenuDTO.getRoleName());
        role.setRoleKey(roleMenuDTO.getRoleKey());
        role.setRoleSort(roleMenuDTO.getRoleSort());
        role.setStatus(roleMenuDTO.getStatus());
        role.setRemark(roleMenuDTO.getRemark());
        role.setDelFlag(SystemConstants.STATUS_NORMAL); // 默认未删除
        save(role);
        for (Long menuId : roleMenuDTO.getMenuIds()) {
            roleMenuMapper.insert(new RoleMenu(role.getId(), menuId));
        }
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult GetroleMenuTree(Long userId) {
        LambdaQueryWrapper<RoleMenu> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(RoleMenu::getRoleId, userId);
        List<RoleMenu> roleMenus = roleMenuMapper.selectList(queryWrapper);

        // 提取menuId列表
        List<Long> menuIds = roleMenus.stream()
                .map(RoleMenu::getMenuId)
                .collect(Collectors.toList());
        return null;
    }

    @Override
    public ResponseResult updateRole(RoleMenuDTO roleMenuDTO) {
        Role role = getById(roleMenuDTO.getId());
        role.setStatus(roleMenuDTO.getStatus());
        role.setRoleName(roleMenuDTO.getRoleName());
        role.setRoleKey(roleMenuDTO.getRoleKey());
        role.setRoleSort(roleMenuDTO.getRoleSort());
        role.setRemark(roleMenuDTO.getRemark());
        updateById(role);
        LambdaQueryWrapper<RoleMenu> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(RoleMenu::getRoleId, roleMenuDTO.getId());
        List<RoleMenu> roleMenus = roleMenuMapper.selectList(queryWrapper);
        roleMenuMapper.delete(queryWrapper);

        // 插入新的RoleMenu数据
        for (Long menuId : roleMenuDTO.getMenuIds()) {
            roleMenuMapper.insert(new RoleMenu(role.getId(), menuId));
        }

        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult listallRole() {
        return ResponseResult.okResult(list(Wrappers.<Role>lambdaQuery().eq(Role::getStatus, SystemConstants.STATUS_NORMAL)));
    }
    @Override
    public List<Long> selectRoleIdByUserId(Long userId) {
        return getBaseMapper().selectRoleIdByUserId(userId);
    }
}
