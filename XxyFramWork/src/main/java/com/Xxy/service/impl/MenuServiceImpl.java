package com.Xxy.service.impl;

import com.Xxy.constants.SystemConstants;
import com.Xxy.domain.ResponseResult;
import com.Xxy.domain.entity.Menu;
import com.Xxy.domain.entity.RoleMenu;
import com.Xxy.domain.vo.GetroleMenuTreeRes;
import com.Xxy.domain.vo.MenuTreeVo;
import com.Xxy.domain.vo.MenuVo;
import com.Xxy.enums.AppHttpCodeEnum;
import com.Xxy.mapper.MenuMapper;
import com.Xxy.mapper.RoleMenuMapper;
import com.Xxy.service.MenuService;
import com.Xxy.utils.BeanCopyUtils;
import com.Xxy.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.injector.methods.Insert;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.Xxy.utils.SystemConverter.buildMenuSelectTree;

/**
 * 菜单权限表(Menu)表服务实现类
 *
 * @author makejava
 * @since 2024-06-22 17:08:15
 */
@Service("menuService")
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {
    @Autowired
    private RoleMenuMapper roleMenuMapper;
    @Autowired
    private MenuService menuService;
    @Override
    public List<String> selectPermsByUserId(Long id) {
        //根据用户id查询对应的权限关键字
        //如果是管理员，返回所有的权限
        if(SecurityUtils.isAdmin()){
            LambdaQueryWrapper<Menu> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.in(Menu::getMenuType,SystemConstants.MENU,SystemConstants.BUTTON);
            lambdaQueryWrapper.eq(Menu::getStatus,SystemConstants.STATUS_NORMAL);
            List<Menu> menuList = list(lambdaQueryWrapper);
            List<String> perms = menuList.stream()
                    .map(Menu::getPerms)
                    .collect(Collectors.toList());
            return perms;
        }
        //否则返回去所具有的权限
        return getBaseMapper().selectPermsByUserId(id);

    }

    @Override
    public List<Menu> selectRouterMenuTreeByUserId(Long userId) {
        MenuMapper menuMapper = getBaseMapper();
        List<Menu>menus =null;
        //判断是否是管理员
        if(SecurityUtils.isAdmin())
        {
            //如果是 返回所有符合要求的Menu
            menus = menuMapper.selectAllRouterMenu();
        }else {
            //如果不是 获取当前用户所具有的Menu
            menus = menuMapper.selectRouterMenuTreeByUserId(userId);
        }
        //构建MenuTree
        //先找出第一层的菜单，然后去找他们的子菜单设置到children
        List<Menu>menuTree = buildMenuTree(menus,0L);
        return menuTree;
    }

    @Override
    public ResponseResult menuList(String status, String menuName) {
        LambdaQueryWrapper<Menu> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 状态为正常的菜单
        //这里判断条件一定要加StringUtils.hasText，如果为空就是返回全部。
        //如果不写这个判断条件，就查不到数据
        lambdaQueryWrapper.eq(StringUtils.hasText(status),Menu::getStatus, status);
        lambdaQueryWrapper.like(StringUtils.hasText(menuName),Menu::getMenuName,menuName);
        // 对isTop进行降序
        lambdaQueryWrapper.orderByAsc(Menu::getParentId,Menu::getOrderNum);
        //将querywrapper转换成list
        List<Menu> menuList = list(lambdaQueryWrapper);
        List<MenuVo> menuVos = BeanCopyUtils.copyBeanList(menuList, MenuVo.class);
        return ResponseResult.okResult(menuVos);
    }



    @Override
    public ResponseResult getMenu(Long id) {
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(Menu::getId,id);
        List<Menu> menuList = list(queryWrapper);
        List<MenuVo> menuVos = BeanCopyUtils.copyBeanList(menuList, MenuVo.class);


        return ResponseResult.okResult(menuVos.get(0));
    }

    @Override
    public ResponseResult updateMenu(Menu menu) {
        if (menu.getId().equals(menu.getParentId())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.UPMENUISME);
        }
        menuService.updateById(menu);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteMenu(Long menuId) {
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Menu::getParentId,menuId);
        Menu menu = menuService.getById(menuId);
        if (count(queryWrapper) != 0) {
            return ResponseResult.errorResult(500,"存在子菜单不允许删除");
        }
        menuService.removeById(menuId);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult GetroleMenuTree(Long userId) {

        Menu menu = new Menu();
        // 获取菜单列表
        List<Menu> menus = (List<Menu>) menuService.menuList(SystemConstants.STATUS_NORMAL, menu.getMenuName()).getData();

        List<MenuTreeVo> menuTreeVos = BeanCopyUtils.copyBeanList(menus, MenuTreeVo.class);
        menuTreeVos.forEach(vo -> vo.setLabel(vo.getMenuName()));

        // 构建树状结构
        List<MenuTreeVo> options = buildMenuSelectTree(menuTreeVos);

        LambdaQueryWrapper<RoleMenu> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(RoleMenu::getRoleId,userId);
        List<RoleMenu> roleMenus = roleMenuMapper.selectList(queryWrapper);
        List<Long> menuIds = roleMenus.stream()
                .map(RoleMenu::getMenuId)
                .collect(Collectors.toList());
        GetroleMenuTreeRes getroleMenuTreeRes =new GetroleMenuTreeRes(options,menuIds);
        return ResponseResult.okResult(getroleMenuTreeRes);

    }


    private List<Menu> buildMenuTree(List<Menu> menus, long parentId) {
        List<Menu> menuTree = menus.stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .map(menu -> menu.setChildren(getChildren(menu, menus)))
                .collect(Collectors.toList());
        return menuTree;
    }
    /**
     * 获取传入参数menu的子menu
     *
     * @param menu
     * @param menus
     * @return
     */
    private List<Menu> getChildren(Menu menu, List<Menu> menus) {
        List<Menu> childrenList = menus.stream()
                .filter(m -> m.getParentId().equals(menu.getId()))
                .map(m->m.setChildren(getChildren(m,menus)))
                .collect(Collectors.toList());
        return childrenList;
    }
}

