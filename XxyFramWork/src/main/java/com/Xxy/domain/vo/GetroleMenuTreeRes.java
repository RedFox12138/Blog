package com.Xxy.domain.vo;

import com.Xxy.domain.entity.RoleMenu;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetroleMenuTreeRes {
    private List<MenuTreeVo> menus;
    private List<Long> checkedKeys;
}
