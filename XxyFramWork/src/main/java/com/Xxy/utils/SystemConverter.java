package com.Xxy.utils;

import com.Xxy.domain.entity.Menu;
import com.Xxy.domain.vo.MenuTreeVo;


import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


//新增角色-获取菜单下拉树列表
public class SystemConverter {

    private SystemConverter() {}

    public static List<MenuTreeVo> buildMenuSelectTree(List<MenuTreeVo> menuTreeVos) {
        List<MenuTreeVo> options = menuTreeVos.stream()
                .filter(o -> o.getParentId().equals(0L))
                .map(o -> o.setChildren(getChildList(menuTreeVos, o)))
                .collect(Collectors.toList());

        return options;
    }

    private static List<MenuTreeVo> getChildList(List<MenuTreeVo> list, MenuTreeVo option) {
        List<MenuTreeVo> options = list.stream()
                .filter(o -> Objects.equals(o.getParentId(), option.getId()))
                .map(o -> o.setChildren(getChildList(list, o)))
                .collect(Collectors.toList());

        return options;
    }
}