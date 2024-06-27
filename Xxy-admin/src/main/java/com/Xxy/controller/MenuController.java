package com.Xxy.controller;

import com.Xxy.constants.SystemConstants;
import com.Xxy.domain.ResponseResult;
import com.Xxy.domain.entity.Menu;
import com.Xxy.domain.vo.MenuTreeVo;
import com.Xxy.service.MenuService;
import com.Xxy.service.RoleService;
import com.Xxy.utils.BeanCopyUtils;
import com.Xxy.utils.SystemConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.config.RepositoryNameSpaceHandler;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.Xxy.utils.SystemConverter.buildMenuSelectTree;

@RestController
@RequestMapping("/system/menu")
public class MenuController {
    @Autowired
    private MenuService menuService;
    @GetMapping("/list")
    public ResponseResult MenuList(String status,String menuName)
    {
        return menuService.menuList(status,menuName);
    }

    @PostMapping
    public ResponseResult AddMenu(@RequestBody Menu menu){
        //save函数可以直接对entity类型的数据库类进行保存
        menuService.save(menu);
        return ResponseResult.okResult();
    }

    @GetMapping("/{id}")
    public ResponseResult GetMenu(@PathVariable("id") Long id)
    {
        return menuService.getMenu(id);
    }

    @PutMapping
    public ResponseResult UpdateMenu(@RequestBody Menu menu)
    {
        return menuService.updateMenu(menu);
    }

    @DeleteMapping("/{menuId}")
    public ResponseResult DeleteMenu(@PathVariable("menuId") Long menuId)
    {
        return menuService.deleteMenu(menuId);
    }

    @GetMapping("/treeselect")
    public ResponseResult treeselect() {
        Menu menu = new Menu();
        // 获取菜单列表
        List<Menu> menus = (List<Menu>) menuService.menuList(SystemConstants.STATUS_NORMAL, menu.getMenuName()).getData();

        List<MenuTreeVo> menuTreeVos = BeanCopyUtils.copyBeanList(menus, MenuTreeVo.class);
        menuTreeVos.forEach(vo -> vo.setLabel(vo.getMenuName()));

        // 构建树状结构
        List<MenuTreeVo> options = buildMenuSelectTree(menuTreeVos);

        // 返回包含树状结构的 ResponseResult
        return ResponseResult.okResult(options);
    }
    @GetMapping("/roleMenuTreeselect/{id}")
    public ResponseResult GetroleMenuTree(@PathVariable("id")Long userId)
    {
        return menuService.GetroleMenuTree(userId);
    }


}

